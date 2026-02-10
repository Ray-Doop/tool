/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/service/wechat/SmartToolsWeixinLoginCallback.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-auth
 * 分层：service
 * 类型：class SmartToolsWeixinLoginCallback
 * 注解：Component、Override
 * 依赖：com.tofries.wxlogin.callback.WeixinLoginCallback、org.springframework.stereotype.Component
 * 公开方法：onLoginSuccess(String sceneId, String openid)；onSubscribeSuccess(String sceneId, String openid)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 微信扫码回调处理。
 */
package com.example.smarttools.modules.auth.service.wechat;

import com.tofries.wxlogin.callback.WeixinLoginCallback;
import org.springframework.stereotype.Component;

@Component
public class SmartToolsWeixinLoginCallback implements WeixinLoginCallback {
    // 微信登录服务
    private final WechatLoginService wechatLoginService;

    public SmartToolsWeixinLoginCallback(WechatLoginService wechatLoginService) {
        // 注入微信登录服务
        this.wechatLoginService = wechatLoginService;
    }

    @Override
    public String onLoginSuccess(String sceneId, String openid) {
        // 扫码登录成功回调
        System.out.println("WechatCallback: onLoginSuccess sceneId=" + sceneId + ", openid=" + openid);
        wechatLoginService.notifyScan(sceneId, openid);
        return "登录成功，请返回网页继续使用。";
    }

    @Override
    public String onSubscribeSuccess(String sceneId, String openid) {
        // 关注后登录成功回调
        System.out.println("WechatCallback: onSubscribeSuccess sceneId=" + sceneId + ", openid=" + openid);
        wechatLoginService.notifyScan(sceneId, openid);
        return "关注成功，登录已完成，请返回网页继续使用。";
    }
}
