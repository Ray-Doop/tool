/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/service/IMeAccountService.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-system
 * 分层：service
 * 类型：interface IMeAccountService
 * 依赖：com.example.smarttools.modules.auth.security.UserPrincipal、com.example.smarttools.modules.system.domain.dto.ChangePasswordRequest、com.example.smarttools.modules.system.domain.dto.CompleteRegistrationRequest、com.example.smarttools.modules.system.domain.dto.UpdateProfileRequest、com.example.smarttools.modules.system.domain.dto.UpdateSettingsRequest、com.example.smarttools.modules.system.domain.dto.UserProfileResponse、com.example.smarttools.modules.system.domain.dto.UserSettingsResponse、com.example.smarttools.modules.system.domain.dto.RecentToolResponse、com.example.smarttools.modules.system.domain.dto.LoginHistoryResponse、com.example.smarttools.modules.system.domain.dto.ProfileStatsResponse、org.springframework.web.multipart.MultipartFile、java.util.List、java.util.Map
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
/**
 * 个人中心账户服务接口。
 */
package com.example.smarttools.modules.system.service;

import com.example.smarttools.modules.auth.security.UserPrincipal;
import com.example.smarttools.modules.system.domain.dto.ChangePasswordRequest;
import com.example.smarttools.modules.system.domain.dto.CompleteRegistrationRequest;
import com.example.smarttools.modules.system.domain.dto.UpdateProfileRequest;
import com.example.smarttools.modules.system.domain.dto.UpdateSettingsRequest;
import com.example.smarttools.modules.system.domain.dto.UserProfileResponse;
import com.example.smarttools.modules.system.domain.dto.UserSettingsResponse;
import com.example.smarttools.modules.system.domain.dto.RecentToolResponse;
import com.example.smarttools.modules.system.domain.dto.LoginHistoryResponse;
import com.example.smarttools.modules.system.domain.dto.ProfileStatsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IMeAccountService {
    // 获取个人资料
    UserProfileResponse getProfile(UserPrincipal principal);

    // 更新个人资料
    UserProfileResponse updateProfile(UserPrincipal principal, UpdateProfileRequest req);

    // 更新头像
    UserProfileResponse updateAvatar(UserPrincipal principal, MultipartFile file);

    // 获取个性化设置
    UserSettingsResponse getSettings(UserPrincipal principal);

    // 更新个性化设置
    UserSettingsResponse updateSettings(UserPrincipal principal, UpdateSettingsRequest req);

    // 修改密码
    void changePassword(UserPrincipal principal, ChangePasswordRequest req);

    // 完成注册信息补全
    UserProfileResponse completeRegistration(UserPrincipal principal, CompleteRegistrationRequest req);

    // 最近使用工具
    List<RecentToolResponse> getRecentTools(UserPrincipal principal);

    // 最近登录记录
    List<LoginHistoryResponse> getLoginHistory(UserPrincipal principal);

    // 删除账号
    void deleteAccount(UserPrincipal principal);

    // 个人统计信息
    ProfileStatsResponse getProfileStats(UserPrincipal principal);

    // 导出个人数据
    Map<String, Object> exportData(UserPrincipal principal);
}
