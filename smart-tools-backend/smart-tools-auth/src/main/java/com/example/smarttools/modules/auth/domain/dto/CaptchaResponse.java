/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/domain/dto/CaptchaResponse.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：domain
 * 类型：record CaptchaResponse
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 图形验证码返回结构。
 */
package com.example.smarttools.modules.auth.domain.dto;

public record CaptchaResponse(
        // 验证码 ID
        String captchaId,
        // 图片 base64 数据
        String image,
        // 有效期（秒）
        long expiresInSeconds
) {
}
