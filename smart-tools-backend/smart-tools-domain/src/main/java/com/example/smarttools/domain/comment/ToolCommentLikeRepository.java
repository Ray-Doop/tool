/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/comment/ToolCommentLikeRepository.java
 * 用途：后端数据访问层（Repository，基于 Spring Data JPA）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：interface ToolCommentLikeRepository
 * 注解：Query
 * 依赖：com.example.smarttools.domain.user.SysUser、org.springframework.data.jpa.repository.JpaRepository、org.springframework.data.jpa.repository.Query、org.springframework.data.repository.query.Param、java.util.Collection、java.util.List、java.util.Optional
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.domain.comment;

import com.example.smarttools.domain.user.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ToolCommentLikeRepository extends JpaRepository<ToolCommentLike, Long> {
    Optional<ToolCommentLike> findByUserAndComment(SysUser user, ToolComment comment);

    boolean existsByUserAndComment(SysUser user, ToolComment comment);

    long deleteByUserAndComment(SysUser user, ToolComment comment);

    long deleteByCommentIdIn(Collection<Long> commentIds);

    @Query("select l.comment.id, count(l) from ToolCommentLike l where l.comment.id in :commentIds group by l.comment.id")
    List<Object[]> countByCommentIds(@Param("commentIds") Collection<Long> commentIds);

    @Query("select l.comment.id from ToolCommentLike l where l.user = :user and l.comment.id in :commentIds")
    List<Long> findLikedCommentIds(@Param("user") SysUser user, @Param("commentIds") Collection<Long> commentIds);
}
