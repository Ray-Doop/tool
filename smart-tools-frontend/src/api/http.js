/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/api/http.js
 * 用途：前端 API 封装（对接后端接口与鉴权）
 * 归属：前端 api
 * 依赖：axios、@/store/auth
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
/*
  HTTP 客户端封装：
  - 统一 baseURL/timeout
  - 自动注入 JWT（Authorization: Bearer <token>）
  - 统一把后端 ApiResponse 错误转换为 Error.message，便于 UI 提示
*/
import axios from 'axios'
import { useAuthStore } from '@/store/auth'

// 统一 HTTP 客户端：集中做超时、拦截器、鉴权注入
export const http = axios.create({
  // baseURL 设置为 '/'：配合 vue devServer proxy，把 /api 转发到后端
  // 如果不走代理，也可以把 baseURL 改成后端地址（例如 http://localhost:9090）
  baseURL: '/',
  timeout: 15000,
  withCredentials: true
})

http.interceptors.request.use(config => {
  // 统一注入 JWT：后端使用 Bearer Token 进行鉴权
  const auth = useAuthStore()
  if (auth.token) {
    // axios 在不同场景 config.headers 可能为空，这里做兜底
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

let refreshingPromise = null

http.interceptors.response.use(
  res => res,
  async err => {
    // 统一错误转换：
    // - 网络/代理错误：err.response 不存在
    // - 后端返回的业务错误：按 ApiResponse.error.message 透传为 Error.message
    if (!err?.response) {
      const e = new Error('后端不可用（请确认后端已启动且端口/代理配置正确）')
      e.cause = err
      throw e
    }

    const status = err.response?.status
    const originalConfig = err.config
    const url = String(originalConfig?.url ?? '')
    const isAuthApi = url.startsWith('/api/auth/')
    if (status === 401 && originalConfig && !originalConfig.__isRetryRequest && !isAuthApi) {
      const auth = useAuthStore()
      try {
        if (!refreshingPromise) {
          refreshingPromise = auth.refreshAccessToken().finally(() => {
            refreshingPromise = null
          })
        }
        await refreshingPromise
        originalConfig.__isRetryRequest = true
        originalConfig.headers = originalConfig.headers ?? {}
        if (auth.token) {
          originalConfig.headers.Authorization = `Bearer ${auth.token}`
        }
        return await http(originalConfig)
      } catch (e) {
        await auth.logout()
        throw e
      }
    }

    const code = err.response?.data?.error?.code
    const msg =
      err.response?.data?.error?.message ||
      err.response?.data?.message ||
      `请求失败（HTTP ${err.response.status}）`
    const e = new Error(msg)
    e.code = code
    e.cause = err
    throw e
  }
)
