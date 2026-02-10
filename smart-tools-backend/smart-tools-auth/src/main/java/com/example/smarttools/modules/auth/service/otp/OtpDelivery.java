/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/otp/OtpDelivery.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：interface OtpDelivery
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * OTP 投递接口（邮件/MQ等）。
 */
package com.example.smarttools.modules.auth.service.otp;

public interface OtpDelivery {
    // 投递验证码到目标渠道
    void deliver(String channel, String target, String code, long expiresInSeconds, String ip);
}
