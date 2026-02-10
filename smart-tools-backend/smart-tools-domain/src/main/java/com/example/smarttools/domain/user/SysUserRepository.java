/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-domain/src/main/java/com/example/smarttools/domain/user/SysUserRepository.java
 * 用途：后端数据访问层（Repository，基于 Spring Data JPA）
 * 归属：后端 smart-tools-domain
 * 分层：domain
 * 类型：interface SysUserRepository
 * 依赖：org.springframework.data.jpa.repository.JpaRepository、org.springframework.data.jpa.repository.JpaSpecificationExecutor、java.util.Optional
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * 用户表仓储接口。
 */
public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {
    /**
     * 按用户名查询用户。
     */
    Optional<SysUser> findByUsername(String username);

    Optional<SysUser> findByEmail(String email);

    Optional<SysUser> findByPhone(String phone);
    
    Optional<SysUser> findByWechatOpenid(String wechatOpenid);

    /**
     * 判断用户名是否已存在。
     */
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByWechatOpenid(String wechatOpenid);
}
