/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/admin/SysErrorLog.java
 * 用途：后端领域实体（JPA Entity，与数据库表映射）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：class SysErrorLog
 * 注解：Entity、Table、Id、GeneratedValue、Column、PrePersist
 * 依赖：jakarta.persistence.Column、jakarta.persistence.Entity、jakarta.persistence.GeneratedValue、jakarta.persistence.GenerationType、jakarta.persistence.Id、jakarta.persistence.PrePersist、jakarta.persistence.Table、java.time.Instant
 * 公开方法：prePersist()；getId()；getSource()；setSource(String source)；getLevel()；setLevel(String level)；getCode()；setCode(String code)；getMessage()；setMessage(String message)；getDetail()；setDetail(String detail)；getPath()；setPath(String path)；getMethod()；setMethod(String method)
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
@Table(name = "sys_error_log")
public class SysErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String source;

    @Column(nullable = false, length = 16)
    private String level;

    @Column(length = 64)
    private String code;

    @Column(nullable = false, length = 512)
    private String message;

    @Column(length = 2000)
    private String detail;

    @Column(length = 256)
    private String path;

    @Column(length = 16)
    private String method;

    @Column(length = 256)
    private String pageUrl;

    @Column(length = 2000)
    private String stack;

    @Column(length = 64)
    private String ip;

    @Column(length = 256)
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
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
