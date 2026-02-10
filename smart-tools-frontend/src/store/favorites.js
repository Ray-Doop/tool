/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/store/favorites.js
 * 用途：前端状态管理（Pinia store）
 * 归属：前端 store
 * 依赖：pinia、@/api/me、@/store/auth
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
/*
  收藏状态管理：
  - 未登录：使用 localStorage 本地收藏，保证可用性
  - 已登录：与后端 /api/me/favorites 同步，服务端为准
*/
import { defineStore } from 'pinia'
import { meApi } from '@/api/me'
import { useAuthStore } from '@/store/auth'

// localStorage key：用于未登录时本地收藏、或登录后缓存收藏列表
const storageKey = 'smart-tools-favorites'

export const useFavoritesStore = defineStore('favorites', {
  state: () => {
    // 初始先从 localStorage 读取，随后再尝试从服务端刷新（若已登录）
    const raw = localStorage.getItem(storageKey)
    if (!raw) return { loaded: false, items: [] }
    try {
      return { loaded: false, items: JSON.parse(raw) }
    } catch {
      return { loaded: false, items: [] }
    }
  },
  actions: {
    ensureLoaded() {
      // 仅首次触发一次 refresh，避免重复请求
      if (this.loaded) return
      this.loaded = true
      void this.refreshFromServer()
    },
    isFavorite(slug) {
      return this.items.includes(slug)
    },
    async toggle(slug) {
      if (!slug) return

      const auth = useAuthStore()
      if (!auth.isAuthed) {
        // 未登录：直接走本地收藏，保证体验可用
        this.items = this.items.includes(slug) ? this.items.filter(s => s !== slug) : [...this.items, slug]
        localStorage.setItem(storageKey, JSON.stringify(this.items))
        return
      }

      if (this.items.includes(slug)) {
        // 已收藏 -> 取消收藏
        await meApi.removeFavorite(slug)
        this.items = this.items.filter(s => s !== slug)
      } else {
        // 未收藏 -> 收藏
        await meApi.addFavorite(slug)
        this.items = [...this.items, slug]
      }
      localStorage.setItem(storageKey, JSON.stringify(this.items))
    },
    async refreshFromServer() {
      const auth = useAuthStore()
      if (!auth.isAuthed) return
      // 已登录：以服务端收藏列表为准
      const items = await meApi.favorites()
      this.items = items
      localStorage.setItem(storageKey, JSON.stringify(this.items))
    }
  }
})
