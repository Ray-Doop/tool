/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/crypto/package-info.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-tools
 * 分层：general
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 加密与安全类工具（Crypto）。
 *
 * 典型工具示例：哈希（MD5/SHA）、HMAC、JWT 解析、密码强度检测、随机数/密钥生成等。
 *
 * 分层约定（建议）：
 * 
 *   controller：对外 HTTP 接口（/api/tools/{slug}/...）。
 *   domain：DTO/VO（请求响应结构）。
 *   mapper：对象转换（如 MapStruct）或与存储模型映射。
 *   service + service.impl：核心业务实现（不写在 controller）。
 *   task：定时/后台任务（如清理缓存、预热数据）。
 * 
 */
package com.example.smarttools.modules.tools.feature.crypto;

