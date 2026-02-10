/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/file/controller/FileToolController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-tools
 * 分层：controller
 * 类型：class FileToolController
 * 注解：RestController、RequestMapping、PostMapping、RequestParam
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.modules.tools.feature.file.service.FileToolService、org.springframework.http.HttpHeaders、org.springframework.http.MediaType、org.springframework.http.ResponseEntity、org.springframework.web.bind.annotation.PostMapping、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RequestParam、org.springframework.web.bind.annotation.RestController、org.springframework.web.multipart.MultipartFile、java.io.IOException
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.file.controller;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.modules.tools.feature.file.service.FileToolService;
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
 * 文件类工具接口
 * 提供 ZIP 压缩/解压、文本拆分、文件合并/拆分、批量重命名、
 * 编码检测与转换、CSV 数据处理、字幕格式转换、文件类型检测等能力
 * 所有下载类接口统一返回二进制流并设置 Content-Disposition 便于浏览器保存
 */
@RestController
@RequestMapping("/api/tools/file")
public class FileToolController {

    private final FileToolService fileToolService;

    public FileToolController(FileToolService fileToolService) {
        this.fileToolService = fileToolService;
    }

    /**
     * 压缩多个文件为 ZIP 包
     * @param files 多文件上传
     * @return 压缩后的 zip 二进制
     */
    @PostMapping("/zip/compress")
    public ResponseEntity<byte[]> compressZip(@RequestParam("files") MultipartFile[] files) throws IOException {
        byte[] bytes = fileToolService.zipCompress(files);
        return fileResponse(bytes, "files.zip");
    }

    /**
     * 解压上传的 ZIP，并再次打包为 ZIP 返回（便于浏览器直接下载）
     * @param file 单个 zip 文件
     * @return 解压后的内容重新打包 zip
     */
    @PostMapping("/zip/extract")
    public ResponseEntity<byte[]> extractZip(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] bytes = fileToolService.zipExtractToZip(file);
        return fileResponse(bytes, "extracted.zip");
    }

    /**
     * 文本文件拆分
     * @param file 文本文件
     * @param mode 拆分方式：lines 按行，bytes 按字节
     * @param size 拆分大小：行数或字节数
     * @return 拆分后的多个文本文件 zip
     */
    @PostMapping("/text/split")
    public ResponseEntity<byte[]> splitText(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "mode", required = false) String mode,
            @RequestParam(value = "size", required = false, defaultValue = "500") int size
    ) throws IOException {
        byte[] bytes = fileToolService.splitTextFile(file, mode, size);
        return fileResponse(bytes, "text-parts.zip");
    }

    /**
     * 原始二进制文件合并（按上传顺序）
     * @param files 多文件
     * @return 合并后的二进制
     */
    @PostMapping("/merge")
    public ResponseEntity<byte[]> mergeFiles(@RequestParam("files") MultipartFile[] files) throws IOException {
        byte[] bytes = fileToolService.mergeFiles(files);
        return fileResponse(bytes, "merged.bin");
    }

    /**
     * 按大小拆分二进制文件
     * @param file 单个文件
     * @param sizeKb 拆分块大小（KB）
     * @return 拆分后的多个二进制文件 zip
     */
    @PostMapping("/split")
    public ResponseEntity<byte[]> splitFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "sizeKb", required = false, defaultValue = "1024") int sizeKb
    ) throws IOException {
        byte[] bytes = fileToolService.splitFileBySize(file, sizeKb);
        return fileResponse(bytes, "file-parts.zip");
    }

    /**
     * 批量重命名上传的多个文件并打包返回
     * @param files 多文件
     * @param prefix 前缀
     * @param start 起始编号
     * @param suffix 后缀
     * @param keepExt 是否保留原扩展名
     * @return 重命名后的文件 zip
     */
    @PostMapping("/batch-rename")
    public ResponseEntity<byte[]> batchRename(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "start", required = false, defaultValue = "1") int start,
            @RequestParam(value = "suffix", required = false) String suffix,
            @RequestParam(value = "keepExt", required = false, defaultValue = "true") boolean keepExt
    ) throws IOException {
        byte[] bytes = fileToolService.batchRename(files, prefix, start, suffix, keepExt);
        return fileResponse(bytes, "renamed.zip");
    }

    /**
     * 文件类型检测（魔数 + 扩展名）
     * @param file 文件
     * @return 类型、MIME与扩展名
     */
    @PostMapping("/type-detect")
    public ApiResponse<FileToolService.FileType> detectType(@RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.ok(fileToolService.detectFileType(file));
    }

    /**
     * 编码检测与转换
     * 响应头携带检测到的编码与目标编码，主体为转换后的文本 bytes
     * @param file 文本文件
     * @param target 目标编码（为空则保持原编码）
     * @return 转换后文本二进制
     */
    @PostMapping("/encoding/convert")
    public ResponseEntity<byte[]> convertEncoding(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "target", required = false) String target
    ) throws IOException {
        var result = fileToolService.convertEncoding(file, target);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"converted.txt\"")
                .header("X-Detected-Encoding", result.detected())
                .header("X-Target-Encoding", result.target())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(result.bytes());
    }

    /**
     * CSV 指定列统计并在末行追加汇总
     * @param file CSV 文件
     * @param column 列名或列序号
     * @param op 操作：sum/avg/min/max
     * @return 追加汇总后的 CSV
     */
    @PostMapping("/data/process")
    public ResponseEntity<byte[]> processData(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "column") String column,
            @RequestParam(value = "op", required = false) String op
    ) throws IOException {
        byte[] bytes = fileToolService.processCsv(file, column, op);
        return fileResponse(bytes, "processed.csv");
    }

    /**
     * 字幕格式转换（SRT/VTT 互转）
     * @param file 字幕文件
     * @param target 目标格式 srt/vtt
     * @return 转换后字幕文件
     */
    @PostMapping("/subtitle/convert")
    public ResponseEntity<byte[]> convertSubtitle(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "target", required = false, defaultValue = "srt") String target
    ) throws IOException {
        byte[] bytes = fileToolService.convertSubtitle(file, target);
        String name = "srt".equalsIgnoreCase(target) ? "subtitle.srt" : "subtitle.vtt";
        return fileResponse(bytes, name);
    }

    /**
     * 统一二进制文件下载响应
     * @param bytes 文件内容
     * @param filename 文件名
     * @return ResponseEntity<byte[]>
     */
    private ResponseEntity<byte[]> fileResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }
}
