/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-common/src/main/java/com/example/smarttools/common/ApiError.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-common
 * 分层：general
 * 类型：record ApiError
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.common;

/**
 * API 错误对象。
 *
 * @param code    业务错误码（用于前端区分错误类型）
 * @param message 面向用户/开发者的错误信息
 */
public record ApiError(String code, String message) {
}
