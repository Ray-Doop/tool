/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/feature/converter/base/service/BaseConverterService.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-tools
 * 分层：service
 * 类型：class BaseConverterService
 * 注解：Service
 * 依赖：com.example.smarttools.common.exception.BizException、org.springframework.http.HttpStatus、org.springframework.stereotype.Service
 * 公开方法：convert(String value, int fromBase, int toBase)
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.feature.converter.base.service;

import com.example.smarttools.common.exception.BizException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BaseConverterService {

    public String convert(String value, int fromBase, int toBase) {
        try {
            // Java's Long.parseLong supports up to base 36
            // But for very large numbers, we might need BigInteger
            java.math.BigInteger bigInt = new java.math.BigInteger(value, fromBase);
            return bigInt.toString(toBase).toUpperCase();
        } catch (NumberFormatException e) {
            throw new BizException(HttpStatus.BAD_REQUEST, "INVALID_NUMBER", "无法解析的数字: " + value + " (Base " + fromBase + ")");
        }
    }
}
