/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/config/StartupLogConfig.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-api
 * 分层：config
 * 类型：class StartupLogConfig
 * 注解：Configuration、Override
 * 依赖：org.slf4j.Logger、org.slf4j.LoggerFactory、org.springframework.boot.context.event.ApplicationReadyEvent、org.springframework.boot.web.context.WebServerApplicationContext、org.springframework.context.ApplicationListener、org.springframework.context.annotation.Configuration、org.springframework.core.env.Environment
 * 公开方法：onApplicationEvent(ApplicationReadyEvent event)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 启动完成后的提示日志输出。
 *
 * 目的：
 * - 本地开发时快速看到服务监听端口与常用入口
 * - 不影响业务逻辑（仅日志）
 */
@Configuration
public class StartupLogConfig implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(StartupLogConfig.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var ctx = event.getApplicationContext();
        Environment env = ctx.getEnvironment();
        int port = -1;
        if (ctx instanceof WebServerApplicationContext webCtx) {
            port = webCtx.getWebServer().getPort();
        }
        var baseUrl = port > 0 ? "http://localhost:" + port : "http://localhost";
        var profiles = env.getActiveProfiles();
        var profilesText = profiles.length == 0 ? "default" : String.join(",", profiles);
        log.info("SmartTools API 启动成功: {}", baseUrl);
        log.info("Active Profiles: {}", profilesText);
        log.info("Datasource URL: {}", env.getProperty("spring.datasource.url"));
        boolean h2ConsoleEnabled = Boolean.parseBoolean(env.getProperty("spring.h2.console.enabled", "false"));
        if (h2ConsoleEnabled) {
            var h2Path = env.getProperty("spring.h2.console.path", "/h2-console");
            log.info("H2 Console: {}{}", baseUrl, h2Path);
        }
    }
}
