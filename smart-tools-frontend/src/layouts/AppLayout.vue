<!--
  @generated-file-note
  文件：smart-tools-frontend/src/layouts/AppLayout.vue
  用途：前端 Vue 单文件组件
  归属：前端 frontend
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  AppLayout：应用通用布局。
  - 左侧：工具目录（搜索 + 菜单）
  - 顶部：登录区（登录/退出 + 弹窗）
  - 右侧：页面内容插槽
-->
<template>
  <div class="flex h-screen w-full bg-[#f8fafc] text-slate-600 font-sans overflow-hidden">
    <!-- Main Content Area (Full Width) -->
    <div class="flex-1 flex flex-col h-full relative z-0">
      <!-- Top Navigation Bar -->
      <header class="flex h-16 shrink-0 items-center justify-between px-6 lg:px-10 bg-white/80 backdrop-blur-md border-b border-slate-200/60 z-20 sticky top-0 supports-[backdrop-filter]:bg-white/60">
        <!-- Brand -->
        <div class="flex items-center gap-3 cursor-pointer group" @click="$router.push('/')">
          <div class="relative flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-600 text-white shadow-lg shadow-indigo-500/20 group-hover:scale-105 transition-transform duration-300">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 13.5l10.5-11.25L12 10.5h8.25L9.75 21.75 12 13.5H3.75z" />
            </svg>
            <div class="absolute inset-0 rounded-xl ring-1 ring-white/20"></div>
          </div>
          <div class="flex flex-col">
            <h1 class="text-base font-bold text-slate-800 tracking-tight leading-none group-hover:text-indigo-600 transition-colors">Smart Tools</h1>
            <span class="text-[10px] font-medium text-slate-400 tracking-wide mt-0.5">NEXT GENERATION</span>
          </div>
        </div>

        <!-- Center Search (Floating) -->
        <div class="hidden md:flex flex-1 max-w-lg mx-8 relative group" ref="searchContainer">
          <div class="absolute inset-y-0 left-0 flex items-center pl-4 pointer-events-none text-slate-400 group-focus-within:text-indigo-500 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
              <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" />
            </svg>
          </div>
          <input
            v-model="catalog.search"
            @focus="searchFocused = true"
            type="text"
            :placeholder="t.searchPlaceholder"
            class="block w-full rounded-full border-0 bg-slate-100/80 py-2.5 pl-11 pr-4 text-sm text-slate-800 placeholder:text-slate-400 focus:bg-white focus:ring-2 focus:ring-inset focus:ring-indigo-500/20 transition-all shadow-sm hover:bg-white"
          />
          <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
            <kbd class="hidden sm:inline-flex items-center gap-1 rounded border border-slate-200 bg-slate-50 px-2 py-0.5 text-[10px] font-medium text-slate-500 font-mono">⌘K</kbd>
          </div>

          <!-- Trending Ranking Dropdown -->
          <div 
            v-if="searchFocused && !catalog.search"
            class="absolute top-full left-0 right-0 mt-2 bg-white rounded-2xl shadow-xl ring-1 ring-slate-900/5 p-4 z-50 overflow-hidden"
          >
            <div class="flex items-center gap-2 mb-3 px-2">
              <span class="flex h-2 w-2 rounded-full bg-orange-400 animate-pulse"></span>
              <h3 class="text-xs font-bold text-slate-500 uppercase tracking-wider">热门搜索 / Trending</h3>
            </div>
            
            <div v-if="trendingTools.length > 0" class="space-y-1">
              <div 
                v-for="(tool, index) in trendingTools.slice(0, 5)" 
                :key="tool.slug"
                class="flex items-center gap-3 p-2 rounded-xl hover:bg-slate-50 cursor-pointer group transition-colors"
                @click="handleTrendingClick(tool.slug)"
              >
                <div 
                  class="flex h-6 w-6 shrink-0 items-center justify-center rounded-lg text-xs font-bold"
                  :class="index < 3 ? 'bg-indigo-50 text-indigo-600' : 'bg-slate-100 text-slate-400'"
                >
                  {{ index + 1 }}
                </div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-medium text-slate-700 truncate group-hover:text-indigo-600 transition-colors">{{ tool.name }}</div>
                  <div class="text-[10px] text-slate-400 truncate">{{ tool.description }}</div>
                </div>
                <div class="text-[10px] font-medium text-green-500 bg-green-50 px-1.5 py-0.5 rounded">{{ tool.trendPercentage || '+5%' }}</div>
              </div>
            </div>
            <div v-else class="text-center py-4 text-xs text-slate-400">
              加载中... / Loading...
            </div>
          </div>
        </div>

        <!-- Right Actions -->
        <div class="flex items-center gap-4">
          <a
            href="https://github.com/Ray-Doop"
            target="_blank"
            class="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-50 rounded-full transition-all"
          >
            <svg viewBox="0 0 24 24" class="h-5 w-5 fill-current" aria-hidden="true"><path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"></path></svg>
          </a>
          
          <div class="h-6 w-px bg-slate-200"></div>

          <!-- Auth -->
          <template v-if="!auth.isAuthed">
            <button
              class="rounded-full bg-slate-900 px-5 py-2 text-sm font-semibold text-white shadow-lg shadow-slate-900/20 hover:bg-slate-800 hover:scale-105 transition-all active:scale-95"
              @click="authModalVisible = true"
            >
              登录 / Sign In
            </button>
          </template>
          <template v-else>
            <div class="group relative cursor-pointer">
              <div class="h-9 w-9 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-500 p-[2px] shadow-md hover:shadow-lg transition-shadow">
                <div class="h-full w-full rounded-full bg-white p-[2px]">
                  <img
                    :src="auth.user?.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(auth.user?.displayName || auth.user?.username || 'User')}&background=random`"
                    alt="avatar"
                    class="h-full w-full rounded-full object-cover"
                  />
                </div>
              </div>
              <div class="absolute right-0 top-full mt-3 w-48 origin-top-right rounded-2xl bg-white p-1 shadow-xl ring-1 ring-black/5 focus:outline-none opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 z-50 transform translate-y-2 group-hover:translate-y-0">
                <div class="px-3 py-2 text-xs font-medium text-slate-400">ACCOUNT</div>
                <a href="#" class="flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors" @click.prevent="goProfile">
                  <svg class="w-4 h-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>
                  个人中心 / Profile
                </a>
                <a href="#" class="flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors" @click.prevent="goSettings">
                  <svg class="w-4 h-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                  设置 / Settings
                </a>
                <div class="my-1 h-px bg-slate-100"></div>
                <a href="#" class="flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors" @click.prevent="auth.logout">
                  <svg class="w-4 h-4 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
                  退出 / Sign Out
                </a>
              </div>
            </div>
          </template>
        </div>
      </header>

      <!-- Layout Body: Sidebar + Content -->
      <div class="flex flex-1 overflow-hidden">
        <!-- Floating Sidebar (Collapsed Style) -->
        <aside class="hidden lg:flex w-20 flex-col items-center py-6 gap-6 bg-[#0f172a] border-r border-slate-800/50 z-10 shadow-2xl">
          <div
            v-for="cat in categories"
            :key="cat.key"
            class="group relative flex h-12 w-12 items-center justify-center rounded-2xl transition-all duration-300 cursor-pointer"
            :class="catalog.activeCategory === cat.key ? 'bg-indigo-500 text-white shadow-lg shadow-indigo-500/25 scale-110' : 'text-slate-500 hover:bg-white/10 hover:text-white hover:scale-105'"
            @click="handleCategoryClick(cat.key)"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
              <path v-for="(d, i) in iconPaths(cat.key)" :key="i" stroke-linecap="round" stroke-linejoin="round" :d="d" />
            </svg>
            <div class="absolute left-full ml-4 px-3 py-1.5 bg-slate-900 text-white text-xs font-medium rounded-lg shadow-xl opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all whitespace-nowrap z-50 translate-x-[-10px] group-hover:translate-x-0 border border-slate-700">
              {{ cat.label }}
            </div>
          </div>
        </aside>

        <!-- Main Scroll View -->
        <main class="flex-1 overflow-y-auto bg-[#f8fafc] custom-scrollbar p-4 md:p-6 lg:p-8 scroll-smooth">
          <div class="mx-auto w-full max-w-[1920px] animate-fade-in-up">
            <slot />
          </div>
          
          <!-- Footer Info -->
          <footer class="mt-20 border-t border-slate-100 pt-10 pb-6 text-center">
            <div class="flex items-center justify-center gap-2 text-slate-400 mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-4 h-4 text-slate-300">
                <path d="M11.645 20.91l-.007-.003-.022-.012a15.247 15.247 0 01-.383-.218 25.18 25.18 0 01-4.244-3.17C4.688 15.36 2.25 12.174 2.25 8.25 2.25 5.322 4.714 3 7.688 3A5.5 5.5 0 0112 5.052 5.5 5.5 0 0116.313 3c2.973 0 5.437 2.322 5.437 5.25 0 3.925-2.438 7.111-4.739 9.256a25.175 25.175 0 01-4.244 3.17 15.247 15.247 0 01-.383.219l-.022.012-.007.004-.003.001a.752.752 0 01-.704 0l-.003-.001z" />
              </svg>
              <span class="text-xs font-medium">Crafted for developers</span>
            </div>
            <p class="text-[10px] text-slate-300">© 2026 Smart Tools Inc. All rights reserved.</p>
          </footer>
        </main>
      </div>
    </div>

    <AuthModal 
      v-model="authModalVisible" 
      @open-wechat-register="openWechatRegister"
    />
    <WechatRegisterModal 
      v-model="wechatRegisterVisible" 
      :wechat-state="currentWechatState" 
      :mode="registerMode"
      :email="registerEmail"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToolCatalogStore } from '@/store/toolCatalog'
import { useAuthStore } from '@/store/auth'
import { useI18n } from '@/composables/useI18n'
import AuthModal from '@/components/AuthModal.vue'
import WechatRegisterModal from '@/components/WechatRegisterModal.vue'
import { toolsApi } from '@/api/tools'

const route = useRoute()
const router = useRouter()
const catalog = useToolCatalogStore()
const auth = useAuthStore()
const { t, currentLanguage } = useI18n()

const authModalVisible = ref(false)
const wechatRegisterVisible = ref(false)
const currentWechatState = ref('')
const registerMode = ref('wechat')
const registerEmail = ref('')
const searchFocused = ref(false)
const trendingTools = ref([])
const searchContainer = ref(null)

// Close search dropdown when clicking outside
const handleClickOutside = (event) => {
  if (searchContainer.value && !searchContainer.value.contains(event.target)) {
    searchFocused.value = false
  }
}

onMounted(async () => {
  tryOpenWechatRegisterFromQuery()
  tryOpenLoginFromQuery()
  document.addEventListener('click', handleClickOutside)
  try {
    const data = await toolsApi.trending()
    trendingTools.value = data
  } catch (e) {
    console.error('Failed to load trending tools for search', e)
  }
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

const categories = computed(() => {
  const source = Array.isArray(catalog.categories) ? catalog.categories : []
  const list = ['all', ...source]
  return list.map(key => ({
    key,
    label: labelForCategory(key)
  }))
})

function handleTrendingClick(slug) {
  searchFocused.value = false
  router.push(`/tools/${slug}`)
}

function openWechatRegister(payload) {
  authModalVisible.value = false
  if (typeof payload === 'string') {
    currentWechatState.value = payload
    registerMode.value = 'wechat'
    registerEmail.value = ''
  } else {
    currentWechatState.value = payload?.wechatState || ''
    registerMode.value = payload?.mode || 'wechat'
    registerEmail.value = payload?.email || ''
  }
  wechatRegisterVisible.value = true
}

function tryOpenWechatRegisterFromQuery() {
  const q = route.query?.wechatRegisterState
  if (typeof q !== 'string' || !q) return
  currentWechatState.value = q
  registerMode.value = 'wechat'
  registerEmail.value = ''
  wechatRegisterVisible.value = true

  const nextQuery = { ...route.query }
  delete nextQuery.wechatRegisterState
  router.replace({ query: nextQuery }).catch(() => {})
}

function tryOpenLoginFromQuery() {
  const q = route.query?.login
  if (q === '1') {
    authModalVisible.value = true
  }
}

function goProfile() {
  router.push('/profile')
}

function goSettings() {
  router.push('/settings')
}

function handleCategoryClick(key) {
  catalog.setCategory(key)
  if (route.path !== '/') {
    router.push('/')
  }
}

onMounted(() => {
  tryOpenWechatRegisterFromQuery()
})

watch(
  () => route.query?.wechatRegisterState,
  () => {
    tryOpenWechatRegisterFromQuery()
  }
)

watch(
  () => route.query?.login,
  () => {
    tryOpenLoginFromQuery()
  }
)

watch(
  () => auth.isAuthed,
  (authed) => {
    if (!authed) return
    const redirect = route.query?.redirect
    if (typeof redirect === 'string' && redirect) {
      router.replace(redirect).catch(() => {})
      return
    }
    if (route.query?.login === '1') {
      const nextQuery = { ...route.query }
      delete nextQuery.login
      delete nextQuery.redirect
      router.replace({ query: nextQuery }).catch(() => {})
    }
  }
)

function labelForCategory(cat) {
  if (cat === 'all') return t.allTools
  if (currentLanguage.value !== 'en') return cat
  const map = {
    '文档处理': 'Documents',
    '图片处理': 'Images',
    '音视频': 'Audio & Video',
    '数据处理': 'Data',
    '图表': 'Charts',
    '开发': 'Developer',
    '开发工具': 'Developer',
    '编码/转换': 'Encoding',
    '格式转换': 'Format',
    '文本处理': 'Text',
    '设计工具': 'Design',
    '文件': 'Files',
    '安全': 'Security',
    '加密/解密': 'Crypto',
    '编码/标识': 'Identifiers',
    '生成器': 'Generators'
  }
  return map[cat] || cat
}

function iconPaths(cat) {
  const map = {
    all: [
      'M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z'
    ],
    '开发': ['M17.25 6.75L22.5 12l-5.25 5.25m-10.5 0L1.5 12l5.25-5.25m7.5-3l-4.5 16.5'],
    '开发工具': ['M17.25 6.75L22.5 12l-5.25 5.25m-10.5 0L1.5 12l5.25-5.25m7.5-3l-4.5 16.5'],
    '安全': ['M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z'],
    '加密/解密': ['M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z'],
    '编码/转换': ['M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25H12'],
    '格式转换': ['M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25H12'],
    '文本处理': ['M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25H12'],
    '设计工具': ['M12 3v18m9-9H3'],
    '文件': ['M3 7.5V6a2.25 2.25 0 012.25-2.25h6.879a2.25 2.25 0 011.59.659l4.412 4.412a2.25 2.25 0 01.659 1.591V18A2.25 2.25 0 0116.5 20.25H5.25A2.25 2.25 0 013 18V7.5z'],
    '文档处理': ['M6 2.25h6l4.5 4.5V21a.75.75 0 01-.75.75H6.75A.75.75 0 016 21V2.25zM12 2.25V6.75h4.5'],
    '图片处理': ['M3 6.75A2.25 2.25 0 015.25 4.5h13.5A2.25 2.25 0 0121 6.75v10.5A2.25 2.25 0 0118.75 19.5H5.25A2.25 2.25 0 013 17.25V6.75zM8.25 11.25l2.25 2.25 3-3 4.5 4.5'],
    '音视频': ['M5.25 5.25v13.5l13.5-6.75-13.5-6.75z'],
    '数据处理': ['M4.5 6.75C4.5 5.231 7.35 4 12 4s7.5 1.231 7.5 2.75v10.5C19.5 18.769 16.65 20 12 20s-7.5-1.231-7.5-2.75V6.75zM4.5 11.25C4.5 12.769 7.35 14 12 14s7.5-1.231 7.5-2.75'],
    '图表': ['M4.5 19.5V4.5m0 15h15m-12-3.75V9m4.5 6.75V7.5m4.5 8.25V12'],
    '编码/标识': ['M3.75 7.5l8.25-4.5 8.25 4.5-8.25 4.5-8.25-4.5zM3.75 16.5l8.25 4.5 8.25-4.5'],
    '生成器': ['M12 3v2.25M12 18.75V21M4.219 4.219l1.591 1.591M18.19 18.19l1.591 1.591M3 12h2.25M18.75 12H21M4.219 19.781l1.591-1.591M18.19 5.81l1.591-1.591'],
    default: ['M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25H12']
  }
  return map[cat] || map.default
}
</script>

<style>
/* 隐藏滚动条但保留滚动功能 (Optional: for cleaner look) */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
  border: 2px solid transparent;
  background-clip: content-box;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background-color: #94a3b8;
}

/* 简单的进入动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.animate-fade-in-up {
  animation: fadeInUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
</style>
