/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/log/SysVisitLogRepository.java
 * 用途：后端数据访问层（Repository，基于 Spring Data JPA）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：interface SysVisitLogRepository
 * 注解：Query
 * 依赖：org.springframework.data.domain.Pageable、org.springframework.data.jpa.repository.JpaRepository、org.springframework.data.jpa.repository.Query、org.springframework.data.repository.query.Param、java.util.List
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.domain.log;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 访问日志表仓储接口。
 */
public interface SysVisitLogRepository extends JpaRepository<SysVisitLog, Long> {
    
    @Query("SELECT v FROM SysVisitLog v WHERE v.userId = :userId AND v.toolSlug IS NOT NULL ORDER BY v.createdAt DESC")
    List<SysVisitLog> findRecentToolsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT v FROM SysVisitLog v WHERE v.userId = :userId AND (v.path LIKE '/api/auth/login%' OR v.path LIKE '/api/auth/otp/login%') ORDER BY v.createdAt DESC")
    List<SysVisitLog> findRecentLoginLogsByUserId(@Param("userId") Long userId, Pageable pageable);

    long countByUserId(Long userId);

    @Query("SELECT COUNT(DISTINCT v.toolSlug) FROM SysVisitLog v WHERE v.userId = :userId AND v.toolSlug IS NOT NULL")
    long countDistinctToolSlugByUserId(@Param("userId") Long userId);

    @Query("SELECT v.toolSlug, COUNT(v) as cnt FROM SysVisitLog v WHERE v.createdAt >= :since AND v.toolSlug IS NOT NULL GROUP BY v.toolSlug ORDER BY cnt DESC")
    List<Object[]> findTrendingTools(@Param("since") java.time.Instant since, Pageable pageable);

    List<SysVisitLog> findByUserIdAndCreatedAtAfter(Long userId, java.time.Instant createdAt);
}
