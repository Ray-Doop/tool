/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/domain/dto/LoginHistoryResponse.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-system
 * 分层：domain
 * 类型：record LoginHistoryResponse
 * 依赖：java.time.Instant
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 登录历史返回结构。
 */
package com.example.smarttools.modules.system.domain.dto;

import java.time.Instant;

public record LoginHistoryResponse(
    // 登录 IP
    String ip,
    // 用户代理
    String userAgent,
    // 登录时间
    Instant loginAt
) {}
