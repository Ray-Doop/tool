/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/admin/SysOperationLog.java
 * 用途：后端领域实体（JPA Entity，与数据库表映射）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：class SysOperationLog
 * 注解：Entity、Table、Id、GeneratedValue、Column、PrePersist
 * 依赖：jakarta.persistence.Column、jakarta.persistence.Entity、jakarta.persistence.GeneratedValue、jakarta.persistence.GenerationType、jakarta.persistence.Id、jakarta.persistence.PrePersist、jakarta.persistence.Table、java.time.Instant
 * 公开方法：prePersist()；getId()；getUserId()；setUserId(Long userId)；getModule()；setModule(String module)；getAction()；setAction(String action)；getTarget()；setTarget(String target)；getResult()；setResult(String result)；getIp()；setIp(String ip)；getUserAgent()；setUserAgent(String userAgent)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.domain.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "sys_operation_log")
public class SysOperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(length = 128)
    private String module;

    @Column(length = 128)
    private String action;

    @Column(length = 256)
    private String target;

    @Column(length = 64)
    private String result;

    @Column(length = 64)
    private String ip;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
