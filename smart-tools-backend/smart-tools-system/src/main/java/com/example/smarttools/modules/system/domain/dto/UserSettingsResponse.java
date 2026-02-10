/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/domain/dto/UserSettingsResponse.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-system
 * 分层：domain
 * 类型：record UserSettingsResponse
 * 依赖：com.fasterxml.jackson.databind.JsonNode
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 用户设置返回结构。
 */
package com.example.smarttools.modules.system.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record UserSettingsResponse(
        // 设置项 JSON
        JsonNode preferences,
        // 邮箱
        String email,
        // 手机号
        String phone,
        // 是否绑定微信
        boolean wechatBound
) {
}
