/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/security/JwtService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-auth
 * 分层：security
 * 类型：class JwtService
 * 注解：Service、Value
 * 依赖：io.jsonwebtoken.Claims、io.jsonwebtoken.Jwts、io.jsonwebtoken.security.Keys、org.springframework.beans.factory.annotation.Value、org.springframework.stereotype.Service、javax.crypto.SecretKey、java.nio.charset.StandardCharsets、java.time.Instant、java.util.Date
 * 公开方法：issueToken(UserPrincipal principal)；issueAccessToken(UserPrincipal principal)；issueRefreshToken(UserPrincipal principal)；parseToken(String token)；parseRefreshToken(String token)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 服务：签发与解析访问/刷新令牌。
 */
@Service
public class JwtService {
    // 对称签名密钥
    private final SecretKey key;
    // 签发者标识
    private final String issuer;
    // 访问令牌有效期
    private final long accessExpirationSeconds;
    // 刷新令牌有效期
    private final long refreshExpirationSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expiration-seconds}") long expirationSeconds,
            @Value("${app.jwt.access-expiration-seconds:0}") long accessExpirationSeconds,
            @Value("${app.jwt.refresh-expiration-seconds:0}") long refreshExpirationSeconds
    ) {
        // 使用配置的 secret 生成 HMAC 密钥
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        // 保存签发者
        this.issuer = issuer;
        // 若未单独配置访问/刷新有效期，则回退到统一配置
        this.accessExpirationSeconds = accessExpirationSeconds > 0 ? accessExpirationSeconds : expirationSeconds;
        this.refreshExpirationSeconds = refreshExpirationSeconds > 0 ? refreshExpirationSeconds : expirationSeconds;
    }

    public String issueToken(UserPrincipal principal) {
        // 默认签发访问令牌
        return issueAccessToken(principal);
    }

    public String issueAccessToken(UserPrincipal principal) {
        // 签发访问令牌
        return issueTokenInternal(principal, accessExpirationSeconds, "access");
    }

    public String issueRefreshToken(UserPrincipal principal) {
        // 签发刷新令牌
        return issueTokenInternal(principal, refreshExpirationSeconds, "refresh");
    }

    private String issueTokenInternal(UserPrincipal principal, long expSeconds, String typ) {
        // 计算签发与过期时间
        var now = Instant.now();
        var exp = now.plusSeconds(Math.max(60, expSeconds));
        // 构造 JWT 负载并签名
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(principal.userId()))
                .claim("username", principal.username())
                .claim("typ", typ)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public UserPrincipal parseToken(String token) {
        // 校验签名与 issuer 并解析 claims
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 拒绝刷新令牌作为访问令牌使用
        var typ = claims.get("typ");
        if (typ != null && "refresh".equalsIgnoreCase(String.valueOf(typ))) {
            throw new IllegalArgumentException("Invalid token type");
        }
        // 读取用户标识与用户名
        var userId = Long.parseLong(claims.getSubject());
        var username = String.valueOf(claims.get("username"));
        return new UserPrincipal(userId, username);
    }

    public UserPrincipal parseRefreshToken(String token) {
        // 校验签名与 issuer 并解析 claims
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 仅允许刷新令牌通过
        var typ = claims.get("typ");
        if (typ == null || !"refresh".equalsIgnoreCase(String.valueOf(typ))) {
            throw new IllegalArgumentException("Invalid token type");
        }
        // 读取用户标识与用户名
        var userId = Long.parseLong(claims.getSubject());
        var username = String.valueOf(claims.get("username"));
        return new UserPrincipal(userId, username);
    }
}
