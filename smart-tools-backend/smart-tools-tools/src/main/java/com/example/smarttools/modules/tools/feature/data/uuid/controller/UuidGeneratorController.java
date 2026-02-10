/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/data/uuid/controller/UuidGeneratorController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class UuidGeneratorController
 * 注解：RestController、RequestMapping、GetMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.modules.tools.feature.data.uuid.domain.dto.UuidGenerateResponse、com.example.smarttools.modules.tools.feature.data.uuid.service.IUuidGeneratorService、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RequestParam、org.springframework.web.bind.annotation.RestController
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.data.uuid.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.modules.tools.feature.data.uuid.domain.dto.UuidGenerateResponse;
import com.example.smarttools.modules.tools.feature.data.uuid.service.IUuidGeneratorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * UUID 生成器工具接口。
 *
 * 用途：生成随机 UUIDv4 字符串，常用于唯一标识、测试数据等。
 *
 * 参数说明：
 * 
 *   count：一次返回的 UUID 数量；服务端会在实现层限制上限，避免滥用。
 * 
 */
@RestController
@RequestMapping("/api/tools/uuid-generator")
public class UuidGeneratorController {
    private final IUuidGeneratorService uuidGeneratorService;

    public UuidGeneratorController(IUuidGeneratorService uuidGeneratorService) {
        this.uuidGeneratorService = uuidGeneratorService;
    }

    @GetMapping("/generate")
    public ApiResponse<UuidGenerateResponse> generate(@RequestParam(name = "count", defaultValue = "1") int count) {
        return ApiResponse.ok(new UuidGenerateResponse(uuidGeneratorService.generate(count)));
    }
}
