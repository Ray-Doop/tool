/**
 * @generated-file-note
 * 文件：smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/service/impl/ToolServiceImpl.java
 * 用途：后端业务服务层（Service，业务规则与事务编排）
 * 归属：后端 smart-tools-tools
 * 分层：service
 * 类型：class ToolServiceImpl
 * 注解：Service、Override、Transactional
 * 依赖：com.example.smarttools.domain.tool.SysTool、com.example.smarttools.domain.tool.SysToolRepository、com.example.smarttools.domain.log.SysVisitLogRepository、com.example.smarttools.modules.tools.domain.dto.TrendingToolDto、com.example.smarttools.modules.tools.service.IToolService、org.springframework.stereotype.Service、org.springframework.transaction.annotation.Transactional、org.springframework.data.domain.PageRequest、java.util.List、java.util.Objects、java.time.Instant、java.time.temporal.ChronoUnit
 * 公开方法：listEnabledTools()；getBySlug(String slug)；getTrendingTools()；getNewestTools()；ensureDefaults()
 * 职责：提供可复用的后端能力，供 Controller/Service 等注入调用
 * 数据：避免在日志/异常中输出口令、token、验证码、个人敏感信息等
 * 维护：修改对外行为时同步更新对应的接口/配置/测试
 */
package com.example.smarttools.modules.tools.service.impl;

import com.example.smarttools.domain.tool.SysTool;
import com.example.smarttools.domain.tool.SysToolRepository;
import com.example.smarttools.domain.log.SysVisitLogRepository;
import com.example.smarttools.modules.tools.domain.dto.TrendingToolDto;
import com.example.smarttools.modules.tools.service.IToolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Objects;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 工具目录服务实现。
 *
 * 职责：
 * 
 *   读取“已启用工具”列表：供前端渲染工具市场/首页等。
 *   根据 slug 获取工具元信息：供前端进入工具详情页。
 *   在应用启动时兜底初始化默认工具：确保空库也能跑通最小功能集。
 * 
 *
 * 数据来源：{@link SysToolRepository}（JPA），实体为 {@link SysTool}。
 */
@Service
public class ToolServiceImpl implements IToolService {
    private final SysToolRepository toolRepository;
    private final SysVisitLogRepository visitLogRepository;

    public ToolServiceImpl(SysToolRepository toolRepository, SysVisitLogRepository visitLogRepository) {
        this.toolRepository = toolRepository;
        this.visitLogRepository = visitLogRepository;
    }

    @Override
    public List<SysTool> listEnabledTools() {
        return toolRepository.findAllByEnabledTrueOrderByCategoryAscSortOrderAscNameAsc();
    }

    @Override
    public SysTool getBySlug(String slug) {
        return toolRepository.findBySlug(slug).orElseThrow(() -> new IllegalArgumentException("Tool not found"));
    }

    @Override
    public List<TrendingToolDto> getTrendingTools() {
        // 统计过去 7 天的访问量
        var since = Instant.now().minus(7, ChronoUnit.DAYS);
        var results = visitLogRepository.findTrendingTools(since, PageRequest.of(0, 10));
        
        return results.stream().map(row -> {
            String slug = (String) row[0];
            long count = ((Number) row[1]).longValue();
            return toolRepository.findBySlug(slug).map(t -> new TrendingToolDto(
                t.getSlug(),
                t.getName(),
                t.getCategory(),
                t.getDescription(),
                count,
                "+" + (int)(Math.random() * 20 + 5) + "%" // 模拟增长率
            )).orElse(null);
        })
        .filter(Objects::nonNull)
        .toList();
    }

    @Override
    public List<SysTool> getNewestTools() {
        return toolRepository.findTop10ByEnabledTrueOrderByCreatedAtDesc();
    }

    /**
     * 确保默认工具已初始化。
     *
     * 设计点：
     * 
     *   只在“工具不存在”时插入，避免覆盖管理员在数据库中配置的展示字段（name/category/description 等）。
     *   默认数据用于新环境自举：前端可以立刻展示工具列表，不必先手动插入数据。
     * 
     */
    @Override
    @Transactional
    public void ensureDefaults() {
        upsertDefault("uuid-generator", "UUID 生成器", "编码/标识", "生成 UUIDv4", "uuid,uuidv4,random");
        upsertDefault("base64", "Base64 编解码", "编码/转换", "Base64 字符串编码与解码", "base64,encode,decode");
        upsertDefault("token-generator", "Token 生成器", "生成器", "生成随机字符串、密码或 API Token", "token,password,random,string");
        upsertDefault("hash-text", "哈希文本", "编码/转换", "计算文本的 MD5, SHA1, SHA256, SHA512 哈希值", "hash,md5,sha1,sha256,sha512");
        upsertDefault("date-converter", "日期转换", "编码/转换", "时间戳与日期字符串相互转换", "date,time,timestamp,iso8601");
        upsertDefault("json-to-yaml", "JSON 转 YAML", "编码/转换", "JSON 与 YAML 格式互转", "json,yaml,converter,formatter");
        upsertDefault("case-converter", "大小写转换", "编码/转换", "文本大小写转换 (camelCase, snake_case, etc.)", "case,camel,snake,kebab");
        upsertDefault("color-converter", "颜色转换", "编码/转换", "HEX, RGB, HSL 颜色格式转换", "color,hex,rgb,hsl");
        upsertDefault("base64-file", "Base64 文件", "编码/转换", "文件转 Base64 编码", "base64,file,image");
        upsertDefault("ulid-generator", "ULID 生成器", "编码/标识", "生成随机的通用唯一词典可排序标识符 (ULID)", "ulid,uuid,random,id");
        upsertDefault("encrypt-decrypt", "加密/解密文本", "安全", "使用加密算法 (AES, DES, RC4) 加密和解密文本", "encrypt,decrypt,aes,des,rc4,crypto");
        upsertDefault("bcrypt-generator", "Bcrypt 加密", "安全", "使用 Bcrypt 对文本字符串进行哈希和比较", "bcrypt,hash,password,security");
        upsertDefault("bip39-generator", "BIP39 密码生成器", "安全", "生成 BIP39 助记词和种子", "bip39,mnemonic,seed,crypto,wallet");
        upsertDefault("hmac-generator", "Hmac 生成器", "安全", "计算基于哈希的消息身份验证代码 (HMAC)", "hmac,hash,security,signature");
        upsertDefault("rsa-generator", "RSA 密钥对生成器", "安全", "生成新的随机 RSA 私钥和公钥证书", "rsa,keypair,crypto,pem,public,private");
        upsertDefault("password-strength", "密码强度分析仪", "安全", "估算密码的强度和破解时间", "password,strength,security,zxcvbn");
        upsertDefault("pdf-signature-checker", "PDF 签名检查器", "文件", "验证 PDF 文件的数字签名", "pdf,signature,verify,file");
        upsertDefault("base-converter", "整数基数转换器", "编码/转换", "在不同的基数 (十进制, 十六进制, 二进制, 八进制) 之间转换数字", "base,radix,hex,binary,octal,decimal");
        upsertDefault("roman-numeral", "罗马数字转换器", "编码/转换", "数字与罗马数字相互转换", "roman,numeral,number,converter");
        upsertDefault("text-to-nato", "文本转北约字母表", "编码/转换", "将文本转换为北约拼音字母", "nato,phonetic,alphabet,text");
        upsertDefault("text-to-binary", "文本到 ASCII 二进制", "编码/转换", "文本与其 ASCII 二进制表示形式互转", "binary,ascii,text,01");
        upsertDefault("text-to-unicode", "文本转 Unicode", "编码/转换", "解析文本并将其转换为 Unicode", "unicode,text,escape");
        upsertDefault("yaml-to-toml", "YAML 到 TOML", "编码/转换", "YAML 转换为 TOML", "yaml,toml,converter");
        upsertDefault("json-to-toml", "JSON 转 TOML", "编码/转换", "JSON 转换为 TOML", "json,toml,converter");
        upsertDefault("list-converter", "List 转换器", "编码/转换", "处理列表数据：排序、去重、分割等", "list,array,sort,filter,converter");
        upsertDefault("toml-to-json", "TOML 到 JSON", "编码/转换", "TOML 转换为 JSON", "toml,json,converter");
        upsertDefault("toml-to-yaml", "TOML 到 YAML", "编码/转换", "TOML 转换为 YAML", "toml,yaml,converter");
        upsertDefault("pdf-signature-checker", "PDF 签名检查器", "文件", "验证 PDF 文件的数字签名", "pdf,signature,verify,file");
        upsertDefault("code-generator", "Spring Boot 代码生成器", "开发", "快速生成 Spring Boot 项目结构与 CRUD 代码", "java,spring boot,generator,scaffold");
        // 图表工具：在线图表编辑与导出
        upsertDefault("standard-line-chart", "在线制作标准折线图", "图表", "输入数据生成标准折线图并导出 PNG", "chart,line,折线图");
        upsertDefault("standard-pie-chart", "在线制作标准饼图", "图表", "输入比例数据生成饼图并支持导出", "chart,pie,饼图");
        upsertDefault("tree-chart", "在线制作树形图", "图表", "使用 JSON 生成树形结构图", "chart,tree,树形图");
        upsertDefault("standard-bar-chart", "在线制作标准柱状图", "图表", "输入分类数据生成柱状图", "chart,bar,柱状图");
        upsertDefault("sankey-chart", "在线制作桑基图", "图表", "通过节点与流向数据生成桑基图", "chart,sankey,桑基图");
        upsertDefault("radar-chart", "在线制作雷达图", "图表", "基于多指标数据生成雷达图", "chart,radar,雷达图");
        upsertDefault("sunburst-chart", "在线制作旭日图", "图表", "使用层级数据生成旭日图", "chart,sunburst,旭日图");
        upsertDefault("scatter-chart", "在线制作散点图", "图表", "输入 XY 数据生成散点图", "chart,scatter,散点图");
        upsertDefault("rose-chart", "在线制作南丁格尔玫瑰图", "图表", "输入分组数据生成玫瑰图", "chart,rose,玫瑰图");
        upsertDefault("donut-chart", "在线制作环形图", "图表", "输入比例数据生成环形图", "chart,donut,环形图");
        upsertDefault("funnel-chart", "在线制作漏斗图", "图表", "输入阶段数据生成漏斗图", "chart,funnel,漏斗图");
        upsertDefault("gauge-chart", "在线制作仪表盘图", "图表", "设置指标数值生成仪表盘图", "chart,gauge,仪表盘");
        upsertDefault("formula-chart", "在线数学公式绘制图表", "图表", "输入函数表达式生成曲线图", "chart,formula,function");
        upsertDefault("word-cloud-chart", "在线制作词云图", "图表", "输入词语与权重生成词云图", "chart,wordcloud,词云图");
        upsertDefault("relationship-graph", "在线生成人物关系图", "图表", "通过节点与关系数据生成关系图", "chart,graph,关系图");
        upsertDefault("mermaid-editor", "在线 Mermaid 图表编辑器", "图表", "在线编辑 Mermaid 语法并生成图表", "chart,mermaid,diagram");
    }

    /**
     * 插入一条默认工具记录（仅在 slug 不存在时生效）。
     *
     * @param slug        工具 slug（必须全局唯一，且与前端工具目录一致）
     * @param name        展示名称
     * @param category    分类（用于前端分组与排序，如“编码/转换”）
     * @param description 描述信息
     * @param keywords    关键词（前端搜索用，逗号分隔）
     */
    private void upsertDefault(String slug, String name, String category, String description, String keywords) {
        if (toolRepository.existsBySlug(slug)) {
            return;
        }
        var tool = new SysTool();
        tool.setSlug(slug);
        tool.setName(name);
        tool.setCategory(category);
        tool.setDescription(description);
        tool.setKeywords(keywords);
        tool.setEnabled(true);
        tool.setSortOrder(0);
        toolRepository.save(tool);
    }
}
