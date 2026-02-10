/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/otp/RedisOtpStore.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class RedisOtpStore
 * 注解：Component、ConditionalOnProperty、Override
 * 依赖：org.springframework.boot.autoconfigure.condition.ConditionalOnProperty、org.springframework.data.redis.core.StringRedisTemplate、org.springframework.data.redis.core.script.DefaultRedisScript、org.springframework.stereotype.Component、java.time.Duration、java.util.List
 * 公开方法：put(String key, String code, long ttlSeconds)；verifyAndConsume(String key, String code, int maxAttempts)；check(String key, String code, int maxAttempts)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * Redis 版 OTP 存储。
 */
package com.example.smarttools.modules.auth.service.otp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.otp.store", havingValue = "redis")
public class RedisOtpStore implements OtpStore {
    // Redis 客户端
    private final StringRedisTemplate redis;
    // 校验并消费脚本
    private final DefaultRedisScript<Long> verifyScript;
    // 仅校验脚本
    private final DefaultRedisScript<Long> checkScript;

    public RedisOtpStore(StringRedisTemplate redis) {
        // 注入 Redis
        this.redis = redis;
        // 初始化 Lua 脚本
        this.verifyScript = new DefaultRedisScript<>();
        this.verifyScript.setResultType(Long.class);
        this.verifyScript.setScriptText("""
                local key = KEYS[1]
                local input = ARGV[1]
                local maxAttempts = tonumber(ARGV[2])
                local code = redis.call('HGET', key, 'code')
                if not code then
                  return 1
                end
                local attempts = tonumber(redis.call('HGET', key, 'attempts') or '0')
                if attempts >= maxAttempts then
                  redis.call('DEL', key)
                  return 3
                end
                if code ~= input then
                  attempts = tonumber(redis.call('HINCRBY', key, 'attempts', 1))
                  if attempts >= maxAttempts then
                    redis.call('DEL', key)
                    return 3
                  end
                  return 2
                end
                redis.call('DEL', key)
                return 0
                """);
        this.checkScript = new DefaultRedisScript<>();
        this.checkScript.setResultType(Long.class);
        this.checkScript.setScriptText("""
                local key = KEYS[1]
                local input = ARGV[1]
                local maxAttempts = tonumber(ARGV[2])
                local code = redis.call('HGET', key, 'code')
                if not code then
                  return 1
                end
                local attempts = tonumber(redis.call('HGET', key, 'attempts') or '0')
                if attempts >= maxAttempts then
                  redis.call('DEL', key)
                  return 3
                end
                if code ~= input then
                  attempts = tonumber(redis.call('HINCRBY', key, 'attempts', 1))
                  if attempts >= maxAttempts then
                    redis.call('DEL', key)
                    return 3
                  end
                  return 2
                end
                return 0
                """);
    }

    @Override
    public void put(String key, String code, long ttlSeconds) {
        // 保存验证码与尝试次数
        var k = key(key);
        redis.opsForHash().put(k, "code", code);
        redis.opsForHash().put(k, "attempts", "0");
        // 设置过期时间
        redis.expire(k, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public void verifyAndConsume(String key, String code, int maxAttempts) {
        // 执行校验脚本
        var input = code == null ? "" : code.trim();
        Long res = redis.execute(verifyScript, List.of(key(key)), input, String.valueOf(maxAttempts));
        long r = res == null ? 1L : res;
        // 0=成功，3=超限，其余=无效
        if (r == 0L) return;
        if (r == 3L) throw new IllegalArgumentException("Too many attempts");
        throw new IllegalArgumentException("Invalid code");
    }

    @Override
    public void check(String key, String code, int maxAttempts) {
        // 执行仅校验脚本
        var input = code == null ? "" : code.trim();
        Long res = redis.execute(checkScript, List.of(key(key)), input, String.valueOf(maxAttempts));
        long r = res == null ? 1L : res;
        // 0=成功，3=超限，其余=无效
        if (r == 0L) return;
        if (r == 3L) throw new IllegalArgumentException("Too many attempts");
        throw new IllegalArgumentException("Invalid code");
    }

    private String key(String raw) {
        // 统一 OTP key 前缀
        return "otp:" + raw;
    }
}
