/**
 * 异步执行配置：提供应用级线程池，用于承载 @Async 等后台任务。
 *
 * 为什么需要线程池：
 * - Web 请求线程（Tomcat）应尽量只做请求处理，耗时任务应交给后台线程池。
 * - 统一的线程池参数可通过配置调优（核心/最大线程、队列长度、拒绝策略）。
 *
 * 注解/定义含义：
 * - @Configuration：声明为配置类，Spring 会扫描并处理其中的 @Bean 方法。
 * - @EnableAsync：开启 Spring 的异步方法支持（识别并代理 @Async）。
 * - @Bean(name="appTaskExecutor")：将线程池注册为名为 appTaskExecutor 的 Bean。
 * - @Value：把配置项注入到方法参数（支持默认值语法 ${key:default}）。
 *
 * 使用方式：在业务代码中用 @Async("appTaskExecutor") 显式指定本线程池执行。
 */
package com.example.smarttools.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    /**
     * 应用级线程池（用于 @Async、异步日志等场景）
     *
     * 关键点：
     * - 核心线程数：常驻处理请求的后台任务
     * - 最大线程数：短时间突发流量的弹性扩容
     * - 队列容量：削峰填谷，防止瞬时高并发把线程打爆
     * - 拒绝策略：CallerRuns 让调用线程“帮忙执行”，形成自然背压
     */
    @Bean(name = "appTaskExecutor")
    public Executor appTaskExecutor(
            @Value("${app.thread-pool.core-size:8}") int coreSize,
            @Value("${app.thread-pool.max-size:64}") int maxSize,
            @Value("${app.thread-pool.queue-capacity:2000}") int queueCapacity,
            @Value("${app.thread-pool.keep-alive-seconds:60}") int keepAliveSeconds
    ) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("smart-tools-");
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setAllowCoreThreadTimeOut(false);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(20);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
