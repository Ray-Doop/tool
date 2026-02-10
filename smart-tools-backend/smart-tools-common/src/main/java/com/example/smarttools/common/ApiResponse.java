/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-common/src/main/java/com/example/smarttools/common/ApiResponse.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-common
 * 分层：general
 * 类型：record ApiResponse
 * 公开方法：ok(T data)；fail(String code, String message)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.common;

/**
 * 统一 API 返回体。
 *
 * 使用约定：
 * - Controller 统一返回 ApiResponse，而不是直接返回裸数据，便于前端统一处理
 * - HTTP 状态码用于表达“请求层级”的成功/失败（例如 400/403/500）
 * - ApiResponse.success 用于表达“业务层级”的成功/失败
 *
 * 例子：
 * - 200 + success=true：正常成功
 * - 400 + success=false：参数/业务校验失败（error.code/error.message 给前端展示）
 *
 * @param success 是否成功
 * @param data    成功时的数据
 * @param error   失败时的错误信息
 */
public record ApiResponse<T>(boolean success, T data, ApiError error) {
    /**
     * 构建成功响应。
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /**
     * 构建失败响应。
     */
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code, message));
    }
}
