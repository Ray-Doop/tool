/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/user/SysUser.java
 * 用途：后端领域实体（JPA Entity，与数据库表映射）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：class SysUser
 * 注解：Entity、Table、Id、GeneratedValue、Column、PrePersist、PreUpdate
 * 依赖：jakarta.persistence.Column、jakarta.persistence.Entity、jakarta.persistence.GeneratedValue、jakarta.persistence.GenerationType、jakarta.persistence.Id、jakarta.persistence.PrePersist、jakarta.persistence.PreUpdate、jakarta.persistence.Table、java.time.Instant
 * 公开方法：prePersist()；preUpdate()；getId()；getUsername()；setUsername(String username)；getDisplayName()；setDisplayName(String displayName)；getAvatarUrl()；setAvatarUrl(String avatarUrl)；getBio()；setBio(String bio)；getPasswordHash()；setPasswordHash(String passwordHash)；getEmail()；setEmail(String email)；getPhone()
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * 用户表实体。
 *
 * 说明：
 * - passwordHash 存储 BCrypt 结果（不存明文）
 * - createdAt/updatedAt 由 @PrePersist/@PreUpdate 自动维护
 */
@Entity
@Table(name = "sys_user")
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String username;

    @Column(name = "display_name", length = 64)
    private String displayName;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Column(length = 512)
    private String bio;

    @Column(length = 128, unique = true)
    private String email;

    @Column(length = 32, unique = true)
    private String phone;

    @Column(name = "wechat_openid", length = 64, unique = true)
    private String wechatOpenid;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "registration_completed", nullable = false)
    private boolean registrationCompleted = true;

    @Column(name = "preferences_json", length = 4096)
    private String preferencesJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    /**
     * 新增时自动写入创建/更新时间。
     */
    public void prePersist() {
        var now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    /**
     * 更新时自动刷新更新时间。
     */
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWechatOpenid() {
        return wechatOpenid;
    }

    public void setWechatOpenid(String wechatOpenid) {
        this.wechatOpenid = wechatOpenid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRegistrationCompleted() {
        return registrationCompleted;
    }

    public void setRegistrationCompleted(boolean registrationCompleted) {
        this.registrationCompleted = registrationCompleted;
    }

    public String getPreferencesJson() {
        return preferencesJson;
    }

    public void setPreferencesJson(String preferencesJson) {
        this.preferencesJson = preferencesJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
