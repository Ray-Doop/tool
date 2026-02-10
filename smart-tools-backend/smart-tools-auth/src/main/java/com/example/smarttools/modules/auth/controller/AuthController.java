/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/controller/AuthController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-auth
 * 分层：controller
 * 注解：RestController、RequestMapping、Value、GetMapping、PostMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.common.exception.BizException、com.example.smarttools.common.util.IpUtil、com.example.smarttools.domain.user.SysUser、com.example.smarttools.modules.auth.domain.dto.AuthResponse、com.example.smarttools.modules.auth.domain.dto.CaptchaResponse、com.example.smarttools.modules.auth.domain.dto.LoginPasswordRequest、com.example.smarttools.modules.auth.domain.dto.LoginRequest、com.example.smarttools.modules.auth.domain.dto.OtpLoginRequest、com.example.smarttools.modules.auth.domain.dto.OtpCheckRequest、com.example.smarttools.modules.auth.domain.dto.OtpSendRequest、com.example.smarttools.modules.auth.domain.dto.OtpSendResponse、com.example.smarttools.modules.auth.domain.dto.RegisterEmailRequest、com.example.smarttools.modules.auth.domain.dto.RegisterPhoneRequest
 * 公开方法：captcha(HttpServletRequest request)；register(@Valid @RequestBody RegisterRequest req, HttpServletResponse response)；registerEmail(@Valid @RequestBody RegisterEmailRequest req, HttpServletRequest request, HttpS…)；registerPhone(@Valid @RequestBody RegisterPhoneRequest req, HttpServletResponse response)；login(@Valid @RequestBody LoginRequest req, HttpServletResponse response)；loginPassword(@Valid @RequestBody LoginPasswordRequest req, HttpServletRequest request, HttpS…)；otpSend(@Valid @RequestBody OtpSendRequest req, HttpServletRequest request)；otpCheck(@Valid @RequestBody OtpCheckRequest req, HttpServletRequest request)；loginOtp(@Valid @RequestBody OtpLoginRequest req, HttpServletRequest request, HttpServle…)；wechatQr()；wechatRegister(@Valid @RequestBody WechatRegisterRequest req, HttpServletRequest request, Http…)；wechatBind(@Valid @RequestBody WechatBindRequest req, HttpServletRequest request, HttpServ…)；wechatDevConfirm(@Valid @RequestBody WechatDevConfirmRequest req, HttpServletResponse response)；refresh(HttpServletRequest request, HttpServletResponse response)；logout(HttpServletResponse response)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 认证与验证码相关接口控制器。
 */
package com.example.smarttools.modules.auth.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.common.util.IpUtil;
import com.example.smarttools.domain.user.SysUser;
import com.example.smarttools.modules.auth.domain.dto.AuthResponse;
import com.example.smarttools.modules.auth.domain.dto.CaptchaResponse;
import com.example.smarttools.modules.auth.domain.dto.LoginPasswordRequest;
import com.example.smarttools.modules.auth.domain.dto.LoginRequest;
import com.example.smarttools.modules.auth.domain.dto.OtpLoginRequest;
import com.example.smarttools.modules.auth.domain.dto.OtpCheckRequest;
import com.example.smarttools.modules.auth.domain.dto.OtpSendRequest;
import com.example.smarttools.modules.auth.domain.dto.OtpSendResponse;
import com.example.smarttools.modules.auth.domain.dto.RegisterEmailRequest;
import com.example.smarttools.modules.auth.domain.dto.RegisterPhoneRequest;
import com.example.smarttools.modules.auth.domain.dto.RegisterRequest;
import com.example.smarttools.modules.auth.domain.dto.WechatBindRequest;
import com.example.smarttools.modules.auth.domain.dto.WechatDevConfirmRequest;
import com.example.smarttools.modules.auth.domain.dto.WechatPollResponse;
import com.example.smarttools.modules.auth.domain.dto.WechatQrResponse;
import com.example.smarttools.modules.auth.domain.dto.WechatRegisterRequest;
import com.example.smarttools.modules.auth.security.JwtService;
import com.example.smarttools.modules.auth.service.IAuthService;
import com.example.smarttools.modules.auth.service.captcha.CaptchaService;
import com.example.smarttools.modules.auth.service.otp.OtpService;
import com.example.smarttools.modules.auth.service.risk.AuthRiskService;
import com.example.smarttools.modules.auth.service.wechat.WechatLoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // 认证业务服务
    private final IAuthService authService;
    // OTP 发送与校验服务
    private final OtpService otpService;
    // 微信登录服务
    private final WechatLoginService wechatLoginService;
    // 图形验证码服务
    private final CaptchaService captchaService;
    // 风控服务
    private final AuthRiskService riskService;
    // JWT 服务
    private final JwtService jwtService;
    // 刷新令牌 Cookie 配置
    private final String refreshCookieName;
    private final String refreshCookiePath;
    private final String refreshCookieSameSite;
    private final boolean refreshCookieSecure;
    private final long refreshCookieMaxAgeSeconds;

    public AuthController(
            IAuthService authService,
            OtpService otpService,
            WechatLoginService wechatLoginService,
            CaptchaService captchaService,
            AuthRiskService riskService,
            JwtService jwtService,
            @Value("${app.jwt.refresh-cookie-name:smart_tools_refresh}") String refreshCookieName,
            @Value("${app.jwt.refresh-cookie-path:/api/auth}") String refreshCookiePath,
            @Value("${app.jwt.refresh-cookie-same-site:Strict}") String refreshCookieSameSite,
            @Value("${app.jwt.refresh-cookie-secure:false}") boolean refreshCookieSecure,
            @Value("${app.jwt.refresh-expiration-seconds:${app.jwt.expiration-seconds}}") long refreshCookieMaxAgeSeconds
    ) {
        // 注入依赖与 Cookie 配置
        this.authService = authService;
        this.otpService = otpService;
        this.wechatLoginService = wechatLoginService;
        this.captchaService = captchaService;
        this.riskService = riskService;
        this.jwtService = jwtService;
        this.refreshCookieName = refreshCookieName;
        this.refreshCookiePath = refreshCookiePath;
        this.refreshCookieSameSite = refreshCookieSameSite;
        this.refreshCookieSecure = refreshCookieSecure;
        this.refreshCookieMaxAgeSeconds = refreshCookieMaxAgeSeconds;
    }

    @GetMapping("/captcha")
    public ApiResponse<CaptchaResponse> captcha(HttpServletRequest request) {
        // 进行 IP 风控校验
        var ip = IpUtil.clientIp(request);
        riskService.checkIp(ip);
        // 创建图形验证码并返回
        var c = captchaService.create(ip);
        return ApiResponse.ok(new CaptchaResponse(c.captchaId(), c.image(), c.expiresInSeconds()));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest req, HttpServletResponse response) {
        // 账号密码注册
        var user = authService.register(req.username(), req.password());
        return ApiResponse.ok(issueAndSetCookie(user, response));
    }

    @PostMapping("/register/email")
    public ApiResponse<AuthResponse> registerEmail(@Valid @RequestBody RegisterEmailRequest req, HttpServletRequest request, HttpServletResponse response) {
        // IP 风控校验
        var ip = IpUtil.clientIp(request);
        riskService.checkIp(ip);
        // 校验两次密码一致
        if (!req.password().equals(req.confirmPassword())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH", "两次密码不一致");
        }
        // 校验图形验证码
        if (!captchaService.verify(req.captchaId(), req.captchaCode(), ip)) {
            riskService.recordCaptchaFailure(ip);
            throw new BizException(HttpStatus.BAD_REQUEST, "CAPTCHA_INVALID", "验证码错误");
        }
        try {
            // 校验邮箱 OTP
            otpService.verify("email", req.email(), req.emailCode());
        } catch (IllegalArgumentException ex) {
            // OTP 校验失败计入风控
            riskService.recordOtpFailure(ip);
            throw new BizException(HttpStatus.BAD_REQUEST, "OTP_INVALID", "邮箱验证码错误或已过期");
        }
        // 完成注册并签发 token
        var user = authService.registerByEmail(req.email(), req.password(), req.username());
        return ApiResponse.ok(issueAndSetCookie(user, response));
    }

    @PostMapping("/register/phone")
    public ApiResponse<AuthResponse> registerPhone(@Valid @RequestBody RegisterPhoneRequest req, HttpServletResponse response) {
        // 校验两次密码一致
        if (!req.password().equals(req.confirmPassword())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH", "两次密码不一致");
        }
        // 手机注册
        var user = authService.registerByPhone(req.phone(), req.password(), req.username());
        return ApiResponse.ok(issueAndSetCookie(user, response));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest req, HttpServletResponse response) {
        // 账号密码登录
        var user = authService.login(req.username(), req.password());
        return ApiResponse.ok(issueAndSetCookie(user, response));
    }

    @PostMapping("/login/password")
    public ApiResponse<AuthResponse> loginPassword(@Valid @RequestBody LoginPasswordRequest req, HttpServletRequest request, HttpServletResponse response) {
        // IP 风控校验
        var ip = IpUtil.clientIp(request);
        riskService.checkIp(ip);
        // 图形验证码校验
        if (!captchaService.verify(req.captchaId(), req.captchaCode(), ip)) {
            riskService.recordCaptchaFailure(ip);
            throw new BizException(HttpStatus.BAD_REQUEST, "CAPTCHA_INVALID", "验证码错误");
        }
        SysUser user;
        try {
            // 用户名/邮箱/手机号登录
            user = authService.loginByIdentifier(req.identifier(), req.password());
        } catch (BizException ex) {
            // 密码错误计入风控
            if ("INVALID_CREDENTIALS".equals(ex.code())) {
                riskService.recordPasswordFailure(ip);
            }
            throw ex;
        }
        return ApiResponse.ok(issueAndSetCookie(user, response));
    }

    @PostMapping("/otp/send")
    public ApiResponse<OtpSendResponse> otpSend(@Valid @RequestBody OtpSendRequest req, HttpServletRequest request) {
        // IP 风控校验与发送频率限制
        var ip = IpUtil.clientIp(request);
        riskService.checkIp(ip);
        riskService.checkOtpSend(ip, req.target());
        // 解析场景（注册/登录）
        var scene = req.scene() == null ? "register" : req.scene().trim().toLowerCase();
        if ("email".equalsIgnoreCase(req.channel())) {
            var user = authService.ensureUserForOtp(req.channel(), req.target());
            // 注册场景下禁止给已注册邮箱发送注册验证码
            if ("register".equals(scene) && user != null) {
                throw new BizException(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_REGISTERED", "邮箱已注册，请直接登录");
            }
        }
        // 发送 OTP
        var result = otpService.send(req.channel(), req.target());
        return ApiResponse.ok(new OtpSendResponse(result.expiresInSeconds()));
    }

    @PostMapping("/otp/check")
    public ApiResponse<Void> otpCheck(@Valid @RequestBody OtpCheckRequest req, HttpServletRequest request) {
        // IP 风控校验
        var ip = IpUtil.clientIp(request);
        riskService.checkIp(ip);
        try {
            // 仅校验 OTP，不消费
            otpService.check(req.channel(), req.target(), req.code());
        } catch (IllegalArgumentException ex) {
            // OTP 校验失败计入风控
            riskService.recordOtpFailure(ip);
            throw new BizException(HttpStatus.BAD_REQUEST, "OTP_INVALID", "邮箱验证码错误或已过期");
        }
        return ApiResponse.ok(null);
    }

    @PostMapping("/login/otp")
    public ApiResponse<AuthResponse> loginOtp(@Valid @RequestBody OtpLoginRequest req, HttpServletRequest request, HttpServletResponse response) {
        // IP 风控校验
        var ip = IpUtil.clientIp(request);
        riskService.checkIp(ip);
        // 读取可选图形验证码
        var captchaId = req.captchaId();
        var captchaCode = req.captchaCode();
        if (captchaId != null && !captchaId.trim().isBlank() && captchaCode != null && !captchaCode.trim().isBlank()) {
            // 图形验证码校验
            if (!captchaService.verify(captchaId, captchaCode, ip)) {
                riskService.recordCaptchaFailure(ip);
                throw new BizException(HttpStatus.BAD_REQUEST, "CAPTCHA_INVALID", "验证码错误");
            }
        }
        try {
            // 校验 OTP 并消费
            otpService.verify(req.channel(), req.target(), req.code());
        } catch (IllegalArgumentException ex) {
            // OTP 校验失败计入风控
            riskService.recordOtpFailure(ip);
            throw new BizException(HttpStatus.BAD_REQUEST, "OTP_INVALID", "邮箱验证码错误或已过期");
        }
        // 获取或创建用户（未注册邮箱自动注册）
        var user = authService.ensureUserForOtp(req.channel(), req.target());
        if (user == null) {
            user = authService.registerByEmailOtp(req.target());
        }
        return ApiResponse.ok(issueAndSetCookie(user, response));
    }

    @GetMapping("/wechat/qr")
    public ApiResponse<WechatQrResponse> wechatQr() {
        // 生成微信登录二维码
        var qr = wechatLoginService.createQr();
        return ApiResponse.ok(new WechatQrResponse(qr.state(), qr.qrContent(), qr.qrImage(), qr.expiresInSeconds()));
    }

    @GetMapping("/wechat/poll")
    public ApiResponse<WechatPollResponse> wechatPoll(@RequestParam("state") String state, HttpServletResponse response) {
        // 轮询微信扫码状态
        var poll = wechatLoginService.poll(state);
        // 若已登录且带刷新令牌，则写入 Cookie
        if ("authed".equals(poll.status()) && poll.refreshToken() != null && !poll.refreshToken().isBlank()) {
            setRefreshCookie(response, poll.refreshToken(), refreshCookieMaxAgeSeconds);
        }
        return ApiResponse.ok(new WechatPollResponse(poll.status(), poll.token()));
    }

    @PostMapping("/wechat/register")
    public ApiResponse<AuthResponse> wechatRegister(@Valid @RequestBody WechatRegisterRequest req, HttpServletRequest request, HttpServletResponse response) {
        // IP 风控校验
        var ip = IpUtil.clientIp(request);
        riskService.checkIp(ip);
        // 图形验证码校验
        if (!captchaService.verify(req.captchaId(), req.captchaCode(), ip)) {
            riskService.recordCaptchaFailure(ip);
            throw new BizException(HttpStatus.BAD_REQUEST, "CAPTCHA_INVALID", "验证码错误");
        }
        var email = req.email();
        if (email != null && !email.trim().isBlank()) {
            var emailCode = req.emailCode();
            // 绑定邮箱时需要邮箱验证码
            if (emailCode == null || emailCode.trim().isBlank()) {
                throw new BizException(HttpStatus.BAD_REQUEST, "OTP_REQUIRED", "请填写邮箱验证码");
            }
            try {
                // 校验邮箱 OTP
                otpService.verify("email", email, emailCode);
            } catch (IllegalArgumentException ex) {
                // OTP 校验失败计入风控
                riskService.recordOtpFailure(ip);
                throw new BizException(HttpStatus.BAD_REQUEST, "OTP_INVALID", "邮箱验证码错误或已过期");
            }
        }
        // 校验扫码状态与获取 openid
        var openid = wechatLoginService.getOpenidForState(req.state());
        if (openid == null || openid.isBlank()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "WECHAT_NOT_CONFIRMED", "请先扫码确认");
        }
        // 注册微信用户并签发 token
        SysUser user = authService.registerWechatUser(openid, req.username(), req.email(), req.phone(), req.password());
        var accessToken = authService.issueToken(user);
        var refreshToken = authService.issueRefreshToken(user);
        // 写入刷新令牌 Cookie
        setRefreshCookie(response, refreshToken, refreshCookieMaxAgeSeconds);
        // 通知扫码登录完成
        wechatLoginService.confirm(req.state(), accessToken, refreshToken);
        return ApiResponse.ok(new AuthResponse(accessToken, false));
    }

    @PostMapping("/wechat/bind")
    public ApiResponse<AuthResponse> wechatBind(@Valid @RequestBody WechatBindRequest req, HttpServletRequest request, HttpServletResponse response) {
        // IP 风控校验
        var ip = IpUtil.clientIp(request);
        riskService.checkIp(ip);
        // 图形验证码校验
        if (!captchaService.verify(req.captchaId(), req.captchaCode(), ip)) {
            riskService.recordCaptchaFailure(ip);
            throw new BizException(HttpStatus.BAD_REQUEST, "CAPTCHA_INVALID", "验证码错误");
        }
        // 校验扫码状态与获取 openid
        var openid = wechatLoginService.getOpenidForState(req.state());
        if (openid == null || openid.isBlank()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "WECHAT_NOT_CONFIRMED", "请先扫码确认");
        }
        // 绑定已有账号并签发 token
        SysUser user = authService.bindWechatUser(openid, req.identifier(), req.password());
        var accessToken = authService.issueToken(user);
        var refreshToken = authService.issueRefreshToken(user);
        // 写入刷新令牌 Cookie
        setRefreshCookie(response, refreshToken, refreshCookieMaxAgeSeconds);
        // 通知扫码登录完成
        wechatLoginService.confirm(req.state(), accessToken, refreshToken);
        return ApiResponse.ok(new AuthResponse(accessToken, false));
    }

    @PostMapping("/wechat/dev/confirm")
    public ApiResponse<AuthResponse> wechatDevConfirm(@Valid @RequestBody WechatDevConfirmRequest req, HttpServletResponse response) {
        // 开发环境用：通过用户名直接确认登录
        SysUser user = authService.ensureUserByUsername(req.username());
        var accessToken = authService.issueToken(user);
        var refreshToken = authService.issueRefreshToken(user);
        // 写入刷新令牌 Cookie 并通知确认
        setRefreshCookie(response, refreshToken, refreshCookieMaxAgeSeconds);
        wechatLoginService.confirm(req.state(), accessToken, refreshToken);
        return ApiResponse.ok(new AuthResponse(accessToken, false));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        // 从 Cookie 读取刷新令牌
        var refreshToken = readCookie(request, refreshCookieName);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BizException(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_MISSING", "未登录");
        }
        try {
            // 解析刷新令牌并签发新 token
            var principal = jwtService.parseRefreshToken(refreshToken);
            var user = authService.ensureUserById(principal.userId());
            return ApiResponse.ok(issueAndSetCookie(user, response));
        } catch (BizException ex) {
            // 业务异常时清理 Cookie
            clearRefreshCookie(response);
            throw ex;
        } catch (Exception ex) {
            // 解析失败时清理 Cookie 并返回未登录
            clearRefreshCookie(response);
            throw new BizException(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_INVALID", "未登录");
        }
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        // 退出登录：清除刷新令牌 Cookie
        clearRefreshCookie(response);
        return ApiResponse.ok(null);
    }

    private AuthResponse issueAndSetCookie(SysUser user, HttpServletResponse response) {
        // 注册未完成时提示前端补全信息
        return issueAndSetCookie(user, response, !user.isRegistrationCompleted());
    }

    private AuthResponse issueAndSetCookie(SysUser user, HttpServletResponse response, boolean needsRegister) {
        // 签发 access/refresh token
        var accessToken = authService.issueToken(user);
        var refreshToken = authService.issueRefreshToken(user);
        // 写入刷新令牌 Cookie
        setRefreshCookie(response, refreshToken, refreshCookieMaxAgeSeconds);
        return new AuthResponse(accessToken, needsRegister);
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken, long maxAgeSeconds) {
        // 构建 HttpOnly Cookie，避免前端脚本读取
        var cookie = ResponseCookie.from(refreshCookieName, refreshToken)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(Math.max(0, maxAgeSeconds))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        // 设置过期 Cookie 清理刷新令牌
        var cookie = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String readCookie(HttpServletRequest request, String name) {
        // 从请求中读取指定 Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return null;
        for (Cookie c : cookies) {
            if (c == null) continue;
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
