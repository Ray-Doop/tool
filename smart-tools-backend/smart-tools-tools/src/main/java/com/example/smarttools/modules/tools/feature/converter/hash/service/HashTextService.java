/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/converter/hash/service/HashTextService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-tools
 * 分层：service
 * 类型：class HashTextService
 * 注解：Service
 * 依赖：com.example.smarttools.common.exception.BizException、org.springframework.http.HttpStatus、org.springframework.stereotype.Service、java.nio.charset.StandardCharsets、java.security.MessageDigest、java.security.NoSuchAlgorithmException、java.util.HexFormat
 * 公开方法：hash(String text, String algorithm)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.converter.hash.service;

import com.example.smarttools.common.exception.BizException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class HashTextService {

    public String hash(String text, String algorithm) {
        try {
            // Map common names to Java standard names if needed
            String algo = resolveAlgorithm(algorithm);
            MessageDigest digest = MessageDigest.getInstance(algo);
            byte[] encodedhash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new BizException(HttpStatus.BAD_REQUEST, "INVALID_ALGORITHM", "不支持的哈希算法: " + algorithm);
        }
    }

    private String resolveAlgorithm(String input) {
        if (input == null) return "SHA-256";
        String upper = input.toUpperCase().replace("-", "");
        return switch (upper) {
            case "MD5" -> "MD5";
            case "SHA1" -> "SHA-1";
            case "SHA256" -> "SHA-256";
            case "SHA512" -> "SHA-512";
            case "SHA384" -> "SHA-384";
            default -> input; // Try as is
        };
    }
}
