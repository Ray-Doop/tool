/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/admin/AdminModuleController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-system
 * 分层：controller
 * 类型：class AdminModuleController
 * 注解：RestController、RequestMapping、GetMapping、PostMapping、PutMapping、DeleteMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.admin.SysModule、com.example.smarttools.domain.admin.SysModuleRepository、jakarta.validation.Valid、jakarta.validation.constraints.NotBlank、org.springframework.http.HttpStatus、org.springframework.web.bind.annotation.DeleteMapping、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.PathVariable、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.PutMapping、org.springframework.web.bind.annotation.RequestBody、org.springframework.web.bind.annotation.RequestMapping
 * 公开方法：create(@Valid @RequestBody ModuleUpsertRequest req)；update(@PathVariable Long id, @Valid @RequestBody ModuleUpsertRequest req)；delete(@PathVariable Long id)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.system.controller.admin;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.admin.SysModule;
import com.example.smarttools.domain.admin.SysModuleRepository;
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
@RequestMapping("/api/admin/modules")
public class AdminModuleController {
    private final SysModuleRepository moduleRepository;

    public AdminModuleController(SysModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public record ModuleUpsertRequest(@NotBlank String code, @NotBlank String name, String description, Integer sortOrder, Boolean enabled) {}

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(value = "onlyEnabled", required = false) Boolean onlyEnabled) {
        var items = (onlyEnabled != null && onlyEnabled)
                ? moduleRepository.findAllByEnabledTrueOrderBySortOrderAscNameAsc()
                : moduleRepository.findAll();
        return ApiResponse.ok(items.stream().map(this::toView).toList());
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody ModuleUpsertRequest req) {
        if (moduleRepository.existsByCode(req.code())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "MODULE_EXISTS", "模块编码已存在");
        }
        var m = new SysModule();
        m.setCode(req.code());
        m.setName(req.name());
        m.setDescription(req.description());
        m.setSortOrder(req.sortOrder == null ? 0 : req.sortOrder);
        m.setEnabled(req.enabled == null || req.enabled);
        var saved = moduleRepository.save(m);
        return ApiResponse.ok(toView(saved));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody ModuleUpsertRequest req) {
        var m = moduleRepository.findById(id).orElseThrow(() ->
                new BizException(HttpStatus.NOT_FOUND, "MODULE_NOT_FOUND", "模块不存在"));
        if (!m.getCode().equals(req.code()) && moduleRepository.existsByCode(req.code())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "MODULE_EXISTS", "模块编码已存在");
        }
        m.setCode(req.code());
        m.setName(req.name());
        m.setDescription(req.description());
        m.setSortOrder(req.sortOrder == null ? m.getSortOrder() : req.sortOrder);
        m.setEnabled(req.enabled == null ? m.isEnabled() : req.enabled);
        var saved = moduleRepository.save(m);
        return ApiResponse.ok(toView(saved));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        moduleRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    private Map<String, Object> toView(SysModule m) {
        return Map.of(
                "id", m.getId(),
                "code", m.getCode(),
                "name", m.getName(),
                "description", m.getDescription(),
                "sortOrder", m.getSortOrder(),
                "enabled", m.isEnabled(),
                "createdAt", m.getCreatedAt(),
                "updatedAt", m.getUpdatedAt()
        );
    }
}
