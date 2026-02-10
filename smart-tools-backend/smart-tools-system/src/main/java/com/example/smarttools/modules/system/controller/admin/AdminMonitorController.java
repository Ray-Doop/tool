/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/admin/AdminMonitorController.java
 * 用途：后端 HTTP 接口层（Controller，路由与请求编排）
 * 归属：后端 smart-tools-system
 * 分层：controller
 * 类型：class AdminMonitorController
 * 注解：RestController、RequestMapping、GetMapping、RequestParam
 * 依赖：com.example.smarttools.common.ApiResponse、com.example.smarttools.domain.admin.SysServerMetricRepository、jakarta.validation.constraints.Min、org.springframework.data.domain.PageRequest、org.springframework.data.domain.Sort、org.springframework.web.bind.annotation.GetMapping、org.springframework.web.bind.annotation.RequestMapping、org.springframework.web.bind.annotation.RequestParam、org.springframework.web.bind.annotation.RestController、java.util.Map
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.system.controller.admin;

import com.example.smarttools.common.ApiResponse;
import com.example.smarttools.domain.admin.SysServerMetricRepository;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/monitor")
public class AdminMonitorController {
    private final SysServerMetricRepository metricRepository;

    public AdminMonitorController(SysServerMetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @GetMapping("/metrics")
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) int size,
            @RequestParam(value = "host", required = false) String host
    ) {
        var pr = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "collectedAt"));
        if (host == null || host.isBlank()) {
            var data = metricRepository.findAll(pr);
            return ApiResponse.ok(Map.of(
                    "items", data.getContent(),
                    "total", data.getTotalElements(),
                    "page", page,
                    "size", size
            ));
        }
        var items = metricRepository.findByHostOrderByCollectedAtDesc(host, pr);
        var total = metricRepository.countByHost(host);
        return ApiResponse.ok(Map.of(
                "items", items,
                "total", total,
                "page", page,
                "size", size
        ));
    }
}
