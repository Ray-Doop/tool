/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/admin/SysServerMetric.java
 * 用途：后端领域实体（JPA Entity，与数据库表映射）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：class SysServerMetric
 * 注解：Entity、Table、Id、GeneratedValue、Column、PrePersist
 * 依赖：jakarta.persistence.Column、jakarta.persistence.Entity、jakarta.persistence.GeneratedValue、jakarta.persistence.GenerationType、jakarta.persistence.Id、jakarta.persistence.PrePersist、jakarta.persistence.Table、java.time.Instant
 * 公开方法：prePersist()；getId()；getHost()；setHost(String host)；getCpuUsage()；setCpuUsage(Double cpuUsage)；getMemUsage()；setMemUsage(Double memUsage)；getDiskUsage()；setDiskUsage(Double diskUsage)；getThreadCount()；setThreadCount(Integer threadCount)；getQps()；setQps(Double qps)；getRtP95()；setRtP95(Double rtP95)
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
@Table(name = "sys_server_metric")
public class SysServerMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128)
    private String host;

    @Column(name = "cpu_usage")
    private Double cpuUsage;

    @Column(name = "mem_usage")
    private Double memUsage;

    @Column(name = "disk_usage")
    private Double diskUsage;

    @Column(name = "thread_count")
    private Integer threadCount;

    private Double qps;

    @Column(name = "rt_p95")
    private Double rtP95;

    @Column(name = "collected_at", nullable = false)
    private Instant collectedAt;

    @PrePersist
    public void prePersist() {
        if (collectedAt == null) {
            collectedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Double getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(Double memUsage) {
        this.memUsage = memUsage;
    }

    public Double getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(Double diskUsage) {
        this.diskUsage = diskUsage;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public Double getQps() {
        return qps;
    }

    public void setQps(Double qps) {
        this.qps = qps;
    }

    public Double getRtP95() {
        return rtP95;
    }

    public void setRtP95(Double rtP95) {
        this.rtP95 = rtP95;
    }

    public Instant getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(Instant collectedAt) {
        this.collectedAt = collectedAt;
    }
}
