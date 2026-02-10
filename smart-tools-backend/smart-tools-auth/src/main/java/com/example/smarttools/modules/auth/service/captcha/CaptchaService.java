/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/captcha/CaptchaService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class CaptchaService
 * 注解：Service、Value
 * 依赖：org.springframework.beans.factory.annotation.Value、org.springframework.stereotype.Service、java.nio.charset.StandardCharsets、java.security.SecureRandom、java.util.Base64、java.util.UUID
 * 公开方法：create(String ip)；verify(String captchaId, String captchaCode, String ip)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 图形验证码服务：生成验证码图片并校验输入。
 */
package com.example.smarttools.modules.auth.service.captcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
public class CaptchaService {
    // 随机数生成器
    private final SecureRandom random = new SecureRandom();
    // 验证码有效期
    private final long ttlSeconds;
    // 允许失败次数
    private final int maxAttempts;
    // 验证码存储
    private final CaptchaStore store;

    public CaptchaService(
            CaptchaStore store,
            @Value("${app.captcha.ttl-seconds:120}") long ttlSeconds,
            @Value("${app.captcha.max-attempts:5}") int maxAttempts
    ) {
        // 注入存储与参数
        this.store = store;
        // 设置有效期下限
        this.ttlSeconds = Math.max(30, ttlSeconds);
        // 设置最大失败次数下限
        this.maxAttempts = Math.max(1, maxAttempts);
    }

    public Captcha create(String ip) {
        // 生成验证码 ID 与文本
        var id = UUID.randomUUID().toString().replace("-", "");
        var text = randomText(4);
        // 保存答案与 IP 绑定
        store.put(id, ip == null ? "" : ip.trim(), text.toLowerCase(), ttlSeconds);
        // 生成 SVG 并 base64 编码
        var svg = renderSvg(text);
        var b64 = Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
        // 返回验证码结构给前端
        return new Captcha(id, "data:image/svg+xml;base64," + b64, ttlSeconds);
    }

    public boolean verify(String captchaId, String captchaCode, String ip) {
        // 校验并消费验证码
        return store.verifyAndConsume(captchaId, captchaCode, ip == null ? "" : ip.trim(), maxAttempts);
    }

    private String randomText(int len) {
        // 排除易混淆字符的字母表
        var alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        var sb = new StringBuilder(len);
        // 随机拼接指定长度
        for (int i = 0; i < len; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    private String renderSvg(String text) {
        // 生成简单的 SVG 图形验证码
        int w = 140;
        int h = 44;
        int seed = random.nextInt(10_000);
        var sb = new StringBuilder(512);
        sb.append("<svg xmlns='http://www.w3.org/2000/svg' width='").append(w).append("' height='").append(h).append("' viewBox='0 0 ").append(w).append(" ").append(h).append("'>");
        // 背景
        sb.append("<rect width='100%' height='100%' rx='10' ry='10' fill='#f8fafc'/>");
        // 干扰线
        sb.append("<g opacity='0.25'>");
        for (int i = 0; i < 6; i++) {
            int x1 = (seed * (i + 3) * 31) % w;
            int y1 = (seed * (i + 5) * 17) % h;
            int x2 = (seed * (i + 7) * 13) % w;
            int y2 = (seed * (i + 11) * 19) % h;
            sb.append("<line x1='").append(x1).append("' y1='").append(y1).append("' x2='").append(x2).append("' y2='").append(y2).append("' stroke='#94a3b8' stroke-width='1'/>");
        }
        sb.append("</g>");
        // 文本字符绘制
        sb.append("<g font-family='Arial, sans-serif' font-size='26' font-weight='700' fill='#0f172a'>");
        for (int i = 0; i < text.length(); i++) {
            int x = 18 + i * 28 + (seed * (i + 2) % 6);
            int y = 32 + ((seed * (i + 5) % 7) - 3);
            int rot = ((seed * (i + 9)) % 21) - 10;
            sb.append("<text x='").append(x).append("' y='").append(y).append("' transform='rotate(").append(rot).append(" ").append(x).append(" ").append(y).append(")'>");
            sb.append(escape(text.substring(i, i + 1)));
            sb.append("</text>");
        }
        sb.append("</g>");
        sb.append("</svg>");
        return sb.toString();
    }

    private String escape(String s) {
        // 防止 SVG 注入
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }

    // 返回给前端的验证码结构
    public record Captcha(String captchaId, String image, long expiresInSeconds) {
    }
}
