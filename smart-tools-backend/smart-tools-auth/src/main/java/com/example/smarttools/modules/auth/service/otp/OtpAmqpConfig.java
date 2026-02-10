/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/otp/OtpAmqpConfig.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class OtpAmqpConfig
 * 注解：Configuration、ConditionalOnProperty、Bean
 * 依赖：org.springframework.amqp.core.Binding、org.springframework.amqp.core.BindingBuilder、org.springframework.amqp.core.DirectExchange、org.springframework.amqp.core.Queue、org.springframework.amqp.support.converter.Jackson2JsonMessageConverter、org.springframework.boot.autoconfigure.condition.ConditionalOnProperty、org.springframework.context.annotation.Bean、org.springframework.context.annotation.Configuration
 * 公开方法：otpExchange()；otpEmailQueue()；otpEmailBinding(DirectExchange otpExchange, Queue otpEmailQueue)；messageConverter()
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * OTP RabbitMQ 基础配置。
 */
package com.example.smarttools.modules.auth.service.otp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.otp.delivery", havingValue = "rabbit")
public class OtpAmqpConfig {
    @Bean
    public DirectExchange otpExchange() {
        // 直连交换机
        return new DirectExchange("smarttools.otp.exchange", true, false);
    }

    @Bean
    public Queue otpEmailQueue() {
        // OTP 邮件队列
        return new Queue("smarttools.otp.email.queue", true);
    }

    @Bean
    public Binding otpEmailBinding(DirectExchange otpExchange, Queue otpEmailQueue) {
        // 绑定交换机与队列
        return BindingBuilder.bind(otpEmailQueue).to(otpExchange).with("otp.email");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        // 消息体 JSON 转换器
        return new Jackson2JsonMessageConverter();
    }
}
