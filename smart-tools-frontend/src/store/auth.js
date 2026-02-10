/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/store/auth.js
 * 用途：前端状态管理（Pinia store）
 * 归属：前端 store
 * 依赖：pinia、@/api/auth、@/api/me
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
/*
  Pinia 鉴权状态管理：
  - 保存 token/username，并持久化到 localStorage
  - 封装登录/注册/验证码/微信扫码等动作
  - http 层会自动把 token 注入到请求头
*/
import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'
import { meApi } from '@/api/me'

// localStorage key：用于刷新页面后保持登录状态
const storageKey = 'smart-tools-auth'

// 前端鉴权状态（Pinia）：
// - token：JWT 字符串（后端签发）
// - username：用于 UI 展示（优先从 /api/me 获取，失败时用 fallback）
//
// 设计点：
// - http 拦截器会自动把 token 注入到 Authorization: Bearer <token>
// - 每次登录成功后通过 applyToken 统一写入 localStorage，保证刷新不丢登录态
export const useAuthStore = defineStore('auth', {
  state: () => {
    // 初始化时只从 localStorage 恢复 username（token 只放内存，靠 refresh cookie 静默刷新）
    const raw = localStorage.getItem(storageKey)
    if (!raw) return { token: null, user: null }
    try {
      const parsed = JSON.parse(raw)
      return { token: null, user: parsed?.user ?? null }
    } catch {
      return { token: null, user: null }
    }
  },
  getters: {
    // 是否已登录
    isAuthed: state => Boolean(state.token),
    username: state => state.user?.username,
    isAdmin: state => Boolean(state.user?.isAdmin)
  },
  actions: {
    persistAuthState() {
      localStorage.setItem(storageKey, JSON.stringify({ user: this.user }))
    },
    async applyToken(token, fallbackUsername) {
      // 统一处理“写入 token + 拉取用户信息 + 持久化”：
      // - token 写入后即可访问 /api/me
      // - /api/me 成功时 username 以服务端为准，避免前端猜测
      this.token = token
      try {
        const me = await meApi.me()
        this.user = me
      } catch {
        this.user = { username: fallbackUsername, isAdmin: false }
      }
      this.persistAuthState()
    },
    updateUser(userData) {
      this.user = { ...this.user, ...userData }
      this.persistAuthState()
    },
    async refreshAccessToken() {
      const token = await authApi.refresh()
      this.token = token
      this.persistAuthState()
    },
    async silentRefresh() {
      if (!localStorage.getItem(storageKey)) return
      try {
        const token = await authApi.refresh()
        await this.applyToken(token, this.username)
      } catch {
        this.logout()
      }
    },
    async login(username, password) {
      // 旧版：用户名密码登录
      const token = await authApi.login(username, password)
      await this.applyToken(token, username)
    },
    async register(username, password) {
      // 旧版：用户名密码注册（注册即登录）
      const token = await authApi.register(username, password)
      await this.applyToken(token, username)
    },
    async loginPassword(identifier, password, captchaId, captchaCode) {
      // 推荐：identifier 支持 用户名/邮箱/手机号（后端解析）
      const token = await authApi.loginPassword(identifier, password, captchaId, captchaCode)
      await this.applyToken(token, identifier)
    },
    async registerEmail(email, emailCode, password, confirmPassword, username, captchaId, captchaCode) {
      // 邮箱密码注册：username 可不传
      const token = await authApi.registerEmail(email, emailCode, password, confirmPassword, username, captchaId, captchaCode)
      await this.applyToken(token, username || email)
    },
    async registerPhone(phone, password, confirmPassword, username) {
      // 手机号密码注册：当前 UI 已去掉短信验证码登录，但注册能力保留
      const token = await authApi.registerPhone(phone, password, confirmPassword, username)
      await this.applyToken(token, username || phone)
    },
    async otpSend(channel, target, captchaId, captchaCode, scene) {
      return await authApi.otpSend(channel, target, captchaId, captchaCode, scene)
    },
    async loginOtp(channel, target, code, captchaId, captchaCode) {
      // 验证码登录：成功后返回 token（登录即注册）
      const res = await authApi.loginOtp(channel, target, code, captchaId, captchaCode)
      await this.applyToken(res?.token, target)
      return res?.needsRegister
    },
    async wechatDevConfirm(state, username) {
      // 微信扫码（开发模式）确认：直接返回 token
      const token = await authApi.wechatDevConfirm(state, username)
      await this.applyToken(token, username || 'wechat')
    },
    async wechatRegister(state, registerMode, email, phone, username, password, captchaId, captchaCode, emailCode) {
      const payload = {
        username: username || undefined,
        email: registerMode === 'email' ? email || undefined : undefined,
        phone: registerMode === 'phone' ? phone || undefined : undefined,
        password,
        captchaId,
        captchaCode,
        emailCode: emailCode || undefined
      }
      const token = await authApi.wechatRegister(state, payload)
      this.token = token
      this.user = { username: username || email || phone || 'wechat', isAdmin: false }
      this.persistAuthState()
    },
    async wechatBind(state, identifier, password, captchaId, captchaCode) {
      const payload = { identifier, password, captchaId, captchaCode }
      const token = await authApi.wechatBind(state, payload)
      await this.applyToken(token, identifier)
    },
    async completeRegistration(username, password, confirmPassword) {
      const profile = await meApi.completeRegistration({ username, password, confirmPassword })
      if (profile) {
        this.user = {
          ...this.user,
          username: profile.username,
          displayName: profile.displayName,
          avatarUrl: profile.avatarUrl
        }
        this.persistAuthState()
      }
      return profile
    },
    async logout() {
      try {
        await authApi.logout()
      } catch {
      }
      this.token = null
      this.user = null
      localStorage.removeItem(storageKey)
    }
  }
})
