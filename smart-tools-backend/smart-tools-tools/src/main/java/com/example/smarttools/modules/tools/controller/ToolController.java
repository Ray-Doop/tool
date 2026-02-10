/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/controller/ToolController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class ToolController
 * 注解：RestController、RequestMapping、GetMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.modules.tools.domain.dto.ToolResponse、com.example.smarttools.modules.tools.domain.dto.TrendingToolDto、com.example.smarttools.modules.tools.service.IToolService、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.PathVariable、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RestController、java.util.List
 * 公开方法：listTools()；getTool(@PathVariable String slug)；trending()；newTools()
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.modules.tools.domain.dto.ToolResponse;
import com.example.smarttools.modules.tools.domain.dto.TrendingToolDto;
import com.example.smarttools.modules.tools.service.IToolService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 工具目录接口。
 *
 * 说明：
 * 
 *   本 Controller 只提供“工具元信息”（slug/name/category 等），用于前端工具列表展示与排序。
 *   具体工具能力（例如 Base64、UUID）由各自的 feature controller 提供独立接口。
 * 
 *
 * 返回结构统一为 {@link ApiResponse}，便于前端统一处理 success/data/error。
 */
@RestController
@RequestMapping("/api/tools")
public class ToolController {
    private final IToolService toolService;

    public ToolController(IToolService toolService) {
        this.toolService = toolService;
    }

    @GetMapping
    public ApiResponse<List<ToolResponse>> listTools() {
        var items = toolService.listEnabledTools().stream()
                .map(t -> new ToolResponse(
                        t.getSlug(),
                        t.getName(),
                        t.getCategory(),
                        t.getDescription(),
                        t.getKeywords(),
                        t.isEnabled(),
                        t.getSortOrder()
                ))
                .toList();
        return ApiResponse.ok(items);
    }

    /**
     * 根据 slug 获取单个工具的元信息。
     *
     * 典型用途：前端进入工具详情页时，先获取展示信息（名称、描述、关键词）。
     *
     * @param slug 工具唯一标识（与前端工具目录名保持一致，如 "base64"）
     */
    @GetMapping("/{slug}")
    public ApiResponse<ToolResponse> getTool(@PathVariable String slug) {
        var t = toolService.getBySlug(slug);
        return ApiResponse.ok(new ToolResponse(
                t.getSlug(),
                t.getName(),
                t.getCategory(),
                t.getDescription(),
                t.getKeywords(),
                t.isEnabled(),
                t.getSortOrder()
        ));
    }

    @GetMapping("/trending")
    public ApiResponse<List<TrendingToolDto>> trending() {
        return ApiResponse.ok(toolService.getTrendingTools());
    }

    @GetMapping("/new")
    public ApiResponse<List<ToolResponse>> newTools() {
        var tools = toolService.getNewestTools().stream()
                .map(t -> new ToolResponse(
                        t.getSlug(),
                        t.getName(),
                        t.getCategory(),
                        t.getDescription(),
                        t.getKeywords(),
                        t.isEnabled(),
                        t.getSortOrder()
                ))
                .toList();
        return ApiResponse.ok(tools);
    }
}
