/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/codegen/controller/CodeGeneratorController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class CodeGeneratorController
 * 注解：RestController、RequestMapping、PostMapping
 * 依赖：com.example.smarttools.modules.tools.feature.codegen.service.CodeGeneratorService、org.springframework.http.HttpHeaders、org.springframework.http.MediaType、org.springframework.http.ResponseEntity、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.RequestBody、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RestController、java.io.IOException、java.util.List
 * 公开方法：generate(@RequestBody GenerateRequest request)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.codegen.controller;

import com.example.smarttools.modules.tools.feature.codegen.service.CodeGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tools/codegen")
public class CodeGeneratorController {

    private final CodeGeneratorService codeGeneratorService;

    public CodeGeneratorController(CodeGeneratorService codeGeneratorService) {
        this.codeGeneratorService = codeGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generate(@RequestBody GenerateRequest request) throws IOException {
        byte[] zipBytes = codeGeneratorService.generateProject(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + request.artifactId() + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }

    public record GenerateRequest(
        String groupId,
        String artifactId,
        String packageName,
        String javaVersion,
        List<String> dependencies,
        String entityName,
        List<FieldSpec> fields
    ) {}

    public record FieldSpec(String name, String type) {}
}
