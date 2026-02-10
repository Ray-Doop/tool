/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/domain/dto/VisitLogRequest.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-system
 * 分层：domain
 * 类型：record VisitLogRequest
 * 注解：Size
 * 依赖：jakarta.validation.constraints.Size
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 访问日志上报请求。
 */
package com.example.smarttools.modules.system.domain.dto;

import jakarta.validation.constraints.Size;

public record VisitLogRequest(
        // 工具标识
        @Size(max = 64) String toolSlug,
        // 访问路径
        @Size(max = 256) String path
) {
}
