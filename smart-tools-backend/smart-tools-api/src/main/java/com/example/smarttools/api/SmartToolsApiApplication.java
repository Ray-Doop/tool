/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/SmartToolsApiApplication.java
 * 用途：后端 Java 源码文件（Spring Boot 模块）
 * 归属：后端 smart-tools-api
 * 分层：general
 * 类型：class SmartToolsApiApplication
 * 注解：SpringBootApplication、EntityScan、EnableJpaRepositories
 * 依赖：org.springframework.boot.SpringApplication、org.springframework.boot.autoconfigure.SpringBootApplication、org.springframework.boot.autoconfigure.domain.EntityScan、org.springframework.data.jpa.repository.config.EnableJpaRepositories
 * 公开方法：main(String[] args)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 后端 API 启动入口（应用装配层）。
 *
 * 职责边界：
 * 
 *   只负责“启动与装配”：启动 Spring Boot、启用组件扫描、启用 JPA Entity/Repository 扫描。
 *   不承载任何业务逻辑；业务逻辑位于各业务模块（auth/tools/system/monitor）。
 * 
 *
 * 扫描策略：
 * 
 *   {@code scanBasePackages="com.example.smarttools"}：统一扫描所有 Maven 模块下的 Spring 组件。
 *   {@link EntityScan} / {@link EnableJpaRepositories}：显式指向 domain 模块的实体与仓储。
 * 
 *
 * Profile 策略：
 * 
 *   若未显式指定 {@code spring.profiles.active}，默认追加 {@code mysql}，便于本地开发快速启动。
 *   线上环境建议通过环境变量/启动参数显式指定 profile，避免意外使用默认配置。
 * 
 */
@SpringBootApplication(scanBasePackages = "com.example.smarttools")
@EntityScan(basePackages = "com.example.smarttools.domain")
@EnableJpaRepositories(basePackages = "com.example.smarttools.domain")
public class SmartToolsApiApplication {
    /**
     * Spring Boot 应用启动方法。
     *
     * 注意：此处只做“默认 profile”兜底，其他配置均由 application-*.yml 提供。
     */
    public static void main(String[] args) {
        var app = new SpringApplication(SmartToolsApiApplication.class);
        if (System.getProperty("spring.profiles.active") == null && System.getenv("SPRING_PROFILES_ACTIVE") == null) {
            app.setAdditionalProfiles("mysql");
        }
        app.run(args);
    }
}
