/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/tool/SysToolRepository.java
 * 用途：后端数据访问层（Repository，基于 Spring Data JPA）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：interface SysToolRepository
 * 依赖：org.springframework.data.jpa.repository.JpaRepository、java.util.List、java.util.Optional
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.domain.tool;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 工具表仓储接口。
 */
public interface SysToolRepository extends JpaRepository<SysTool, Long> {
    /**
     * 按 slug 查询工具。
     */
    Optional<SysTool> findBySlug(String slug);

    /**
     * 查询所有启用工具并按分类/排序/名称排序。
     */
    List<SysTool> findAllByEnabledTrueOrderByCategoryAscSortOrderAscNameAsc();

    /**
     * 判断 slug 是否存在。
     */
    boolean existsBySlug(String slug);

    List<SysTool> findTop10ByEnabledTrueOrderByCreatedAtDesc();
}
