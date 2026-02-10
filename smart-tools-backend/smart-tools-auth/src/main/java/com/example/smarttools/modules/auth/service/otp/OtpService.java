/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/otp/OtpService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class OtpService
 * 注解：Service、Qualifier、Value
 * 依赖：org.slf4j.Logger、org.slf4j.LoggerFactory、org.springframework.beans.factory.annotation.Qualifier、org.springframework.beans.factory.annotation.Value、org.springframework.stereotype.Service、java.security.SecureRandom、java.util.concurrent.CompletableFuture、java.util.concurrent.Executor
 * 公开方法：send(String channel, String target)；verify(String channel, String target, String code)；check(String channel, String target, String code)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * OTP 服务：生成、存储、投递与校验一次性验证码。
 */
package com.example.smarttools.modules.auth.service.otp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class OtpService {
    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    // 数字验证码随机数生成器
    private final SecureRandom random = new SecureRandom();
    // OTP 存储（Redis / 内存）
    private final OtpStore store;
    // OTP 发送通道（邮件 / MQ）
    private final OtpDelivery delivery;
    // 异步发送线程池
    private final Executor appTaskExecutor;
    // 验证码长度
    private final int codeLength;
    // 验证码有效期（秒）
    private final long ttlSeconds;
    // 最大尝试次数
    private final int maxAttempts;
    // 是否输出验证码日志
    private final boolean logCode;

    public OtpService(
            OtpStore store,
            OtpDelivery delivery,
            @Qualifier("appTaskExecutor") Executor appTaskExecutor,
            @Value("${app.otp.code-length:6}") int codeLength,
            @Value("${app.otp.ttl-seconds:60}") long ttlSeconds,
            @Value("${app.otp.max-attempts:5}") int maxAttempts,
            @Value("${app.otp.log-code:true}") boolean logCode
    ) {
        // 注入存储、投递与线程池
        this.store = store;
        this.delivery = delivery;
        this.appTaskExecutor = appTaskExecutor;
        // 限制验证码长度范围
        this.codeLength = Math.max(4, Math.min(codeLength, 10));
        // 有效期下限保护，避免过短导致体验差
        this.ttlSeconds = Math.max(60, ttlSeconds);
        // 最大尝试次数下限保护
        this.maxAttempts = Math.max(1, maxAttempts);
        // 是否输出验证码日志（生产可关闭）
        this.logCode = logCode;
    }

    public OtpSendResult send(String channel, String target) {
        // 生成 OTP key 与随机验证码
        var key = key(channel, target);
        var code = generateCode(codeLength);
        // 先落库再异步发送，保证发送失败也能追踪状态
        store.put(key, code, ttlSeconds);
        // 异步投递，避免阻塞接口响应
        CompletableFuture.runAsync(() -> {
            try {
                // 交给具体投递实现
                delivery.deliver(channel, target, code, ttlSeconds, null);
            } catch (Exception e) {
                // 发送失败不影响后续接口返回
                log.error("OTP delivery failed: channel={} target={}", channel, target, e);
            }
        }, appTaskExecutor);
        // 仅用于调试或测试环境输出验证码
        if (logCode) {
            log.info("OTP {} {} code={}", channel, target, code);
        }
        // 返回有效期给前端展示倒计时
        return new OtpSendResult(ttlSeconds);
    }

    public void verify(String channel, String target, String code) {
        // 校验成功即消费
        var key = key(channel, target);
        store.verifyAndConsume(key, code, maxAttempts);
    }

    public void check(String channel, String target, String code) {
        // 仅校验，不消费
        var key = key(channel, target);
        store.check(key, code, maxAttempts);
    }

    private String key(String channel, String target) {
        // 统一格式化渠道与目标，避免重复 key
        var ch = channel == null ? "" : channel.trim().toLowerCase();
        var t = target == null ? "" : target.trim();
        // 邮箱统一小写，确保同一账号命中同一 key
        if ("email".equals(ch)) {
            t = t.toLowerCase();
        }
        return ch + ":" + t;
    }

    private String generateCode(int len) {
        // 计算范围：如 len=6 => [100000, 999999]
        int max = 1;
        for (int i = 0; i < len; i++) max *= 10;
        int min = max / 10;
        // 生成随机数并补零
        int val = random.nextInt(max - min) + min;
        return String.format("%0" + len + "d", val);
    }

    // 发送结果结构，向前端传递有效期
    public record OtpSendResult(long expiresInSeconds) {
    }
}
