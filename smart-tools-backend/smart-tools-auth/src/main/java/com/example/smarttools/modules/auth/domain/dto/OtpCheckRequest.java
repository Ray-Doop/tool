/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/domain/dto/OtpCheckRequest.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：domain
 * 类型：record OtpCheckRequest
 * 注解：NotBlank
 * 依赖：jakarta.validation.constraints.NotBlank、jakarta.validation.constraints.Pattern、jakarta.validation.constraints.Size
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * OTP 校验请求结构（仅校验，不消费）。
 */
package com.example.smarttools.modules.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OtpCheckRequest(
        // OTP 渠道（当前仅支持 email）
        @NotBlank @Pattern(regexp = "^email$") String channel,
        // 发送目标（邮箱）
        @NotBlank @Size(min = 3, max = 128) String target,
        // OTP 验证码
        @NotBlank @Pattern(regexp = "^[0-9]{4,10}$") String code
) {
}
