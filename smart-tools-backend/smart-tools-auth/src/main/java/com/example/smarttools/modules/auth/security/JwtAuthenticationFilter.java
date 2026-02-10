/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/security/JwtAuthenticationFilter.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：security
 * 类型：class JwtAuthenticationFilter
 * 注解：Component、Override
 * 依赖：jakarta.servlet.FilterChain、jakarta.servlet.ServletException、jakarta.servlet.http.HttpServletRequest、jakarta.servlet.http.HttpServletResponse、org.springframework.http.HttpHeaders、com.example.smarttools.domain.admin.SysAdminRepository、org.springframework.security.authentication.UsernamePasswordAuthenticationToken、org.springframework.security.core.authority.SimpleGrantedAuthority、org.springframework.security.core.context.SecurityContextHolder、org.springframework.stereotype.Component、org.springframework.web.filter.OncePerRequestFilter、java.io.IOException、java.util.stream.Collectors
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import com.example.smarttools.domain.admin.SysAdminRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * JWT 鉴权过滤器：从 Authorization 解析 token 并写入 SecurityContext。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // JWT 解析与签发服务
    private final JwtService jwtService;
    // 管理员判定仓储
    private final SysAdminRepository adminRepository;

    public JwtAuthenticationFilter(JwtService jwtService, SysAdminRepository adminRepository) {
        // 注入 JWT 服务
        this.jwtService = jwtService;
        this.adminRepository = adminRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 读取 Authorization 头部
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        // Bearer 前缀的 token 才进行解析
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring("Bearer ".length());
            try {
                // 解析 token 得到用户主体
                var principal = jwtService.parseToken(token);
                // 计算角色：基础 USER + 启用的管理员追加 ADMIN
                java.util.List<String> roles = new java.util.ArrayList<>(principal.roles());
                try {
                    if (adminRepository.existsByUserIdAndEnabledTrue(principal.userId())) {
                        roles.add("ADMIN");
                    }
                } catch (Exception ignore) {
                }
                // 将角色映射为 Spring Security 权限
                var authorities = roles.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toList());
                // 构造认证对象写入上下文
                var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ignored) {
                // token 解析失败则清空上下文，交由后续鉴权处理
                SecurityContextHolder.clearContext();
            }
        }
        // 继续执行过滤链
        filterChain.doFilter(request, response);
    }
}
