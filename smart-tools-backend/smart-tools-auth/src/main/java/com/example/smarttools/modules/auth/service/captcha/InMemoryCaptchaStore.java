/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/captcha/InMemoryCaptchaStore.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class InMemoryCaptchaStore
 * 注解：Component、ConditionalOnProperty、Override
 * 依赖：org.springframework.boot.autoconfigure.condition.ConditionalOnProperty、org.springframework.stereotype.Component、java.time.Instant、java.util.concurrent.ConcurrentHashMap
 * 公开方法：put(String captchaId, String ip, String answer, long ttlSeconds)；verifyAndConsume(String captchaId, String code, String ip, int maxAttempts)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 内存版图形验证码存储（开发环境默认）。
 */
package com.example.smarttools.modules.auth.service.captcha;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "app.captcha.store", havingValue = "memory", matchIfMissing = true)
public class InMemoryCaptchaStore implements CaptchaStore {
    // 内存存储结构
    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();

    @Override
    public void put(String captchaId, String ip, String answer, long ttlSeconds) {
        // 写入验证码并设置过期时间
        var expiresAt = Instant.now().plusSeconds(ttlSeconds);
        store.put(captchaId, new Entry(expiresAt, ip == null ? "" : ip, answer == null ? "" : answer, 0));
    }

    @Override
    public boolean verifyAndConsume(String captchaId, String code, String ip, int maxAttempts) {
        // 参数缺失直接失败
        if (captchaId == null || captchaId.isBlank() || code == null || code.isBlank()) return false;
        // 查找验证码条目
        var entry = store.get(captchaId);
        if (entry == null) return false;
        // 过期即删除
        if (Instant.now().isAfter(entry.expiresAt())) {
            store.remove(captchaId);
            return false;
        }
        // 校验 IP 绑定
        var clientIp = ip == null ? "" : ip;
        if (!entry.ip().isEmpty() && !entry.ip().equals(clientIp)) {
            store.remove(captchaId);
            return false;
        }
        // 比对验证码内容
        var ok = entry.answer().equalsIgnoreCase(code.trim());
        if (ok) {
            store.remove(captchaId);
            return true;
        }
        // 失败则增加尝试次数，超过阈值删除
        store.computeIfPresent(captchaId, (k, v) -> {
            var next = v.attempts() + 1;
            return next >= maxAttempts ? null : new Entry(v.expiresAt(), v.ip(), v.answer(), next);
        });
        return false;
    }

    // 存储条目：过期时间/IP/答案/尝试次数
    private record Entry(Instant expiresAt, String ip, String answer, int attempts) {
    }
}
