/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/converter/base64/service/impl/Base64ServiceImpl.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-tools
 * 分层：service
 * 类型：class Base64ServiceImpl
 * 注解：Service、Override
 * 依赖：com.example.smarttools.modules.tools.feature.converter.base64.service.IBase64Service、org.springframework.stereotype.Service、java.nio.charset.StandardCharsets、java.util.Base64
 * 公开方法：encode(String text)；decode(String base64)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.converter.base64.service.impl;

import com.example.smarttools.modules.tools.feature.converter.base64.service.IBase64Service;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64 编解码服务实现。
 *
 * 设计说明：
 * 
 *   统一使用 UTF-8 编码进行字符串/字节数组转换，避免平台默认编码差异导致乱码。
 *   解码失败时抛出 {@link IllegalArgumentException}，由全局异常处理器统一转换为 400。
 * 
 */
@Service
public class Base64ServiceImpl implements IBase64Service {
    @Override
    public String encode(String text) {
        var bytes = text == null ? new byte[0] : text.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public String decode(String base64) {
        try {
            var bytes = Base64.getDecoder().decode(base64 == null ? "" : base64.trim());
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid base64");
        }
    }
}
