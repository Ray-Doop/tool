/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/composables/useI18n.js
 * 用途：前端 JavaScript 源码文件
 * 归属：前端 frontend
 * 依赖：vue、@/store/app、@/i18n/messages
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
import { computed } from 'vue'
import { useAppStore } from '@/store/app'
import messages from '@/i18n/messages'

export function useI18n() {
  const store = useAppStore()
  
  const currentLanguage = computed(() => store.language)
  
  const t = computed(() => {
    return messages[store.language] || messages['zh']
  })
  
  const setLanguage = (lang) => {
    store.setLanguage(lang)
  }
  
  return {
    currentLanguage,
    t,
    setLanguage
  }
}
