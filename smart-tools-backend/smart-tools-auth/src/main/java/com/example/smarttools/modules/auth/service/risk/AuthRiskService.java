/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/risk/AuthRiskService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class AuthRiskService
 * 注解：Service、Value
 * 依赖：com.example.smarttools.common.exception.BizException、org.springframework.beans.factory.annotation.Value、org.springframework.http.HttpStatus、org.springframework.stereotype.Service、java.time.Instant、java.util.Arrays、java.util.Set、java.util.concurrent.ConcurrentHashMap、java.util.stream.Collectors
 * 公开方法：checkIp(String ip)；recordCaptchaFailure(String ip)；recordPasswordFailure(String ip)；recordOtpFailure(String ip)；checkOtpSend(String ip, String target)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 鉴权风控服务：IP 白名单、失败封禁、OTP 发送限流与冷却。
 */
package com.example.smarttools.modules.auth.service.risk;

import com.example.smarttools.common.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AuthRiskService {
    // 失败记录：key=ip:type，用于累计失败次数与封禁窗口
    private final ConcurrentHashMap<String, FailureEntry> failures = new ConcurrentHashMap<>();
    // 发送窗口：key=otpSend:ip:target，用于统计每小时发送次数
    private final ConcurrentHashMap<String, WindowEntry> otpSendWindows = new ConcurrentHashMap<>();
    // 冷却时间：key=otpSend:ip:target，记录最近一次发送时间
    private final ConcurrentHashMap<String, Instant> otpSendCooldowns = new ConcurrentHashMap<>();
    // IP 白名单：非空时只允许名单内访问
    private final Set<String> ipAllowlist;

    // 风控总开关（生产开启、开发可关闭）
    private final boolean riskEnabled;
    // 图形验证码错误阈值
    private final int captchaFailThreshold;
    // 密码错误阈值
    private final int passwordFailThreshold;
    // OTP 校验错误阈值
    private final int otpFailThreshold;
    // 错误计数窗口时长
    private final long failureWindowSeconds;
    // 触发封禁时长
    private final long blockSeconds;
    // 每小时 OTP 发送上限
    private final int otpSendPerHour;
    // OTP 发送冷却时间
    private final long otpSendCooldownSeconds;

    public AuthRiskService(
            @Value("${app.risk.enabled:false}") boolean riskEnabled,
            @Value("${app.risk.ip-allowlist:}") String ipAllowlist,
            @Value("${app.risk.failure-window-seconds:600}") long failureWindowSeconds,
            @Value("${app.risk.block-seconds:900}") long blockSeconds,
            @Value("${app.risk.captcha-fail-threshold:10}") int captchaFailThreshold,
            @Value("${app.risk.password-fail-threshold:8}") int passwordFailThreshold,
            @Value("${app.risk.otp-fail-threshold:8}") int otpFailThreshold,
            @Value("${app.risk.otp-send-per-hour:5}") int otpSendPerHour,
            @Value("${app.risk.otp-send-cooldown-seconds:60}") long otpSendCooldownSeconds
    ) {
        // 风控总开关
        this.riskEnabled = riskEnabled;
        // 解析 IP 白名单配置
        this.ipAllowlist = Arrays.stream(ipAllowlist == null ? new String[0] : ipAllowlist.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
        // 失败计数窗口与封禁时长下限保护
        this.failureWindowSeconds = Math.max(60, failureWindowSeconds);
        this.blockSeconds = Math.max(60, blockSeconds);
        // 各类失败阈值下限保护
        this.captchaFailThreshold = Math.max(3, captchaFailThreshold);
        this.passwordFailThreshold = Math.max(3, passwordFailThreshold);
        this.otpFailThreshold = Math.max(3, otpFailThreshold);
        // OTP 发送限额与冷却时间下限保护
        this.otpSendPerHour = Math.max(1, otpSendPerHour);
        this.otpSendCooldownSeconds = Math.max(10, otpSendCooldownSeconds);
    }

    public void checkIp(String ip) {
        // 风控关闭时直接放行
        if (!riskEnabled) return;
        // 标准化客户端 IP
        var clientIp = ip == null ? "" : ip.trim();
        // 白名单启用时仅允许名单内 IP
        if (!ipAllowlist.isEmpty() && !ipAllowlist.contains(clientIp)) {
            throw new BizException(HttpStatus.FORBIDDEN, "IP_NOT_ALLOWED", "该 IP 不允许访问");
        }
        // 检查 IP 是否已被封禁
        var now = Instant.now();
        var entry = failures.get(key(clientIp, "ip"));
        // IP 级封禁校验
        if (entry != null && entry.blockedUntil() != null && now.isBefore(entry.blockedUntil())) {
            throw new BizException(HttpStatus.TOO_MANY_REQUESTS, "IP_BLOCKED", "请求过于频繁，请稍后再试");
        }
    }

    public void recordCaptchaFailure(String ip) {
        // 记录图形验证码失败
        if (!riskEnabled) return;
        recordFailure(ip, "captcha", captchaFailThreshold);
    }

    public void recordPasswordFailure(String ip) {
        // 记录密码校验失败
        if (!riskEnabled) return;
        recordFailure(ip, "password", passwordFailThreshold);
    }

    public void recordOtpFailure(String ip) {
        // 记录 OTP 校验失败
        if (!riskEnabled) return;
        recordFailure(ip, "otp", otpFailThreshold);
    }

    public void checkOtpSend(String ip, String target) {
        // 风控关闭时直接放行
        if (!riskEnabled) return;
        // 规范化 IP 与目标
        var clientIp = ip == null ? "" : ip.trim();
        var t = target == null ? "" : target.trim().toLowerCase();
        var now = Instant.now();
        var k = "otpSend:" + clientIp + ":" + t;
        // 冷却时间校验，防止刷新页面绕过倒计时
        var last = otpSendCooldowns.get(k);
        if (last != null && now.isBefore(last.plusSeconds(otpSendCooldownSeconds))) {
            throw new BizException(HttpStatus.TOO_MANY_REQUESTS, "OTP_SEND_COOLDOWN", "发送过于频繁，请稍后再试");
        }
        // 每小时发送次数统计
        var windowStart = now.minusSeconds(3600);
        otpSendWindows.compute(k, (kk, v) -> {
            // 初次或跨小时重置窗口
            if (v == null || v.windowStart().isBefore(windowStart)) {
                return new WindowEntry(now, 1);
            }
            // 窗口期内计数累加
            return new WindowEntry(v.windowStart(), v.count() + 1);
        });
        var w = otpSendWindows.get(k);
        // 超过每小时上限直接拒绝
        if (w != null && w.count() > otpSendPerHour) {
            throw new BizException(HttpStatus.TOO_MANY_REQUESTS, "OTP_SEND_LIMIT", "发送过于频繁，请稍后再试");
        }
        // 记录本次发送时间
        otpSendCooldowns.put(k, now);
    }

    private void recordFailure(String ip, String type, int threshold) {
        // 记录失败计数，超过阈值触发封禁
        if (!riskEnabled) return;
        var clientIp = ip == null ? "" : ip.trim();
        var now = Instant.now();
        // 在窗口期内累计失败次数，达到阈值后封禁
        failures.compute(key(clientIp, type), (k, v) -> {
            // 首次失败创建窗口
            if (v == null) {
                return new FailureEntry(now, 1, null);
            }
            // 已封禁直接保持
            if (v.blockedUntil() != null && now.isBefore(v.blockedUntil())) {
                return v;
            }
            // 失败窗口过期则重置计数
            if (now.isAfter(v.windowStart().plusSeconds(failureWindowSeconds))) {
                return new FailureEntry(now, 1, null);
            }
            // 窗口期内递增计数
            var next = v.count() + 1;
            // 超过阈值则触发封禁
            if (next >= threshold) {
                var blockedUntil = now.plusSeconds(blockSeconds);
                // 同步写入 IP 级封禁，统一拦截
                failures.put(key(clientIp, "ip"), new FailureEntry(now, next, blockedUntil));
                return new FailureEntry(v.windowStart(), next, blockedUntil);
            }
            // 未达到阈值仅更新计数
            return new FailureEntry(v.windowStart(), next, null);
        });
    }

    private String key(String ip, String type) {
        // 以 IP 与类型组合生成唯一 key
        return (ip == null ? "" : ip) + ":" + type;
    }

    // 失败统计条目：窗口起点、计数、封禁截止时间
    private record FailureEntry(Instant windowStart, int count, Instant blockedUntil) {
    }

    // OTP 发送窗口条目：窗口起点与计数
    private record WindowEntry(Instant windowStart, int count) {
    }
}
