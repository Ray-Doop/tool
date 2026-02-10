/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/otp/OtpStore.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：interface OtpStore
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * OTP 存储接口。
 */
package com.example.smarttools.modules.auth.service.otp;

public interface OtpStore {
    // 写入验证码
    void put(String key, String code, long ttlSeconds);

    // 校验并消费验证码
    void verifyAndConsume(String key, String code, int maxAttempts);

    // 校验但不消费验证码
    void check(String key, String code, int maxAttempts);
}
