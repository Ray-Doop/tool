# Smart Tools（全栈项目）

Smart Tools 是一个“模块化工具箱网站”全栈项目：前端提供工具目录/工具详情/登录体系/个人中心/管理后台入口；后端提供统一鉴权、工具目录与互动（点赞/收藏/评论）、个人中心、日志与监控等 REST API。

## 仓库结构（全部目录）

```text
/
  smart-tools-frontend/        # 前端：Vue 3 + Vue CLI（SPA）
  smart-tools-backend/         # 后端：Spring Boot 3 + Maven 多模块（REST API）
  db/                          # 数据库脚本
```

## 总体架构（前后端如何协作）

- 前端是 SPA：浏览器只加载静态资源（dist），所有业务数据通过 `/api/**` 调后端
- 后端是 REST API：统一返回结构为 `ApiResponse{ success, data, error }`，便于前端统一处理成功/失败
- 鉴权：JWT（Access Token）+ Refresh Cookie（后端签发、前端触发 refresh），接口层由 Spring Security 统一拦截
- 上传：后端提供 `/uploads/**` 静态访问（头像等），前端只存 URL
- 日志与监控：前端上报访问与错误日志，后端持久化；提供健康检查接口

## 前端模块（smart-tools-frontend）

前端采用“工具插件化”方式组织：每个工具一个目录，通过约定（`meta.js` + `index.vue`）自动注册到工具目录与路由渲染。

- 入口与全局：应用启动、全局错误上报、鉴权静默刷新  
  - 入口见 [main.js](smart-tools-frontend/src/main.js)
- 网络层：Axios 统一封装、自动注入 token、401 自动 refresh 并重试  
  - 见 [http.js](smart-tools-frontend/src/api/http.js)
- 状态层：Pinia（用户态/工具目录/收藏等）  
  - 鉴权见 [auth store](smart-tools-frontend/src/store/auth.js)
  - 工具目录见 [toolCatalog store](smart-tools-frontend/src/store/toolCatalog.js)
- 路由与权限：路由守卫控制“需登录/需管理员”页面  
  - 见 [router](smart-tools-frontend/src/router/index.js)
- 布局与登录：未登录访问受限页会回首页并通过 `?login=1` 打开登录弹窗  
  - 见 [AppLayout.vue](smart-tools-frontend/src/layouts/AppLayout.vue)
- 工具注册中心：扫描所有工具模块并输出可渲染的工具列表  
  - 见 [registry.js](smart-tools-frontend/src/tools/registry.js)

更多前端细节见 [smart-tools-frontend/README.md](./smart-tools-frontend/README.md)。

## 后端模块（smart-tools-backend）

后端是 Maven 多模块工程，模块边界按业务拆分；由 API 模块统一装配启动并扫描所有模块组件。

- 启动与装配：`smart-tools-api`
  - 启动类： [SmartToolsApiApplication.java](smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/SmartToolsApiApplication.java)
  - 全局异常： [GlobalExceptionHandler](smart-tools-backend/smart-tools-api/src/main/java/com/example/smarttools/api/exception/GlobalExceptionHandler.java)
- 通用基础：`smart-tools-common`
  - 统一返回体： [ApiResponse](smart-tools-backend/smart-tools-common/src/main/java/com/example/smarttools/common/ApiResponse.java)
  - 业务异常： [BizException](smart-tools-backend/smart-tools-common/src/main/java/com/example/smarttools/common/exception/BizException.java)
- 数据模型：`smart-tools-domain`
  - 统一承载 JPA Entity/Repository（用户、工具、点赞收藏评论、访问/错误日志、后台 RBAC 等）
- 登录与鉴权：`smart-tools-auth`
  - 认证接口：`/api/auth/**`（注册/登录/验证码/OTP/微信等）见 [AuthController](smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/controller/AuthController.java)
  - 安全链路：CORS + JWT 解析 + 限流 + 权限规则见 [SecurityConfig](smart-tools-backend/smart-tools-auth/src/main/java/com/example/smarttools/modules/auth/security/SecurityConfig.java)
- 工具业务：`smart-tools-tools`
  - 工具目录：`GET /api/tools`、`GET /api/tools/{slug}` 等见 [ToolController](smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/controller/ToolController.java)
  - 互动能力：点赞/评论/评论点赞、统计等见 [ToolEngagementController](smart-tools-backend/smart-tools-tools/src/main/java/com/example/smarttools/modules/tools/controller/ToolEngagementController.java)
- 个人中心与日志：`smart-tools-system`
  - 个人中心：`/api/me/**`（资料/设置/收藏/历史/导出等）见 [MeController](smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/me/MeController.java)
  - 前端上报：`/api/logs/visit`、`/api/logs/error` 见 [LogController](smart-tools-backend/smart-tools-system/src/main/java/com/example/smarttools/modules/system/controller/log/LogController.java)
- 监控探活：`smart-tools-monitor`
  - 依赖探活：`GET /api/monitor/health`（MySQL/Redis/RabbitMQ）见 [HealthController](smart-tools-backend/smart-tools-monitor/src/main/java/com/example/smarttools/modules/monitor/controller/HealthController.java)

更多后端细节见 [smart-tools-backend/README.md](./smart-tools-backend/README.md)。

## 典型业务链路（从页面到接口）

- 进入首页 → 前端先用本地工具注册中心渲染目录 → 再拉后端 `GET /api/tools` 覆盖名称/分类/上下线/排序等
- 进入工具页 `/tools/:slug`（受路由守卫保护）→ 未登录会跳首页并附带 `?login=1&redirect=...` → 登录成功回跳 → 工具页拉取 `stats/comments` 并支持点赞/评论
- 前端异常与访问日志 → `POST /api/logs/error`、`POST /api/logs/visit` → 后端异步落库，便于统计与排障

## 本地开发（快速启动）

### 1) 启动后端

```bash
cd smart-tools-backend
mvn -pl smart-tools-api -am spring-boot:run
```

- 默认 API：http://localhost:9090
- 配置入口： [application.yml](smart-tools-backend/smart-tools-api/src/main/resources/application.yml)

### 2) 启动前端

```bash
cd smart-tools-frontend
npm install
npm run serve
```

- 默认前端：http://localhost:5173
- 前端开发环境会代理 `/api` 到后端（默认 `http://localhost:9090`，可用 `VUE_APP_BACKEND_URL` 覆盖）

## 构建与部署

### 前端构建

```bash
cd smart-tools-frontend
npm run build
```

产物在 `smart-tools-frontend/dist/`；部署到静态站点时需要 SPA 回退到 `index.html`。

### 后端打包与运行

```bash
cd smart-tools-backend
mvn -pl smart-tools-api -am package
```

通常会在 `smart-tools-backend/smart-tools-api/target/` 生成可运行 jar。


