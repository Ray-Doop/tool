/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/main.js
 * 用途：前端 JavaScript 源码文件
 * 归属：前端 frontend
 * 依赖：vue、./App.vue、element-plus、pinia、./store/auth、./router
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
// 应用入口：初始化 Vue 应用、全局插件、路由与样式
import { createApp } from 'vue'
import App from './App.vue'

// UI 组件库：Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 全局状态管理：Pinia
import { createPinia } from 'pinia'
import { useAuthStore } from './store/auth'

// 前端路由
import { router } from './router'

// Tailwind 基础样式
import './styles/index.css'

const logError = async payload => {
  try {
    await fetch('/api/logs/error', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
  } catch (e) {
  }
}

;(async () => {
  const app = createApp(App)
  const pinia = createPinia()
  app.use(pinia).use(router).use(ElementPlus, { zIndex: 10000 })

  app.config.errorHandler = (err, instance, info) => {
    logError({
      source: 'frontend',
      level: 'error',
      message: err?.message || 'Vue error',
      detail: info || '',
      pageUrl: window.location.href,
      stack: err?.stack || ''
    })
  }

  window.addEventListener('error', event => {
    const err = event?.error
    logError({
      source: 'frontend',
      level: 'error',
      message: err?.message || event?.message || 'Window error',
      detail: '',
      pageUrl: window.location.href,
      stack: err?.stack || ''
    })
  })

  window.addEventListener('unhandledrejection', event => {
    const reason = event?.reason
    logError({
      source: 'frontend',
      level: 'error',
      message: reason?.message || 'Unhandled rejection',
      detail: typeof reason === 'string' ? reason : '',
      pageUrl: window.location.href,
      stack: reason?.stack || ''
    })
  })

  const auth = useAuthStore(pinia)
  await auth.silentRefresh()

  app.mount('#app')
})()
