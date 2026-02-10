/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/domain/dto/RegisterPhoneRequest.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：domain
 * 类型：record RegisterPhoneRequest
 * 注解：NotBlank、Size
 * 依赖：jakarta.validation.constraints.NotBlank、jakarta.validation.constraints.Pattern、jakarta.validation.constraints.Size
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 手机注册请求结构。
 */
package com.example.smarttools.modules.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterPhoneRequest(
        // 手机号
        @NotBlank @Pattern(regexp = "^[0-9]{6,20}$") String phone,
        // 密码
        @NotBlank @Size(min = 6, max = 64) String password,
        // 确认密码
        @NotBlank @Size(min = 6, max = 64) String confirmPassword,
        // 可选用户名
        @Size(min = 3, max = 32) String username
) {
}
