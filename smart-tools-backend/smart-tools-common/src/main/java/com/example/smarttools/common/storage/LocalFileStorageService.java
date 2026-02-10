/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-common/src/main/java/com/example/smarttools/common/storage/LocalFileStorageService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-common
 * 分层：general
 * 类型：class LocalFileStorageService
 * 注解：Service、Value、Override
 * 依赖：org.slf4j.Logger、org.slf4j.LoggerFactory、org.springframework.beans.factory.annotation.Value、org.springframework.stereotype.Service、org.springframework.web.multipart.MultipartFile、java.io.File、java.io.IOException、java.util.UUID
 * 公开方法：upload(MultipartFile file, String subDir)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.common.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 本地文件存储实现
 */
@Service
public class LocalFileStorageService implements IFileStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageService.class);

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file, String subDir) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 1. 准备目录 (使用绝对路径，避免 Tomcat 在临时目录中寻找)
        File dir = new File(uploadDir + File.separator + subDir).getAbsoluteFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        // 使用 UUID 防止文件名冲突
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;

        // 3. 保存文件
        File dest = new File(dir, filename);
        try {
            log.info("Saving file to: {}", dest.getAbsolutePath());
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }

        // 4. 返回访问路径 (假设前端代理了 /uploads -> 后端 /uploads)
        // 注意：这里返回的是 URL 路径，要是 / 风格
        return "/uploads/" + subDir + "/" + filename;
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot > 0) return filename.substring(dot);
        return "";
    }
}
