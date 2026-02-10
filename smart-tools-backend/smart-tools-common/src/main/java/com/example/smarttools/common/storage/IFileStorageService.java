/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-common/src/main/java/com/example/smarttools/common/storage/IFileStorageService.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-common
 * 分层：general
 * 类型：interface IFileStorageService
 * 依赖：org.springframework.web.multipart.MultipartFile
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.common.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件存储接口
 */
public interface IFileStorageService {
    /**
     * 上传文件
     *
     * @param file    文件对象
     * @param subDir  子目录（例如 "avatars", "tools/base64"）
     * @return 文件的访问 URL（相对路径或绝对路径）
     */
    String upload(MultipartFile file, String subDir);
}
