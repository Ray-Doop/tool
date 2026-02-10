/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/file/service/FileToolService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-tools
 * 分层：service
 * 类型：class FileToolService
 * 注解：Service
 * 依赖：org.springframework.stereotype.Service、org.springframework.web.multipart.MultipartFile、java.io.ByteArrayInputStream、java.io.ByteArrayOutputStream、java.io.IOException、java.io.InputStream、java.nio.ByteBuffer、java.nio.CharBuffer、java.nio.charset.CharacterCodingException、java.nio.charset.Charset、java.nio.charset.CharsetDecoder、java.nio.charset.CodingErrorAction、java.nio.charset.StandardCharsets、java.util.ArrayList
 * 公开方法：zipCompress(MultipartFile[] files)；zipExtractToZip(MultipartFile zipFile)；splitTextFile(MultipartFile file, String mode, int size)；mergeFiles(MultipartFile[] files)；splitFileBySize(MultipartFile file, int sizeKb)；batchRename(MultipartFile[] files, String prefix, int startIndex, String suffix, boolean ke…)；detectFileType(MultipartFile file)；convertEncoding(MultipartFile file, String targetEncoding)；processCsv(MultipartFile file, String column, String operation)；convertSubtitle(MultipartFile file, String target)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.file.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class FileToolService {

    /**
     * 将上传文件压缩为 ZIP
     */
    public byte[] zipCompress(MultipartFile[] files) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;
                ZipEntry entry = new ZipEntry(safeName(file.getOriginalFilename()));
                zos.putNextEntry(entry);
                zos.write(file.getBytes());
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * 解压 ZIP 并重新打包为 ZIP 返回
     */
    public byte[] zipExtractToZip(MultipartFile zipFile) throws IOException {
        try (InputStream input = zipFile.getInputStream();
             ZipInputStream zis = new ZipInputStream(input);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                ZipEntry out = new ZipEntry(entry.getName());
                zos.putNextEntry(out);
                zis.transferTo(zos);
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * 文本文件拆分并输出多个文本片段
     */
    public byte[] splitTextFile(MultipartFile file, String mode, int size) throws IOException {
        byte[] bytes = file.getBytes();
        Charset charset = detectCharset(bytes);
        String text = new String(bytes, charset);
        List<String> parts = new ArrayList<>();
        if ("bytes".equalsIgnoreCase(mode)) {
            int chunk = Math.max(size, 1);
            for (int i = 0; i < bytes.length; i += chunk) {
                int end = Math.min(bytes.length, i + chunk);
                parts.add(new String(bytes, i, end - i, charset));
            }
        } else {
            int lines = Math.max(size, 1);
            String[] split = text.split("\\R", -1);
            StringBuilder current = new StringBuilder();
            int count = 0;
            for (String line : split) {
                current.append(line).append(System.lineSeparator());
                count += 1;
                if (count >= lines) {
                    parts.add(current.toString());
                    current.setLength(0);
                    count = 0;
                }
            }
            if (current.length() > 0) parts.add(current.toString());
        }
        return zipTextParts(parts);
    }

    /**
     * 按顺序合并多个二进制文件
     */
    public byte[] mergeFiles(MultipartFile[] files) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;
                baos.write(file.getBytes());
            }
            return baos.toByteArray();
        }
    }

    /**
     * 按块大小拆分二进制文件
     */
    public byte[] splitFileBySize(MultipartFile file, int sizeKb) throws IOException {
        int chunk = Math.max(sizeKb, 1) * 1024;
        byte[] bytes = file.getBytes();
        List<byte[]> parts = new ArrayList<>();
        for (int i = 0; i < bytes.length; i += chunk) {
            int end = Math.min(bytes.length, i + chunk);
            byte[] part = new byte[end - i];
            System.arraycopy(bytes, i, part, 0, part.length);
            parts.add(part);
        }
        return zipBinaryParts(parts, "part", ".bin");
    }

    /**
     * 批量重命名文件并打包
     */
    public byte[] batchRename(MultipartFile[] files, String prefix, int startIndex, String suffix, boolean keepExtension) throws IOException {
        List<MultipartFile> ordered = new ArrayList<>();
        for (MultipartFile f : files) {
            if (f != null && !f.isEmpty()) ordered.add(f);
        }
        ordered.sort(Comparator.comparing(f -> safeName(f.getOriginalFilename())));
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            int index = startIndex;
            for (MultipartFile file : ordered) {
                String ext = keepExtension ? extensionOf(file.getOriginalFilename()) : "";
                String name = "%s%d%s%s".formatted(prefix == null ? "" : prefix, index, suffix == null ? "" : suffix, ext);
                ZipEntry entry = new ZipEntry(name);
                zos.putNextEntry(entry);
                zos.write(file.getBytes());
                zos.closeEntry();
                index += 1;
            }
            zos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * 基于魔数与扩展名推断类型与 MIME
     */
    public FileType detectFileType(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String ext = extensionOf(file.getOriginalFilename());
        String type = detectMagic(bytes);
        String mime = mimeByType(type, ext);
        return new FileType(type, mime, ext);
    }

    /**
     * 自动检测源编码并转换为指定目标编码
     */
    public EncodingResult convertEncoding(MultipartFile file, String targetEncoding) throws IOException {
        byte[] bytes = file.getBytes();
        Charset source = detectCharset(bytes);
        Charset target = targetEncoding == null || targetEncoding.isBlank()
                ? source
                : Charset.forName(targetEncoding);
        String text = new String(bytes, source);
        byte[] converted = text.getBytes(target);
        return new EncodingResult(source.name(), target.name(), converted);
    }

    /**
     * CSV 指定列统计并写入末行
     */
    public byte[] processCsv(MultipartFile file, String column, String operation) throws IOException {
        byte[] bytes = file.getBytes();
        Charset charset = detectCharset(bytes);
        String content = new String(bytes, charset);
        String[] lines = content.split("\\R");
        if (lines.length == 0) return bytes;
        List<String> headers = parseCsvLine(lines[0]);
        int idx = resolveColumnIndex(headers, column);
        if (idx < 0) return bytes;
        List<Double> values = new ArrayList<>();
        for (int i = 1; i < lines.length; i += 1) {
            List<String> row = parseCsvLine(lines[i]);
            if (row.size() <= idx) continue;
            String cell = row.get(idx);
            try {
                values.add(Double.parseDouble(cell));
            } catch (NumberFormatException ignored) {
            }
        }
        double result = calculate(values, operation);
        String summaryLabel = operation == null || operation.isBlank() ? "sum" : operation.toLowerCase(Locale.ROOT);
        StringBuilder out = new StringBuilder(content);
        if (!content.endsWith("\n")) out.append("\n");
        List<String> summary = new ArrayList<>();
        for (int i = 0; i < headers.size(); i += 1) {
            if (i == 0) summary.add(summaryLabel);
            else if (i == idx) summary.add(String.valueOf(result));
            else summary.add("");
        }
        out.append(String.join(",", summary));
        return out.toString().getBytes(charset);
    }

    /**
     * 字幕格式转换（SRT/VTT）
     */
    public byte[] convertSubtitle(MultipartFile file, String target) throws IOException {
        byte[] bytes = file.getBytes();
        Charset charset = detectCharset(bytes);
        String content = new String(bytes, charset);
        List<Cue> cues = parseSubtitle(content);
        String out = "srt".equalsIgnoreCase(target) ? toSrt(cues) : toVtt(cues);
        return out.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 将文本片段打包成 ZIP
     */
    private byte[] zipTextParts(List<String> parts) throws IOException {
        List<byte[]> bytes = new ArrayList<>();
        for (String part : parts) {
            bytes.add(part.getBytes(StandardCharsets.UTF_8));
        }
        return zipBinaryParts(bytes, "part", ".txt");
    }

    /**
     * 将二进制片段打包为 ZIP
     */
    private byte[] zipBinaryParts(List<byte[]> parts, String prefix, String suffix) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (int i = 0; i < parts.size(); i += 1) {
                String name = "%s-%d%s".formatted(prefix, i + 1, suffix);
                zos.putNextEntry(new ZipEntry(name));
                zos.write(parts.get(i));
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * 编码检测：优先 BOM，其次 UTF-8 校验，最后回退 GBK
     */
    private Charset detectCharset(byte[] bytes) {
        if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF) {
            return StandardCharsets.UTF_8;
        }
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xFF) == 0xFF) {
            return StandardCharsets.UTF_16BE;
        }
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xFE) {
            return StandardCharsets.UTF_16LE;
        }
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            decoder.decode(ByteBuffer.wrap(bytes));
            return StandardCharsets.UTF_8;
        } catch (CharacterCodingException ignored) {
        }
        return Charset.forName("GBK");
    }

    /**
     * 规范文件名，避免目录穿越
     */
    private String safeName(String name) {
        if (name == null || name.isBlank()) return "file";
        return name.replace("\\", "/").substring(name.lastIndexOf('/') + 1);
    }

    /**
     * 获取文件扩展名（含点）
     */
    private String extensionOf(String name) {
        if (name == null) return "";
        int idx = name.lastIndexOf('.');
        return idx >= 0 ? name.substring(idx) : "";
    }

    /**
     * 简易魔数判断
     */
    private String detectMagic(byte[] bytes) {
        if (bytes.length >= 4) {
            if (bytes[0] == 0x25 && bytes[1] == 0x50 && bytes[2] == 0x44 && bytes[3] == 0x46) return "pdf";
            if (bytes[0] == 0x50 && bytes[1] == 0x4B) return "zip";
            if ((bytes[0] & 0xFF) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) return "png";
            if ((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8) return "jpg";
            if (bytes[0] == 0x47 && bytes[1] == 0x49 && bytes[2] == 0x46) return "gif";
            if (bytes[0] == 0x52 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x46) return "riff";
        }
        if (bytes.length >= 12) {
            String head = new String(bytes, 4, 8, StandardCharsets.ISO_8859_1);
            if (head.contains("ftyp")) return "mp4";
        }
        return "unknown";
    }

    /**
     * 根据类型与扩展名推断 MIME
     */
    private String mimeByType(String type, String ext) {
        return switch (type) {
            case "pdf" -> "application/pdf";
            case "zip" -> "application/zip";
            case "png" -> "image/png";
            case "jpg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "mp4" -> "video/mp4";
            case "riff" -> {
                if (".wav".equalsIgnoreCase(ext)) yield "audio/wav";
                yield "application/octet-stream";
            }
            default -> "application/octet-stream";
        };
    }

    /**
     * 解析单行 CSV（支持双引号）
     */
    private List<String> parseCsvLine(String line) {
        List<String> out = new ArrayList<>();
        if (line == null) return out;
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i += 1) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i += 1;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        out.add(sb.toString());
        return out;
    }

    /**
     * 根据列名或序号解析列索引
     */
    private int resolveColumnIndex(List<String> headers, String column) {
        if (column == null || column.isBlank()) return -1;
        for (int i = 0; i < headers.size(); i += 1) {
            if (headers.get(i).equalsIgnoreCase(column)) return i;
        }
        try {
            int idx = Integer.parseInt(column);
            return Math.max(0, Math.min(idx, headers.size() - 1));
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    /**
     * 统计值计算
     */
    private double calculate(List<Double> values, String operation) {
        if (values.isEmpty()) return 0;
        String op = operation == null ? "sum" : operation.toLowerCase(Locale.ROOT);
        return switch (op) {
            case "avg", "average" -> values.stream().mapToDouble(v -> v).average().orElse(0);
            case "min" -> values.stream().mapToDouble(v -> v).min().orElse(0);
            case "max" -> values.stream().mapToDouble(v -> v).max().orElse(0);
            default -> values.stream().mapToDouble(v -> v).sum();
        };
    }

    /**
     * 识别字幕格式并解析为统一 Cue
     */
    private List<Cue> parseSubtitle(String content) {
        if (content.startsWith("WEBVTT")) {
            return parseVtt(content);
        }
        return parseSrt(content);
    }

    /**
     * 解析 SRT
     */
    private List<Cue> parseSrt(String content) {
        List<Cue> cues = new ArrayList<>();
        String[] blocks = content.split("\\R\\s*\\R");
        for (String block : blocks) {
            String[] lines = block.split("\\R");
            if (lines.length < 2) continue;
            String timeLine = lines[1].contains("-->") ? lines[1] : lines[0];
            String[] parts = timeLine.split("-->");
            if (parts.length < 2) continue;
            long start = parseTime(parts[0].trim());
            long end = parseTime(parts[1].trim());
            StringBuilder text = new StringBuilder();
            int startLine = lines[1].contains("-->") ? 2 : 1;
            for (int i = startLine; i < lines.length; i += 1) {
                if (text.length() > 0) text.append("\n");
                text.append(lines[i]);
            }
            cues.add(new Cue(start, end, text.toString()));
        }
        return cues;
    }

    /**
     * 解析 VTT（头部移除后复用 SRT 解析）
     */
    private List<Cue> parseVtt(String content) {
        String cleaned = content.replaceFirst("(?s)^WEBVTT.*?\\R\\R", "");
        return parseSrt(cleaned);
    }

    /**
     * 输出为 SRT
     */
    private String toSrt(List<Cue> cues) {
        StringBuilder sb = new StringBuilder();
        int idx = 1;
        for (Cue cue : cues) {
            sb.append(idx++).append("\n");
            sb.append(formatTime(cue.start)).append(" --> ").append(formatTime(cue.end)).append("\n");
            sb.append(cue.text).append("\n\n");
        }
        return sb.toString().trim();
    }

    /**
     * 输出为 VTT
     */
    private String toVtt(List<Cue> cues) {
        StringBuilder sb = new StringBuilder("WEBVTT\n\n");
        for (Cue cue : cues) {
            sb.append(formatTimeVtt(cue.start)).append(" --> ").append(formatTimeVtt(cue.end)).append("\n");
            sb.append(cue.text).append("\n\n");
        }
        return sb.toString().trim();
    }

    /**
     * 解析字幕时间戳
     */
    private long parseTime(String time) {
        String[] parts = time.replace(",", ".").split(":");
        if (parts.length < 3) return 0;
        long hours = Long.parseLong(parts[0].trim());
        long minutes = Long.parseLong(parts[1].trim());
        String[] secParts = parts[2].split("\\.");
        long seconds = Long.parseLong(secParts[0].trim());
        long ms = secParts.length > 1 ? Long.parseLong(padRight(secParts[1], 3)) : 0;
        return ((hours * 60 + minutes) * 60 + seconds) * 1000 + ms;
    }

    /**
     * 格式化 SRT 时间戳
     */
    private String formatTime(long ms) {
        long total = ms / 1000;
        long hours = total / 3600;
        long minutes = (total % 3600) / 60;
        long seconds = total % 60;
        long millis = ms % 1000;
        return "%02d:%02d:%02d,%03d".formatted(hours, minutes, seconds, millis);
    }

    /**
     * 格式化 VTT 时间戳
     */
    private String formatTimeVtt(long ms) {
        long total = ms / 1000;
        long hours = total / 3600;
        long minutes = (total % 3600) / 60;
        long seconds = total % 60;
        long millis = ms % 1000;
        return "%02d:%02d:%02d.%03d".formatted(hours, minutes, seconds, millis);
    }

    /**
     * 时间毫秒字符串补齐
     */
    private String padRight(String text, int len) {
        StringBuilder sb = new StringBuilder(text == null ? "" : text);
        while (sb.length() < len) sb.append('0');
        if (sb.length() > len) return sb.substring(0, len);
        return sb.toString();
    }

    /**
     * 文件类型检测结果
     */
    public record FileType(String type, String mime, String extension) {}

    /**
     * 编码检测与转换结果
     */
    public record EncodingResult(String detected, String target, byte[] bytes) {}

    /**
     * 统一字幕片段结构
     */
    public record Cue(long start, long end, String text) {}
}
