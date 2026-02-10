/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/captcha/CaptchaStore.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：interface CaptchaStore
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 图形验证码存储接口。
 */
package com.example.smarttools.modules.auth.service.captcha;

public interface CaptchaStore {
    // 写入验证码答案与 IP 绑定
    void put(String captchaId, String ip, String answer, long ttlSeconds);

    // 校验验证码并在成功时消费
    boolean verifyAndConsume(String captchaId, String code, String ip, int maxAttempts);
}
