/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/id/ulid/controller/UlidGeneratorController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class UlidGeneratorController
 * 注解：RestController、RequestMapping、GetMapping
 * 依赖：com.example.smarttools.common.ApiResponse、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RequestParam、org.springframework.web.bind.annotation.RestController、java.util.ArrayList、java.util.List、java.util.Random
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.id.ulid.controller;

import com.example.smarttools.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/tools/ulid-generator")
public class UlidGeneratorController {

    // Simple ULID implementation characters
    private static final char[] CROCKFORD_BASE32 = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private final Random random = new Random();

    @GetMapping("/generate")
    public ApiResponse<List<String>> generate(@RequestParam(name = "count", defaultValue = "1") int count) {
        int limit = Math.max(1, Math.min(count, 100));
        List<String> result = new ArrayList<>(limit);
        for (int i = 0; i < limit; i++) {
            result.add(generateUlid());
        }
        return ApiResponse.ok(result);
    }

    private String generateUlid() {
        long now = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(26);
        
        // Timestamp (48 bits)
        for (int i = 9; i >= 0; i--) {
            sb.append(CROCKFORD_BASE32[(int) ((now >>> (i * 5)) & 31)]);
        }
        
        // Randomness (80 bits) - 16 chars
        for (int i = 0; i < 16; i++) {
            sb.append(CROCKFORD_BASE32[random.nextInt(32)]);
        }
        
        return sb.toString();
    }
}
