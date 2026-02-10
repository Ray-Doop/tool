/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/wechat/WechatLoginService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class WechatLoginService
 * 注解：Service、Value
 * 依赖：com.example.smarttools.modules.auth.service.IAuthService、com.tofries.wxlogin.WeixinLoginService、org.springframework.beans.factory.annotation.Value、org.springframework.stereotype.Service、java.net.URI、java.net.http.HttpClient、java.net.http.HttpRequest、java.net.http.HttpResponse、java.time.Duration、java.time.Instant、java.util.Base64、java.util.concurrent.ConcurrentHashMap
 * 公开方法：createQr()；poll(String state)；confirm(String state, String token, String refreshToken)；notifyScan(String state, String openid)；getOpenidForState(String state)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 微信扫码登录服务：生成二维码、轮询状态、发放 token。
 */
package com.example.smarttools.modules.auth.service.wechat;

import com.example.smarttools.modules.auth.service.IAuthService;
import com.tofries.wxlogin.WeixinLoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WechatLoginService {
    // 扫码状态缓存
    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();
    // 二维码有效期
    private final long ttlSeconds;
    // 微信登录 SDK
    private final WeixinLoginService weixinLoginService;
    // 认证服务
    private final IAuthService authService;
    // 扫码后的提示时间窗口
    private final long scannedHintSeconds = 20;
    // 用于拉取二维码图片
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();

    public WechatLoginService(
            @Value("${app.wechat.qr-ttl-seconds:300}") long ttlSeconds,
            WeixinLoginService weixinLoginService,
            IAuthService authService
    ) {
        // 有效期下限保护
        this.ttlSeconds = Math.max(60, ttlSeconds);
        this.weixinLoginService = weixinLoginService;
        this.authService = authService;
    }

    public WechatQr createQr() {
        // 生成二维码 state
        var state = weixinLoginService.getRandomSceneId();
        var expiresAt = Instant.now().plusSeconds(ttlSeconds);
        String qrContent = null;
        String qrImage = null;
        try {
            // 获取微信登录二维码链接
            qrContent = weixinLoginService.getLoginQrCodeUrl(state);
        } catch (Exception ignored) {
            // ignore
        }

        // 如果获取失败，或返回了 ticket=null 的无效链接（通常因缺少 appId/appSecret），则降级为 Dev 模式
        if (qrContent == null || qrContent.isBlank() || qrContent.contains("ticket=null")) {
            // 返回一个特殊的 schema，前端识别后展示“模拟扫码”界面
            qrContent = "smarttools://dev-qr?state=" + state;
        } else if (qrContent.startsWith("http")) {
            // 拉取二维码图片并转为 data url
            qrImage = fetchImageDataUrl(qrContent);
        }

        // 记录二维码状态
        store.put(state, new Entry(expiresAt, null, null, null, null, false));
        return new WechatQr(state, qrContent, qrImage, ttlSeconds);
    }

    public WechatPoll poll(String state) {
        // 获取缓存记录
        var entry = store.get(state);
        if (entry == null) return new WechatPoll("expired", null, null);
        if (Instant.now().isAfter(entry.expiresAt())) {
            store.remove(state);
            return new WechatPoll("expired", null, null);
        }
        if (entry.token != null) {
            // 已签发 token
            return new WechatPoll("authed", entry.token, entry.refreshToken);
        }
        
        String openid = null;
        boolean confirmed = entry.confirmed();
        // 从微信 SDK 获取 openid
        var confirmedOpenid = weixinLoginService.getOpenId(state);
        if (confirmedOpenid != null && !confirmedOpenid.isBlank()) {
            openid = confirmedOpenid.trim();
            confirmed = true;
            final boolean finalConfirmed = confirmed;
            final String finalOpenid = openid;
            // 缓存确认状态
            store.computeIfPresent(state, (k, v) -> new Entry(v.expiresAt(), v.token(), v.refreshToken(), finalOpenid, v.scannedAt(), finalConfirmed));
        } else if (entry.openid() != null && !entry.openid().isBlank()) {
            openid = entry.openid().trim();
            if (!confirmed && entry.scannedAt() != null && Instant.now().isBefore(entry.scannedAt().plusSeconds(scannedHintSeconds))) {
                // 扫码后短时间内返回 scanned 状态
                return new WechatPoll("scanned", null, null);
            }
            confirmed = true;
            final boolean finalConfirmed = true;
            final String finalOpenid = openid;
            // 更新确认状态
            store.computeIfPresent(state, (k, v) -> new Entry(v.expiresAt(), v.token(), v.refreshToken(), finalOpenid, v.scannedAt(), finalConfirmed));
        }

        if (openid != null && !openid.isBlank() && confirmed) {
             // 1. 如果用户已存在，直接登录
             var existing = authService.findUserByWechatOpenid(openid.trim());
             if (existing != null) {
                 // 签发 token 并写入缓存
                 var token = authService.issueToken(existing);
                 var refreshToken = authService.issueRefreshToken(existing);
                 store.computeIfPresent(state, (k, v) -> new Entry(v.expiresAt(), token, refreshToken, v.openid(), v.scannedAt(), v.confirmed()));
                 return new WechatPoll("authed", token, refreshToken);
             }
             
             // 2. 如果用户不存在，缓存 openid 并返回 need_register 状态
             //    前端将引导用户补全信息 (用户名/邮箱/密码/验证码)
             final String finalOpenid = openid.trim();
             store.computeIfPresent(state, (k, v) -> new Entry(v.expiresAt(), null, null, finalOpenid, v.scannedAt(), v.confirmed()));
             return new WechatPoll("need_register", null, null);
        }

        // 默认返回等待扫码状态
        return new WechatPoll("pending", null, null);
    }

    public void confirm(String state, String token, String refreshToken) {
        // 将签发的 token 写入缓存，轮询可直接返回
        store.computeIfPresent(state, (k, v) -> new Entry(v.expiresAt(), token, refreshToken, v.openid(), v.scannedAt(), v.confirmed()));
    }

    public void notifyScan(String state, String openid) {
        // 扫码通知，记录 openid 与扫码时间
        if (state == null || openid == null) return;
        var id = openid.trim();
        if (id.isBlank()) return;
        store.computeIfPresent(state, (k, v) -> new Entry(v.expiresAt(), v.token(), v.refreshToken(), id, Instant.now(), false));
    }

    public String getOpenidForState(String state) {
        // 根据 state 获取已确认 openid
        var entry = store.get(state);
        if (entry == null) return null;
        if (Instant.now().isAfter(entry.expiresAt())) {
            store.remove(state);
            return null;
        }
        if (!entry.confirmed()) return null;
        if (entry.openid() != null && !entry.openid().isBlank()) return entry.openid();
        // 兜底向微信 SDK 获取 openid
        var openid = weixinLoginService.getOpenId(state);
        if (openid != null && !openid.isBlank()) {
            store.computeIfPresent(state, (k, v) -> new Entry(v.expiresAt(), v.token(), v.refreshToken(), openid.trim(), v.scannedAt(), true));
            return openid.trim();
        }
        return null;
    }

    // 二维码返回结构
    public record WechatQr(String state, String qrContent, String qrImage, long expiresInSeconds) {
    }

    private String fetchImageDataUrl(String url) {
        try {
            var req =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .timeout(Duration.ofSeconds(5))
                            .header(
                                    "User-Agent",
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                            .GET()
                            .build();
            // 拉取二维码图片
            var resp = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) return null;
            var bytes = resp.body();
            if (bytes == null || bytes.length == 0) return null;
            var contentType = resp.headers().firstValue("Content-Type").orElse("image/jpeg");
            return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception ignored) {
            return null;
        }
    }

    // 轮询返回结构
    public record WechatPoll(String status, String token, String refreshToken) {
    }

    // 缓存条目：过期时间、token、openid、扫码时间与确认状态
    private record Entry(Instant expiresAt, String token, String refreshToken, String openid, Instant scannedAt, boolean confirmed) {
    }
}
