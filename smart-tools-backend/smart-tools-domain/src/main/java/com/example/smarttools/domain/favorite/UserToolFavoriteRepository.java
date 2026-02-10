/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/favorite/UserToolFavoriteRepository.java
 * 用途：后端数据访问层（Repository，基于 Spring Data JPA）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：interface UserToolFavoriteRepository
 * 依赖：com.example.smarttools.domain.user.SysUser、org.springframework.data.jpa.repository.JpaRepository、java.util.List、java.util.Optional
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.domain.favorite;

import com.example.smarttools.domain.user.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 收藏表仓储接口。
 */
public interface UserToolFavoriteRepository extends JpaRepository<UserToolFavorite, Long> {
    /**
     * 查询用户收藏列表（按时间倒序）。
     */
    List<UserToolFavorite> findAllByUserOrderByCreatedAtDesc(SysUser user);

    /**
     * 查询是否存在某条收藏。
     */
    Optional<UserToolFavorite> findByUserAndToolSlug(SysUser user, String toolSlug);

    /**
     * 判断是否已收藏。
     */
    boolean existsByUserAndToolSlug(SysUser user, String toolSlug);

    /**
     * 取消收藏，返回删除行数。
     */
    long deleteByUserAndToolSlug(SysUser user, String toolSlug);

    long countByUser(SysUser user);

    long countByToolSlug(String toolSlug);
}
