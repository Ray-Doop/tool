/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/media/package-info.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-tools
 * 分层：general
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 图片与视频类工具（Media）。
 *
 * 典型工具示例：图片压缩、裁剪、格式转换、EXIF 读取、视频转码参数生成等。
 *
 * 设计建议：
 * 
 *   大文件处理建议使用异步任务（task）+ 存储（对象存储/本地目录），避免阻塞 HTTP 线程。
 *   对外下载/回调地址要做白名单校验，防止 SSRF 等风险。
 * 
 */
package com.example.smarttools.modules.tools.feature.media;

