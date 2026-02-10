/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/domain/dto/WechatRegisterRequest.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：domain
 * 类型：record WechatRegisterRequest
 * 注解：NotBlank、Size
 * 依赖：jakarta.validation.constraints.NotBlank、jakarta.validation.constraints.Size
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 微信注册请求结构（扫码后补全信息）。
 */
package com.example.smarttools.modules.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WechatRegisterRequest(
        // 登录状态标识
        @NotBlank @Size(min = 8, max = 64) String state,
        // 用户名
        @Size(min = 3, max = 32) String username,
        // 可选邮箱
        @Size(max = 128) String email,
        // 邮箱验证码（绑定邮箱时必填）
        @Size(max = 8) String emailCode,
        // 可选手机号
        @Size(max = 32) String phone,
        // 登录密码
        @NotBlank @Size(min = 6, max = 64) String password,
        // 图形验证码 ID
        @NotBlank @Size(min = 8, max = 64) String captchaId,
        // 图形验证码内容
        @NotBlank @Size(min = 4, max = 8) String captchaCode
) {
}
