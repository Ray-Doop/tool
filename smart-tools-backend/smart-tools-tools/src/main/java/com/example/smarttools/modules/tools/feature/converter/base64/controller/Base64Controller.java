/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/converter/base64/controller/Base64Controller.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class Base64Controller
 * 注解：RestController、RequestMapping、PostMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.modules.tools.feature.converter.base64.domain.dto.Base64DecodeRequest、com.example.smarttools.modules.tools.feature.converter.base64.domain.dto.Base64EncodeRequest、com.example.smarttools.modules.tools.feature.converter.base64.domain.dto.Base64Response、com.example.smarttools.modules.tools.feature.converter.base64.service.IBase64Service、jakarta.validation.Valid、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.RequestBody、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RestController
 * 公开方法：encode(@Valid @RequestBody Base64EncodeRequest req)；decode(@Valid @RequestBody Base64DecodeRequest req)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.converter.base64.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.modules.tools.feature.converter.base64.domain.dto.Base64DecodeRequest;
import com.example.smarttools.modules.tools.feature.converter.base64.domain.dto.Base64EncodeRequest;
import com.example.smarttools.modules.tools.feature.converter.base64.domain.dto.Base64Response;
import com.example.smarttools.modules.tools.feature.converter.base64.service.IBase64Service;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Base64 编解码工具接口。
 *
 * 路径约定：/api/tools/{slug}/... 与工具 slug 保持一致（此处 slug=base64）。
 *
 * 错误处理：
 * 
 *   请求体字段为空：由 jakarta validation 触发，返回 400 + VALIDATION_ERROR。
 *   非法 base64：服务端抛 IllegalArgumentException，返回 400 + BAD_REQUEST。
 * 
 */
@RestController
@RequestMapping("/api/tools/base64")
public class Base64Controller {
    private final IBase64Service base64Service;

    public Base64Controller(IBase64Service base64Service) {
        this.base64Service = base64Service;
    }

    @PostMapping("/encode")
    public ApiResponse<Base64Response> encode(@Valid @RequestBody Base64EncodeRequest req) {
        return ApiResponse.ok(new Base64Response(base64Service.encode(req.text())));
    }

    /**
     * Base64 解码。
     *
     * @param req 请求体，base64 字段为待解码内容
     */
    @PostMapping("/decode")
    public ApiResponse<Base64Response> decode(@Valid @RequestBody Base64DecodeRequest req) {
        return ApiResponse.ok(new Base64Response(base64Service.decode(req.base64())));
    }
}
