/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/converter/hash/controller/HashTextController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class HashTextController
 * 注解：RestController、RequestMapping、PostMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.modules.tools.feature.converter.hash.domain.dto.HashTextRequest、com.example.smarttools.modules.tools.feature.converter.hash.domain.dto.HashTextResponse、com.example.smarttools.modules.tools.feature.converter.hash.service.HashTextService、jakarta.validation.Valid、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.RequestBody、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RestController
 * 公开方法：calculate(@Valid @RequestBody HashTextRequest req)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.converter.hash.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.modules.tools.feature.converter.hash.domain.dto.HashTextRequest;
import com.example.smarttools.modules.tools.feature.converter.hash.domain.dto.HashTextResponse;
import com.example.smarttools.modules.tools.feature.converter.hash.service.HashTextService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tools/hash-text")
public class HashTextController {

    private final HashTextService hashTextService;

    public HashTextController(HashTextService hashTextService) {
        this.hashTextService = hashTextService;
    }

    @PostMapping("/calculate")
    public ApiResponse<HashTextResponse> calculate(@Valid @RequestBody HashTextRequest req) {
        String result = hashTextService.hash(req.text(), req.algorithm());
        return ApiResponse.ok(new HashTextResponse(req.algorithm(), result));
    }
}
