/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/otp/RabbitEmailOtpDelivery.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class RabbitEmailOtpDelivery
 * 注解：Component、ConditionalOnProperty、Override
 * 依赖：org.slf4j.Logger、org.slf4j.LoggerFactory、org.springframework.amqp.AmqpException、org.springframework.amqp.rabbit.core.RabbitTemplate、org.springframework.boot.autoconfigure.condition.ConditionalOnProperty、org.springframework.stereotype.Component
 * 公开方法：deliver(String channel, String target, String code, long expiresInSeconds, String ip)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * RabbitMQ 方式投递 OTP 邮件。
 */
package com.example.smarttools.modules.auth.service.otp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.otp.delivery", havingValue = "rabbit")
public class RabbitEmailOtpDelivery implements OtpDelivery {
    private static final Logger log = LoggerFactory.getLogger(RabbitEmailOtpDelivery.class);

    // RabbitMQ 客户端
    private final RabbitTemplate rabbitTemplate;

    public RabbitEmailOtpDelivery(RabbitTemplate rabbitTemplate) {
        // 注入 RabbitTemplate
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void deliver(String channel, String target, String code, long expiresInSeconds, String ip) {
        // 当前仅支持 email 渠道
        if (!"email".equalsIgnoreCase(channel)) {
            throw new IllegalArgumentException("Unsupported channel");
        }
        try {
            // 发送消息到交换机
            rabbitTemplate.convertAndSend(
                    "smarttools.otp.exchange",
                    "otp.email",
                    new EmailOtpMessage(target, code, expiresInSeconds, ip)
            );
            log.info("OTP email enqueued target={} expiresInSeconds={}", maskEmail(target), expiresInSeconds);
        } catch (AmqpException ex) {
            // 发送失败转为统一异常
            log.error("OTP email enqueue failed target={} error={}", maskEmail(target), ex.getMessage(), ex);
            throw new IllegalArgumentException("邮件发送失败");
        }
    }

    private String maskEmail(String email) {
        // 脱敏邮箱，避免日志泄露
        if (email == null) return "";
        var e = email.trim();
        int at = e.indexOf('@');
        if (at <= 1) return "***";
        var prefix = e.substring(0, at);
        var domain = e.substring(at);
        var visible = Math.min(2, prefix.length());
        return prefix.substring(0, visible) + "***" + domain;
    }
}
