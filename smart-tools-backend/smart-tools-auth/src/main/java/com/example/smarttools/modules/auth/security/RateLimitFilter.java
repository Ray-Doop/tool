/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/security/RateLimitFilter.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：security
 * 类型：class RateLimitFilter
 * 注解：Component、Value、Override
 * 依赖：jakarta.servlet.FilterChain、jakarta.servlet.ServletException、jakarta.servlet.http.HttpServletRequest、jakarta.servlet.http.HttpServletResponse、org.springframework.beans.factory.annotation.Value、org.springframework.http.MediaType、org.springframework.stereotype.Component、org.springframework.web.filter.OncePerRequestFilter、java.io.IOException、java.nio.charset.StandardCharsets、java.time.Instant、java.util.concurrent.ConcurrentHashMap、java.util.concurrent.atomic.AtomicInteger
 * 公开方法：shouldNotFilter(HttpServletRequest request)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 鉴权接口基础限流过滤器：按 IP 统计 /api/auth 请求量。
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

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    // 简单滑动窗口：按 IP 统计 /api/auth 请求量
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    // 清理计数器，避免窗口无限增长
    private final AtomicInteger cleanupCounter = new AtomicInteger(0);
    // 每分钟请求上限
    private final int authRequestsPerMinute;
    // 风控总开关
    private final boolean riskEnabled;
    // 窗口缓存上限
    private final int maxEntries;
    // 清理触发频率
    private final int cleanupEveryRequests;

    public RateLimitFilter(
            @Value("${app.risk.auth-requests-per-minute:120}") int authRequestsPerMinute,
            @Value("${app.risk.enabled:false}") boolean riskEnabled,
            @Value("${app.risk.window-cache-max-entries:200000}") int maxEntries,
            @Value("${app.risk.window-cleanup-every-requests:2000}") int cleanupEveryRequests
    ) {
        // 低于 30 次/分钟时强制抬高，避免过度拦截
        this.authRequestsPerMinute = Math.max(30, authRequestsPerMinute);
        // 风控总开关
        this.riskEnabled = riskEnabled;
        this.maxEntries = Math.max(1000, maxEntries);
        this.cleanupEveryRequests = Math.max(500, cleanupEveryRequests);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 获取请求路径
        var uri = request.getRequestURI();
        // 仅对 /api/auth/** 做基础限流
        return uri == null || !uri.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 关闭风控时直接放行
        if (!riskEnabled) {
            filterChain.doFilter(request, response);
            return;
        }
        // 获取客户端 IP 并生成统计 key
        var ip = clientIp(request);
        var now = Instant.now();
        var key = ip;
        // 滑动窗口累计每分钟请求量
        windows.compute(key, (k, v) -> {
            // 首次访问创建窗口
            if (v == null) return new Window(now, now, 1);
            // 超过 60 秒则重置窗口
            if (now.isAfter(v.start().plusSeconds(60))) return new Window(now, now, 1);
            // 窗口期内递增计数
            return new Window(v.start(), now, v.count() + 1);
        });
        cleanupIfNeeded(now);
        // 获取当前窗口统计结果
        var w = windows.get(key);
        // 超过阈值直接返回 429
        if (w != null && w.count() > authRequestsPerMinute) {
            writeTooManyRequests(response);
            return;
        }
        // 未超过阈值则继续请求链
        filterChain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        // 优先解析代理头部
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // 取代理链的第一个 IP
            var first = xff.split(",")[0].trim();
            if (!first.isBlank()) return first;
        }
        var xrip = request.getHeader("X-Real-IP");
        // 反向代理常见头
        if (xrip != null && !xrip.isBlank()) return xrip.trim();
        // 回退到直连地址
        var addr = request.getRemoteAddr();
        return addr == null ? "" : addr;
    }

    private void writeTooManyRequests(HttpServletResponse response) throws IOException {
        // 避免走全局异常，直接返回简化的 429 JSON
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

    // 统计窗口：起始时间、最近访问时间与计数
    private record Window(Instant start, Instant lastAccess, int count) {
    }
}
