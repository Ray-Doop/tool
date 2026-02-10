/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/domain/dto/LoginRequest.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：domain
 * 类型：record LoginRequest
 * 注解：NotBlank
 * 依赖：jakarta.validation.constraints.NotBlank
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 账号密码登录请求结构。
 */
package com.example.smarttools.modules.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        // 用户名
        @NotBlank String username,
        // 密码
        @NotBlank String password
) {
}
