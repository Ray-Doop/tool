/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-common/src/main/java/com/example/smarttools/common/exception/BizException.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-common
 * 分层：exception
 * 类型：class BizException
 * 依赖：org.springframework.http.HttpStatus
 * 公开方法：status()；code()
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.common.exception;

import org.springframework.http.HttpStatus;

public class BizException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public BizException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status == null ? HttpStatus.BAD_REQUEST : status;
        this.code = code == null ? "BAD_REQUEST" : code;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }
}

