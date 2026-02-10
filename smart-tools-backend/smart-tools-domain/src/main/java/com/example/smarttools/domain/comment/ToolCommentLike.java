/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/comment/ToolCommentLike.java
 * 用途：后端领域实体（JPA Entity，与数据库表映射）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：class ToolCommentLike
 * 注解：Entity、Table、Id、GeneratedValue、ManyToOne、JoinColumn、Column、PrePersist
 * 依赖：com.example.smarttools.domain.user.SysUser、jakarta.persistence.Column、jakarta.persistence.Entity、jakarta.persistence.FetchType、jakarta.persistence.GeneratedValue、jakarta.persistence.GenerationType、jakarta.persistence.Id、jakarta.persistence.JoinColumn、jakarta.persistence.ManyToOne、jakarta.persistence.PrePersist、jakarta.persistence.Table、jakarta.persistence.UniqueConstraint、java.time.Instant
 * 公开方法：prePersist()；getId()；getUser()；setUser(SysUser user)；getComment()；setComment(ToolComment comment)；getCreatedAt()
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.domain.comment;

import com.example.smarttools.domain.user.SysUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "tool_comment_like",
        uniqueConstraints = @UniqueConstraint(name = "uk_tool_comment_like", columnNames = {"user_id", "comment_id"})
)
public class ToolCommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private SysUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private ToolComment comment;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public ToolComment getComment() {
        return comment;
    }

    public void setComment(ToolComment comment) {
        this.comment = comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
