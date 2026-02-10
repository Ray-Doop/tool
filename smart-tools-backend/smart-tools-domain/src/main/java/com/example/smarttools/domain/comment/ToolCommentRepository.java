/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/comment/ToolCommentRepository.java
 * 用途：后端数据访问层（Repository，基于 Spring Data JPA）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：interface ToolCommentRepository
 * 注解：EntityGraph
 * 依赖：com.example.smarttools.domain.user.SysUser、org.springframework.data.jpa.repository.EntityGraph、org.springframework.data.jpa.repository.JpaRepository、java.util.List、java.util.Optional
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.domain.comment;

import com.example.smarttools.domain.user.SysUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ToolCommentRepository extends JpaRepository<ToolComment, Long> {
    @EntityGraph(attributePaths = "user")
    List<ToolComment> findAllByToolSlugOrderByCreatedAtAsc(String toolSlug);

    @EntityGraph(attributePaths = "user")
    List<ToolComment> findAllByUserOrderByCreatedAtDesc(SysUser user);

    List<ToolComment> findAllByParentId(Long parentId);

    boolean existsByParentId(Long parentId);

    long countByToolSlug(String toolSlug);

    Optional<ToolComment> findByIdAndUser(Long id, SysUser user);
}
