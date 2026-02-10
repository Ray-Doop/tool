/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/converter/base/domain/dto/BaseConvertRequest.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-tools
 * 分层：domain
 * 类型：record BaseConvertRequest
 * 注解：NotBlank、Min
 * 依赖：jakarta.validation.constraints.Max、jakarta.validation.constraints.Min、jakarta.validation.constraints.NotBlank
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.converter.base.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BaseConvertRequest(
        @NotBlank(message = "Value cannot be empty")
        String value,
        
        @Min(2) @Max(36)
        int fromBase,
        
        @Min(2) @Max(36)
        int toBase
) {}
