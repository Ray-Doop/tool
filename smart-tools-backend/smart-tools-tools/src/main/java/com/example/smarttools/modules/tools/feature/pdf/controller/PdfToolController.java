/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/pdf/controller/PdfToolController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class PdfToolController
 * 注解：RestController、RequestMapping、PostMapping、RequestParam
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.modules.tools.feature.pdf.service.PdfToolService、org.springframework.http.HttpHeaders、org.springframework.http.MediaType、org.springframework.http.ResponseEntity、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RequestParam、org.springframework.web.bind.annotation.RestController、org.springframework.web.multipart.MultipartFile、java.io.IOException
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.pdf.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.modules.tools.feature.pdf.service.PdfToolService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * PDF 工具接口
 * 提供合并、拆分、压缩、水印、页码、编辑、加/解密、签名、元数据读写等能力
 * 下载类接口返回二进制流，便于浏览器直接保存
 */
@RestController
@RequestMapping("/api/tools/pdf")
public class PdfToolController {

    private final PdfToolService pdfToolService;

    public PdfToolController(PdfToolService pdfToolService) {
        this.pdfToolService = pdfToolService;
    }

    /**
     * PDF 合并
     */
    @PostMapping("/merge")
    public ResponseEntity<byte[]> merge(@RequestParam("files") MultipartFile[] files) throws IOException {
        byte[] bytes = pdfToolService.merge(files);
        return fileResponse(bytes, "merged.pdf");
    }

    /**
     * PDF 拆分
     */
    @PostMapping("/split")
    public ResponseEntity<byte[]> split(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "mode", required = false, defaultValue = "each") String mode,
            @RequestParam(value = "pages", required = false) String pages
    ) throws IOException {
        byte[] bytes = pdfToolService.split(file, mode, pages);
        return fileResponse(bytes, "pdf-pages.zip");
    }

    /**
     * PDF 压缩
     */
    @PostMapping("/compress")
    public ResponseEntity<byte[]> compress(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] bytes = pdfToolService.compress(file);
        return fileResponse(bytes, "compressed.pdf");
    }

    /**
     * PDF 水印
     */
    @PostMapping("/watermark")
    public ResponseEntity<byte[]> watermark(
            @RequestParam("file") MultipartFile file,
            @RequestParam("text") String text,
            @RequestParam(value = "fontSize", required = false, defaultValue = "36") int fontSize,
            @RequestParam(value = "opacity", required = false, defaultValue = "0.2") float opacity,
            @RequestParam(value = "rotation", required = false, defaultValue = "45") float rotation
    ) throws IOException {
        byte[] bytes = pdfToolService.watermark(file, text, fontSize, opacity, rotation);
        return fileResponse(bytes, "watermark.pdf");
    }

    /**
     * PDF 添加页码
     */
    @PostMapping("/page-number")
    public ResponseEntity<byte[]> pageNumber(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "start", required = false, defaultValue = "1") int start,
            @RequestParam(value = "fontSize", required = false, defaultValue = "12") int fontSize
    ) throws IOException {
        byte[] bytes = pdfToolService.addPageNumbers(file, start, fontSize);
        return fileResponse(bytes, "page-number.pdf");
    }

    /**
     * PDF 编辑（保留页）
     */
    @PostMapping("/edit")
    public ResponseEntity<byte[]> edit(
            @RequestParam("file") MultipartFile file,
            @RequestParam("pages") String pages
    ) throws IOException {
        byte[] bytes = pdfToolService.keepPages(file, pages);
        return fileResponse(bytes, "edited.pdf");
    }

    /**
     * PDF 加密
     */
    @PostMapping("/encrypt")
    public ResponseEntity<byte[]> encrypt(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userPassword") String userPassword,
            @RequestParam(value = "ownerPassword", required = false) String ownerPassword
    ) throws IOException {
        byte[] bytes = pdfToolService.encrypt(file, userPassword, ownerPassword);
        return fileResponse(bytes, "encrypted.pdf");
    }

    /**
     * PDF 解密
     */
    @PostMapping("/decrypt")
    public ResponseEntity<byte[]> decrypt(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password
    ) throws IOException {
        byte[] bytes = pdfToolService.decrypt(file, password);
        return fileResponse(bytes, "decrypted.pdf");
    }

    /**
     * 读取 PDF 元数据
     */
    @PostMapping("/metadata/read")
    public ApiResponse<PdfToolService.PdfMetadata> readMetadata(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "password", required = false) String password
    ) throws IOException {
        return ApiResponse.ok(pdfToolService.readMetadata(file, password));
    }

    /**
     * 更新 PDF 元数据
     */
    @PostMapping("/metadata/update")
    public ResponseEntity<byte[]> updateMetadata(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "keywords", required = false) String keywords
    ) throws IOException {
        byte[] bytes = pdfToolService.updateMetadata(file, password, new PdfToolService.PdfMetadata(title, author, subject, keywords));
        return fileResponse(bytes, "metadata.pdf");
    }

    /**
     * PDF 签名文字
     */
    @PostMapping("/sign")
    public ResponseEntity<byte[]> sign(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "signer", required = false) String signer,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "location", required = false) String location
    ) throws IOException {
        byte[] bytes = pdfToolService.addSignatureText(file, signer, reason, location);
        return fileResponse(bytes, "signed.pdf");
    }

    /**
     * PDF 盖章（复用水印逻辑）
     */
    @PostMapping("/stamp")
    public ResponseEntity<byte[]> stamp(
            @RequestParam("file") MultipartFile file,
            @RequestParam("text") String text,
            @RequestParam(value = "fontSize", required = false, defaultValue = "36") int fontSize,
            @RequestParam(value = "opacity", required = false, defaultValue = "0.2") float opacity,
            @RequestParam(value = "rotation", required = false, defaultValue = "0") float rotation
    ) throws IOException {
        byte[] bytes = pdfToolService.watermark(file, text, fontSize, opacity, rotation);
        return fileResponse(bytes, "stamp.pdf");
    }

    /**
     * 统一二进制文件下载响应
     */
    private ResponseEntity<byte[]> fileResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }
}
