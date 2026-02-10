<!--
  @generated-file-note
  文件：smart-tools-frontend/README.md
  用途：说明文档
  归属：前端 frontend
  用途：项目说明/设计文档
  维护：与代码/配置变更保持一致，避免文档与实现脱节
-->
# Smart Tools Frontend (Vue CLI)

## 安装依赖

在 [package.json](file:///c:/Users/30274/Desktop/xxm/test_vue/package.json) 更新后，执行：

```bash
cd C:\Users\30274\Desktop\xxm\test_vue
npm install
```

## 本地启动

```bash
cd C:\Users\30274\Desktop\xxm\test_vue
npm run serve
```

- 前端默认端口：5173（见 [vue.config.js](file:///c:/Users/30274/Desktop/xxm/test_vue/vue.config.js)）
- 已配置代理：`/api` -> `http://localhost:8080`

## 工具模块约定

每个工具一个目录，至少包含：

- `src/tools/<slug>/meta.js`
- `src/tools/<slug>/index.vue`

工具会被 [registry.js](file:///c:/Users/30274/Desktop/xxm/test_vue/src/tools/registry.js) 自动扫描注册。

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
