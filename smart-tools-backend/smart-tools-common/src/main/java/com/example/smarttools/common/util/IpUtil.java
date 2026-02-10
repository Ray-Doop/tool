/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-common/src/main/java/com/example/smarttools/common/util/IpUtil.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-common
 * 分层：util
 * 类型：class IpUtil
 * 依赖：jakarta.servlet.http.HttpServletRequest
 * 公开方法：clientIp(HttpServletRequest request)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.common.util;

import jakarta.servlet.http.HttpServletRequest;

public final class IpUtil {
    private IpUtil() {
    }

    public static String clientIp(HttpServletRequest request) {
        if (request == null) return "";
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            var first = xff.split(",")[0].trim();
            if (!first.isBlank()) return first;
        }
        var xrip = request.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) return xrip.trim();
        var addr = request.getRemoteAddr();
        return addr == null ? "" : addr;
    }
}

