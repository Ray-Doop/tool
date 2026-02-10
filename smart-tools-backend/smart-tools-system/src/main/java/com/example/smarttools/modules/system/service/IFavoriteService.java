/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/service/IFavoriteService.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-system
 * 分层：service
 * 类型：interface IFavoriteService
 * 依赖：com.example.smarttools.modules.auth.security.UserPrincipal、java.util.List
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 收藏服务接口。
 */
package com.example.smarttools.modules.system.service;

import com.example.smarttools.modules.auth.security.UserPrincipal;

import java.util.List;

public interface IFavoriteService {
    // 查询收藏列表
    List<String> listFavorites(UserPrincipal principal);

    // 添加收藏
    void addFavorite(UserPrincipal principal, String toolSlug);

    // 取消收藏
    void removeFavorite(UserPrincipal principal, String toolSlug);
}
