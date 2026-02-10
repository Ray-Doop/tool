/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/impl/AuthServiceImpl.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class AuthServiceImpl
 * 注解：Service、Override、Transactional
 * 依赖：com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.user.SysUser、com.example.smarttools.domain.user.SysUserRepository、com.example.smarttools.modules.auth.security.JwtService、com.example.smarttools.modules.auth.security.UserPrincipal、com.example.smarttools.modules.auth.service.IAuthService、org.springframework.http.HttpStatus、org.springframework.security.crypto.password.PasswordEncoder、org.springframework.stereotype.Service、org.springframework.transaction.annotation.Transactional、java.security.SecureRandom、java.util.Locale
 * 公开方法：register(String username, String password)；registerByEmail(String email, String password, String username)；registerByPhone(String phone, String password, String username)；login(String username, String password)；loginByIdentifier(String identifier, String password)；ensureUserForOtp(String channel, String target)；registerByEmailOtp(String email)；issueToken(SysUser user)；issueRefreshToken(SysUser user)；ensureUserById(Long userId)；ensureUserByUsername(String username)；ensureUserForWechatOpenid(String openid)；findUserByWechatOpenid(String openid)；bindWechatUser(String openid, String identifier, String password)；registerWechatUser(String openid, String username, String email, String phone, String password)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 认证服务实现：注册、登录与用户创建。
 */
package com.example.smarttools.modules.auth.service.impl;

import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.user.SysUser;
import com.example.smarttools.domain.user.SysUserRepository;
import com.example.smarttools.modules.auth.security.JwtService;
import com.example.smarttools.modules.auth.security.UserPrincipal;
import com.example.smarttools.modules.auth.service.IAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Locale;

@Service
public class AuthServiceImpl implements IAuthService {
    // 用户数据访问
    private final SysUserRepository userRepository;
    // 密码哈希器
    private final PasswordEncoder passwordEncoder;
    // JWT 签发服务
    private final JwtService jwtService;
    // 随机数生成器，用于生成临时密码
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(SysUserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        // 注入依赖
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public SysUser register(String username, String password) {
        // 用户名重复校验
        if (userRepository.existsByUsername(username)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "USERNAME_ALREADY_EXISTS", "用户名已存在");
        }

        // 创建用户并保存
        var user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);

        return user;
    }

    @Override
    @Transactional
    public SysUser registerByEmail(String email, String password, String username) {
        // 邮箱重复校验
        if (userRepository.existsByEmail(email)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_REGISTERED", "邮箱已注册，请直接登录");
        }
        // 生成最终用户名（优先用户输入）
        var finalUsername = ensureUsername(username, emailLocalPart(email));
        // 若用户名冲突，则再次生成
        if (userRepository.existsByUsername(finalUsername)) {
            finalUsername = ensureUsername(null, finalUsername);
        }

        // 创建用户并保存
        var user = new SysUser();
        user.setUsername(finalUsername);
        user.setEmail(email.toLowerCase(Locale.ROOT));
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public SysUser registerByPhone(String phone, String password, String username) {
        // 手机号重复校验
        if (userRepository.existsByPhone(phone)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "PHONE_ALREADY_REGISTERED", "手机号已注册，请直接登录");
        }
        // 生成最终用户名（优先用户输入）
        var finalUsername = ensureUsername(username, "u" + lastDigits(phone, 6));
        // 若用户名冲突，则再次生成
        if (userRepository.existsByUsername(finalUsername)) {
            finalUsername = ensureUsername(null, finalUsername);
        }

        // 创建用户并保存
        var user = new SysUser();
        user.setUsername(finalUsername);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    @Override
    public SysUser login(String username, String password) {
        // 按用户名查找用户
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户未注册"));
        // 禁用用户拒绝登录
        if (!user.isEnabled()) {
            throw new BizException(HttpStatus.FORBIDDEN, "USER_DISABLED", "账号已禁用");
        }
        // 密码校验
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "INVALID_CREDENTIALS", "用户名或密码错误");
        }
        return user;
    }

    @Override
    public SysUser loginByIdentifier(String identifier, String password) {
        // 解析标识符（用户名/邮箱/手机号）
        var user = resolveUserByIdentifier(identifier)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户未注册"));
        // 禁用用户拒绝登录
        if (!user.isEnabled()) {
            throw new BizException(HttpStatus.FORBIDDEN, "USER_DISABLED", "账号已禁用");
        }
        // 密码校验
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "INVALID_CREDENTIALS", "用户名或密码错误");
        }
        return user;
    }

    @Override
    @Transactional
    public SysUser ensureUserForOtp(String channel, String target) {
        // 仅支持邮箱 OTP
        if ("email".equalsIgnoreCase(channel)) {
            var email = target.toLowerCase(Locale.ROOT);
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }

    @Override
    @Transactional
    public SysUser registerByEmailOtp(String email) {
        // 邮箱格式与空值校验
        var e = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (e.isBlank()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "EMAIL_REQUIRED", "邮箱不能为空");
        }
        // 已存在则直接返回
        var existing = userRepository.findByEmail(e).orElse(null);
        if (existing != null) return existing;
        // 创建未完成注册的用户，等待补全资料
        var user = new SysUser();
        user.setEmail(e);
        user.setUsername(ensureUsername(null, emailLocalPart(e)));
        user.setPasswordHash(passwordEncoder.encode(randomSecret()));
        user.setRegistrationCompleted(false);
        userRepository.save(user);
        return user;
    }

    @Override
    public String issueToken(SysUser user) {
        // 签发访问令牌
        return jwtService.issueAccessToken(new UserPrincipal(user.getId(), user.getUsername()));
    }

    @Override
    public String issueRefreshToken(SysUser user) {
        // 签发刷新令牌
        return jwtService.issueRefreshToken(new UserPrincipal(user.getId(), user.getUsername()));
    }

    @Override
    public SysUser ensureUserById(Long userId) {
        // 参数校验
        if (userId == null) {
            throw new BizException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户未注册");
        }
        // 按 ID 查询用户
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户未注册"));
        // 禁用用户拒绝登录
        if (!user.isEnabled()) {
            throw new BizException(HttpStatus.FORBIDDEN, "USER_DISABLED", "账号已禁用");
        }
        return user;
    }

    @Override
    @Transactional
    public SysUser ensureUserByUsername(String username) {
        // 为空时生成默认用户名
        var name = username == null ? "" : username.trim();
        if (name.isBlank()) {
            name = "wx_" + lastDigits(String.valueOf(System.currentTimeMillis()), 8);
        }
        // 已存在则直接返回
        var existing = userRepository.findByUsername(name).orElse(null);
        if (existing != null) return existing;
        // 创建新用户
        var user = new SysUser();
        user.setUsername(ensureUsername(name, name));
        user.setPasswordHash(passwordEncoder.encode(randomSecret()));
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public SysUser ensureUserForWechatOpenid(String openid) {
        // openid 校验
        var id = openid == null ? "" : openid.trim();
        if (id.isBlank()) {
            throw new IllegalArgumentException("Invalid openid");
        }
        // 已存在则直接返回
        var existing = userRepository.findByWechatOpenid(id).orElse(null);
        if (existing != null) return existing;

        // 创建微信用户
        var user = new SysUser();
        user.setWechatOpenid(id);
        user.setUsername(ensureUsername(null, "wx_" + lastDigits(id, 8)));
        user.setPasswordHash(passwordEncoder.encode(randomSecret()));
        userRepository.save(user);
        return user;
    }

    @Override
    public SysUser findUserByWechatOpenid(String openid) {
        // openid 为空直接返回
        var id = openid == null ? "" : openid.trim();
        if (id.isBlank()) return null;
        return userRepository.findByWechatOpenid(id).orElse(null);
    }

    @Override
    @Transactional
    public SysUser bindWechatUser(String openid, String identifier, String password) {
        // openid 校验
        var id = openid == null ? "" : openid.trim();
        if (id.isBlank()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "INVALID_OPENID", "openid 无效");
        }
        // openid 重复校验
        if (userRepository.existsByWechatOpenid(id)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "WECHAT_ALREADY_BOUND", "微信已绑定其他账号");
        }
        // 校验账号密码
        var user = loginByIdentifier(identifier, password);
        var current = user.getWechatOpenid();
        if (current != null && !current.isBlank() && !current.equals(id)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "WECHAT_ALREADY_BOUND", "该账号已绑定微信");
        }
        // 绑定 openid
        if (current == null || current.isBlank()) {
            user.setWechatOpenid(id);
            userRepository.save(user);
        }
        return user;
    }

    private java.util.Optional<SysUser> resolveUserByIdentifier(String identifier) {
        // 标准化标识符
        var id = identifier == null ? "" : identifier.trim();
        if (id.isBlank()) return java.util.Optional.empty();
        // 含 @ 视为邮箱
        if (id.contains("@")) {
            return userRepository.findByEmail(id.toLowerCase(Locale.ROOT));
        }
        // 全数字视为手机号
        if (id.chars().allMatch(Character::isDigit)) {
            return userRepository.findByPhone(id);
        }
        // 否则按用户名查找
        return userRepository.findByUsername(id);
    }

    private String ensureUsername(String preferred, String fallbackBase) {
        // 优先使用用户输入
        var base = preferred == null ? "" : preferred.trim();
        if (base.isBlank()) base = fallbackBase == null ? "" : fallbackBase.trim();
        if (base.isBlank()) base = "user";
        // 仅保留安全字符
        base = base.replaceAll("[^a-zA-Z0-9_]", "_");
        // 限制长度
        if (base.length() > 32) base = base.substring(0, 32);
        var candidate = base;
        // 尝试追加随机尾巴避免冲突
        for (int i = 0; i < 50; i++) {
            if (!userRepository.existsByUsername(candidate)) return candidate;
            candidate = base + "_" + lastDigits(String.valueOf(System.currentTimeMillis()), 6);
            if (candidate.length() > 32) candidate = candidate.substring(0, 32);
        }
        throw new IllegalArgumentException("Failed to allocate username");
    }

    private String emailLocalPart(String email) {
        // 取邮箱 @ 前部分作为用户名候选
        if (email == null) return "user";
        int at = email.indexOf('@');
        var part = at > 0 ? email.substring(0, at) : email;
        part = part.trim();
        return part.isBlank() ? "user" : part;
    }

    @Override
    @Transactional
    public SysUser registerWechatUser(String openid, String username, String email, String phone, String password) {
        // openid 校验
        var id = openid == null ? "" : openid.trim();
        if (id.isBlank()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "INVALID_OPENID", "openid 无效");
        }
        // 已存在直接返回
        if (userRepository.existsByWechatOpenid(id)) {
            return userRepository.findByWechatOpenid(id).orElseThrow();
        }

        // 生成用户名并创建用户
        var finalUsername = ensureUsername(username, "wx_" + lastDigits(id, 8));
        var user = new SysUser();
        user.setWechatOpenid(id);
        user.setUsername(finalUsername);

        if (email != null && !email.trim().isBlank()) {
            // 邮箱重复校验
            var e = email.trim().toLowerCase(Locale.ROOT);
            if (userRepository.existsByEmail(e)) {
                throw new BizException(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_REGISTERED", "邮箱已注册，请直接登录");
            }
            user.setEmail(e);
        }
        if (phone != null && !phone.trim().isBlank()) {
            // 手机号重复校验
            var p = phone.trim();
            if (userRepository.existsByPhone(p)) {
                throw new BizException(HttpStatus.BAD_REQUEST, "PHONE_ALREADY_REGISTERED", "手机号已注册，请直接登录");
            }
            user.setPhone(p);
        }

        // 密码加密保存
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    private String lastDigits(String s, int n) {
        // 提取末尾数字作为随机尾巴
        if (s == null) return "0";
        var digits = s.replaceAll("\\D+", "");
        if (digits.isEmpty()) digits = "0";
        return digits.length() <= n ? digits : digits.substring(digits.length() - n);
    }

    private String randomSecret() {
        // 生成随机字符串作为临时密码
        byte[] buf = new byte[24];
        secureRandom.nextBytes(buf);
        StringBuilder sb = new StringBuilder(buf.length * 2);
        for (byte b : buf) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }
}
