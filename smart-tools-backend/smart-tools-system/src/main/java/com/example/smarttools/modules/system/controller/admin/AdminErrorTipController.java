/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/admin/AdminErrorTipController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-system
 * 分层：controller
 * 类型：class AdminErrorTipController
 * 注解：RestController、RequestMapping、NotBlank、GetMapping、RequestParam、PostMapping、PutMapping、DeleteMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.admin.SysErrorLogRepository、com.example.smarttools.domain.admin.SysErrorTip、com.example.smarttools.domain.admin.SysErrorTipRepository、jakarta.validation.Valid、jakarta.validation.constraints.Min、jakarta.validation.constraints.NotBlank、org.springframework.data.domain.PageRequest、org.springframework.data.domain.Sort、org.springframework.http.HttpStatus、org.springframework.web.bind.annotation.DeleteMapping、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.PathVariable
 * 公开方法：create(@Valid @RequestBody ErrorTipUpsertRequest req)；update(@PathVariable Long id, @Valid @RequestBody ErrorTipUpsertRequest req)；delete(@PathVariable Long id)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.system.controller.admin;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.admin.SysErrorLogRepository;
import com.example.smarttools.domain.admin.SysErrorTip;
import com.example.smarttools.domain.admin.SysErrorTipRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/error-tips")
public class AdminErrorTipController {
    private final SysErrorTipRepository errorTipRepository;
    private final SysErrorLogRepository errorLogRepository;

    public AdminErrorTipController(SysErrorTipRepository errorTipRepository, SysErrorLogRepository errorLogRepository) {
        this.errorTipRepository = errorTipRepository;
        this.errorLogRepository = errorLogRepository;
    }

    public record ErrorTipUpsertRequest(
            @NotBlank String code,
            @NotBlank String title,
            @NotBlank String message,
            @NotBlank String severity,
            String solution,
            Boolean enabled
    ) {}

    @GetMapping
    public ApiResponse<Object> list(
            @RequestParam(value = "onlyEnabled", required = false) Boolean onlyEnabled,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) int size
    ) {
        if (onlyEnabled != null && onlyEnabled) {
            var items = errorTipRepository.findAllByEnabledTrueOrderByIdAsc();
            return ApiResponse.ok(items.stream().map(this::toView).toList());
        }
        var pr = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "id"));
        var data = errorTipRepository.findAll(pr);
        return ApiResponse.ok(Map.of(
                "items", data.getContent().stream().map(this::toView).toList(),
                "total", data.getTotalElements(),
                "page", page,
                "size", size
        ));
    }

    @GetMapping("/logs")
    public ApiResponse<Map<String, Object>> listLogs(
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) int size,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        var pr = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = errorLogRepository.search(
                blankToNull(source),
                blankToNull(level),
                blankToNull(keyword),
                pr
        );
        return ApiResponse.ok(Map.of(
                "items", result.getContent().stream().map(this::toLogView).toList(),
                "total", result.getTotalElements(),
                "page", page,
                "size", size
        ));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody ErrorTipUpsertRequest req) {
        if (errorTipRepository.existsByCode(req.code())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "ERROR_TIP_EXISTS", "错误提示编码已存在");
        }
        var tip = new SysErrorTip();
        tip.setCode(req.code());
        tip.setTitle(req.title());
        tip.setMessage(req.message());
        tip.setSeverity(req.severity());
        tip.setSolution(req.solution());
        tip.setEnabled(req.enabled == null || req.enabled);
        var saved = errorTipRepository.save(tip);
        return ApiResponse.ok(toView(saved));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody ErrorTipUpsertRequest req) {
        var tip = errorTipRepository.findById(id).orElseThrow(() ->
                new BizException(HttpStatus.NOT_FOUND, "ERROR_TIP_NOT_FOUND", "错误提示不存在"));
        if (!tip.getCode().equals(req.code()) && errorTipRepository.existsByCode(req.code())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "ERROR_TIP_EXISTS", "错误提示编码已存在");
        }
        tip.setCode(req.code());
        tip.setTitle(req.title());
        tip.setMessage(req.message());
        tip.setSeverity(req.severity());
        tip.setSolution(req.solution());
        tip.setEnabled(req.enabled == null ? tip.isEnabled() : req.enabled);
        var saved = errorTipRepository.save(tip);
        return ApiResponse.ok(toView(saved));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        errorTipRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    private Map<String, Object> toView(SysErrorTip tip) {
        return Map.of(
                "id", tip.getId(),
                "code", tip.getCode(),
                "title", tip.getTitle(),
                "message", tip.getMessage(),
                "severity", tip.getSeverity(),
                "solution", tip.getSolution(),
                "enabled", tip.isEnabled(),
                "createdAt", tip.getCreatedAt(),
                "updatedAt", tip.getUpdatedAt()
        );
    }

    private Map<String, Object> toLogView(com.example.smarttools.domain.admin.SysErrorLog log) {
        return Map.ofEntries(
                Map.entry("id", log.getId()),
                Map.entry("source", log.getSource()),
                Map.entry("level", log.getLevel()),
                Map.entry("code", log.getCode()),
                Map.entry("message", log.getMessage()),
                Map.entry("detail", log.getDetail()),
                Map.entry("path", log.getPath()),
                Map.entry("method", log.getMethod()),
                Map.entry("pageUrl", log.getPageUrl()),
                Map.entry("stack", log.getStack()),
                Map.entry("ip", log.getIp()),
                Map.entry("userAgent", log.getUserAgent()),
                Map.entry("createdAt", log.getCreatedAt())
        );
    }

    private String blankToNull(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text;
    }
}
