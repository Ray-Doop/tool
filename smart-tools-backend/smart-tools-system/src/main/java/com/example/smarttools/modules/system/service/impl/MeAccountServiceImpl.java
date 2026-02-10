/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/service/impl/MeAccountServiceImpl.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-system
 * 分层：service
 * 注解：Service、Override、Transactional
 * 依赖：com.example.smarttools.common.exception.BizException、com.example.smarttools.domain.user.SysUser、com.example.smarttools.domain.user.SysUserRepository、com.example.smarttools.modules.auth.security.UserPrincipal、com.example.smarttools.modules.system.domain.dto.ChangePasswordRequest、com.example.smarttools.modules.system.domain.dto.CompleteRegistrationRequest、com.example.smarttools.modules.system.domain.dto.UpdateProfileRequest、com.example.smarttools.modules.system.domain.dto.UpdateSettingsRequest、com.example.smarttools.modules.system.domain.dto.UserProfileResponse、com.example.smarttools.modules.system.domain.dto.UserSettingsResponse、com.example.smarttools.common.storage.IFileStorageService、com.example.smarttools.modules.system.service.IMeAccountService、com.fasterxml.jackson.databind.JsonNode、com.fasterxml.jackson.databind.ObjectMapper
 * 公开方法：getProfile(UserPrincipal principal)；updateProfile(UserPrincipal principal, UpdateProfileRequest req)；updateAvatar(UserPrincipal principal, MultipartFile file)；getSettings(UserPrincipal principal)；updateSettings(UserPrincipal principal, UpdateSettingsRequest req)；changePassword(UserPrincipal principal, ChangePasswordRequest req)；completeRegistration(UserPrincipal principal, CompleteRegistrationRequest req)；deleteAccount(UserPrincipal principal)；getRecentTools(UserPrincipal principal)；getLoginHistory(UserPrincipal principal)；getProfileStats(UserPrincipal principal)；exportData(UserPrincipal principal)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 个人中心服务实现：资料、设置、统计、导出与收藏等。
 */
package com.example.smarttools.modules.system.service.impl;

import com.example.smarttools.common.exception.BizException;
import com.example.smarttools.domain.user.SysUser;
import com.example.smarttools.domain.user.SysUserRepository;
import com.example.smarttools.modules.auth.security.UserPrincipal;
import com.example.smarttools.modules.system.domain.dto.ChangePasswordRequest;
import com.example.smarttools.modules.system.domain.dto.CompleteRegistrationRequest;
import com.example.smarttools.modules.system.domain.dto.UpdateProfileRequest;
import com.example.smarttools.modules.system.domain.dto.UpdateSettingsRequest;
import com.example.smarttools.modules.system.domain.dto.UserProfileResponse;
import com.example.smarttools.modules.system.domain.dto.UserSettingsResponse;
import com.example.smarttools.common.storage.IFileStorageService;
import com.example.smarttools.modules.system.service.IMeAccountService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;

import com.example.smarttools.domain.favorite.UserToolFavoriteRepository;
import com.example.smarttools.domain.log.SysVisitLogRepository;
import com.example.smarttools.domain.tool.SysToolRepository;
import com.example.smarttools.modules.system.domain.dto.LoginHistoryResponse;
import com.example.smarttools.modules.system.domain.dto.ProfileStatsResponse;
import com.example.smarttools.modules.system.domain.dto.RecentToolResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MeAccountServiceImpl implements IMeAccountService {
    // 用户仓库
    private final SysUserRepository userRepository;
    // 密码加密器
    private final PasswordEncoder passwordEncoder;
    // JSON 处理器
    private final ObjectMapper objectMapper;
    // 文件存储服务
    private final IFileStorageService fileStorageService;
    // 访问日志仓库
    private final SysVisitLogRepository visitLogRepository;
    // 工具仓库
    private final SysToolRepository toolRepository;
    // 收藏仓库
    private final UserToolFavoriteRepository favoriteRepository;

    public MeAccountServiceImpl(SysUserRepository userRepository, PasswordEncoder passwordEncoder, ObjectMapper objectMapper, IFileStorageService fileStorageService, SysVisitLogRepository visitLogRepository, SysToolRepository toolRepository, UserToolFavoriteRepository favoriteRepository) {
        // 注入依赖
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.fileStorageService = fileStorageService;
        this.visitLogRepository = visitLogRepository;
        this.toolRepository = toolRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public UserProfileResponse getProfile(UserPrincipal principal) {
        // 读取用户基础资料
        var user = requireEnabledUser(principal);
        return toProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UserPrincipal principal, UpdateProfileRequest req) {
        // 更新用户资料（可选字段）
        var user = requireEnabledUser(principal);

        // 规范化空字符串为 null
        var displayName = normalizeToNull(req.displayName());
        var avatarUrl = normalizeToNull(req.avatarUrl());
        var bio = normalizeToNull(req.bio());
        var email = normalizeToNull(req.email());
        var phone = normalizeToNull(req.phone());

        if (email != null) {
            // 邮箱唯一性校验
            var e = email.toLowerCase(Locale.ROOT);
            var existing = userRepository.findByEmail(e).orElse(null);
            if (existing != null && !existing.getId().equals(user.getId())) {
                throw new BizException(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_REGISTERED", "邮箱已被占用");
            }
            user.setEmail(e);
        } else {
            user.setEmail(null);
        }

        if (phone != null) {
            // 手机号唯一性校验
            var existing = userRepository.findByPhone(phone).orElse(null);
            if (existing != null && !existing.getId().equals(user.getId())) {
                throw new BizException(HttpStatus.BAD_REQUEST, "PHONE_ALREADY_REGISTERED", "手机号已被占用");
            }
            user.setPhone(phone);
        } else {
            user.setPhone(null);
        }

        // 更新展示信息
        user.setDisplayName(displayName);
        user.setAvatarUrl(avatarUrl);
        user.setBio(bio);

        // 持久化更新
        userRepository.save(user);
        return toProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateAvatar(UserPrincipal principal, MultipartFile file) {
        // 更新头像
        var user = requireEnabledUser(principal);
        
        // 文件不能为空
        if (file.isEmpty()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "FILE_EMPTY", "文件不能为空");
        }
        
        try {
            // 使用通用存储服务上传，指定 avatars 子目录
            String avatarUrl = fileStorageService.upload(file, "avatars");
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
        } catch (Exception e) {
            // 统一封装上传异常
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "UPLOAD_FAILED", "文件上传失败: " + e.getMessage());
        }
        
        return toProfileResponse(user);
    }

    @Override
    public UserSettingsResponse getSettings(UserPrincipal principal) {
        // 返回用户设置
        var user = requireEnabledUser(principal);
        return toSettingsResponse(user);
    }

    @Override
    @Transactional
    public UserSettingsResponse updateSettings(UserPrincipal principal, UpdateSettingsRequest req) {
        // 更新用户设置
        var user = requireEnabledUser(principal);
        try {
            // 序列化设置内容
            var json = objectMapper.writeValueAsString(req.preferences());
            // 限制大小，避免过大写入
            if (json.length() > 4096) {
                throw new BizException(HttpStatus.BAD_REQUEST, "SETTINGS_TOO_LARGE", "设置内容过大");
            }
            user.setPreferencesJson(json);
        } catch (IOException e) {
            // 反序列化失败视为格式不合法
            throw new BizException(HttpStatus.BAD_REQUEST, "SETTINGS_INVALID", "设置格式不合法");
        }
        // 保存设置
        userRepository.save(user);
        return toSettingsResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(UserPrincipal principal, ChangePasswordRequest req) {
        // 修改密码
        var user = requireEnabledUser(principal);
        // 校验旧密码
        if (!passwordEncoder.matches(req.oldPassword(), user.getPasswordHash())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "INVALID_CREDENTIALS", "原密码错误");
        }
        // 新密码不能与旧密码相同
        if (passwordEncoder.matches(req.newPassword(), user.getPasswordHash())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "PASSWORD_SAME_AS_OLD", "新密码不能与原密码相同");
        }
        // 保存新密码
        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserProfileResponse completeRegistration(UserPrincipal principal, CompleteRegistrationRequest req) {
        // 补全注册信息
        var user = requireEnabledUser(principal);
        if (user.isRegistrationCompleted()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "REGISTRATION_ALREADY_COMPLETED", "账号已完成注册");
        }
        // 两次密码一致性校验
        if (!req.password().equals(req.confirmPassword())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH", "两次密码不一致");
        }
        var username = normalizeToNull(req.username());
        if (username == null) {
            throw new BizException(HttpStatus.BAD_REQUEST, "USERNAME_REQUIRED", "用户名不能为空");
        }
        // 用户名唯一性校验
        var existing = userRepository.findByUsername(username).orElse(null);
        if (existing != null && !existing.getId().equals(user.getId())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "USERNAME_ALREADY_EXISTS", "用户名已存在");
        }
        // 更新用户信息
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRegistrationCompleted(true);
        userRepository.save(user);
        return toProfileResponse(user);
    }

    @Override
    @Transactional
    public void deleteAccount(UserPrincipal principal) {
        // 删除账号
        var user = requireEnabledUser(principal);
        // Soft delete or hard delete? For simplicity, we use hard delete here, but in real world, soft delete is preferred.
        // Or we can just disable the user.
        // Let's hard delete for now as per requirement.
        userRepository.delete(user);
    }

    @Override
    public List<RecentToolResponse> getRecentTools(UserPrincipal principal) {
        // 获取最近使用工具
        var user = requireEnabledUser(principal);
        // Find latest 100 logs to ensure we get enough unique tools
        var logs = visitLogRepository.findRecentToolsByUserId(user.getId(), PageRequest.of(0, 100));
        
        // Dedup by toolSlug and take top 10
        Map<String, RecentToolResponse> uniqueTools = new java.util.LinkedHashMap<>();
        for (var log : logs) {
            if (uniqueTools.size() >= 10) break;
            if (uniqueTools.containsKey(log.getToolSlug())) continue;
            
            // 优先使用工具名称
            var toolName = toolRepository.findBySlug(log.getToolSlug())
                    .map(t -> t.getName())
                    .orElse(log.getToolSlug());
            uniqueTools.put(log.getToolSlug(), new RecentToolResponse(log.getToolSlug(), toolName, log.getCreatedAt()));
        }
        
        return new java.util.ArrayList<>(uniqueTools.values());
    }

    @Override
    public List<LoginHistoryResponse> getLoginHistory(UserPrincipal principal) {
        // 获取最近登录记录
        var user = requireEnabledUser(principal);
        // Assuming login logs have path like '/api/auth/login%'
        return visitLogRepository.findRecentLoginLogsByUserId(user.getId(), PageRequest.of(0, 10))
                .stream()
                .map(log -> new LoginHistoryResponse(log.getIp(), log.getUserAgent(), log.getCreatedAt()))
                .toList();
    }

    @Override
    public ProfileStatsResponse getProfileStats(UserPrincipal principal) {
        // 计算用户统计信息
        var user = requireEnabledUser(principal);
        long totalVisits = visitLogRepository.countByUserId(user.getId());
        long uniqueToolsUsed = visitLogRepository.countDistinctToolSlugByUserId(user.getId());
        long favoritesCount = favoriteRepository.countByUser(user);
        
        // Calculate daily visits for last 365 days
        var oneYearAgo = java.time.Instant.now().minus(365, java.time.temporal.ChronoUnit.DAYS);
        var logs = visitLogRepository.findByUserIdAndCreatedAtAfter(user.getId(), oneYearAgo);
        
        // 按日期聚合访问次数
        Map<String, Long> dailyVisits = logs.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        log -> log.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString(),
                        java.util.stream.Collectors.counting()
                ));
        
        return new ProfileStatsResponse(totalVisits, uniqueToolsUsed, favoritesCount, dailyVisits);
    }

    @Override
    public Map<String, Object> exportData(UserPrincipal principal) {
        // 导出用户数据
        var user = requireEnabledUser(principal);
        var data = new HashMap<String, Object>();
        
        // 基础资料、设置、统计
        data.put("profile", toProfileResponse(user));
        data.put("settings", toSettingsResponse(user));
        data.put("stats", getProfileStats(principal));
        
        // Export favorites
        var favorites = favoriteRepository.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(f -> f.getToolSlug())
                .toList();
        data.put("favorites", favorites);
        
        // Export recent history (limit 100 for safety)
        var history = visitLogRepository.findRecentToolsByUserId(user.getId(), PageRequest.of(0, 100)).stream()
                .map(log -> Map.of(
                        "tool", log.getToolSlug(),
                        "time", log.getCreatedAt().toString(),
                        "ip", log.getIp() == null ? "" : log.getIp()
                ))
                .toList();
        data.put("history", history);
        
        return data;
    }

    private SysUser requireEnabledUser(UserPrincipal principal) {
        // 校验已登录且用户存在
        if (principal == null || principal.userId() == null) {
            throw new BizException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "未登录");
        }
        var user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在"));
        if (!user.isEnabled()) {
            throw new BizException(HttpStatus.FORBIDDEN, "USER_DISABLED", "账号已禁用");
        }
        return user;
    }

    private UserProfileResponse toProfileResponse(SysUser user) {
        // 构建个人资料响应
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl == null || avatarUrl.isBlank()) {
            avatarUrl = "/uploads/avatars/morentouxiang.png";
        }
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                avatarUrl,
                user.getBio(),
                user.getEmail(),
                user.getPhone(),
                user.getWechatOpenid() != null && !user.getWechatOpenid().isBlank(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private UserSettingsResponse toSettingsResponse(SysUser user) {
        // 构建设置响应
        return new UserSettingsResponse(
                readPreferences(user.getPreferencesJson()),
                user.getEmail(),
                user.getPhone(),
                user.getWechatOpenid() != null && !user.getWechatOpenid().isBlank()
        );
    }

    private JsonNode readPreferences(String json) {
        // 读取并解析设置 JSON
        if (json == null || json.isBlank()) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            // 解析失败时返回空对象
            return objectMapper.createObjectNode();
        }
    }

    private String normalizeToNull(String s) {
        // 空白字符串转为 null
        if (s == null) return null;
        var v = s.trim();
        return v.isBlank() ? null : v;
    }
}
