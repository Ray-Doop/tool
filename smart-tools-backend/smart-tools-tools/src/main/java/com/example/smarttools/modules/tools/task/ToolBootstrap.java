/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/task/ToolBootstrap.java
 * 用途：后端配置/组件（Spring Bean 与运行时扩展）
 * 归属：后端 smart-tools-tools
 * 分层：general
 * 类型：class ToolBootstrap
 * 注解：Component、Override
 * 依赖：com.example.smarttools.modules.tools.service.IToolService、org.springframework.boot.ApplicationArguments、org.springframework.boot.ApplicationRunner、org.springframework.stereotype.Component
 * 公开方法：run(ApplicationArguments args)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.task;

import com.example.smarttools.modules.tools.service.IToolService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 工具模块启动任务。
 *
 * 用途：在应用启动完成后，初始化默认工具目录数据（{@link com.example.smarttools.domain.tool.SysTool}）。
 *
 * 为什么用 ApplicationRunner：
 * 
 *   保证 Spring 容器已就绪（Repository 可用）。
 *   只运行一次，适合做“空库自举”的初始化逻辑。
 * 
 */
@Component
public class ToolBootstrap implements ApplicationRunner {
    private final IToolService toolService;

    public ToolBootstrap(IToolService toolService) {
        this.toolService = toolService;
    }

    @Override
    public void run(ApplicationArguments args) {
        toolService.ensureDefaults();
    }
}
