/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/admin/AdminPermissionController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-system
 * 分层：controller
 * 类型：class AdminPermissionController
 * 注解：RestController、RequestMapping、NotBlank、GetMapping、PostMapping、PutMapping、DeleteMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.admin.SysPermission、com.example.smarttools.domain.admin.SysPermissionRepository、jakarta.validation.Valid、jakarta.validation.constraints.NotBlank、org.springframework.http.HttpStatus、org.springframework.web.bind.annotation.DeleteMapping、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.PathVariable、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.PutMapping、org.springframework.web.bind.annotation.RequestBody、org.springframework.web.bind.annotation.RequestMapping
 * 公开方法：create(@Valid @RequestBody PermissionUpsertRequest req)；update(@PathVariable Long id, @Valid @RequestBody PermissionUpsertRequest req)；delete(@PathVariable Long id)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.system.controller.admin;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.admin.SysPermission;
import com.example.smarttools.domain.admin.SysPermissionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/admin/permissions")
public class AdminPermissionController {
    private final SysPermissionRepository permissionRepository;

    public AdminPermissionController(SysPermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public record PermissionUpsertRequest(
            @NotBlank String code,
            @NotBlank String name,
            @NotBlank String type,
            Long parentId,
            String path,
            String method,
            Boolean enabled
    ) {}

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(value = "onlyEnabled", required = false) Boolean onlyEnabled) {
        var items = (onlyEnabled != null && onlyEnabled)
                ? permissionRepository.findAllByEnabledTrueOrderByIdAsc()
                : permissionRepository.findAll();
        return ApiResponse.ok(items.stream().map(this::toView).toList());
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody PermissionUpsertRequest req) {
        if (permissionRepository.existsByCode(req.code())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "PERMISSION_EXISTS", "权限编码已存在");
        }
        var p = new SysPermission();
        p.setCode(req.code());
        p.setName(req.name());
        p.setType(req.type());
        p.setParentId(req.parentId());
        p.setPath(req.path());
        p.setMethod(req.method());
        p.setEnabled(req.enabled == null || req.enabled);
        var saved = permissionRepository.save(p);
        return ApiResponse.ok(toView(saved));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody PermissionUpsertRequest req) {
        var p = permissionRepository.findById(id).orElseThrow(() ->
                new BizException(HttpStatus.NOT_FOUND, "PERMISSION_NOT_FOUND", "权限不存在"));
        if (!p.getCode().equals(req.code()) && permissionRepository.existsByCode(req.code())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "PERMISSION_EXISTS", "权限编码已存在");
        }
        p.setCode(req.code());
        p.setName(req.name());
        p.setType(req.type());
        p.setParentId(req.parentId());
        p.setPath(req.path());
        p.setMethod(req.method());
        p.setEnabled(req.enabled == null ? p.isEnabled() : req.enabled);
        var saved = permissionRepository.save(p);
        return ApiResponse.ok(toView(saved));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    private Map<String, Object> toView(SysPermission p) {
        return Map.of(
                "id", p.getId(),
                "code", p.getCode(),
                "name", p.getName(),
                "type", p.getType(),
                "parentId", p.getParentId(),
                "path", p.getPath(),
                "method", p.getMethod(),
                "enabled", p.isEnabled(),
                "createdAt", p.getCreatedAt(),
                "updatedAt", p.getUpdatedAt()
        );
    }
}
