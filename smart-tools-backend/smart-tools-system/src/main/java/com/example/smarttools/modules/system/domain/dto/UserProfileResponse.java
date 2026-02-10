/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/domain/dto/UserProfileResponse.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-system
 * 分层：domain
 * 类型：record UserProfileResponse
 * 依赖：java.time.Instant
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 用户个人资料返回结构。
 */
package com.example.smarttools.modules.system.domain.dto;

import java.time.Instant;

public record UserProfileResponse(
        // 用户 ID
        Long id,
        // 用户名
        String username,
        // 显示名称
        String displayName,
        // 头像 URL
        String avatarUrl,
        // 个人简介
        String bio,
        // 邮箱
        String email,
        // 手机号
        String phone,
        // 是否绑定微信
        boolean wechatBound,
        // 创建时间
        Instant createdAt,
        // 更新时间
        Instant updatedAt
) {
}
