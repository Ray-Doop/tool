# Smart Tools Frontend
基于 Vue 3 + Vue CLI（@vue/cli-service 5）实现的前端 SPA，提供工具列表、工具详情、用户中心与管理后台等能力。整体采用“工具插件化”架构：每个工具一个独立目录，通过约定（`meta.js` + `index.vue`）自动注册到工具列表与路由。

## 主要功能
- 工具目录：分类筛选、搜索、热门/新增推荐
- 工具详情页：动态加载工具组件、点赞/收藏、评论/回复
- 登录体系：验证码登录、密码登录、微信扫码登录（含开发模式模拟扫码）
- 用户中心：个人信息、设置、收藏（未登录时本地收藏，登录后与后端同步）
- 管理后台：用户/角色/权限/工具等管理页面入口（需要管理员权限）

## 技术栈
- Vue 3、Vue Router 4、Pinia 2
- Vue CLI 5（webpack）、Babel、ESLint
- UI：Element Plus、TailwindCSS
- 网络：Axios（统一拦截器、自动注入 token、401 自动刷新）
- 工具能力（按需使用）：ECharts、Mermaid、FFmpeg.wasm、pdf-lib、pdfjs-dist、xlsx、tesseract.js 等

## 核心设计（读代码入口）
- 工具注册中心：[registry.js](smart-tools-frontend/src/tools/registry.js)
  - 自动扫描 `src/tools/**/meta.js` 与 `src/tools/**/index.vue` 进行注册（`require.context`）
  - 输出的每个工具对象包含：`slug/name/category/description/keywords/enabled/sortOrder/component`
- 工具目录 Store：[toolCatalog.js](smart-tools-frontend/src/store/toolCatalog.js)
  - 首次加载使用本地注册中心 `toolRegistry.list()`（离线也可用）
  - 再异步拉取后端 `/api/tools` 覆盖名称/分类/上下线/排序等（不会引入未知前端组件）
- 鉴权与刷新：[auth store](smart-tools-frontend/src/store/auth.js) + [http.js](smart-tools-frontend/src/api/http.js)
  - token 默认只放内存；用户信息持久化到 `localStorage`
  - `http` 请求拦截器自动加 `Authorization: Bearer <token>`
  - 遇到 401 会自动走 `/api/auth/refresh` 刷新并重试（排除 `/api/auth/**` 自身）
- 路由与守卫：[router](smart-tools-frontend/src/router/index.js)
  - `/tools/:slug`、`/profile`、`/settings`、`/admin/**` 均需要登录
  - 管理后台页面额外要求 `user.isAdmin`
- 错误上报：[main.js](smart-tools-frontend/src/main.js#L24-L73)
  - Vue error/window error/unhandledrejection 会上报到 `/api/logs/error`

## 目录结构
```text
smart-tools-frontend/
  public/                 # HTML 模板与静态资源
  src/
    api/                  # 后端 API 封装（axios）
    components/           # 通用组件
    composables/          # 组合式函数（hooks）
    i18n/                 # 文案/国际化
    layouts/              # 布局（App/Admin）
    pages/                # 页面（路由级）
    router/               # 路由与守卫
    store/                # Pinia 状态
    styles/               # 全局样式
    tools/                # 具体工具模块（按 slug 组织）
  vue.config.js           # Vue CLI 配置（端口、代理、标题等）
```

## 环境要求
- Node.js（建议使用 LTS 版本）
- npm

## 快速开始
在仓库根目录进入前端目录：

```bash
cd smart-tools-frontend
npm install
```

启动开发服务：

```bash
npm run serve
```

默认访问：

- 前端：http://localhost:5173

## 后端联调（代理 /api）

开发环境通过 devServer 代理将 `/api` 与 `/uploads` 转发到后端，避免跨域问题。

- 默认后端地址：`http://localhost:9090`
- 可通过环境变量覆盖：`VUE_APP_BACKEND_URL`（构建期注入，Vue CLI 约定以 `VUE_APP_` 开头）

示例（PowerShell）：

```bash
$env:VUE_APP_BACKEND_URL="http://localhost:9090"
npm run serve
```

代理与端口配置见 [vue.config.js](./vue.config.js)。

## 需要的后端接口（约定）

前端默认假设后端提供以下能力（接口名以当前代码为准）：
- 认证：`/api/auth/*`（验证码、密码登录、OTP、微信扫码、refresh、logout）
- 用户：`/api/me/*`（个人资料、设置、收藏、最近使用等）
- 工具：`/api/tools`、`/api/tools/:slug/stats`、`/api/tools/:slug/comments`、`/api/tools/:slug/like`
- 日志：`/api/logs/visit`、`/api/logs/error`
- 管理后台：`/api/admin/*`（用户/角色/权限/工具/监控/审计等）

## 站点标题（浏览器 Tab Title）

全站默认标题由 Vue CLI 的 pages 配置提供：
- 配置位置：[vue.config.js](./vue.config.js) 的 `pages.index.title`
- HTML 模板兜底：[public/index.html](./public/index.html)
如需修改站点标题，优先修改 `pages.index.title`。

## 工具模块约定（新增工具）
每个工具一个目录（slug 作为目录名），至少包含：
- `src/tools/<slug>/meta.js`：工具元信息（名称、分类等）
- `src/tools/<slug>/index.vue`：工具页面组件
工具会由 `src/tools/registry.js` 统一注册与渲染。新增工具后，通常不需要手动改路由。

### meta.js 字段建议
`meta.js` 默认导出一个对象，常用字段：
- `slug`：与目录名一致（路由参数也是它）
- `name`：展示名称
- `category`：分类（用于侧边栏与筛选）
- `description`：卡片/详情页描述
- `keywords`：搜索关键字数组
- `enabled`：可选；`false` 表示下线（不会出现在列表里）
- `sortOrder`：可选；同分类下排序权重（数字越小越靠前）

示例见：[uuid-generator meta.js](smart-tools-frontend/src/tools/uuid-generator/meta.js#L6-L13)。

## 构建与部署

构建：

```bash
cd smart-tools-frontend
npm run build
```

构建产物输出到 `smart-tools-frontend/dist/`。将 `dist` 目录部署到静态站点即可（Nginx、对象存储、CDN 等）。

### SPA 回退（history 模式）

项目使用 `createWebHistory()`，静态服务器需要配置回退到 `index.html`。

Nginx 示例：

```nginx
server {
  listen 80;
  server_name _;

  root /var/www/smart-tools-frontend/dist;
  index index.html;

  location / {
    try_files $uri $uri/ /index.html;
  }
}
```

## 常用命令

```bash
npm run serve
npm run build
npm run lint
```

## 常见问题

### 访问后端报错“后端不可用”

前端 `http` 层会把网络类错误统一提示为“后端不可用…”。优先检查：

- 后端是否启动、端口是否为 `9090`
- 是否正确配置了 `VUE_APP_BACKEND_URL`
- 是否确实通过前端访问（开发环境应访问 `http://localhost:5173`，由 devServer 代理转发 `/api`）

### 为什么工具页需要登录？

路由守卫对 `/tools/:slug` 设置了 `requiresAuth`，未登录会回到首页并自动弹出登录弹窗（通过 `?login=1` 触发）。
