/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/service/IToolEngagementService.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-tools
 * 分层：service
 * 类型：interface IToolEngagementService
 * 依赖：com.example.smarttools.modules.auth.security.UserPrincipal、com.example.smarttools.modules.tools.domain.dto.ToolCommentCreateRequest、com.example.smarttools.modules.tools.domain.dto.ToolCommentResponse、com.example.smarttools.modules.tools.domain.dto.ToolStatsResponse、com.example.smarttools.modules.tools.domain.dto.ToolCommentUpdateRequest、java.util.List
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.service;

import com.example.smarttools.modules.auth.security.UserPrincipal;
import com.example.smarttools.modules.tools.domain.dto.ToolCommentCreateRequest;
import com.example.smarttools.modules.tools.domain.dto.ToolCommentResponse;
import com.example.smarttools.modules.tools.domain.dto.ToolStatsResponse;
import com.example.smarttools.modules.tools.domain.dto.ToolCommentUpdateRequest;

import java.util.List;

public interface IToolEngagementService {
    ToolStatsResponse getStats(UserPrincipal principal, String toolSlug);

    void addLike(UserPrincipal principal, String toolSlug);

    void removeLike(UserPrincipal principal, String toolSlug);

    List<ToolCommentResponse> listComments(UserPrincipal principal, String toolSlug);

    ToolCommentResponse addComment(UserPrincipal principal, String toolSlug, ToolCommentCreateRequest req);

    void updateComment(UserPrincipal principal, String toolSlug, Long commentId, ToolCommentUpdateRequest req);

    void deleteComment(UserPrincipal principal, String toolSlug, Long commentId);

    void likeComment(UserPrincipal principal, Long commentId);

    void unlikeComment(UserPrincipal principal, Long commentId);
}
