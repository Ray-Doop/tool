/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/data/uuid/service/impl/UuidGeneratorServiceImpl.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-tools
 * 分层：service
 * 类型：class UuidGeneratorServiceImpl
 * 注解：Service、Override
 * 依赖：com.example.smarttools.modules.tools.feature.data.uuid.service.IUuidGeneratorService、org.springframework.stereotype.Service、java.util.ArrayList、java.util.List、java.util.UUID
 * 公开方法：generate(int count)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.data.uuid.service.impl;

import com.example.smarttools.modules.tools.feature.data.uuid.service.IUuidGeneratorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UuidGeneratorServiceImpl implements IUuidGeneratorService {
    @Override
    public List<String> generate(int count) {
        int n = Math.max(1, Math.min(count, 100));
        List<String> res = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            res.add(UUID.randomUUID().toString());
        }
        return res;
    }
}

