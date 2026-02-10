/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/exception/GlobalExceptionHandler.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-api
 * 分层：exception
 * 类型：class GlobalExceptionHandler
 * 注解：RestControllerAdvice、ExceptionHandler
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.admin.SysErrorLog、com.example.smarttools.domain.admin.SysErrorLogRepository、jakarta.validation.ConstraintViolationException、jakarta.servlet.http.HttpServletRequest、org.slf4j.Logger、org.slf4j.LoggerFactory、org.springframework.http.HttpStatus、org.springframework.http.ResponseEntity、org.springframework.security.access.AccessDeniedException、org.springframework.transaction.PlatformTransactionManager、org.springframework.transaction.TransactionDefinition、org.springframework.transaction.support.TransactionTemplate
 * 公开方法：handleBiz(BizException ex, HttpServletRequest request)；handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request)；handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request)；handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request)；handleAccessDenied(AccessDeniedException ex, HttpServletRequest request)；handleGeneric(Exception ex, HttpServletRequest request)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.api.exception;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.admin.SysErrorLog;
import com.example.smarttools.domain.admin.SysErrorLogRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：将后端异常统一转换为 ApiResponse。
 *
 * 核心目标：
 *
 *   统一响应结构：无论成功/失败，返回体都使用 {@link ApiResponse} 便于前端统一处理。
 *   统一错误语义：将不同来源的异常映射到明确的 HTTP 状态码与业务错误码。
 *   避免信息泄露：对未捕获异常隐藏堆栈细节，仅在服务端日志中记录。
 *
 *
 *
 * ApiResponse.success 约定（业务层级）：
 *
 *   success=true：业务处理成功
 *   success=false：业务处理失败（error.code/error.message 可供前端展示）
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final SysErrorLogRepository errorLogRepository;
    private final TransactionTemplate errorLogTx;

    public GlobalExceptionHandler(SysErrorLogRepository errorLogRepository, PlatformTransactionManager transactionManager) {
        this.errorLogRepository = errorLogRepository;
        var template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.errorLogTx = template;
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Void>> handleBiz(BizException ex, HttpServletRequest request) {
        log.warn("BizException: {} {}", ex.code(), ex.getMessage());
        persist(request, "backend", "warn", ex.code(), ex.getMessage(), null, ex);
        return ResponseEntity.status(ex.status()).body(ApiResponse.fail(ex.code(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("IllegalArgument: {}", ex.getMessage());
        persist(request, "backend", "warn", "BAD_REQUEST", ex.getMessage(), null, ex);
        return ResponseEntity.badRequest().body(ApiResponse.fail("BAD_REQUEST", ex.getMessage()));
    }

    /**
     * 处理 @Valid 的请求体校验失败（例如 record 字段标注了 @NotBlank）。
     *
     * 返回首个字段错误，避免一次性返回过多信息导致前端提示不友好。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation failed");
        log.warn("ValidationError: {}", msg);
        persist(request, "backend", "warn", "VALIDATION_ERROR", msg, null, ex);
        return ResponseEntity.badRequest().body(ApiResponse.fail("VALIDATION_ERROR", msg));
    }

    /**
     * 处理 @RequestParam/@PathVariable 等参数的校验失败（ConstraintViolation）。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("ConstraintViolation: {}", ex.getMessage());
        persist(request, "backend", "warn", "VALIDATION_ERROR", ex.getMessage(), null, ex);
        return ResponseEntity.badRequest().body(ApiResponse.fail("VALIDATION_ERROR", ex.getMessage()));
    }

    /**
     * 处理 Spring Security 的拒绝访问异常（用户已登录但无权限）。
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("AccessDenied: {}", ex.getMessage());
        persist(request, "backend", "warn", "FORBIDDEN", ex.getMessage(), null, ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.fail("FORBIDDEN", ex.getMessage()));
    }

    /**
     * 兜底异常处理。
     *
     * 注意：只记录服务端日志，不向客户端暴露内部异常堆栈细节。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        persist(request, "backend", "error", "INTERNAL_ERROR", "Unhandled exception", null, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("INTERNAL_ERROR", "Internal server error"));
    }

    private void persist(HttpServletRequest request, String source, String level, String code, String message, String detail, Exception ex) {
        try {
            var logItem = new SysErrorLog();
            logItem.setSource(source);
            logItem.setLevel(level);
            logItem.setCode(trim(code, 64));
            logItem.setMessage(trim(message, 512));
            logItem.setDetail(trim(detail, 2000));
            if (request != null) {
                logItem.setPath(trim(request.getRequestURI(), 256));
                logItem.setMethod(trim(request.getMethod(), 16));
                logItem.setIp(trim(resolveIp(request), 64));
                logItem.setUserAgent(trim(request.getHeader("User-Agent"), 256));
            }
            if (ex != null) {
                logItem.setStack(trim(buildStack(ex), 2000));
            }
            errorLogTx.executeWithoutResult(status -> errorLogRepository.save(logItem));
        } catch (Exception e) {
            log.warn("Persist error log failed", e);
        }
    }

    private String resolveIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String buildStack(Exception ex) {
        var sb = new StringBuilder();
        sb.append(ex.getClass().getName());
        if (ex.getMessage() != null) {
            sb.append(": ").append(ex.getMessage());
        }
        for (var element : ex.getStackTrace()) {
            sb.append("\n at ").append(element);
        }
        return sb.toString();
    }

    private String trim(String text, int max) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text.length() > max ? text.substring(0, max) : text;
    }
}
