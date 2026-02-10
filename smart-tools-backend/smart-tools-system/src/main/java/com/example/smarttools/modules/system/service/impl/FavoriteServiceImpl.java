/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/service/impl/FavoriteServiceImpl.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-system
 * 分层：service
 * 类型：class FavoriteServiceImpl
 * 注解：Service、Override、Transactional
 * 依赖：com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.favorite.UserToolFavorite、com.example.smarttools.domain.favorite.UserToolFavoriteRepository、com.example.smarttools.domain.tool.SysToolRepository、com.example.smarttools.domain.user.SysUserRepository、com.example.smarttools.modules.auth.security.UserPrincipal、com.example.smarttools.modules.system.service.IFavoriteService、org.springframework.http.HttpStatus、org.springframework.stereotype.Service、org.springframework.transaction.annotation.Transactional、java.util.List
 * 公开方法：listFavorites(UserPrincipal principal)；addFavorite(UserPrincipal principal, String toolSlug)；removeFavorite(UserPrincipal principal, String toolSlug)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 收藏服务实现：新增、查询、删除收藏。
 */
package com.example.smarttools.modules.system.service.impl;

import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.favorite.UserToolFavorite;
import com.example.smarttools.domain.favorite.UserToolFavoriteRepository;
import com.example.smarttools.domain.tool.SysToolRepository;
import com.example.smarttools.domain.user.SysUserRepository;
import com.example.smarttools.modules.auth.security.UserPrincipal;
import com.example.smarttools.modules.system.service.IFavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteServiceImpl implements IFavoriteService {
    // 用户仓库
    private final SysUserRepository userRepository;
    // 收藏仓库
    private final UserToolFavoriteRepository favoriteRepository;
    // 工具仓库
    private final SysToolRepository toolRepository;

    public FavoriteServiceImpl(SysUserRepository userRepository, UserToolFavoriteRepository favoriteRepository, SysToolRepository toolRepository) {
        // 注入依赖
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.toolRepository = toolRepository;
    }

    @Override
    public List<String> listFavorites(UserPrincipal principal) {
        // 查询用户收藏
        var user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new BizException(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND", "用户不存在"));
        return favoriteRepository.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(UserToolFavorite::getToolSlug)
                .toList();
    }

    @Override
    @Transactional
    public void addFavorite(UserPrincipal principal, String toolSlug) {
        // 添加收藏
        var user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new BizException(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND", "用户不存在"));
        
        // 移除工具存在性校验，允许收藏尚未同步到数据库的工具（以保证前端新上线工具可立即被收藏）
        /*
        if (!toolRepository.existsBySlug(toolSlug)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "TOOL_NOT_FOUND", "工具不存在");
        }
        */

        // 已收藏则直接返回
        if (favoriteRepository.existsByUserAndToolSlug(user, toolSlug)) {
            return;
        }
        // 保存收藏记录
        var fav = new UserToolFavorite();
        fav.setUser(user);
        fav.setToolSlug(toolSlug);
        favoriteRepository.save(fav);
    }

    @Override
    @Transactional
    public void removeFavorite(UserPrincipal principal, String toolSlug) {
        // 删除收藏
        var user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new BizException(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND", "用户不存在"));
        favoriteRepository.deleteByUserAndToolSlug(user, toolSlug);
    }
}
