/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/api/auth.js
 * 用途：前端 API 封装（对接后端接口与鉴权）
 * 归属：前端 api
 * 依赖：@/api/http
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
/*
  认证相关 API 封装：
  - 调用后端 /api/auth/** 接口
  - 只处理“请求 + success 判断”，不保存状态
  - token/username 等状态由 Pinia auth store 负责
*/
import { http } from '@/api/http'

// 认证 API（前端封装层）：
// - 只做“请求 + 统一解析后端 ApiResponse”
// - 业务状态（token/username）由 Pinia auth store 维护
// - 失败统一抛出 Error，交由 UI 层决定如何提示
export const authApi = {
  async captcha() {
    const { data } = await http.get('/api/auth/captcha')
    if (!data?.success) throw new Error(data?.error?.message ?? 'captcha failed')
    return data.data
  },
  async register(username, password) {
    // 旧版注册：username + password
    const { data } = await http.post('/api/auth/register', { username, password })
    if (!data?.success) throw new Error(data?.error?.message ?? 'register failed')
    return data.data.token
  },
  async login(username, password) {
    // 旧版登录：只支持 username
    const { data } = await http.post('/api/auth/login', { username, password })
    if (!data?.success) throw new Error(data?.error?.message ?? 'login failed')
    return data.data.token
  },
  async registerEmail(email, emailCode, password, confirmPassword, username, captchaId, captchaCode) {
    // 邮箱密码注册：username 可不传（后端会自动生成可用用户名）
    const { data } = await http.post('/api/auth/register/email', {
      email,
      emailCode,
      password,
      confirmPassword,
      username: username || undefined,
      captchaId,
      captchaCode
    })
    if (!data?.success) throw new Error(data?.error?.message ?? 'register email failed')
    return data.data.token
  },
  async registerPhone(phone, password, confirmPassword, username) {
    // 手机号密码注册：当前 UI 已去掉短信验证码登录，但保留该能力
    const { data } = await http.post('/api/auth/register/phone', {
      phone,
      password,
      confirmPassword,
      username: username || undefined
    })
    if (!data?.success) throw new Error(data?.error?.message ?? 'register phone failed')
    return data.data.token
  },
  async loginPassword(identifier, password, captchaId, captchaCode) {
    const { data } = await http.post('/api/auth/login/password', { identifier, password, captchaId, captchaCode })
    if (!data?.success) throw new Error(data?.error?.message ?? 'password login failed')
    return data.data.token
  },
  async otpSend(channel, target, captchaId, captchaCode, scene) {
    const payload = { channel, target }
    const cid = captchaId || 'skip'
    const ccode = captchaCode || 'skip'
    payload.captchaId = cid
    payload.captchaCode = ccode
    if (scene) payload.scene = scene
    const { data } = await http.post('/api/auth/otp/send', payload)
    if (!data?.success) throw new Error(data?.error?.message ?? 'otp send failed')
    return data.data
  },
  async loginOtp(channel, target, code, captchaId, captchaCode) {
    // 验证码登录：登录成功后后端返回 token
    const { data } = await http.post('/api/auth/login/otp', { channel, target, code, captchaId, captchaCode })
    if (!data?.success) throw new Error(data?.error?.message ?? 'otp login failed')
    return data.data
  },
  async otpCheck(channel, target, code) {
    try {
      const { data } = await http.post('/api/auth/otp/check', { channel, target, code })
      if (!data?.success) return false
      return true
    } catch (e) {
      if (e?.code === 'OTP_INVALID') return false
      throw e
    }
  },
  async wechatQr() {
    // 微信扫码（开发模式）：获取 state 与 qrContent
    const { data } = await http.get('/api/auth/wechat/qr')
    if (!data?.success) throw new Error(data?.error?.message ?? 'wechat qr failed')
    return data.data
  },
  async wechatPoll(state) {
    // 微信扫码轮询：后端返回 pending/authed/expired
    const { data } = await http.get('/api/auth/wechat/poll', { params: { state } })
    if (!data?.success) throw new Error(data?.error?.message ?? 'wechat poll failed')
    return data.data
  },
  async wechatRegister(state, payload) {
    const { data } = await http.post('/api/auth/wechat/register', { state, ...payload })
    if (!data?.success) throw new Error(data?.error?.message ?? 'wechat register failed')
    return data.data.token
  },
  async wechatBind(state, payload) {
    const { data } = await http.post('/api/auth/wechat/bind', { state, ...payload })
    if (!data?.success) throw new Error(data?.error?.message ?? 'wechat bind failed')
    return data.data.token
  },
  async wechatDevConfirm(state, username) {
    // 开发模式模拟扫码确认：会直接签发 token
    const { data } = await http.post('/api/auth/wechat/dev/confirm', { state, username: username || undefined })
    if (!data?.success) throw new Error(data?.error?.message ?? 'wechat confirm failed')
    return data.data.token
  },
  async refresh() {
    const { data } = await http.post('/api/auth/refresh')
    if (!data?.success) throw new Error(data?.error?.message ?? 'refresh failed')
    return data.data.token
  },
  async logout() {
    const { data } = await http.post('/api/auth/logout')
    if (!data?.success) throw new Error(data?.error?.message ?? 'logout failed')
  }
}
