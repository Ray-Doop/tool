/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/otp/EmailOtpMessage.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：record EmailOtpMessage
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * OTP 邮件消息体。
 */
package com.example.smarttools.modules.auth.service.otp;

public record EmailOtpMessage(
        // 收件人邮箱
        String to,
        // 验证码
        String code,
        // 有效期（秒）
        long expiresInSeconds,
        // 请求 IP
        String ip
) {
}
