/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/api/tools.js
 * 用途：前端 API 封装（对接后端接口与鉴权）
 * 归属：前端 api
 * 依赖：@/api/http
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
/*
  工具目录 API 封装：
  - 从后端读取工具配置（名称/分类/描述等）
  - 前端只覆盖已注册的工具 meta，避免后端下发未知组件
*/
import { http } from '@/api/http'

// 工具目录相关 API：从后端读取工具配置（名称/分类/描述等）
export const toolsApi = {
  async list() {
    const { data } = await http.get('/api/tools')
    if (!data?.success) throw new Error(data?.error?.message ?? 'tools failed')
    return data.data
  },
  async trending() {
    const { data } = await http.get('/api/tools/trending')
    if (!data?.success) return []
    return data.data
  },
  async newTools() {
    const { data } = await http.get('/api/tools/new')
    if (!data?.success) return []
    return data.data
  },
  async stats(slug) {
    const { data } = await http.get(`/api/tools/${encodeURIComponent(slug)}/stats`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'stats failed')
    return data.data
  },
  async like(slug) {
    const { data } = await http.post(`/api/tools/${encodeURIComponent(slug)}/like`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'like failed')
  },
  async unlike(slug) {
    const { data } = await http.delete(`/api/tools/${encodeURIComponent(slug)}/like`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'unlike failed')
  },
  async comments(slug) {
    const { data } = await http.get(`/api/tools/${encodeURIComponent(slug)}/comments`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'comments failed')
    return data.data
  },
  async addComment(slug, payload) {
    const { data } = await http.post(`/api/tools/${encodeURIComponent(slug)}/comments`, payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'comment add failed')
    return data.data
  },
  async updateComment(slug, commentId, payload) {
    const { data } = await http.put(`/api/tools/${encodeURIComponent(slug)}/comments/${commentId}`, payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'comment update failed')
    return data.data
  },
  async deleteComment(slug, commentId) {
    const { data } = await http.delete(`/api/tools/${encodeURIComponent(slug)}/comments/${commentId}`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'comment delete failed')
  },
  async likeComment(slug, commentId) {
    const { data } = await http.post(`/api/tools/${encodeURIComponent(slug)}/comments/${commentId}/like`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'comment like failed')
  },
  async unlikeComment(slug, commentId) {
    const { data } = await http.delete(`/api/tools/${encodeURIComponent(slug)}/comments/${commentId}/like`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'comment unlike failed')
  }
}
