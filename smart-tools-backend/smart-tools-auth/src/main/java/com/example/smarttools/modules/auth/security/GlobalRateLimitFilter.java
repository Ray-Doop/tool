/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/security/GlobalRateLimitFilter.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：security
 * 类型：class GlobalRateLimitFilter
 * 注解：Component、Value、Override
 * 依赖：jakarta.servlet.FilterChain、jakarta.servlet.ServletException、jakarta.servlet.http.HttpServletRequest、jakarta.servlet.http.HttpServletResponse、org.springframework.beans.factory.annotation.Value、org.springframework.http.MediaType、org.springframework.stereotype.Component、org.springframework.web.filter.OncePerRequestFilter、java.io.IOException、java.nio.charset.StandardCharsets、java.time.Instant、java.util.concurrent.ConcurrentHashMap、java.util.concurrent.atomic.AtomicInteger
 * 公开方法：shouldNotFilter(HttpServletRequest request)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局风控限流：按 IP 与设备码统计 /api 请求量。
 *
 * 用于抗突发流量与刷接口，优先在鉴权前执行，减少后续链路开销。
 */
@Component
public class GlobalRateLimitFilter extends OncePerRequestFilter {
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicInteger cleanupCounter = new AtomicInteger(0);
    private final int globalRequestsPerMinute;
    private final int deviceRequestsPerMinute;
    private final boolean riskEnabled;
    private final int maxEntries;
    private final int cleanupEveryRequests;

    public GlobalRateLimitFilter(
            @Value("${app.risk.global-requests-per-minute:600}") int globalRequestsPerMinute,
            @Value("${app.risk.device-requests-per-minute:300}") int deviceRequestsPerMinute,
            @Value("${app.risk.enabled:false}") boolean riskEnabled,
            @Value("${app.risk.window-cache-max-entries:200000}") int maxEntries,
            @Value("${app.risk.window-cleanup-every-requests:2000}") int cleanupEveryRequests
    ) {
        this.globalRequestsPerMinute = Math.max(60, globalRequestsPerMinute);
        this.deviceRequestsPerMinute = Math.max(30, deviceRequestsPerMinute);
        this.riskEnabled = riskEnabled;
        this.maxEntries = Math.max(1000, maxEntries);
        this.cleanupEveryRequests = Math.max(500, cleanupEveryRequests);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        var uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/api/")) return true;
        return uri.startsWith("/api/monitor/")
                || uri.startsWith("/api/logs/")
                || uri.startsWith("/uploads/")
                || uri.startsWith("/h2-console/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!riskEnabled) {
            filterChain.doFilter(request, response);
            return;
        }
        var now = Instant.now();
        var ip = clientIp(request);
        var device = deviceId(request);
        var ipKey = "ip:" + ip;
        var deviceKey = device == null ? null : "dev:" + device;
        if (exceeded(ipKey, now, globalRequestsPerMinute) || exceeded(deviceKey, now, deviceRequestsPerMinute)) {
            writeTooManyRequests(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean exceeded(String key, Instant now, int limit) {
        if (key == null || key.isBlank()) return false;
        windows.compute(key, (k, v) -> {
            if (v == null) return new Window(now, now, 1);
            if (now.isAfter(v.start().plusSeconds(60))) return new Window(now, now, 1);
            return new Window(v.start(), now, v.count() + 1);
        });
        cleanupIfNeeded(now);
        var w = windows.get(key);
        return w != null && w.count() > limit;
    }

    private String deviceId(HttpServletRequest request) {
        var did = request.getHeader("X-Device-Id");
        if (did != null && !did.isBlank()) return did.trim();
        var code = request.getHeader("X-Device-Code");
        if (code != null && !code.isBlank()) return code.trim();
        return null;
    }

    private String clientIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            var first = xff.split(",")[0].trim();
            if (!first.isBlank()) return first;
        }
        var xrip = request.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) return xrip.trim();
        var addr = request.getRemoteAddr();
        return addr == null ? "" : addr;
    }

    private void writeTooManyRequests(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"success\":false,\"data\":null,\"error\":{\"code\":\"TOO_MANY_REQUESTS\",\"message\":\"请求过于频繁，请稍后再试\"}}");
    }

    private void cleanupIfNeeded(Instant now) {
        var needCleanup = cleanupCounter.incrementAndGet() % cleanupEveryRequests == 0 || windows.size() > maxEntries;
        if (!needCleanup) return;
        windows.entrySet().removeIf(entry -> now.isAfter(entry.getValue().lastAccess().plusSeconds(120)));
    }

    private record Window(Instant start, Instant lastAccess, int count) {
    }
}
