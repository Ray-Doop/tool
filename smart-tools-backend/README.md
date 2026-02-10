# Smart Tools Backend

基于 Spring Boot 3 + Maven 多模块的后端服务，提供鉴权、工具能力、系统管理与监控等 API。

## 模块说明

后端根目录为 `smart-tools-backend/`，主要模块如下：

- `smart-tools-api`：装配与入口层（启动类、Web 配置、全局异常处理等）
  - 启动类：[SmartToolsApiApplication](smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/SmartToolsApiApplication.java)
  - 全局异常：[GlobalExceptionHandler](smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/exception/GlobalExceptionHandler.java)
- `smart-tools-common`：通用基础能力（统一返回体、业务异常、工具类等）
  - 统一返回体：[ApiResponse](smart-tools-backend/smart-tools-common/src/main/java/com/example/smarttools/common/ApiResponse.java)
  - 业务异常：[BizException](smart-tools-backend/smart-tools-common/src/main/java/com/example/smarttools/common/exception/BizException.java)
- `smart-tools-domain`：数据模型层（JPA Entity + Repository）
  - 覆盖用户、工具、点赞/收藏/评论、访问/错误日志、后台 RBAC（角色/权限）等
- `smart-tools-auth`：鉴权与登录体系
  - `/api/auth/**`：注册、登录、图形验证码、OTP、微信登录等见 [AuthController](smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/controller/AuthController.java)
  - Spring Security：JWT 解析 + CORS + 限流 + 权限规则见 [SecurityConfig](smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/security/SecurityConfig.java)
- `smart-tools-tools`：工具目录与互动能力
  - 工具目录：`GET /api/tools`、`GET /api/tools/{slug}`、`GET /api/tools/trending`、`GET /api/tools/new` 见 [ToolController](smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/controller/ToolController.java)
  - 点赞/评论：`/api/tools/{slug}/like`、`/api/tools/{slug}/comments`、`/api/tools/{slug}/stats` 等见 [ToolEngagementController](smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/controller/ToolEngagementController.java)
- `smart-tools-system`：个人中心、收藏与日志上报等“用户侧系统能力”
  - 个人中心：`/api/me/**` 见 [MeController](smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/me/MeController.java)
  - 前端上报：`/api/logs/visit`、`/api/logs/error` 见 [LogController](smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/log/LogController.java)
- `smart-tools-monitor`：探活与基础监控
  - `GET /api/monitor/health`：MySQL/Redis/RabbitMQ 轻量探活见 [HealthController](smart-tools-backend/smart-tools-monitor/src/main/java/com/example/smarttools/modules/monitor/controller/HealthController.java)

## API 约定（前端如何统一处理）

- 所有 controller 返回统一结构 `ApiResponse{ success, data, error }`
  - `success=true`：业务成功
  - `success=false`：业务失败，`error.code/error.message` 给前端展示
- 未捕获异常由全局异常处理兜底，避免将堆栈暴露给客户端
  - 见 [GlobalExceptionHandler](smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/exception/GlobalExceptionHandler.java)

## 环境要求

- JDK 17（项目声明 `java.version=17`）
- Maven（建议 3.8+）

## 快速开始（本地）

在仓库根目录进入后端目录：

```bash
cd smart-tools-backend
mvn -pl smart-tools-api -am spring-boot:run
```

默认地址：

- API：http://localhost:9090
- H2 Console（仅当使用 H2 配置时可用）：http://localhost:9090/h2-console

端口可通过环境变量 `SERVER_PORT` 覆盖（默认 9090）。

## Profile 与数据库选择（H2 / MySQL）

- 配置入口：
  - 基础默认配置：[application.yml](smart-tools-backend/smart-tools-api/src/main/resources/application.yml)（包含 H2 datasource）
  - MySQL 配置： [application-mysql.yml](smart-tools-backend/smart-tools-api/src/main/resources/application-mysql.yml)（profile=mysql）
- 注意：后端启动类在未指定 `spring.profiles.active` 时会追加 `mysql` profile  
  - 见 [SmartToolsApiApplication](smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/SmartToolsApiApplication.java)
- 使用 H2（内存库）启动：需要显式指定一个“非空/非 null”profile，避免自动追加 mysql  
  - 示例（PowerShell）：
    - `$env:SPRING_PROFILES_ACTIVE=""`
    - `mvn -pl smart-tools-api -am spring-boot:run`



## 关键环境变量

建议通过环境变量管理敏感配置，避免在代码库中明文保存：

- `SERVER_PORT`：服务端口（默认 9090）
- `CORS_ALLOWED_ORIGINS`：允许跨域的前端地址（默认 `http://localhost:5173`，支持逗号分隔）
- `JWT_SECRET`：JWT 密钥（至少 32 字符）
- `MAIL_HOST` / `MAIL_PORT` / `MAIL_USERNAME` / `MAIL_PASSWORD`：邮件服务配置（如启用邮箱 OTP）
- `WXLOGIN_APP_ID` / `WXLOGIN_APP_SECRET`：微信登录配置（如启用）
- `MYSQL_URL` / `MYSQL_USERNAME` / `MYSQL_PASSWORD`：MySQL 配置（profile=mysql）
- `REDIS_HOST` / `REDIS_PORT` / `RABBITMQ_HOST` / ...：Redis/RabbitMQ（profile=mysql）

## 常用命令

```bash
cd smart-tools-backend
mvn -pl smart-tools-api -am spring-boot:run
mvn -pl smart-tools-api -am test
```
