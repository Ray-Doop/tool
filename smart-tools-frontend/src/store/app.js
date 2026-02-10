/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/store/app.js
 * 用途：前端状态管理（Pinia store）
 * 归属：前端 store
 * 依赖：pinia
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    language: localStorage.getItem('smart-tools-lang') || 'zh'
  }),
  actions: {
    setLanguage(lang) {
      this.language = lang
      localStorage.setItem('smart-tools-lang', lang)
    }
  }
})
