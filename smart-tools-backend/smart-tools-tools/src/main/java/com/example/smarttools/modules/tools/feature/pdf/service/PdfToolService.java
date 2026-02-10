/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/pdf/service/PdfToolService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-tools
 * 分层：service
 * 类型：class PdfToolService
 * 注解：Service
 * 依赖：org.apache.pdfbox.multipdf.PDFMergerUtility、org.apache.pdfbox.pdmodel.PDDocument、org.apache.pdfbox.pdmodel.PDDocumentInformation、org.apache.pdfbox.pdmodel.PDPage、org.apache.pdfbox.pdmodel.common.PDRectangle、org.apache.pdfbox.pdmodel.encryption.AccessPermission、org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy、org.apache.pdfbox.pdmodel.font.PDType1Font、org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState、org.apache.pdfbox.pdmodel.PDPageContentStream、org.apache.pdfbox.util.Matrix、org.springframework.stereotype.Service、org.springframework.web.multipart.MultipartFile、java.io.ByteArrayOutputStream
 * 公开方法：merge(MultipartFile[] files)；split(MultipartFile file, String mode, String pages)；compress(MultipartFile file)；watermark(MultipartFile file, String text, int fontSize, float opacity, float rotation)；addPageNumbers(MultipartFile file, int startNumber, int fontSize)；keepPages(MultipartFile file, String pages)；encrypt(MultipartFile file, String userPassword, String ownerPassword)；decrypt(MultipartFile file, String password)；readMetadata(MultipartFile file, String password)；updateMetadata(MultipartFile file, String password, PdfMetadata meta)；addSignatureText(MultipartFile file, String signer, String reason, String location)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.pdf.service;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfToolService {

    /**
     * 合并多个 PDF
     */
    public byte[] merge(MultipartFile[] files) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        merger.setDestinationStream(baos);
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            merger.addSource(file.getInputStream());
        }
        merger.mergeDocuments(null);
        return baos.toByteArray();
    }

    /**
     * PDF 拆分，按页打包为 ZIP
     */
    public byte[] split(MultipartFile file, String mode, String pages) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            List<Integer> targets = "range".equalsIgnoreCase(mode)
                    ? parsePageRanges(pages, document.getNumberOfPages())
                    : allPages(document.getNumberOfPages());
            return splitToZip(document, targets);
        }
    }

    /**
     * PDF 压缩（重新保存）
     */
    public byte[] compress(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * 添加文字水印
     */
    public byte[] watermark(MultipartFile file, String text, int fontSize, float opacity, float rotation) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (PDPage page : document.getPages()) {
                PDRectangle rect = page.getMediaBox();
                PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
                PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
                gs.setNonStrokingAlphaConstant(opacity);
                cs.setGraphicsStateParameters(gs);
                cs.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                float centerX = rect.getWidth() / 2;
                float centerY = rect.getHeight() / 2;
                cs.beginText();
                cs.setTextMatrix(Matrix.getRotateInstance(Math.toRadians(rotation), centerX, centerY));
                cs.showText(text);
                cs.endText();
                cs.close();
            }
            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * 添加页码
     */
    public byte[] addPageNumbers(MultipartFile file, int startNumber, int fontSize) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int num = document.getNumberOfPages();
            for (int i = 0; i < num; i += 1) {
                PDPage page = document.getPage(i);
                PDRectangle rect = page.getMediaBox();
                PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
                cs.setFont(PDType1Font.HELVETICA, fontSize);
                String text = String.valueOf(startNumber + i);
                float x = rect.getWidth() / 2 - (text.length() * fontSize / 4f);
                float y = 20;
                cs.beginText();
                cs.newLineAtOffset(x, y);
                cs.showText(text);
                cs.endText();
                cs.close();
            }
            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * 按页码保留页面
     */
    public byte[] keepPages(MultipartFile file, String pages) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream());
             PDDocument out = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            List<Integer> targets = parsePageRanges(pages, document.getNumberOfPages());
            for (int index : targets) {
                if (index < 1 || index > document.getNumberOfPages()) continue;
                out.importPage(document.getPage(index - 1));
            }
            out.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * PDF 加密
     */
    public byte[] encrypt(MultipartFile file, String userPassword, String ownerPassword) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            AccessPermission ap = new AccessPermission();
            StandardProtectionPolicy policy = new StandardProtectionPolicy(
                    ownerPassword == null ? "" : ownerPassword,
                    userPassword == null ? "" : userPassword,
                    ap
            );
            policy.setEncryptionKeyLength(128);
            document.protect(policy);
            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * PDF 解密
     */
    public byte[] decrypt(MultipartFile file, String password) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream(), password);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            document.setAllSecurityToBeRemoved(true);
            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * 读取 PDF 元数据
     */
    public PdfMetadata readMetadata(MultipartFile file, String password) throws IOException {
        try (PDDocument document = password == null || password.isBlank()
                ? PDDocument.load(file.getInputStream())
                : PDDocument.load(file.getInputStream(), password)) {
            PDDocumentInformation info = document.getDocumentInformation();
            return new PdfMetadata(info.getTitle(), info.getAuthor(), info.getSubject(), info.getKeywords());
        }
    }

    /**
     * 更新 PDF 元数据
     */
    public byte[] updateMetadata(MultipartFile file, String password, PdfMetadata meta) throws IOException {
        try (PDDocument document = password == null || password.isBlank()
                ? PDDocument.load(file.getInputStream())
                : PDDocument.load(file.getInputStream(), password);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDDocumentInformation info = document.getDocumentInformation();
            if (meta.title() != null) info.setTitle(meta.title());
            if (meta.author() != null) info.setAuthor(meta.author());
            if (meta.subject() != null) info.setSubject(meta.subject());
            if (meta.keywords() != null) info.setKeywords(meta.keywords());
            document.setDocumentInformation(info);
            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * 添加签名文字
     */
    public byte[] addSignatureText(MultipartFile file, String signer, String reason, String location) throws IOException {
        String text = "签名: " + (signer == null ? "" : signer);
        if (reason != null && !reason.isBlank()) text += " | " + reason;
        if (location != null && !location.isBlank()) text += " | " + location;
        try (PDDocument document = PDDocument.load(file.getInputStream());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (PDPage page : document.getPages()) {
                PDRectangle rect = page.getMediaBox();
                PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                cs.beginText();
                cs.newLineAtOffset(36, rect.getHeight() - 36);
                cs.showText(text);
                cs.endText();
                cs.close();
            }
            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * 拆分后的 PDF 片段打包为 ZIP
     */
    private byte[] splitToZip(PDDocument document, List<Integer> pages) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos)) {
            for (int index : pages) {
                if (index < 1 || index > document.getNumberOfPages()) continue;
                try (PDDocument part = new PDDocument()) {
                    part.importPage(document.getPage(index - 1));
                    ByteArrayOutputStream temp = new ByteArrayOutputStream();
                    part.save(temp);
                    zos.putNextEntry(new java.util.zip.ZipEntry("page-" + index + ".pdf"));
                    zos.write(temp.toByteArray());
                    zos.closeEntry();
                }
            }
            zos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * 解析页码范围（支持 1-3,5,8-10）
     */
    private List<Integer> parsePageRanges(String pages, int total) {
        List<Integer> list = new ArrayList<>();
        if (pages == null || pages.isBlank()) return allPages(total);
        String[] parts = pages.split(",");
        for (String part : parts) {
            String p = part.trim();
            if (p.contains("-")) {
                String[] range = p.split("-");
                if (range.length == 2) {
                    int start = parseInt(range[0], 1);
                    int end = parseInt(range[1], total);
                    for (int i = start; i <= end; i += 1) list.add(i);
                }
            } else {
                int idx = parseInt(p, -1);
                if (idx > 0) list.add(idx);
            }
        }
        return list.isEmpty() ? allPages(total) : list;
    }

    /**
     * 生成全页列表
     */
    private List<Integer> allPages(int total) {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= total; i += 1) list.add(i);
        return list;
    }

    /**
     * 安全解析整数
     */
    private int parseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * PDF 元数据结构
     */
    public record PdfMetadata(String title, String author, String subject, String keywords) {}
}
