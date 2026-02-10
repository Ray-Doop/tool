/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/admin/AdminUserController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-system
 * 分层：controller
 * 注解：RestController、RequestMapping、GetMapping、RequestParam、PutMapping、PathVariable
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.admin.SysAdmin、com.example.smarttools.domain.admin.SysAdminRepository、com.example.smarttools.domain.admin.SysPermission、com.example.smarttools.domain.admin.SysPermissionRepository、com.example.smarttools.domain.admin.SysRole、com.example.smarttools.domain.admin.SysRolePermission、com.example.smarttools.domain.admin.SysRolePermissionRepository、com.example.smarttools.domain.admin.SysRoleRepository、com.example.smarttools.domain.admin.SysUserRole、com.example.smarttools.domain.admin.SysUserRoleRepository、com.example.smarttools.domain.user.SysUser、com.example.smarttools.domain.user.SysUserRepository
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.system.controller.admin;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.admin.SysAdmin;
import com.example.smarttools.domain.admin.SysAdminRepository;
import com.example.smarttools.domain.admin.SysPermission;
import com.example.smarttools.domain.admin.SysPermissionRepository;
import com.example.smarttools.domain.admin.SysRole;
import com.example.smarttools.domain.admin.SysRolePermission;
import com.example.smarttools.domain.admin.SysRolePermissionRepository;
import com.example.smarttools.domain.admin.SysRoleRepository;
import com.example.smarttools.domain.admin.SysUserRole;
import com.example.smarttools.domain.admin.SysUserRoleRepository;
import com.example.smarttools.domain.user.SysUser;
import com.example.smarttools.domain.user.SysUserRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 后台用户管理接口。
 *
 * 提供用户列表查询、状态变更、密码重置、角色分配等功能。
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SysUserRoleRepository userRoleRepository;
    private final SysRoleRepository roleRepository;
    private final SysRolePermissionRepository rolePermissionRepository;
    private final SysPermissionRepository permissionRepository;
    private final SysAdminRepository adminRepository;

    public AdminUserController(SysUserRepository userRepository, PasswordEncoder passwordEncoder, SysUserRoleRepository userRoleRepository, SysRoleRepository roleRepository, SysRolePermissionRepository rolePermissionRepository, SysPermissionRepository permissionRepository, SysAdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * 重置密码请求体。
     */
    public record ResetPasswordRequest(@NotBlank String newPassword) {}

    /**
     * 用户角色分配请求体。
     */
    public record UserRoleRequest(List<Long> roleIds) {}

    /**
     * 获取用户列表（分页）。
     *
     * 包含用户的角色、权限、管理员身份等聚合信息。
     *
     * @param page    页码（从0开始）
     * @param size    每页条数
     * @param keyword 搜索关键词（暂未实现后端搜索逻辑，预留参数）
     * @param roleId  角色ID筛选
     * @return 分页后的用户列表及详细信息
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "roleId", required = false) Long roleId
    ) {
        Specification<SysUser> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<Predicate>();
            if (StringUtils.hasText(keyword)) {
                var like = "%" + keyword.trim() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("username"), like),
                        cb.like(root.get("email"), like),
                        cb.like(root.get("phone"), like)
                ));
            }
            if (roleId != null) {
                // 子查询或 Join 筛选
                // 由于 SysUser 没有直接关联 SysRole，这里可以通过子查询
                // 或者简单点：先查出 userIds，但分页会麻烦。
                // 推荐：使用 JPA Criteria 子查询
                var subquery = query.subquery(Long.class);
                var subRoot = subquery.from(SysUserRole.class);
                subquery.select(subRoot.get("userId"))
                        .where(cb.equal(subRoot.get("roleId"), roleId));
                predicates.add(root.get("id").in(subquery));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var pr = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        var data = userRepository.findAll(spec, pr);
        var items = data.getContent();
        var userIds = items.stream().map(SysUser::getId).toList();
        var userRoles = userIds.isEmpty() ? List.<SysUserRole>of() : userRoleRepository.findAllByUserIdIn(userIds);
        var userRoleMap = userRoles.stream()
                .collect(Collectors.groupingBy(SysUserRole::getUserId, Collectors.mapping(SysUserRole::getRoleId, Collectors.toList())));
        var roleIds = userRoles.stream().map(SysUserRole::getRoleId).distinct().toList();
        var roles = roleIds.isEmpty() ? List.<SysRole>of() : roleRepository.findAllById(roleIds);
        var roleMap = roles.stream().collect(Collectors.toMap(SysRole::getId, r -> r));
        var rolePermissions = roleIds.isEmpty() ? List.<SysRolePermission>of() : rolePermissionRepository.findAllByRoleIdIn(roleIds);
        var rolePermissionMap = rolePermissions.stream()
                .collect(Collectors.groupingBy(SysRolePermission::getRoleId, Collectors.mapping(SysRolePermission::getPermissionId, Collectors.toList())));
        var permissionIds = rolePermissions.stream().map(SysRolePermission::getPermissionId).distinct().toList();
        var permissions = permissionIds.isEmpty() ? List.<SysPermission>of() : permissionRepository.findAllById(permissionIds);
        var permissionMap = permissions.stream().collect(Collectors.toMap(SysPermission::getId, p -> p));
        var admins = userIds.isEmpty() ? List.<SysAdmin>of() : adminRepository.findAllByUserIdIn(userIds);
        var adminMap = admins.stream()
                .filter(SysAdmin::isEnabled)
                .collect(Collectors.toMap(SysAdmin::getUserId, a -> a, (a, b) -> a));
        return ApiResponse.ok(Map.of(
                "items", items.stream().map(u -> {
                    var roleIdList = userRoleMap.getOrDefault(u.getId(), List.of());
                    var roleViews = roleIdList.stream()
                            .map(roleMap::get)
                            .filter(Objects::nonNull)
                            .map(r -> Map.of(
                                    "id", r.getId(),
                                    "code", r.getCode(),
                                    "name", r.getName()
                            ))
                            .toList();
                    var permissionViewMap = new LinkedHashMap<Long, SysPermission>();
                    for (var rId : roleIdList) {
                        var permIds = rolePermissionMap.getOrDefault(rId, List.of());
                        for (var permId : permIds) {
                            var perm = permissionMap.get(permId);
                            if (perm != null) {
                                permissionViewMap.putIfAbsent(perm.getId(), perm);
                            }
                        }
                    }
                    var permissionViews = permissionViewMap.values().stream()
                            .map(p -> Map.of(
                                    "id", p.getId(),
                                    "code", p.getCode(),
                                    "name", p.getName()
                            ))
                            .toList();
                    var isAdmin = adminMap.containsKey(u.getId());
                    var map = new LinkedHashMap<String, Object>();
                    map.put("id", u.getId());
                    map.put("username", u.getUsername());
                    map.put("displayName", u.getDisplayName());
                    map.put("avatarUrl", u.getAvatarUrl());
                    map.put("email", u.getEmail());
                    map.put("phone", u.getPhone());
                    map.put("wechatBound", u.getWechatOpenid() != null && !u.getWechatOpenid().isBlank());
                    map.put("enabled", u.isEnabled());
                    map.put("roles", roleViews);
                    map.put("permissions", permissionViews);
                    map.put("isAdmin", isAdmin);
                    return map;
                }).toList(),
                "total", data.getTotalElements(),
                "page", page,
                "size", size
        ));
    }

    /**
     * 更新用户启用状态。
     *
     * @param id      用户ID
     * @param enabled 是否启用
     */
    @PutMapping("/{id}/enable")
    public ApiResponse<SysUser> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam(value = "enabled", defaultValue = "true") boolean enabled
    ) {
        var user = userRepository.findById(id).orElseThrow(() ->
                new BizException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在"));
        user.setEnabled(enabled);
        userRepository.save(user);
        return ApiResponse.ok(user);
    }

    /**
     * 重置用户密码。
     *
     * @param id  用户ID
     * @param req 新密码请求体
     */
    @PostMapping("/{id}/reset-password")
    public ApiResponse<SysUser> resetPassword(
            @PathVariable("id") Long id,
            @RequestBody ResetPasswordRequest req
    ) {
        var user = userRepository.findById(id).orElseThrow(() ->
                new BizException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在"));
        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
        return ApiResponse.ok(user);
    }

    /**
     * 删除用户。
     *
     * @param id 用户ID
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Long id) {
        if (!userRepository.existsById(id)) {
            throw new BizException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在");
        }
        userRepository.deleteById(id);
        return ApiResponse.ok(true);
    }

    /**
     * 获取指定用户的角色列表。
     *
     * @param id 用户ID
     */
    @GetMapping("/{id}/roles")
    public ApiResponse<Map<String, Object>> listRoles(@PathVariable("id") Long id) {
        if (!userRepository.existsById(id)) {
            throw new BizException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在");
        }
        var roleIds = userRoleRepository.findAllByUserId(id).stream()
                .map(SysUserRole::getRoleId)
                .toList();
        return ApiResponse.ok(Map.of(
                "userId", id,
                "roleIds", roleIds
        ));
    }

    /**
     * 更新指定用户的角色列表。
     *
     * @param id  用户ID
     * @param req 包含角色ID列表的请求体
     */
    @PutMapping("/{id}/roles")
    public ApiResponse<Map<String, Object>> updateRoles(@PathVariable("id") Long id, @RequestBody UserRoleRequest req) {
        if (!userRepository.existsById(id)) {
            throw new BizException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在");
        }
        var roleIds = req == null || req.roleIds() == null
                ? List.<Long>of()
                : req.roleIds().stream().filter(Objects::nonNull).distinct().toList();
        if (!roleIds.isEmpty()) {
            var roles = roleRepository.findAllById(roleIds);
            if (roles.size() != roleIds.size()) {
                throw new BizException(HttpStatus.BAD_REQUEST, "ROLE_NOT_FOUND", "角色不存在");
            }
        }
        userRoleRepository.deleteAllByUserId(id);
        if (!roleIds.isEmpty()) {
            var items = roleIds.stream().map(roleId -> {
                var ur = new SysUserRole();
                ur.setUserId(id);
                ur.setRoleId(roleId);
                return ur;
            }).toList();
            userRoleRepository.saveAll(items);
        }
        return ApiResponse.ok(Map.of(
                "userId", id,
                "roleIds", roleIds
        ));
    }
}
