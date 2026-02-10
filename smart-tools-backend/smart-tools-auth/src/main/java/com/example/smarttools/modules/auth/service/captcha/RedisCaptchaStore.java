/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/captcha/RedisCaptchaStore.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class RedisCaptchaStore
 * 注解：Component、ConditionalOnProperty、Override
 * 依赖：org.springframework.boot.autoconfigure.condition.ConditionalOnProperty、org.springframework.data.redis.core.StringRedisTemplate、org.springframework.data.redis.core.script.DefaultRedisScript、org.springframework.stereotype.Component、java.time.Duration、java.util.List
 * 公开方法：put(String captchaId, String ip, String answer, long ttlSeconds)；verifyAndConsume(String captchaId, String code, String ip, int maxAttempts)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * Redis 版图形验证码存储。
 */
package com.example.smarttools.modules.auth.service.captcha;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.captcha.store", havingValue = "redis")
public class RedisCaptchaStore implements CaptchaStore {
    // Redis 客户端
    private final StringRedisTemplate redis;
    // Lua 校验脚本
    private final DefaultRedisScript<Long> verifyScript;

    public RedisCaptchaStore(StringRedisTemplate redis) {
        // 注入 Redis
        this.redis = redis;
        // 初始化 Lua 脚本
        this.verifyScript = new DefaultRedisScript<>();
        this.verifyScript.setResultType(Long.class);
        this.verifyScript.setScriptText("""
                local key = KEYS[1]
                local input = ARGV[1]
                local ip = ARGV[2]
                local maxAttempts = tonumber(ARGV[3])
                local answer = redis.call('HGET', key, 'answer')
                if not answer then
                  return 1
                end
                local storedIp = redis.call('HGET', key, 'ip') or ''
                if storedIp ~= '' and storedIp ~= ip then
                  redis.call('DEL', key)
                  return 1
                end
                local attempts = tonumber(redis.call('HGET', key, 'attempts') or '0')
                if attempts >= maxAttempts then
                  redis.call('DEL', key)
                  return 1
                end
                if string.lower(answer) ~= string.lower(input) then
                  attempts = tonumber(redis.call('HINCRBY', key, 'attempts', 1))
                  if attempts >= maxAttempts then
                    redis.call('DEL', key)
                  end
                  return 2
                end
                redis.call('DEL', key)
                return 0
                """);
    }

    @Override
    public void put(String captchaId, String ip, String answer, long ttlSeconds) {
        // 使用 Hash 结构保存答案/IP/尝试次数
        var key = key(captchaId);
        redis.opsForHash().put(key, "answer", answer == null ? "" : answer);
        redis.opsForHash().put(key, "ip", ip == null ? "" : ip);
        redis.opsForHash().put(key, "attempts", "0");
        // 设置过期时间
        redis.expire(key, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public boolean verifyAndConsume(String captchaId, String code, String ip, int maxAttempts) {
        // 参数缺失直接失败
        if (captchaId == null || captchaId.isBlank() || code == null || code.isBlank()) return false;
        // 执行 Lua 校验脚本
        var key = key(captchaId);
        var input = code.trim();
        Long res = redis.execute(verifyScript, List.of(key), input, ip == null ? "" : ip, String.valueOf(maxAttempts));
        long r = res == null ? 1L : res;
        // 0=成功，其余均失败
        return r == 0L;
    }

    private String key(String captchaId) {
        // 统一 Redis key 前缀
        return "captcha:" + captchaId;
    }
}
