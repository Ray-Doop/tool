/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/admin/AdminRoleController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-system
 * 分层：controller
 * 类型：class AdminRoleController
 * 注解：RestController、RequestMapping、GetMapping、RequestParam、PostMapping、PutMapping、DeleteMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.admin.SysPermissionRepository、com.example.smarttools.domain.admin.SysRole、com.example.smarttools.domain.admin.SysRolePermission、com.example.smarttools.domain.admin.SysRolePermissionRepository、com.example.smarttools.domain.admin.SysRoleRepository、jakarta.validation.Valid、jakarta.validation.constraints.NotBlank、org.springframework.http.HttpStatus、org.springframework.web.bind.annotation.DeleteMapping、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.PathVariable、org.springframework.web.bind.annotation.PostMapping
 * 公开方法：create(@Valid @RequestBody RoleUpsertRequest req)；update(@PathVariable Long id, @Valid @RequestBody RoleUpsertRequest req)；listPermissions(@PathVariable Long id)；updatePermissions(@PathVariable Long id, @RequestBody RolePermissionRequest req)；delete(@PathVariable Long id)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.system.controller.admin;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.admin.SysPermissionRepository;
import com.example.smarttools.domain.admin.SysRole;
import com.example.smarttools.domain.admin.SysRolePermission;
import com.example.smarttools.domain.admin.SysRolePermissionRepository;
import com.example.smarttools.domain.admin.SysRoleRepository;
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
import java.util.Objects;

import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * 后台角色管理接口。
 *
 * 提供角色的增删改查及权限分配功能。
 */
@RestController
@RequestMapping("/api/admin/roles")
public class AdminRoleController {
    private final SysRoleRepository roleRepository;
    private final SysRolePermissionRepository rolePermissionRepository;
    private final SysPermissionRepository permissionRepository;

    public AdminRoleController(SysRoleRepository roleRepository, SysRolePermissionRepository rolePermissionRepository, SysPermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * 角色创建/更新请求体。
     */
    public record RoleUpsertRequest(@NotBlank String code, @NotBlank String name, String description, Boolean enabled) {}

    /**
     * 角色权限分配请求体。
     */
    public record RolePermissionRequest(List<Long> permissionIds) {}

    /**
     * 获取角色列表（支持分页与全量查询）。
     *
     * @param onlyEnabled 是否仅查询启用的角色（用于下拉选择，此时不分页）
     * @param page        页码（从0开始）
     * @param size        每页条数
     * @return 角色列表
     */
    @GetMapping
    public ApiResponse<Object> list(
            @RequestParam(value = "onlyEnabled", required = false) Boolean onlyEnabled,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) int size
    ) {
        if (onlyEnabled != null && onlyEnabled) {
            var items = roleRepository.findAllByEnabledTrueOrderByNameAsc();
            return ApiResponse.ok(items.stream().map(this::toView).toList());
        }
        var pr = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        var data = roleRepository.findAll(pr);
        return ApiResponse.ok(Map.of(
                "items", data.getContent().stream().map(this::toView).toList(),
                "total", data.getTotalElements(),
                "page", page,
                "size", size
        ));
    }

    /**
     * 创建新角色。
     *
     * @param req 角色信息
     * @return 创建后的角色信息
     */
    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody RoleUpsertRequest req) {
        if (roleRepository.existsByCode(req.code())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "ROLE_EXISTS", "角色编码已存在");
        }
        var role = new SysRole();
        role.setCode(req.code());
        role.setName(req.name());
        role.setDescription(req.description());
        role.setEnabled(req.enabled == null || req.enabled);
        var saved = roleRepository.save(role);
        return ApiResponse.ok(toView(saved));
    }

    /**
     * 更新角色信息。
     *
     * @param id  角色ID
     * @param req 角色信息
     * @return 更新后的角色信息
     */
    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody RoleUpsertRequest req) {
        var role = roleRepository.findById(id).orElseThrow(() ->
                new BizException(HttpStatus.NOT_FOUND, "ROLE_NOT_FOUND", "角色不存在"));
        if (!role.getCode().equals(req.code()) && roleRepository.existsByCode(req.code())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "ROLE_EXISTS", "角色编码已存在");
        }
        role.setCode(req.code());
        role.setName(req.name());
        role.setDescription(req.description());
        role.setEnabled(req.enabled == null ? role.isEnabled() : req.enabled);
        var saved = roleRepository.save(role);
        return ApiResponse.ok(toView(saved));
    }

    /**
     * 获取角色的权限列表。
     *
     * @param id 角色ID
     * @return 权限ID列表
     */
    @GetMapping("/{id}/permissions")
    public ApiResponse<Map<String, Object>> listPermissions(@PathVariable Long id) {
        if (!roleRepository.existsById(id)) {
            throw new BizException(HttpStatus.NOT_FOUND, "ROLE_NOT_FOUND", "角色不存在");
        }
        var permissionIds = rolePermissionRepository.findAllByRoleId(id).stream()
                .map(SysRolePermission::getPermissionId)
                .toList();
        return ApiResponse.ok(Map.of(
                "roleId", id,
                "permissionIds", permissionIds
        ));
    }

    /**
     * 更新角色的权限。
     *
     * @param id  角色ID
     * @param req 权限ID列表
     */
    @PutMapping("/{id}/permissions")
    public ApiResponse<Map<String, Object>> updatePermissions(@PathVariable Long id, @RequestBody RolePermissionRequest req) {
        if (!roleRepository.existsById(id)) {
            throw new BizException(HttpStatus.NOT_FOUND, "ROLE_NOT_FOUND", "角色不存在");
        }
        var permissionIds = req == null || req.permissionIds() == null
                ? List.<Long>of()
                : req.permissionIds().stream().filter(Objects::nonNull).distinct().toList();
        if (!permissionIds.isEmpty()) {
            var permissions = permissionRepository.findAllById(permissionIds);
            if (permissions.size() != permissionIds.size()) {
                throw new BizException(HttpStatus.BAD_REQUEST, "PERMISSION_NOT_FOUND", "权限不存在");
            }
        }
        rolePermissionRepository.deleteAllByRoleId(id);
        if (!permissionIds.isEmpty()) {
            var items = permissionIds.stream().map(permissionId -> {
                var rp = new SysRolePermission();
                rp.setRoleId(id);
                rp.setPermissionId(permissionId);
                return rp;
            }).toList();
            rolePermissionRepository.saveAll(items);
        }
        return ApiResponse.ok(Map.of(
                "roleId", id,
                "permissionIds", permissionIds
        ));
    }

    /**
     * 删除角色。
     *
     * @param id 角色ID
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    private Map<String, Object> toView(SysRole role) {
        return Map.of(
                "id", role.getId(),
                "code", role.getCode(),
                "name", role.getName(),
                "description", role.getDescription(),
                "enabled", role.isEnabled(),
                "createdAt", role.getCreatedAt(),
                "updatedAt", role.getUpdatedAt()
        );
    }
}
