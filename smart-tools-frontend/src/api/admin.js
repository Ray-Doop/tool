/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/api/admin.js
 * 用途：前端 API 封装（对接后端接口与鉴权）
 * 归属：前端 api
 * 依赖：@/api/http
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
import { http } from '@/api/http'

export const adminApi = {
  async listUsers(params = {}) {
    const { data } = await http.get('/api/admin/users', { params })
    if (!data?.success) throw new Error(data?.error?.message ?? 'users failed')
    return data.data
  },
  async updateUserStatus(id, enabled) {
    const { data } = await http.put(`/api/admin/users/${id}/enable`, null, { params: { enabled } })
    if (!data?.success) throw new Error(data?.error?.message ?? 'update user failed')
    return data.data
  },
  async resetUserPassword(id, newPassword) {
    const { data } = await http.post(`/api/admin/users/${id}/reset-password`, { newPassword })
    if (!data?.success) throw new Error(data?.error?.message ?? 'reset password failed')
    return data.data
  },
  async deleteUser(id) {
    const { data } = await http.delete(`/api/admin/users/${id}`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'delete user failed')
    return data.data
  },
  async listUserRoles(id) {
    const { data } = await http.get(`/api/admin/users/${id}/roles`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'user roles failed')
    return data.data
  },
  async updateUserRoles(id, roleIds) {
    const { data } = await http.put(`/api/admin/users/${id}/roles`, { roleIds })
    if (!data?.success) throw new Error(data?.error?.message ?? 'update user roles failed')
    return data.data
  },
  async listModules(params = {}) {
    const { data } = await http.get('/api/admin/modules', { params })
    if (!data?.success) throw new Error(data?.error?.message ?? 'modules failed')
    return data.data
  },
  async createModule(payload) {
    const { data } = await http.post('/api/admin/modules', payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'create module failed')
    return data.data
  },
  async updateModule(id, payload) {
    const { data } = await http.put(`/api/admin/modules/${id}`, payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'update module failed')
    return data.data
  },
  async deleteModule(id) {
    const { data } = await http.delete(`/api/admin/modules/${id}`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'delete module failed')
    return data.data
  },
  async listFunctions(params = {}) {
    const { data } = await http.get('/api/admin/functions', { params })
    if (!data?.success) throw new Error(data?.error?.message ?? 'functions failed')
    return data.data
  },
  async createFunction(payload) {
    const { data } = await http.post('/api/admin/functions', payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'create function failed')
    return data.data
  },
  async updateFunction(id, payload) {
    const { data } = await http.put(`/api/admin/functions/${id}`, payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'update function failed')
    return data.data
  },
  async deleteFunction(id) {
    const { data } = await http.delete(`/api/admin/functions/${id}`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'delete function failed')
    return data.data
  },
  async listRoles(params = {}) {
    const { data } = await http.get('/api/admin/roles', { params })
    if (!data?.success) throw new Error(data?.error?.message ?? 'roles failed')
    return data.data
  },
  async createRole(payload) {
    const { data } = await http.post('/api/admin/roles', payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'create role failed')
    return data.data
  },
  async updateRole(id, payload) {
    const { data } = await http.put(`/api/admin/roles/${id}`, payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'update role failed')
    return data.data
  },
  async deleteRole(id) {
    const { data } = await http.delete(`/api/admin/roles/${id}`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'delete role failed')
    return data.data
  },
  async listRolePermissions(id) {
    const { data } = await http.get(`/api/admin/roles/${id}/permissions`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'role permissions failed')
    return data.data
  },
  async updateRolePermissions(id, permissionIds) {
    const { data } = await http.put(`/api/admin/roles/${id}/permissions`, { permissionIds })
    if (!data?.success) throw new Error(data?.error?.message ?? 'update role permissions failed')
    return data.data
  },
  async listPermissions(params = {}) {
    const { data } = await http.get('/api/admin/permissions', { params })
    if (!data?.success) throw new Error(data?.error?.message ?? 'permissions failed')
    return data.data
  },
  async createPermission(payload) {
    const { data } = await http.post('/api/admin/permissions', payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'create permission failed')
    return data.data
  },
  async updatePermission(id, payload) {
    const { data } = await http.put(`/api/admin/permissions/${id}`, payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'update permission failed')
    return data.data
  },
  async deletePermission(id) {
    const { data } = await http.delete(`/api/admin/permissions/${id}`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'delete permission failed')
    return data.data
  },
  async listErrorTips(params = {}) {
    const { data } = await http.get('/api/admin/error-tips', { params })
    if (!data?.success) throw new Error(data?.error?.message ?? 'error tips failed')
    return data.data
  },
  async listErrorLogs(params = {}) {
    const { data } = await http.get('/api/admin/error-tips/logs', { params })
    if (!data?.success) throw new Error(data?.error?.message ?? 'error logs failed')
    return data.data
  },
  async createErrorTip(payload) {
    const { data } = await http.post('/api/admin/error-tips', payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'create error tip failed')
    return data.data
  },
  async updateErrorTip(id, payload) {
    const { data } = await http.put(`/api/admin/error-tips/${id}`, payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'update error tip failed')
    return data.data
  },
  async deleteErrorTip(id) {
    const { data } = await http.delete(`/api/admin/error-tips/${id}`)
    if (!data?.success) throw new Error(data?.error?.message ?? 'delete error tip failed')
    return data.data
  },
  async listOperationLogs(params = {}) {
    const { data } = await http.get('/api/admin/operation-logs', { params })
    if (!data?.success) throw new Error(data?.error?.message ?? 'operation logs failed')
    return data.data
  },
  async listMetrics(params = {}) {
    const { data } = await http.get('/api/admin/monitor/metrics', { params })
    if (!data?.success) throw new Error(data?.error?.message ?? 'metrics failed')
    return data.data
  },
  async listTools(params = {}) {
    const { data } = await http.get('/api/admin/tools', { params })
    if (!data?.success) throw new Error(data?.error?.message ?? 'tools failed')
    return data.data
  },
  async createTool(payload, options = {}) {
    const { data } = await http.post('/api/admin/tools', payload, { params: { generateFrontend: options.generateFrontend !== false } })
    if (!data?.success) throw new Error(data?.error?.message ?? 'create tool failed')
    return data.data
  },
  async updateTool(id, payload) {
    const { data } = await http.put(`/api/admin/tools/${id}`, payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'update tool failed')
    return data.data
  },
  async deleteTool(id, options = {}) {
    const { data } = await http.delete(`/api/admin/tools/${id}`, { params: { deleteFrontend: options.deleteFrontend !== false } })
    if (!data?.success) throw new Error(data?.error?.message ?? 'delete tool failed')
    return data.data
  }
}
