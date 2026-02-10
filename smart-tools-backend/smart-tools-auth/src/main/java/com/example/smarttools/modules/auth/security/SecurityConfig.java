/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/security/SecurityConfig.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：security
 * 类型：class SecurityConfig
 * 注解：Configuration、EnableWebSecurity、Value、Bean
 * 依赖：org.springframework.beans.factory.annotation.Value、org.springframework.context.annotation.Bean、org.springframework.context.annotation.Configuration、org.springframework.http.HttpMethod、org.springframework.security.config.Customizer、org.springframework.security.config.annotation.web.builders.HttpSecurity、org.springframework.security.config.annotation.web.configuration.EnableWebSecurity、org.springframework.security.config.http.SessionCreationPolicy、org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder、org.springframework.security.crypto.password.PasswordEncoder、org.springframework.security.web.SecurityFilterChain、org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter、org.springframework.web.cors.CorsConfiguration、org.springframework.web.cors.CorsConfigurationSource
 * 公开方法：passwordEncoder()；corsConfigurationSource()；securityFilterChain(HttpSecurity http)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.auth.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * 安全配置（Spring Security）。
 *
 * 目标：
 * 
 *   无状态：使用 JWT 鉴权，不使用服务端 Session。
 *   统一跨域：允许前端域名调用 API（origin 由配置决定）。
 *   限流防刷：在鉴权前对敏感接口做基础限流。
 * 
 *
 * 请求处理链（按顺序）：
 * 
 *   {@link GlobalRateLimitFilter}：全局限流（IP/设备码维度）。
 *   {@link RateLimitFilter}：认证接口限流（登录/验证码接口防刷）。
 *   {@link JwtAuthenticationFilter}：解析 token，将用户身份写入 SecurityContext。
 * 
 *
 * 放行策略：
 * 
 *   /api/auth/**：登录/验证码/OTP 等必须匿名可访问。
 *   /api/tools/**：工具能力通常面向游客开放（示例：base64/uuid）。
 *   /api/monitor/**（GET）：探活接口通常对外开放或给网关健康检查使用。
 * 
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // JWT 解析过滤器
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // 认证接口基础限流过滤器
    private final RateLimitFilter rateLimitFilter;
    // 全局 IP/设备限流过滤器
    private final GlobalRateLimitFilter globalRateLimitFilter;
    // 允许跨域的前端域名列表
    private final List<String> allowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RateLimitFilter rateLimitFilter,
            GlobalRateLimitFilter globalRateLimitFilter,
            @Value("${app.cors.allowed-origins}") String allowedOrigins
    ) {
        // 构造函数负责注入过滤器与跨域配置
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimitFilter = rateLimitFilter;
        this.globalRateLimitFilter = globalRateLimitFilter;
        // 允许跨域域名按逗号分隔
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 BCrypt 作为密码哈希算法
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // CORS 规则配置：允许前端域名跨域请求
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(allowedOrigins);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        // 应用到全路径
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 配置安全过滤链与授权规则
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 认证相关接口匿名可访问
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/wxverify").permitAll()
                        .requestMatchers("/wxlogin/**").permitAll()
                        .requestMatchers("/api/tools/**").permitAll()
                        // 后台管理接口仅限 ADMIN 角色
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 健康检查与访问日志接口匿名可访问
                        .requestMatchers(HttpMethod.GET, "/api/monitor/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/logs/visit").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/logs/error").permitAll()
                        // 本地控制台与上传资源访问
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        // 其余接口必须鉴权
                        .anyRequest().authenticated()
                )
                // 允许同源 iframe（H2 控制台需要）
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // 未登录统一返回 JSON
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":false,\"data\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"未登录或登录已过期\"}}");
                }))
                // 限流过滤器优先于鉴权过滤器执行
                .addFilterBefore(globalRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                // JWT 鉴权过滤器在用户名密码过滤器前执行
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
