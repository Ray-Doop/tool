/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/log/OperationLogInterceptor.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-api
 * 分层：log
 * 类型：class OperationLogInterceptor
 * 注解：Component、Override
 * 依赖：com.example.smarttools.modules.auth.security.UserPrincipal、com.example.smarttools.modules.system.service.OperationLogService、jakarta.servlet.http.HttpServletRequest、jakarta.servlet.http.HttpServletResponse、org.springframework.security.core.context.SecurityContextHolder、org.springframework.stereotype.Component、org.springframework.web.servlet.HandlerInterceptor、java.util.Set
 * 公开方法：afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Excep…)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.api.log;

import com.example.smarttools.modules.auth.security.UserPrincipal;
import com.example.smarttools.modules.system.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * 操作日志拦截器：记录 /api 请求的模块、动作与结果。
 */
@Component
public class OperationLogInterceptor implements HandlerInterceptor {
    private static final Set<String> IGNORE_PREFIX = Set.of(
            "/api/logs",
            "/api/monitor",
            "/uploads",
            "/h2-console"
    );

    private final OperationLogService logService;

    public OperationLogInterceptor(OperationLogService logService) {
        this.logService = logService;
    }

    /**
     * 请求完成后记录日志，避免影响主请求链。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        var uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/api/")) return;
        for (var prefix : IGNORE_PREFIX) {
            if (uri.startsWith(prefix)) return;
        }
        var module = resolveModule(uri);
        var action = request.getMethod();
        var target = uri;
        var result = ex == null ? String.valueOf(response.getStatus()) : "EXCEPTION";
        var userId = resolveUserId();
        var ip = resolveIp(request);
        var userAgent = request.getHeader("User-Agent");
        logService.record(userId, module, action, target, result, ip, userAgent);
    }

    private Long resolveUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        var principal = auth.getPrincipal();
        if (principal instanceof UserPrincipal p) {
            return p.userId();
        }
        return null;
    }

    private String resolveModule(String uri) {
        var parts = uri.split("/");
        if (parts.length > 2) return parts[2];
        return "api";
    }

    private String resolveIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        var xrip = request.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) return xrip.trim();
        return request.getRemoteAddr();
    }
}
