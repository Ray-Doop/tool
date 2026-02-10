/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/IAuthService.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：interface IAuthService
 * 依赖：com.example.smarttools.domain.user.SysUser
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 认证服务接口。
 */
package com.example.smarttools.modules.auth.service;

import com.example.smarttools.domain.user.SysUser;

public interface IAuthService {
    // 账号密码注册
    SysUser register(String username, String password);

    // 邮箱注册
    SysUser registerByEmail(String email, String password, String username);

    // 手机注册
    SysUser registerByPhone(String phone, String password, String username);

    // 用户名密码登录
    SysUser login(String username, String password);

    // 用户名/邮箱/手机号登录
    SysUser loginByIdentifier(String identifier, String password);

    // 根据 OTP 场景查找用户
    SysUser ensureUserForOtp(String channel, String target);

    // 邮箱 OTP 自动注册
    SysUser registerByEmailOtp(String email);

    // 签发访问令牌
    String issueToken(SysUser user);

    // 签发刷新令牌
    String issueRefreshToken(SysUser user);

    // 根据 ID 校验并返回用户
    SysUser ensureUserById(Long userId);

    // 根据用户名校验并返回用户
    SysUser ensureUserByUsername(String username);

    // 根据微信 openid 查找或创建用户
    SysUser ensureUserForWechatOpenid(String openid);

    // 根据微信 openid 查询用户
    SysUser findUserByWechatOpenid(String openid);

    // 微信注册并绑定信息
    SysUser registerWechatUser(String openid, String username, String email, String phone, String password);

    SysUser bindWechatUser(String openid, String identifier, String password);
}
