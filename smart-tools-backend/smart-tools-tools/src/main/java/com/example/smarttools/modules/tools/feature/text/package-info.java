/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/text/package-info.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-tools
 * 分层：general
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 文本类工具（Text）。
 *
 * 典型工具示例：大小写转换、去重/排序、正则测试、文本差异对比、字数统计等。
 *
 * 建议：
 * 
 *   对多行/大文本处理注意性能，必要时做长度限制与流式处理。
 *   正则相关工具要避免 ReDoS：可限制正则长度与执行时间（如实现支持）。
 * 
 */
package com.example.smarttools.modules.tools.feature.text;

