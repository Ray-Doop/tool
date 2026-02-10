/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/otp/EmailOtpConsumer.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class EmailOtpConsumer
 * 注解：Component、ConditionalOnProperty、RabbitListener
 * 依赖：org.springframework.amqp.AmqpRejectAndDontRequeueException、org.springframework.amqp.rabbit.annotation.RabbitListener、org.springframework.boot.autoconfigure.condition.ConditionalOnProperty、org.springframework.stereotype.Component
 * 公开方法：handle(EmailOtpMessage message)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * RabbitMQ OTP 邮件消费者。
 */
package com.example.smarttools.modules.auth.service.otp;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.otp.delivery", havingValue = "rabbit")
public class EmailOtpConsumer {
    // 邮件发送器
    private final EmailOtpSender emailOtpSender;

    public EmailOtpConsumer(EmailOtpSender emailOtpSender) {
        // 注入邮件发送器
        this.emailOtpSender = emailOtpSender;
    }

    @RabbitListener(queues = "smarttools.otp.email.queue")
    public void handle(EmailOtpMessage message) {
        try {
            // 消费消息并发送验证码
            emailOtpSender.sendLoginCode(message.to(), message.code(), message.expiresInSeconds(), message.ip());
        } catch (RuntimeException ex) {
            // 失败拒绝重入队列，避免死循环
            throw new AmqpRejectAndDontRequeueException("OTP email send failed", ex);
        }
    }
}
