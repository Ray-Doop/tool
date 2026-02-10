/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/config/WebMvcConfig.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-api
 * 分层：config
 * 类型：class WebMvcConfig
 * 注解：Configuration、Value、Override
 * 依赖：org.springframework.beans.factory.annotation.Value、com.example.smarttools.api.log.OperationLogInterceptor、org.springframework.context.annotation.Configuration、org.springframework.web.servlet.config.annotation.InterceptorRegistry、org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry、org.springframework.web.servlet.config.annotation.WebMvcConfigurer、java.io.File
 * 公开方法：addResourceHandlers(ResourceHandlerRegistry registry)；addInterceptors(InterceptorRegistry registry)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.api.config;

import org.springframework.beans.factory.annotation.Value;
import com.example.smarttools.api.log.OperationLogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC 配置：上传目录映射与操作日志拦截。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final OperationLogInterceptor operationLogInterceptor;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public WebMvcConfig(OperationLogInterceptor operationLogInterceptor) {
        this.operationLogInterceptor = operationLogInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 映射 /uploads/** 到本地目录
        // 使用 toURI() 兼容 Windows 路径格式
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(dir.toURI().toString());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 接入操作日志拦截器
        registry.addInterceptor(operationLogInterceptor);
    }
}
