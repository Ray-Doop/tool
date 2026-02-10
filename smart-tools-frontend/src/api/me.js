/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/api/me.js
 * 用途：前端 API 封装（对接后端接口与鉴权）
 * 归属：前端 api
 * 依赖：@/api/http
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
/*
  用户相关 API 封装：
  - 需要登录；token 由 http 拦截器自动注入
  - 提供“我的信息 / 收藏列表 / 收藏增删”等能力
*/
import { http } from '@/api/http'

// 个人中心 API：需要登录（token 会由 http 拦截器自动注入）
export const meApi = {
  async me() {
    // 获取当前登录用户信息：后端从 JWT 解析出 userId/username
    const { data } = await http.get('/api/me')
    if (!data?.success) throw new Error(data?.error?.message ?? 'me failed')
    return data.data
  },
  async favorites() {
    // 收藏列表：返回工具 slug 数组
    const { data } = await http.get('/api/me/favorites')
    if (!data?.success) throw new Error(data?.error?.message ?? 'favorites failed')
    return data.data
  },
  async addFavorite(toolSlug) {
    // toolSlug 可能包含特殊字符，因此使用 encodeURIComponent
    const { data } = await http.post(`/api/me/favorites/${encodeURIComponent(toolSlug)}`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'favorite add failed')
  },
  async removeFavorite(toolSlug) {
    // 取消收藏：DELETE 语义更明确
    const { data } = await http.delete(`/api/me/favorites/${encodeURIComponent(toolSlug)}`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'favorite remove failed')
  },
  async profile() {
    const { data } = await http.get('/api/me/profile')
    if (!data?.success) throw new Error(data?.error?.message ?? 'profile failed')
    return data.data
  },
  async settings() {
    const { data } = await http.get('/api/me/settings')
    if (!data?.success) throw new Error(data?.error?.message ?? 'settings failed')
    return data.data
  },
  async updateProfile(profileData) {
    const { data } = await http.put('/api/me/profile', profileData)
    if (!data?.success) throw new Error(data?.error?.message ?? 'update profile failed')
    return data.data
  },
  async updateSettings(settingsData) {
    const { data } = await http.put('/api/me/settings', settingsData)
    if (!data?.success) throw new Error(data?.error?.message ?? 'update settings failed')
    return data.data
  },
  async changePassword(passwordData) {
    const { data } = await http.post('/api/me/settings/password', passwordData)
    if (!data?.success) throw new Error(data?.error?.message ?? 'change password failed')
    return data.data
  },
  async completeRegistration(payload) {
    const { data } = await http.post('/api/me/complete-registration', payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'complete registration failed')
    return data.data
  },
  async uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    const { data } = await http.post('/api/me/profile/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (!data?.success) throw new Error(data?.error?.message ?? 'upload avatar failed')
    return data.data
  },
  async recentTools() {
    const { data } = await http.get('/api/me/recent-tools')
    if (!data?.success) throw new Error(data?.error?.message ?? 'get recent tools failed')
    return data.data
  },
  async loginHistory() {
    const { data } = await http.get('/api/me/login-history')
    if (!data?.success) throw new Error(data?.error?.message ?? 'get login history failed')
    return data.data
  },
  async deleteAccount() {
    const { data } = await http.delete('/api/me/account')
    if (!data?.success) throw new Error(data?.error?.message ?? 'delete account failed')
  },
  async stats() {
    const { data } = await http.get('/api/me/stats')
    if (!data?.success) throw new Error(data?.error?.message ?? 'get stats failed')
    return data.data
  },
  async exportData() {
    const { data } = await http.get('/api/me/export')
    if (!data?.success) throw new Error(data?.error?.message ?? 'export data failed')
    return data.data
  }
}
