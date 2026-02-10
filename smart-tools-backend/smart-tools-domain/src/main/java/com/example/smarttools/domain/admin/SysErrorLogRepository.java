/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/admin/SysErrorLogRepository.java
 * 用途：后端数据访问层（Repository，基于 Spring Data JPA）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：interface SysErrorLogRepository
 * 注解：Query、Param
 * 依赖：org.springframework.data.domain.Page、org.springframework.data.domain.Pageable、org.springframework.data.jpa.repository.JpaRepository、org.springframework.data.jpa.repository.Query、org.springframework.data.repository.query.Param
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.domain.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SysErrorLogRepository extends JpaRepository<SysErrorLog, Long> {
    @Query("""
            select e from SysErrorLog e
            where (:source is null or e.source = :source)
              and (:level is null or e.level = :level)
              and (:keyword is null
                   or lower(e.message) like lower(concat('%', :keyword, '%'))
                   or lower(e.code) like lower(concat('%', :keyword, '%'))
                   or lower(e.path) like lower(concat('%', :keyword, '%'))
                   or lower(e.pageUrl) like lower(concat('%', :keyword, '%')))
            """)
    Page<SysErrorLog> search(
            @Param("source") String source,
            @Param("level") String level,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
