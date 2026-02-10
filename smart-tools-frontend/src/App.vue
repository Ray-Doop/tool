<!--
  @generated-file-note
  文件：smart-tools-frontend/src/App.vue
  用途：前端 Vue 单文件组件
  归属：前端 frontend
  依赖：vue-router
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  App 根组件：
  - 仅承载路由出口 RouterView
  - 具体页面由 pages 与 layouts 组合渲染
-->
<template>
  <!-- App 根容器：由路由渲染页面 -->
  <RouterView />
</template>

<script setup>
// 仅引入 RouterView：页面结构由 pages + layouts 组合完成
import { RouterView } from 'vue-router'
</script>
