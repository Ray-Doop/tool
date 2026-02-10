/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/me/MeController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-system
 * 分层：controller
 * 注解：RestController、RequestMapping、GetMapping、PutMapping、AuthenticationPrincipal、Valid、PostMapping、RequestParam、PathVariable、DeleteMapping
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.modules.auth.security.UserPrincipal、com.example.smarttools.domain.admin.SysAdminRepository、com.example.smarttools.modules.system.domain.dto.ChangePasswordRequest、com.example.smarttools.modules.system.domain.dto.CompleteRegistrationRequest、com.example.smarttools.modules.system.domain.dto.RecentToolResponse、com.example.smarttools.modules.system.domain.dto.LoginHistoryResponse、com.example.smarttools.modules.system.domain.dto.ProfileStatsResponse、com.example.smarttools.modules.system.domain.dto.UpdateProfileRequest、com.example.smarttools.modules.system.domain.dto.UpdateSettingsRequest、com.example.smarttools.modules.system.domain.dto.UserMeResponse、com.example.smarttools.modules.system.domain.dto.UserProfileResponse、com.example.smarttools.modules.system.domain.dto.UserSettingsResponse、com.example.smarttools.modules.system.service.IFavoriteService
 * 公开方法：me(@AuthenticationPrincipal UserPrincipal principal)；profile(@AuthenticationPrincipal UserPrincipal principal)；settings(@AuthenticationPrincipal UserPrincipal principal)；favorites(@AuthenticationPrincipal UserPrincipal principal)；recentTools(@AuthenticationPrincipal UserPrincipal principal)；loginHistory(@AuthenticationPrincipal UserPrincipal principal)；deleteAccount(@AuthenticationPrincipal UserPrincipal principal)；stats(@AuthenticationPrincipal UserPrincipal principal)；export(@AuthenticationPrincipal UserPrincipal principal)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 个人中心接口：基本资料、设置、收藏、历史等。
 */
package com.example.smarttools.modules.system.controller.me;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.modules.auth.security.UserPrincipal;
import com.example.smarttools.domain.admin.SysAdminRepository;
import com.example.smarttools.modules.system.domain.dto.ChangePasswordRequest;
import com.example.smarttools.modules.system.domain.dto.CompleteRegistrationRequest;
import com.example.smarttools.modules.system.domain.dto.RecentToolResponse;
import com.example.smarttools.modules.system.domain.dto.LoginHistoryResponse;
import com.example.smarttools.modules.system.domain.dto.ProfileStatsResponse;
import com.example.smarttools.modules.system.domain.dto.UpdateProfileRequest;
import com.example.smarttools.modules.system.domain.dto.UpdateSettingsRequest;
import com.example.smarttools.modules.system.domain.dto.UserMeResponse;
import com.example.smarttools.modules.system.domain.dto.UserProfileResponse;
import com.example.smarttools.modules.system.domain.dto.UserSettingsResponse;
import com.example.smarttools.modules.system.service.IFavoriteService;
import com.example.smarttools.modules.system.service.IMeAccountService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/me")
public class MeController {
    // 收藏服务
    private final IFavoriteService favoriteService;
    // 个人账户服务
    private final IMeAccountService meAccountService;
    // 管理员判定仓储
    private final SysAdminRepository adminRepository;

    public MeController(IFavoriteService favoriteService, IMeAccountService meAccountService, SysAdminRepository adminRepository) {
        // 注入依赖
        this.favoriteService = favoriteService;
        this.meAccountService = meAccountService;
        this.adminRepository = adminRepository;
    }

    @GetMapping
    public ApiResponse<UserMeResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        // 返回简版个人信息（用于头部展示）
        var profile = meAccountService.getProfile(principal);
        boolean isAdmin = false;
        try {
            isAdmin = adminRepository.existsByUserIdAndEnabledTrue(profile.id());
        } catch (Exception ignore) {
        }
        return ApiResponse.ok(new UserMeResponse(profile.id(), profile.username(), profile.displayName(), profile.avatarUrl(), isAdmin));
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> profile(@AuthenticationPrincipal UserPrincipal principal) {
        // 返回完整个人资料
        return ApiResponse.ok(meAccountService.getProfile(principal));
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest req
    ) {
        // 更新昵称/头像/邮箱/电话等
        return ApiResponse.ok(meAccountService.updateProfile(principal, req));
    }

    @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserProfileResponse> updateAvatar(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file
    ) {
        // 上传并更新头像
        return ApiResponse.ok(meAccountService.updateAvatar(principal, file));
    }

    @GetMapping("/settings")
    public ApiResponse<UserSettingsResponse> settings(@AuthenticationPrincipal UserPrincipal principal) {
        // 查询用户个性化设置
        return ApiResponse.ok(meAccountService.getSettings(principal));
    }

    @PutMapping("/settings")
    public ApiResponse<UserSettingsResponse> updateSettings(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateSettingsRequest req
    ) {
        // 更新用户个性化设置
        return ApiResponse.ok(meAccountService.updateSettings(principal, req));
    }

    @PostMapping("/settings/password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest req
    ) {
        // 修改密码
        meAccountService.changePassword(principal, req);
        return ApiResponse.ok(null);
    }

    @PostMapping("/complete-registration")
    public ApiResponse<UserProfileResponse> completeRegistration(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CompleteRegistrationRequest req
    ) {
        // 首次登录补全信息
        return ApiResponse.ok(meAccountService.completeRegistration(principal, req));
    }

    @GetMapping("/favorites")
    public ApiResponse<List<String>> favorites(@AuthenticationPrincipal UserPrincipal principal) {
        // 查询收藏工具列表
        return ApiResponse.ok(favoriteService.listFavorites(principal));
    }

    @PostMapping("/favorites/{toolSlug}")
    public ApiResponse<Void> addFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug
    ) {
        // 添加收藏
        favoriteService.addFavorite(principal, toolSlug);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/favorites/{toolSlug}")
    public ApiResponse<Void> removeFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("toolSlug") String toolSlug
    ) {
        // 取消收藏
        favoriteService.removeFavorite(principal, toolSlug);
        return ApiResponse.ok(null);
    }

    @GetMapping("/recent-tools")
    public ApiResponse<List<RecentToolResponse>> recentTools(@AuthenticationPrincipal UserPrincipal principal) {
        // 最近使用工具
        return ApiResponse.ok(meAccountService.getRecentTools(principal));
    }

    @GetMapping("/login-history")
    public ApiResponse<List<LoginHistoryResponse>> loginHistory(@AuthenticationPrincipal UserPrincipal principal) {
        // 登录历史列表
        return ApiResponse.ok(meAccountService.getLoginHistory(principal));
    }

    @DeleteMapping("/account")
    public ApiResponse<Void> deleteAccount(@AuthenticationPrincipal UserPrincipal principal) {
        // 删除账号
        meAccountService.deleteAccount(principal);
        return ApiResponse.ok(null);
    }

    @GetMapping("/stats")
    public ApiResponse<ProfileStatsResponse> stats(@AuthenticationPrincipal UserPrincipal principal) {
        // 统计信息（访问数、收藏数等）
        return ApiResponse.ok(meAccountService.getProfileStats(principal));
    }

    @GetMapping("/export")
    public ApiResponse<Map<String, Object>> export(@AuthenticationPrincipal UserPrincipal principal) {
        // 导出用户数据
        return ApiResponse.ok(meAccountService.exportData(principal));
    }
}
