<!--
  @generated-file-note
  文件：smart-tools-frontend/src/modules/README.md
  用途：说明文档
  归属：前端 frontend
  用途：项目说明/设计文档
  维护：与代码/配置变更保持一致，避免文档与实现脱节
-->
# 前端模块化说明

## 分层

- `src/modules/core`：应用级基础设施（注册中心、HTTP、路由守卫等）
- `src/modules/tools`：工具模块（每个工具一个目录）
- `src/modules/user`：用户域模块（登录态、收藏等）
- `src/pages`：页面容器（组合多个模块形成页面）
- `src/layouts`：布局组件
- `src/components`：通用 UI 组件

## 工具模块约定

每个工具目录至少包含：

- `src/tools/<slug>/meta.js`：工具元数据（名称/分类/关键字等）
- `src/tools/<slug>/index.vue`：工具页面组件

