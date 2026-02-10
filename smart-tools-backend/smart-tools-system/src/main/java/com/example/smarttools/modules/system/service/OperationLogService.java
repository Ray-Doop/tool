/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/service/OperationLogService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-system
 * 分层：service
 * 类型：class OperationLogService
 * 注解：Service、Async
 * 依赖：com.example.smarttools.domain.admin.SysOperationLog、com.example.smarttools.domain.admin.SysOperationLogRepository、org.springframework.scheduling.annotation.Async、org.springframework.stereotype.Service
 * 公开方法：record(Long userId, String module, String action, String target, String result, String…)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.system.service;

import com.example.smarttools.domain.admin.SysOperationLog;
import com.example.smarttools.domain.admin.SysOperationLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务：异步落库，减少主请求耗时。
 */
@Service
public class OperationLogService {
    private final SysOperationLogRepository logRepository;

    public OperationLogService(SysOperationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * 记录一次操作日志，字段长度会做裁剪，避免数据库溢出。
     */
    @Async("appTaskExecutor")
    public void record(Long userId, String module, String action, String target, String result, String ip, String userAgent) {
        var log = new SysOperationLog();
        log.setUserId(userId);
        log.setModule(trim(module, 128));
        log.setAction(trim(action, 128));
        log.setTarget(trim(target, 256));
        log.setResult(trim(result, 64));
        log.setIp(trim(ip, 64));
        log.setUserAgent(trim(userAgent, 512));
        logRepository.save(log);
    }

    private String trim(String text, int max) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text.length() > max ? text.substring(0, max) : text;
    }
}
