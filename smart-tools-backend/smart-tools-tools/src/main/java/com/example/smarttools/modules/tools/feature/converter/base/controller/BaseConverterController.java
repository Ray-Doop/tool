/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/converter/base/controller/BaseConverterController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class BaseConverterController
 * 注解：RestController、RequestMapping、PostMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.modules.tools.feature.converter.base.domain.dto.BaseConvertRequest、com.example.smarttools.modules.tools.feature.converter.base.domain.dto.BaseConvertResponse、com.example.smarttools.modules.tools.feature.converter.base.service.BaseConverterService、jakarta.validation.Valid、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.RequestBody、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RestController
 * 公开方法：convert(@Valid @RequestBody BaseConvertRequest req)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.converter.base.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.modules.tools.feature.converter.base.domain.dto.BaseConvertRequest;
import com.example.smarttools.modules.tools.feature.converter.base.domain.dto.BaseConvertResponse;
import com.example.smarttools.modules.tools.feature.converter.base.service.BaseConverterService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tools/base-converter")
public class BaseConverterController {

    private final BaseConverterService baseConverterService;

    public BaseConverterController(BaseConverterService baseConverterService) {
        this.baseConverterService = baseConverterService;
    }

    @PostMapping("/convert")
    public ApiResponse<BaseConvertResponse> convert(@Valid @RequestBody BaseConvertRequest req) {
        String result = baseConverterService.convert(req.value(), req.fromBase(), req.toBase());
        return ApiResponse.ok(new BaseConvertResponse(req.value(), req.fromBase(), req.toBase(), result));
    }
}
