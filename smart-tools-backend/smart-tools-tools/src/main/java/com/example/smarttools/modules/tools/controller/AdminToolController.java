/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/controller/AdminToolController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class AdminToolController
 * 注解：RestController、RequestMapping、NotBlank、GetMapping、PostMapping、RequestParam、PutMapping、DeleteMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.tool.SysTool、com.example.smarttools.domain.tool.SysToolRepository、jakarta.validation.Valid、jakarta.validation.constraints.NotBlank、org.springframework.http.HttpStatus、org.springframework.web.bind.annotation.DeleteMapping、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.PathVariable、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.PutMapping、org.springframework.web.bind.annotation.RequestBody、org.springframework.web.bind.annotation.RequestMapping
 * 公开方法：list()；update(@PathVariable Long id, @Valid @RequestBody ToolUpsertRequest req)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.tool.SysTool;
import com.example.smarttools.domain.tool.SysToolRepository;
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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tools")
public class AdminToolController {
    private final SysToolRepository toolRepository;

    public AdminToolController(SysToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    public record ToolUpsertRequest(
            @NotBlank String slug,
            @NotBlank String name,
            @NotBlank String category,
            String description,
            String keywords,
            Integer sortOrder,
            Boolean enabled
    ) {}

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        var items = toolRepository.findAll().stream().map(this::toView).toList();
        return ApiResponse.ok(items);
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody ToolUpsertRequest req,
                                                   @RequestParam(value = "generateFrontend", required = false, defaultValue = "true") boolean generateFrontend) {
        if (toolRepository.existsBySlug(req.slug())) {
            throw new BizException(HttpStatus.CONFLICT, "TOOL_SLUG_EXISTS", "工具标识已存在");
        }
        var t = new SysTool();
        t.setSlug(req.slug());
        t.setName(req.name());
        t.setCategory(req.category());
        t.setDescription(req.description());
        t.setKeywords(req.keywords());
        t.setEnabled(req.enabled == null || req.enabled);
        t.setSortOrder(req.sortOrder == null ? 0 : req.sortOrder);
        var saved = toolRepository.save(t);
        if (generateFrontend) {
            try {
                generateFrontendScaffold(saved.getSlug(), saved.getName(), saved.getDescription());
            } catch (Exception ignore) {
            }
        }
        return ApiResponse.ok(toView(saved));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody ToolUpsertRequest req) {
        var t = toolRepository.findById(id).orElseThrow(() ->
                new BizException(HttpStatus.NOT_FOUND, "TOOL_NOT_FOUND", "工具不存在"));
        if (!t.getSlug().equals(req.slug()) && toolRepository.existsBySlug(req.slug())) {
            throw new BizException(HttpStatus.CONFLICT, "TOOL_SLUG_EXISTS", "工具标识已存在");
        }
        t.setSlug(req.slug());
        t.setName(req.name());
        t.setCategory(req.category());
        t.setDescription(req.description());
        t.setKeywords(req.keywords());
        t.setEnabled(req.enabled == null ? t.isEnabled() : req.enabled);
        t.setSortOrder(req.sortOrder == null ? t.getSortOrder() : req.sortOrder);
        var saved = toolRepository.save(t);
        return ApiResponse.ok(toView(saved));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id,
                                       @RequestParam(value = "deleteFrontend", required = false, defaultValue = "true") boolean deleteFrontend) {
        var t = toolRepository.findById(id).orElseThrow(() ->
                new BizException(HttpStatus.NOT_FOUND, "TOOL_NOT_FOUND", "工具不存在"));
        toolRepository.deleteById(id);
        if (deleteFrontend) {
            try {
                deleteFrontendScaffold(t.getSlug());
            } catch (Exception ignore) {
            }
        }
        return ApiResponse.ok(true);
    }

    private Map<String, Object> toView(SysTool t) {
        return Map.ofEntries(
                Map.entry("id", t.getId()),
                Map.entry("slug", t.getSlug()),
                Map.entry("name", t.getName()),
                Map.entry("category", t.getCategory()),
                Map.entry("description", t.getDescription()),
                Map.entry("keywords", t.getKeywords()),
                Map.entry("enabled", t.isEnabled()),
                Map.entry("sortOrder", t.getSortOrder()),
                Map.entry("createdAt", t.getCreatedAt()),
                Map.entry("updatedAt", t.getUpdatedAt())
        );
    }

    private Path frontendToolsRoot() {
        var env = System.getenv("FRONTEND_ROOT");
        if (env != null && !env.isBlank()) {
            return Path.of(env).resolve("src").resolve("tools");
        }
        var p1 = Path.of("..").resolve("smart-tools-frontend").resolve("src").resolve("tools").normalize();
        if (Files.exists(p1)) return p1;
        var p2 = Path.of("..").resolve("test_vue").resolve("src").resolve("tools").normalize();
        if (Files.exists(p2)) return p2;
        return Path.of("e:/grxm/xxm/smart-tools-frontend/src/tools");
    }

    private void generateFrontendScaffold(String slug, String name, String description) throws Exception {
        var root = frontendToolsRoot();
        var dir = root.resolve(slug);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        var metaJs = """
export default {
  slug: "%s",
  name: "%s",
  category: "工具",
  description: "%s",
  keywords: []
}
""".formatted(slug, name, description == null ? "" : description);
        var indexVue = """
<template>
  <div class="max-w-5xl mx-auto space-y-6">
    <div class="rounded-3xl bg-white shadow-xl border border-slate-100">
      <div class="p-6">
        <div class="text-xl font-bold text-slate-900">%s</div>
        <div class="mt-2 text-sm text-slate-500">%s</div>
      </div>
    </div>
    <div class="rounded-3xl bg-white shadow-sm border border-slate-100 p-6">
      <el-input v-model="text" type="textarea" :rows="10" placeholder="输入内容" />
      <div class="mt-4">
        <el-button type="primary" @click="run">运行</el-button>
      </div>
      <div class="mt-4 text-sm text-slate-500">{{ result }}</div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
const text = ref('')
const result = ref('')
function run() {
  result.value = text.value
}
</script>
""".formatted(name, description == null ? "" : description);
        Files.writeString(dir.resolve("meta.js"), metaJs, StandardCharsets.UTF_8);
        Files.writeString(dir.resolve("index.vue"), indexVue, StandardCharsets.UTF_8);
    }

    private void deleteFrontendScaffold(String slug) throws Exception {
        var dir = frontendToolsRoot().resolve(slug);
        if (Files.exists(dir)) {
            try (var s = Files.walk(dir)) {
                s.sorted((a, b) -> b.compareTo(a)).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (Exception ignore) {
                    }
                });
            }
        }
    }
}
