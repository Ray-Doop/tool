/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/comment/ToolComment.java
 * 用途：后端领域实体（JPA Entity，与数据库表映射）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：class ToolComment
 * 注解：Entity、Table、Id、GeneratedValue、ManyToOne、JoinColumn、Column、PrePersist、PreUpdate
 * 依赖：com.example.smarttools.domain.user.SysUser、jakarta.persistence.Column、jakarta.persistence.Entity、jakarta.persistence.FetchType、jakarta.persistence.GeneratedValue、jakarta.persistence.GenerationType、jakarta.persistence.Id、jakarta.persistence.JoinColumn、jakarta.persistence.ManyToOne、jakarta.persistence.PrePersist、jakarta.persistence.PreUpdate、jakarta.persistence.Table、java.time.Instant
 * 公开方法：prePersist()；preUpdate()；getId()；getUser()；setUser(SysUser user)；getToolSlug()；setToolSlug(String toolSlug)；getParentId()；setParentId(Long parentId)；getContent()；setContent(String content)；getCreatedAt()；getUpdatedAt()
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "tool_comment")
public class ToolComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private SysUser user;

    @Column(name = "tool_slug", nullable = false, length = 64)
    private String toolSlug;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        var now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
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

    public String getToolSlug() {
        return toolSlug;
    }

    public void setToolSlug(String toolSlug) {
        this.toolSlug = toolSlug;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
