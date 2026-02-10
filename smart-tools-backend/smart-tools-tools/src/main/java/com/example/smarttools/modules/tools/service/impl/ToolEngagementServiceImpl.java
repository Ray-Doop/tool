/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/service/impl/ToolEngagementServiceImpl.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-tools
 * 分层：service
 * 类型：class ToolEngagementServiceImpl
 * 注解：Service、Override、Transactional
 * 依赖：com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.comment.ToolComment、com.example.smarttools.domain.comment.ToolCommentLike、com.example.smarttools.domain.comment.ToolCommentLikeRepository、com.example.smarttools.domain.comment.ToolCommentRepository、com.example.smarttools.domain.favorite.UserToolFavoriteRepository、com.example.smarttools.domain.like.UserToolLike、com.example.smarttools.domain.like.UserToolLikeRepository、com.example.smarttools.domain.user.SysUser、com.example.smarttools.domain.user.SysUserRepository、com.example.smarttools.modules.auth.security.UserPrincipal、com.example.smarttools.modules.tools.domain.dto.ToolCommentCreateRequest、com.example.smarttools.modules.tools.domain.dto.ToolCommentResponse、com.example.smarttools.modules.tools.domain.dto.ToolStatsResponse
 * 公开方法：getStats(UserPrincipal principal, String toolSlug)；addLike(UserPrincipal principal, String toolSlug)；removeLike(UserPrincipal principal, String toolSlug)；listComments(UserPrincipal principal, String toolSlug)；addComment(UserPrincipal principal, String toolSlug, ToolCommentCreateRequest req)；updateComment(UserPrincipal principal, String toolSlug, Long commentId, ToolCommentUpdateRequ…)；deleteComment(UserPrincipal principal, String toolSlug, Long commentId)；likeComment(UserPrincipal principal, Long commentId)；unlikeComment(UserPrincipal principal, Long commentId)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.service.impl;

import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.comment.ToolComment;
import com.example.smarttools.domain.comment.ToolCommentLike;
import com.example.smarttools.domain.comment.ToolCommentLikeRepository;
import com.example.smarttools.domain.comment.ToolCommentRepository;
import com.example.smarttools.domain.favorite.UserToolFavoriteRepository;
import com.example.smarttools.domain.like.UserToolLike;
import com.example.smarttools.domain.like.UserToolLikeRepository;
import com.example.smarttools.domain.user.SysUser;
import com.example.smarttools.domain.user.SysUserRepository;
import com.example.smarttools.modules.auth.security.UserPrincipal;
import com.example.smarttools.modules.tools.domain.dto.ToolCommentCreateRequest;
import com.example.smarttools.modules.tools.domain.dto.ToolCommentResponse;
import com.example.smarttools.modules.tools.domain.dto.ToolStatsResponse;
import com.example.smarttools.modules.tools.domain.dto.ToolCommentUpdateRequest;
import com.example.smarttools.modules.tools.service.IToolEngagementService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ToolEngagementServiceImpl implements IToolEngagementService {
    private static final String DEFAULT_AVATAR_URL = "/uploads/avatars/morentouxiang.png";

    private final SysUserRepository userRepository;
    private final UserToolFavoriteRepository favoriteRepository;
    private final UserToolLikeRepository likeRepository;
    private final ToolCommentRepository commentRepository;
    private final ToolCommentLikeRepository commentLikeRepository;

    public ToolEngagementServiceImpl(
            SysUserRepository userRepository,
            UserToolFavoriteRepository favoriteRepository,
            UserToolLikeRepository likeRepository,
            ToolCommentRepository commentRepository,
            ToolCommentLikeRepository commentLikeRepository
    ) {
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ToolStatsResponse getStats(UserPrincipal principal, String toolSlug) {
        var favoritesCount = favoriteRepository.countByToolSlug(toolSlug);
        var likesCount = likeRepository.countByToolSlug(toolSlug);
        var commentsCount = commentRepository.countByToolSlug(toolSlug);
        boolean liked = false;
        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null) {
                liked = likeRepository.existsByUserAndToolSlug(user, toolSlug);
            }
        }
        return new ToolStatsResponse(favoritesCount, likesCount, commentsCount, liked);
    }

    @Override
    @Transactional
    public void addLike(UserPrincipal principal, String toolSlug) {
        var user = requireUser(principal);
        if (likeRepository.existsByUserAndToolSlug(user, toolSlug)) {
            return;
        }
        var like = new UserToolLike();
        like.setUser(user);
        like.setToolSlug(toolSlug);
        likeRepository.save(like);
    }

    @Override
    @Transactional
    public void removeLike(UserPrincipal principal, String toolSlug) {
        var user = requireUser(principal);
        likeRepository.deleteByUserAndToolSlug(user, toolSlug);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToolCommentResponse> listComments(UserPrincipal principal, String toolSlug) {
        var comments = commentRepository.findAllByToolSlugOrderByCreatedAtAsc(toolSlug);
        if (comments.isEmpty()) {
            return List.of();
        }

        var commentIds = comments.stream().map(ToolComment::getId).toList();
        var likeCounts = new HashMap<Long, Long>();
        for (var row : commentLikeRepository.countByCommentIds(commentIds)) {
            var id = (Long) row[0];
            var count = ((Number) row[1]).longValue();
            likeCounts.put(id, count);
        }

        var likedIds = new HashSet<Long>();
        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null) {
                likedIds.addAll(commentLikeRepository.findLikedCommentIds(user, commentIds));
            }
        }

        var map = new HashMap<Long, ToolCommentResponse>();
        var topLevel = new ArrayList<ToolCommentResponse>();

        for (var comment : comments) {
            var user = comment.getUser();
            var replies = new ArrayList<ToolCommentResponse>();
            var response = new ToolCommentResponse(
                    comment.getId(),
                    comment.getToolSlug(),
                    comment.getParentId(),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt(),
                    likeCounts.getOrDefault(comment.getId(), 0L),
                    likedIds.contains(comment.getId()),
                    user.getId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    resolveAvatarUrl(user.getAvatarUrl()),
                    principal != null && user.getId().equals(principal.userId()),
                    replies
            );
            map.put(comment.getId(), response);
        }

        for (var comment : comments) {
            var response = map.get(comment.getId());
            if (comment.getParentId() != null && map.containsKey(comment.getParentId())) {
                map.get(comment.getParentId()).replies().add(response);
            } else {
                topLevel.add(response);
            }
        }

        for (var response : map.values()) {
            response.replies().sort((a, b) -> a.createdAt().compareTo(b.createdAt()));
        }
        topLevel.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));

        return topLevel;
    }

    @Override
    @Transactional
    public ToolCommentResponse addComment(UserPrincipal principal, String toolSlug, ToolCommentCreateRequest req) {
        var user = requireUser(principal);
        var content = req.content() == null ? "" : req.content().trim();
        if (content.isBlank()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "COMMENT_EMPTY", "评论内容不能为空");
        }
        Long parentId = req.parentId();
        if (parentId != null) {
            var parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new BizException(HttpStatus.BAD_REQUEST, "COMMENT_NOT_FOUND", "评论不存在"));
            if (!toolSlug.equals(parent.getToolSlug())) {
                throw new BizException(HttpStatus.BAD_REQUEST, "COMMENT_TOOL_MISMATCH", "评论不属于该工具");
            }
        }
        var comment = new ToolComment();
        comment.setUser(user);
        comment.setToolSlug(toolSlug);
        comment.setParentId(parentId);
        comment.setContent(content);
        var saved = commentRepository.save(comment);
        return new ToolCommentResponse(
                saved.getId(),
                saved.getToolSlug(),
                saved.getParentId(),
                saved.getContent(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                0L,
                false,
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                resolveAvatarUrl(user.getAvatarUrl()),
                true,
                new ArrayList<>()
        );
    }

    @Override
    @Transactional
    public void updateComment(UserPrincipal principal, String toolSlug, Long commentId, ToolCommentUpdateRequest req) {
        var user = requireUser(principal);
        var comment = commentRepository.findByIdAndUser(commentId, user)
                .orElseThrow(() -> new BizException(HttpStatus.BAD_REQUEST, "COMMENT_NOT_FOUND", "评论不存在"));
        if (!toolSlug.equals(comment.getToolSlug())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "COMMENT_TOOL_MISMATCH", "评论不属于该工具");
        }
        if (comment.getParentId() != null) {
            throw new BizException(HttpStatus.BAD_REQUEST, "REPLY_EDIT_DISABLED", "回复不支持编辑");
        }
        var content = req.content() == null ? "" : req.content().trim();
        if (content.isBlank()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "COMMENT_EMPTY", "评论内容不能为空");
        }
        comment.setContent(content);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(UserPrincipal principal, String toolSlug, Long commentId) {
        var user = requireUser(principal);
        var root = commentRepository.findByIdAndUser(commentId, user)
                .orElseThrow(() -> new BizException(HttpStatus.BAD_REQUEST, "COMMENT_NOT_FOUND", "评论不存在"));
        if (!toolSlug.equals(root.getToolSlug())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "COMMENT_TOOL_MISMATCH", "评论不属于该工具");
        }
        if (commentRepository.existsByParentId(root.getId())) {
            commentLikeRepository.deleteByCommentIdIn(Set.of(root.getId()));
            root.setContent("该评论已删除");
            commentRepository.save(root);
            return;
        }

        commentLikeRepository.deleteByCommentIdIn(Set.of(root.getId()));
        commentRepository.delete(root);
    }

    @Override
    @Transactional
    public void likeComment(UserPrincipal principal, Long commentId) {
        var user = requireUser(principal);
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BizException(HttpStatus.BAD_REQUEST, "COMMENT_NOT_FOUND", "评论不存在"));
        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            return;
        }
        var like = new ToolCommentLike();
        like.setUser(user);
        like.setComment(comment);
        commentLikeRepository.save(like);
    }

    @Override
    @Transactional
    public void unlikeComment(UserPrincipal principal, Long commentId) {
        var user = requireUser(principal);
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BizException(HttpStatus.BAD_REQUEST, "COMMENT_NOT_FOUND", "评论不存在"));
        commentLikeRepository.deleteByUserAndComment(user, comment);
    }

    private SysUser requireUser(UserPrincipal principal) {
        if (principal == null) {
            throw new BizException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "未登录或登录已过期");
        }
        return userRepository.findById(principal.userId())
                .orElseThrow(() -> new BizException(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND", "用户不存在"));
    }

    private String resolveAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return DEFAULT_AVATAR_URL;
        }
        return avatarUrl;
    }
}
