/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/otp/InMemoryOtpStore.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class InMemoryOtpStore
 * 注解：Component、ConditionalOnProperty、Override
 * 依赖：org.springframework.beans.factory.annotation.Value、org.springframework.boot.autoconfigure.condition.ConditionalOnProperty、org.springframework.stereotype.Component、java.time.Instant、java.util.concurrent.ConcurrentHashMap、java.util.concurrent.atomic.AtomicLong
 * 公开方法：put(String key, String code, long ttlSeconds)；verifyAndConsume(String key, String code, int maxAttempts)；check(String key, String code, int maxAttempts)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 内存版 OTP 存储（开发环境默认）。
 */
package com.example.smarttools.modules.auth.service.otp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@ConditionalOnProperty(name = "app.otp.store", havingValue = "memory", matchIfMissing = true)
public class InMemoryOtpStore implements OtpStore {
    // 内存存储结构
    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();
    // 清理间隔（秒）
    private final long cleanupIntervalSeconds;
    // 上一次清理时间
    private final AtomicLong lastCleanupEpochSeconds = new AtomicLong(0);

    public InMemoryOtpStore(@Value("${app.otp.cleanup-interval-seconds:60}") long cleanupIntervalSeconds) {
        // 清理间隔下限保护
        this.cleanupIntervalSeconds = Math.max(10, cleanupIntervalSeconds);
    }

    @Override
    public void put(String key, String code, long ttlSeconds) {
        // 写入前尝试清理过期数据
        cleanupIfNeeded();
        // 写入验证码与过期时间
        var expiresAt = Instant.now().plusSeconds(ttlSeconds);
        store.put(key, new Entry(code, expiresAt, 0));
    }

    @Override
    public void verifyAndConsume(String key, String code, int maxAttempts) {
        // 校验前清理过期数据
        cleanupIfNeeded();
        var entry = store.get(key);
        if (entry == null) {
            throw new IllegalArgumentException("Invalid code");
        }
        // 过期即删除
        if (Instant.now().isAfter(entry.expiresAt())) {
            store.remove(key);
            throw new IllegalArgumentException("Code expired");
        }
        // 超过尝试次数即删除
        if (entry.attempts() >= maxAttempts) {
            store.remove(key);
            throw new IllegalArgumentException("Too many attempts");
        }
        // 比对验证码
        var input = code == null ? "" : code.trim();
        if (!entry.code().equals(input)) {
            // 失败则累加尝试次数
            store.computeIfPresent(key, (k, v) -> new Entry(v.code(), v.expiresAt(), v.attempts() + 1));
            throw new IllegalArgumentException("Invalid code");
        }
        // 成功则消费
        store.remove(key);
    }

    @Override
    public void check(String key, String code, int maxAttempts) {
        // 校验前清理过期数据
        cleanupIfNeeded();
        var entry = store.get(key);
        if (entry == null) {
            throw new IllegalArgumentException("Invalid code");
        }
        // 过期即删除
        if (Instant.now().isAfter(entry.expiresAt())) {
            store.remove(key);
            throw new IllegalArgumentException("Code expired");
        }
        // 超过尝试次数即删除
        if (entry.attempts() >= maxAttempts) {
            store.remove(key);
            throw new IllegalArgumentException("Too many attempts");
        }
        // 比对验证码，不消费
        var input = code == null ? "" : code.trim();
        if (!entry.code().equals(input)) {
            // 失败则累加尝试次数
            store.computeIfPresent(key, (k, v) -> new Entry(v.code(), v.expiresAt(), v.attempts() + 1));
            throw new IllegalArgumentException("Invalid code");
        }
    }

    private void cleanupIfNeeded() {
        // 定时清理过期验证码，降低内存占用
        long now = Instant.now().getEpochSecond();
        long last = lastCleanupEpochSeconds.get();
        if (now - last < cleanupIntervalSeconds) return;
        if (!lastCleanupEpochSeconds.compareAndSet(last, now)) return;
        var nowInstant = Instant.ofEpochSecond(now);
        store.entrySet().removeIf(e -> nowInstant.isAfter(e.getValue().expiresAt()));
    }

    // 存储条目：验证码/过期时间/尝试次数
    private record Entry(String code, Instant expiresAt, int attempts) {
    }
}
