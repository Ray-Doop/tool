/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-monitor/src/main/java/com/example/smarttools/modules/monitor/controller/HealthController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-monitor
 * 分层：controller
 * 类型：class HealthController
 * 注解：RestController、RequestMapping、GetMapping
 * 依赖：com.example.smarttools.common.ApiResponse、org.springframework.amqp.rabbit.core.RabbitTemplate、org.springframework.data.redis.core.StringRedisTemplate、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RestController、javax.sql.DataSource、java.util.LinkedHashMap
 * 公开方法：health()
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.monitor.controller;

import com.example.smarttools.common.ApiResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.LinkedHashMap;

/**
 * 运行状态/依赖探活接口。
 *
 * 注意：本接口只做“轻量探活”，用于快速判断外部依赖是否可连通：
 * 
 *   MySQL：获取连接并调用 isValid(timeout)
 *   Redis：执行 ping
 *   RabbitMQ：与 broker 建立通道并执行轻量指令
 * 
 *
 * 设计原则：
 * 
 *   best-effort：任何一个依赖失败都不会影响其他依赖的检测结果。
 *   不抛异常：失败返回 false，避免健康检查导致接口 500。
 * 
 */
@RestController
@RequestMapping("/api/monitor")
public class HealthController {
    private final DataSource dataSource;
    private final StringRedisTemplate redis;
    private final RabbitTemplate rabbit;

    public HealthController(DataSource dataSource, StringRedisTemplate redis, RabbitTemplate rabbit) {
        this.dataSource = dataSource;
        this.redis = redis;
        this.rabbit = rabbit;
    }

    @GetMapping("/health")
    public ApiResponse<Object> health() {
        var data = new LinkedHashMap<String, Object>();
        data.put("mysql", checkMysql());
        data.put("redis", checkRedis());
        data.put("rabbitmq", checkRabbit());
        return ApiResponse.ok(data);
    }

    private boolean checkMysql() {
        try (var conn = dataSource.getConnection()) {
            return conn.isValid(1);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean checkRedis() {
        try {
            var pong = redis.getConnectionFactory().getConnection().ping();
            return pong != null && !pong.isBlank();
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean checkRabbit() {
        try {
            rabbit.execute(channel -> {
                channel.basicQos(1);
                return null;
            });
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
