/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/admin/AdminFunctionController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-system
 * 分层：controller
 * 类型：class AdminFunctionController
 * 注解：RestController、RequestMapping、NotNull、NotBlank、GetMapping、PostMapping、PutMapping、DeleteMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.admin.SysFunction、com.example.smarttools.domain.admin.SysFunctionRepository、jakarta.validation.Valid、jakarta.validation.constraints.NotBlank、jakarta.validation.constraints.NotNull、org.springframework.http.HttpStatus、org.springframework.web.bind.annotation.DeleteMapping、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.PathVariable、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.PutMapping、org.springframework.web.bind.annotation.RequestBody
 * 公开方法：create(@Valid @RequestBody FunctionUpsertRequest req)；update(@PathVariable Long id, @Valid @RequestBody FunctionUpsertRequest req)；delete(@PathVariable Long id)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.system.controller.admin;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.admin.SysFunction;
import com.example.smarttools.domain.admin.SysFunctionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/api/admin/functions")
public class AdminFunctionController {
    private final SysFunctionRepository functionRepository;

    public AdminFunctionController(SysFunctionRepository functionRepository) {
        this.functionRepository = functionRepository;
    }

    public record FunctionUpsertRequest(
            @NotNull Long moduleId,
            @NotBlank String code,
            @NotBlank String name,
            String description,
            String endpoint,
            String method,
            Boolean enabled
    ) {}

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(value = "moduleId", required = false) Long moduleId) {
        var items = moduleId == null
                ? functionRepository.findAll()
                : functionRepository.findAllByModuleIdOrderByCreatedAtDesc(moduleId);
        return ApiResponse.ok(items.stream().map(this::toView).toList());
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody FunctionUpsertRequest req) {
        var f = new SysFunction();
        f.setModuleId(req.moduleId());
        f.setCode(req.code());
        f.setName(req.name());
        f.setDescription(req.description());
        f.setEndpoint(req.endpoint());
        f.setMethod(req.method());
        f.setEnabled(req.enabled == null || req.enabled);
        var saved = functionRepository.save(f);
        return ApiResponse.ok(toView(saved));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody FunctionUpsertRequest req) {
        var f = functionRepository.findById(id).orElseThrow(() ->
                new BizException(HttpStatus.NOT_FOUND, "FUNCTION_NOT_FOUND", "功能不存在"));
        f.setModuleId(req.moduleId());
        f.setCode(req.code());
        f.setName(req.name());
        f.setDescription(req.description());
        f.setEndpoint(req.endpoint());
        f.setMethod(req.method());
        f.setEnabled(req.enabled == null ? f.isEnabled() : req.enabled);
        var saved = functionRepository.save(f);
        return ApiResponse.ok(toView(saved));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        functionRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    private Map<String, Object> toView(SysFunction f) {
        return Map.of(
                "id", f.getId(),
                "moduleId", f.getModuleId(),
                "code", f.getCode(),
                "name", f.getName(),
                "description", f.getDescription(),
                "endpoint", f.getEndpoint(),
                "method", f.getMethod(),
                "enabled", f.isEnabled(),
                "createdAt", f.getCreatedAt(),
                "updatedAt", f.getUpdatedAt()
        );
    }
}
