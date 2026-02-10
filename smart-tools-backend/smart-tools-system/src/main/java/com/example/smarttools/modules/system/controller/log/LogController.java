/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/log/LogController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-system
 * 分层：controller
 * 类型：class LogController
 * 注解：RestController、RequestMapping、PostMapping、Valid、AuthenticationPrincipal、Async
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.domain.admin.SysErrorLog、com.example.smarttools.domain.admin.SysErrorLogRepository、com.example.smarttools.domain.log.SysVisitLog、com.example.smarttools.domain.log.SysVisitLogRepository、com.example.smarttools.modules.auth.security.UserPrincipal、com.example.smarttools.modules.system.domain.dto.ErrorLogRequest、com.example.smarttools.modules.system.domain.dto.VisitLogRequest、jakarta.servlet.http.HttpServletRequest、jakarta.validation.Valid、org.springframework.scheduling.annotation.Async、org.springframework.security.core.annotation.AuthenticationPrincipal、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.RequestBody
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.system.controller.log;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.domain.admin.SysErrorLog;
import com.example.smarttools.domain.admin.SysErrorLogRepository;
import com.example.smarttools.domain.log.SysVisitLog;
import com.example.smarttools.domain.log.SysVisitLogRepository;
import com.example.smarttools.modules.auth.security.UserPrincipal;
import com.example.smarttools.modules.system.domain.dto.ErrorLogRequest;
import com.example.smarttools.modules.system.domain.dto.VisitLogRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    private final SysVisitLogRepository visitLogRepository;
    private final SysErrorLogRepository errorLogRepository;

    public LogController(SysVisitLogRepository visitLogRepository, SysErrorLogRepository errorLogRepository) {
        this.visitLogRepository = visitLogRepository;
        this.errorLogRepository = errorLogRepository;
    }

    @PostMapping("/visit")
    public ApiResponse<Void> visit(
            @Valid @RequestBody VisitLogRequest req,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        persistAsync(req, httpRequest, principal);
        return ApiResponse.ok(null);
    }

    @PostMapping("/error")
    public ApiResponse<Void> error(
            @Valid @RequestBody ErrorLogRequest req,
            HttpServletRequest httpRequest
    ) {
        persistErrorAsync(req, httpRequest);
        return ApiResponse.ok(null);
    }

    @Async("appTaskExecutor")
    void persistAsync(VisitLogRequest req, HttpServletRequest httpRequest, UserPrincipal principal) {
        var log = new SysVisitLog();
        log.setToolSlug(req.toolSlug());
        log.setPath(req.path());
        log.setIp(resolveIp(httpRequest));
        log.setUserAgent(httpRequest.getHeader("User-Agent"));
        if (principal != null) {
            log.setUserId(principal.userId());
        }
        visitLogRepository.save(log);
    }

    @Async("appTaskExecutor")
    void persistErrorAsync(ErrorLogRequest req, HttpServletRequest httpRequest) {
        var log = new SysErrorLog();
        log.setSource(trim(req.source(), 32));
        log.setLevel(trim(req.level(), 16));
        log.setCode(trim(req.code(), 64));
        log.setMessage(trim(req.message(), 512));
        log.setDetail(trim(req.detail(), 2000));
        log.setPath(trim(req.path(), 256));
        log.setMethod(trim(req.method(), 16));
        log.setPageUrl(trim(req.pageUrl(), 256));
        log.setStack(trim(req.stack(), 2000));
        log.setIp(resolveIp(httpRequest));
        log.setUserAgent(trim(httpRequest.getHeader("User-Agent"), 256));
        errorLogRepository.save(log);
    }

    private String resolveIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String trim(String text, int max) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text.length() > max ? text.substring(0, max) : text;
    }
}
