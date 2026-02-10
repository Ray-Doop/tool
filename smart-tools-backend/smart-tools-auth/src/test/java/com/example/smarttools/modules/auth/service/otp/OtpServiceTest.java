/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/test/java/com/example/smarttools/modules/auth/service/otp/OtpServiceTest.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class OtpServiceTest
 * 注解：Test
 * 依赖：org.junit.jupiter.api.Assertions、org.junit.jupiter.api.Test、java.util.concurrent.Executor
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * OTP 服务单元测试。
 */
package com.example.smarttools.modules.auth.service.otp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;

public class OtpServiceTest {
    @Test
    void sendPersistsEvenWhenDeliveryThrows() {
        // 使用内存存储与抛异常的投递器
        var store = new InMemoryOtpStore(60);
        final String[] captured = new String[1];
        OtpDelivery delivery = (channel, target, code, expiresInSeconds, ip) -> {
            captured[0] = code;
            throw new IllegalArgumentException("mail not configured");
        };
        Executor executor = Runnable::run;
        var service = new OtpService(store, delivery, executor, 6, 300, 5, false);

        Assertions.assertDoesNotThrow(() -> service.send("email", "a@b.com"));
        var code = captured[0];
        Assertions.assertNotNull(code);
        Assertions.assertDoesNotThrow(() -> service.verify("email", "a@b.com", code));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.verify("email", "a@b.com", code));
    }

    @Test
    void sendPersistsAfterDeliveryAndVerifyConsumes() {
        // 使用内存存储与捕获验证码的投递器
        var store = new InMemoryOtpStore(60);
        final String[] captured = new String[1];
        OtpDelivery delivery = (channel, target, code, expiresInSeconds, ip) -> captured[0] = code;
        Executor executor = Runnable::run;
        var service = new OtpService(store, delivery, executor, 6, 300, 5, false);
        service.send("email", "a@b.com");

        // 获取发送的验证码
        var code = captured[0];
        Assertions.assertNotNull(code);
        // 首次校验成功
        Assertions.assertDoesNotThrow(() -> service.verify("email", "a@b.com", code));
        // 再次校验应失败（已被消费）
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.verify("email", "a@b.com", code));
    }
}
