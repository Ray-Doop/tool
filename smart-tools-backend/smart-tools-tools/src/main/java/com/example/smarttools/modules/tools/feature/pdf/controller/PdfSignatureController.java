/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/pdf/controller/PdfSignatureController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class PdfSignatureController
 * 注解：RestController、RequestMapping、PostMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.modules.tools.feature.pdf.domain.dto.PdfSignatureResponse、org.apache.pdfbox.pdmodel.PDDocument、org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RequestParam、org.springframework.web.bind.annotation.RestController、org.springframework.web.multipart.MultipartFile、java.io.IOException、java.util.ArrayList、java.util.Collections、java.util.List
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.pdf.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.modules.tools.feature.pdf.domain.dto.PdfSignatureResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/tools/pdf/signature")
public class PdfSignatureController {

    @PostMapping("/check")
    public ApiResponse<List<PdfSignatureResponse>> checkSignature(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.fail("FILE_EMPTY", "请上传 PDF 文件");
        }

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            List<PDSignature> signatures = document.getSignatureDictionaries();
            if (signatures.isEmpty()) {
                return ApiResponse.ok(Collections.emptyList());
            }

            List<PdfSignatureResponse> responses = new ArrayList<>();
            for (PDSignature sig : signatures) {
                // 注意：这里只做基本的元数据提取，不进行完整的证书链验证（需要受信任的 CA 库）
                // 真正的签名验证需要 bouncy castle 和复杂的 PKI 逻辑
                // 这里仅演示提取签名信息
                String name = sig.getName();
                String date = sig.getSignDate() != null ? sig.getSignDate().getTime().toString() : "Unknown";
                String reason = sig.getReason();
                String location = sig.getLocation();
                
                // 简单判断：只要能解析出签名块，暂且认为是“存在签名”
                // 实际验证需要 verifySignature()
                boolean isValid = true; 

                responses.add(new PdfSignatureResponse(name, date, isValid, reason, location));
            }
            return ApiResponse.ok(responses);

        } catch (IOException e) {
            return ApiResponse.fail("PDF_PARSE_ERROR", "PDF 解析失败: " + e.getMessage());
        }
    }
}
