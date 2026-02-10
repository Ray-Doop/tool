/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/otp/EmailOtpSender.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class EmailOtpSender
 * 注解：Service、Value
 * 依赖：org.slf4j.Logger、org.slf4j.LoggerFactory、org.springframework.beans.factory.annotation.Value、org.springframework.lang.Nullable、org.springframework.mail.MailSendException、org.springframework.mail.javamail.JavaMailSender、org.springframework.mail.javamail.MimeMessageHelper、org.springframework.stereotype.Service、jakarta.mail.MessagingException
 * 公开方法：sendLoginCode(String to, String code, long expiresInSeconds, @Nullable String ip)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 发送 OTP 邮件的具体实现。
 */
package com.example.smarttools.modules.auth.service.otp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
public class EmailOtpSender {
    private static final Logger log = LoggerFactory.getLogger(EmailOtpSender.class);

    // JavaMail 发送器
    private final JavaMailSender mailSender;
    // 发件人地址（可选）
    private final String from;
    // 应用名称
    private final String appName;
    // 邮箱账号与密码（用于判断是否配置）
    private final String mailUsername;
    private final String mailPassword;

    public EmailOtpSender(
            JavaMailSender mailSender,
            @Value("${spring.mail-from:}") String from,
            @Value("${app.name:SmartTools}") String appName,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword
    ) {
        // 注入邮件配置
        this.mailSender = mailSender;
        this.from = from;
        this.appName = appName;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;

        // 未配置时仅提示，不直接报错
        if (this.mailUsername.isBlank() || this.mailPassword.isBlank()) {
            log.warn("SMTP 未配置：请设置 MAIL_USERNAME/MAIL_PASSWORD 或 spring.mail.username/spring.mail.password");
        }
    }

    public void sendLoginCode(String to, String code, long expiresInSeconds, @Nullable String ip) {
        // 必须先配置邮箱账号与密码
        if (mailUsername.isBlank() || mailPassword.isBlank()) {
            throw new IllegalArgumentException("邮件服务未配置（请设置 MAIL_USERNAME/MAIL_PASSWORD）");
        }
        try {
            // 构建邮件内容
            var mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setTo(to);
            if (!from.isBlank()) {
                helper.setFrom(from);
            } else {
                helper.setFrom(mailUsername);
            }
            helper.setSubject(appName + " 登录验证码");
            var minutes = Math.max(1, expiresInSeconds / 60);
            var body = new StringBuilder();
            body.append("你的验证码为：").append(code).append("\n");
            body.append("有效期：").append(minutes).append(" 分钟\n");
            if (ip != null && !ip.isBlank()) {
                // 可选展示请求 IP
                body.append("请求 IP：").append(ip).append("\n");
            }
            body.append("\n如非本人操作，请忽略本邮件。");
            helper.setText(body.toString(), false);
            // 发送邮件
            mailSender.send(mime);
        } catch (MessagingException ex) {
            // 组装邮件失败
            throw new MailSendException("Failed to build email message", ex);
        }
    }
}
