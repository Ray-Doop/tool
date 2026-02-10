/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/api/log.js
 * 用途：前端 API 封装（对接后端接口与鉴权）
 * 归属：前端 api
 * 依赖：@/api/http
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
import { http } from '@/api/http'

export const logApi = {
  async visit(toolSlug, path) {
    try {
      await http.post('/api/logs/visit', { toolSlug, path: path || window.location.pathname })
    } catch (e) {
      // 忽略日志上报错误，不影响主流程
      console.warn('log visit failed', e)
    }
  }
}
