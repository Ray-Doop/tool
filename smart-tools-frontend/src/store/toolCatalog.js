/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/store/toolCatalog.js
 * 用途：前端状态管理（Pinia store）
 * 归属：前端 store
 * 依赖：pinia、@/tools/registry、@/api/tools、vue、@/components/GenericFileTool.vue
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
/*
  工具目录状态管理：
  - 本地 toolRegistry 作为“离线可用”的基础数据
  - 后端 /api/tools 用于动态覆盖：名称/分类/描述/上下线/排序等
*/
import { defineStore } from 'pinia'
import { toolRegistry } from '@/tools/registry'
import { toolsApi } from '@/api/tools'
import { defineComponent, h, markRaw } from 'vue'
import GenericFileTool from '@/components/GenericFileTool.vue'

// 工具目录 Store：本地注册中心 + 远端配置的混合模式
export const useToolCatalogStore = defineStore('toolCatalog', {
  state: () => ({
    // Flag to track if data is loaded
    loaded: false,
    search: '',
    tools: [],
    activeCategory: 'all'
  }),
  getters: {
    filteredTools: state => {
      const byCategory = state.activeCategory === 'all'
        ? state.tools
        : state.tools.filter(t => t.category === state.activeCategory)
      const q = (typeof state.search === 'string' ? state.search : '').trim().toLowerCase()
      if (!q) return byCategory
      return byCategory.filter(t => {
        // Handle keywords whether they are array (local) or string (server)
        const keywords = Array.isArray(t.keywords) ? t.keywords.join(' ') : (t.keywords || '')
        const hay = [t.slug, t.name, t.description ?? '', keywords].join(' ').toLowerCase()
        return hay.includes(q)
      })
    },
    categories: state => {
      const set = new Set()
      for (const t of state.tools) {
        if (t.category) set.add(t.category)
      }
      const order = [
        '文档处理',
        '图片处理',
        '音视频',
        '数据处理',
        '图表',
        '开发',
        '开发工具',
        '编码/转换',
        '格式转换',
        '文本处理',
        '设计工具',
        '文件',
        '安全',
        '加密/解密',
        '编码/标识',
        '生成器'
      ]
      const ordered = []
      for (const key of order) {
        if (set.has(key)) {
          ordered.push(key)
          set.delete(key)
        }
      }
      return [...ordered, ...Array.from(set).sort((a, b) => a.localeCompare(b))]
    }
  },
  actions: {
    setSearch(v) {
      this.search = v
    },
    setCategory(cat) {
      this.activeCategory = cat || 'all'
    },
    bySlug(slug) {
      return this.tools.find(t => t.slug === slug)
    },
    ensureLoaded() {
      // 首次加载：优先使用前端本地注册中心（离线也可用）
      if (this.loaded) return
      this.loaded = true
      this.tools = toolRegistry.list()
      if (this.activeCategory !== 'all' && !this.tools.some(t => t.category === this.activeCategory)) {
        this.activeCategory = 'all'
      }
      // 再异步拉取后端配置（用于动态控制名称/分类/上下线/排序等）
      void this.refreshFromServer()
    },
    async refreshFromServer() {
      try {
        const serverTools = await toolsApi.list()
        const localBySlug = new Map(this.tools.map(t => [t.slug, t]))
        const merged = serverTools.map(st => {
          const local = localBySlug.get(st.slug)
          
          // Normalize keywords to array
          let kw = st.keywords ?? local?.keywords
          if (typeof kw === 'string') {
            kw = kw.split(',').map(k => k.trim()).filter(k => k)
          } else if (!Array.isArray(kw)) {
            kw = []
          }

          return {
            ...local,
            slug: st.slug,
            name: st.name,
            category: st.category,
            description: st.description ?? local?.description,
            keywords: kw,
            enabled: st.enabled ?? local?.enabled ?? true,
            sortOrder: typeof st.sortOrder === 'number' ? st.sortOrder : (local?.sortOrder ?? 0),
            component: local?.component || markRaw(defineComponent({
              name: `ServerTool_${st.slug}`,
              setup() {
                return () => h(GenericFileTool, {
                  title: st.name,
                  description: st.description,
                  mode: st.slug
                })
              }
            }))
          }
        })
        this.tools = merged.filter(t => t && t.slug)
        if (this.activeCategory !== 'all' && !this.tools.some(t => t.category === this.activeCategory)) {
          this.activeCategory = 'all'
        }
      } catch {
      }
    }
  }
})
