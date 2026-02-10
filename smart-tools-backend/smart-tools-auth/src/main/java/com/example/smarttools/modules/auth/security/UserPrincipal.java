/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/security/UserPrincipal.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：security
 * 类型：record UserPrincipal
 * 依赖：java.util.List
 * 公开方法：roles()
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 登录用户主体信息（写入 SecurityContext）。
 */
package com.example.smarttools.modules.auth.security;

import java.util.List;

public record UserPrincipal(Long userId, String username) {
    // 角色列表，用于 Spring Security 授权
    public List<String> roles() {
        // 当前系统仅提供基础 USER 角色
        return List.of("USER");
    }
}
