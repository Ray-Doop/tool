/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/package-info.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-tools
 * 分层：general
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 工具模块的“功能分区（feature）”根包。
 *
 * 本包用于按“工具分类”组织代码，目标是做到：
 * 
 *   同类工具聚合：例如 crypto/web/network/math/text 等，便于按领域扩展。
 *   结构统一：每个分类内部都推荐按 controller/domain/mapper/service/task 分层。
 *   前后端对齐：工具 slug（接口路径）与前端工具目录名保持一致。
 * 
 *
 * 目录约定（建议）：
 * <pre>
 * feature
 *  ├─ crypto
 *  │   ├─ controller / domain / mapper / service / service.impl / task
 *  ├─ network
 *  └─ ...
 * </pre>
 *
 * 注意：分类包本身不承载具体工具；具体工具可以继续在分类下按子包细分。
 */
package com.example.smarttools.modules.tools.feature;

