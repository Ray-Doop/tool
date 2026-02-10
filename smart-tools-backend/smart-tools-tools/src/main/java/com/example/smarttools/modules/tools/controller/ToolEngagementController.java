/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/controller/ToolEngagementController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class ToolEngagementController
 * 注解：RestController、RequestMapping、GetMapping、AuthenticationPrincipal、PathVariable、PostMapping、DeleteMapping、Valid、PutMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.modules.auth.security.UserPrincipal、com.example.smarttools.modules.tools.domain.dto.ToolCommentCreateRequest、com.example.smarttools.modules.tools.domain.dto.ToolCommentResponse、com.example.smarttools.modules.tools.domain.dto.ToolStatsResponse、com.example.smarttools.modules.tools.domain.dto.ToolCommentUpdateRequest、com.example.smarttools.modules.tools.service.IToolEngagementService、jakarta.validation.Valid、org.springframework.security.core.annotation.AuthenticationPrincipal、org.springframework.web.bind.annotation.DeleteMapping、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.PathVariable、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.PutMapping
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.modules.auth.security.UserPrincipal;
import com.example.smarttools.modules.tools.domain.dto.ToolCommentCreateRequest;
import com.example.smarttools.modules.tools.domain.dto.ToolCommentResponse;
import com.example.smarttools.modules.tools.domain.dto.ToolStatsResponse;
import com.example.smarttools.modules.tools.domain.dto.ToolCommentUpdateRequest;
import com.example.smarttools.modules.tools.service.IToolEngagementService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
public class ToolEngagementController {
    private final IToolEngagementService engagementService;

    public ToolEngagementController(IToolEngagementService engagementService) {
        this.engagementService = engagementService;
    }

    @GetMapping("/{toolSlug}/stats")
    public ApiResponse<ToolStatsResponse> stats(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug
    ) {
        return ApiResponse.ok(engagementService.getStats(principal, toolSlug));
    }

    @PostMapping("/{toolSlug}/like")
    public ApiResponse<Void> like(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug
    ) {
        engagementService.addLike(principal, toolSlug);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{toolSlug}/like")
    public ApiResponse<Void> unlike(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug
    ) {
        engagementService.removeLike(principal, toolSlug);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{toolSlug}/comments")
    public ApiResponse<List<ToolCommentResponse>> comments(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug
    ) {
        return ApiResponse.ok(engagementService.listComments(principal, toolSlug));
    }

    @PostMapping("/{toolSlug}/comments")
    public ApiResponse<ToolCommentResponse> addComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug,
            @Valid @RequestBody ToolCommentCreateRequest req
    ) {
        return ApiResponse.ok(engagementService.addComment(principal, toolSlug, req));
    }

    @PutMapping("/{toolSlug}/comments/{commentId}")
    public ApiResponse<Void> updateComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody ToolCommentUpdateRequest req
    ) {
        engagementService.updateComment(principal, toolSlug, commentId, req);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{toolSlug}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug,
            @PathVariable("commentId") Long commentId
    ) {
        engagementService.deleteComment(principal, toolSlug, commentId);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{toolSlug}/comments/{commentId}/like")
    public ApiResponse<Void> likeComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug,
            @PathVariable("commentId") Long commentId
    ) {
        engagementService.likeComment(principal, commentId);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{toolSlug}/comments/{commentId}/like")
    public ApiResponse<Void> unlikeComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug,
            @PathVariable("commentId") Long commentId
    ) {
        engagementService.unlikeComment(principal, commentId);
        return ApiResponse.ok(null);
    }
}
