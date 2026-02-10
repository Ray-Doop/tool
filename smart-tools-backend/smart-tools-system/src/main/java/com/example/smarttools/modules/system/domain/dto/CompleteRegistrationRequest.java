/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/domain/dto/CompleteRegistrationRequest.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-system
 * 分层：domain
 * 类型：record CompleteRegistrationRequest
 * 注解：NotBlank
 * 依赖：jakarta.validation.constraints.NotBlank、jakarta.validation.constraints.Size
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 补全注册信息请求。
 */
package com.example.smarttools.modules.system.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompleteRegistrationRequest(
        // 用户名
        @NotBlank @Size(min = 3, max = 32) String username,
        // 密码
        @NotBlank @Size(min = 6, max = 72) String password,
        // 确认密码
        @NotBlank @Size(min = 6, max = 72) String confirmPassword
) {
}
