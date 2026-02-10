/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/domain/dto/UpdateSettingsRequest.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-system
 * 分层：domain
 * 类型：record UpdateSettingsRequest
 * 注解：NotNull
 * 依赖：com.fasterxml.jackson.databind.JsonNode、jakarta.validation.constraints.NotNull
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 更新设置请求。
 */
package com.example.smarttools.modules.system.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;

public record UpdateSettingsRequest(
        // 设置项 JSON
        @NotNull JsonNode preferences
) {
}
