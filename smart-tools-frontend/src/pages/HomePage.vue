<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/HomePage.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  依赖：vue、vue-router、@/layouts/AppLayout.vue、@/components/ToolCard.vue、@/store/toolCatalog、@/store/auth、@/composables/useI18n、element-plus、@/api/tools
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  HomePage：首页。
  - 组合 AppLayout 与 ToolCard
  - 使用 toolCatalog store 的过滤结果渲染卡片列表
-->
<template>
  <AppLayout>
    <!-- Dynamic Hero Area with Interactive Gradient -->
    <div class="relative mb-12 overflow-hidden rounded-[2.5rem] bg-gradient-to-br from-slate-900 via-[#1e1b4b] to-slate-900 px-8 py-16 shadow-2xl shadow-indigo-500/10 sm:px-16 sm:py-24 ring-1 ring-white/10">
      <!-- Animated Background Blobs -->
      <div class="absolute -left-20 -top-20 h-96 w-96 rounded-full bg-indigo-500 opacity-20 blur-[128px] animate-pulse-slow"></div>
      <div class="absolute -right-20 -bottom-20 h-96 w-96 rounded-full bg-violet-500 opacity-20 blur-[128px] animate-pulse-slow delay-1000"></div>
      
      <div class="relative z-10 max-w-3xl">
        <div class="inline-flex items-center gap-2 rounded-full bg-white/10 px-3 py-1 text-xs font-medium text-indigo-200 backdrop-blur-md ring-1 ring-inset ring-white/20 mb-6">
          <span class="flex h-2 w-2 rounded-full bg-green-400"></span>
          {{ t.heroBadge }}
        </div>
        
        <h1 class="text-4xl font-black tracking-tight text-white sm:text-6xl lg:text-7xl">
          {{ t.heroTitlePrefix }}<br />
          <span class="text-transparent bg-clip-text bg-gradient-to-r from-indigo-300 via-purple-300 to-pink-300">{{ t.heroTitleSuffix }}</span>
        </h1>
        <p class="mt-6 text-lg text-slate-300 sm:text-xl max-w-2xl leading-relaxed">
          {{ t.heroSubtitle }}
        </p>
        
        <div class="mt-10 flex flex-wrap gap-4">
          <button class="group relative inline-flex items-center gap-2 rounded-full bg-white px-8 py-4 text-sm font-bold text-slate-900 transition-all hover:scale-105 hover:bg-indigo-50">
            {{ t.explore }}
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4 transition-transform group-hover:translate-x-1">
              <path stroke-linecap="round" stroke-linejoin="round" d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
            </svg>
          </button>
          <a
            href="https://github.com/Ray-Doop"
            target="_blank"
            rel="noreferrer"
            class="inline-flex items-center gap-2 rounded-full bg-white/5 px-8 py-4 text-sm font-bold text-white backdrop-blur-sm transition-all hover:bg-white/10 ring-1 ring-white/10 hover:ring-white/20"
          >
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-4 h-4 text-slate-400">
              <path fill-rule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25zm-2.625 6c-.54 0-.828.419-.936.634a6.953 6.953 0 00-.309.756 5.44 5.44 0 01-.569 1.622 5.436 5.436 0 01-1.634 3.993 4.046 4.046 0 01-1.889 1.1 1.1 1.0 0 00-.763 1.25c.105.516.48.903.963 1.066a5.535 5.535 0 002.583-.02c.506-.118 1.004-.306 1.488-.564.836-.45 1.545-1.127 2.053-1.956a6.974 6.974 0 00.676-3.235v-1.125a1.125 1.125 0 012.25 0v.562c0 .87.564 1.638 1.393 1.905l.39.126a1.125 1.125 0 001.442-1.077V12c0-4.963-4.037-9-9-9z" clip-rule="evenodd" />
            </svg>
            GitHub 源码
          </a>
        </div>
      </div>
    </div>

    <!-- Quick Access / Trending -->
    <div v-if="trendingTools.length > 0 && showFeaturedSections" class="mb-12">
      <div class="flex items-center justify-between px-2 mb-6">
        <h2 class="text-lg font-bold text-slate-800 flex items-center gap-2">
          <span class="flex h-2 w-2 rounded-full bg-orange-400 animate-pulse"></span>
          热门趋势 / Trending
        </h2>
        <button class="text-xs font-medium text-slate-400 hover:text-indigo-600 transition-colors">查看分析 / Analytics</button>
      </div>
      
      <!-- Grid Container -->
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        <ToolCard 
          v-for="tool in trendingTools.slice(0, 8)" 
          :key="tool.slug"
          :tool="tool"
          :badge="{ text: tool.trendPercentage || '+5%', type: 'success' }"
          :footer-text="'近7日访问 ' + tool.visitCount + ' 次'"
          @click.prevent="handleToolClick(tool.slug)"
        />
      </div>
    </div>

    <!-- New Features -->
    <div v-if="newFeatureTools.length > 0 && showFeaturedSections" class="mb-12">
      <div class="flex items-center justify-between px-2 mb-6">
        <h2 class="text-lg font-bold text-slate-800 flex items-center gap-2">
          <span class="flex h-2 w-2 rounded-full bg-blue-400 animate-pulse"></span>
          新增功能 / New Features
        </h2>
      </div>
      
      <!-- Grid Container -->
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        <ToolCard 
          v-for="tool in newFeatureTools.slice(0, 8)" 
          :key="tool.slug"
          :tool="tool"
          :badge="{ text: 'New', type: 'primary' }"
          @click.prevent="handleToolClick(tool.slug)"
        />
      </div>
    </div>

    <!-- Main Grid -->
    <div v-if="filteredTools.length > 0">
      <div class="mb-8 flex items-end justify-between px-2 border-b border-slate-100 pb-4">
        <div>
          <h2 class="text-2xl font-bold text-slate-800">{{ t.allTools }}</h2>
          <p class="text-sm text-slate-400 mt-1">{{ t.defaultDescription }}</p>
        </div>
        
        <!-- View Toggle (List/Grid) -->
        <div class="flex bg-slate-100 rounded-lg p-1">
          <button class="p-1.5 rounded-md bg-white shadow-sm text-slate-700">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4"><path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z" /></svg>
          </button>
          <button class="p-1.5 rounded-md text-slate-400 hover:text-slate-600">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4"><path stroke-linecap="round" stroke-linejoin="round" d="M3.75 5.25h16.5m-16.5 4.5h16.5m-16.5 4.5h16.5m-16.5 4.5h16.5" /></svg>
          </button>
        </div>
      </div>
      
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 3xl:grid-cols-6">
        <ToolCard v-for="t in filteredTools" :key="t.slug" :tool="t" @click.prevent="handleToolClick(t.slug)" />
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="flex flex-col items-center justify-center py-32 text-center">
      <div class="flex h-24 w-24 items-center justify-center rounded-full bg-slate-50 text-slate-300 ring-8 ring-slate-50">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-10 h-10">
          <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" />
        </svg>
      </div>
      <h3 class="mt-6 text-xl font-bold text-slate-900">{{ t.noTools }}</h3>
      <p class="mt-2 text-sm text-slate-500 max-w-xs mx-auto">{{ t.tryOther }}</p>
      <button class="mt-8 text-sm font-semibold text-indigo-600 hover:text-indigo-500" @click="toolStore.search = ''">
        {{ t.clearSearch }}
      </button>
    </div>
  </AppLayout>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'
import ToolCard from '@/components/ToolCard.vue'
import { useToolCatalogStore } from '@/store/toolCatalog'
import { useAuthStore } from '@/store/auth'
import { useI18n } from '@/composables/useI18n'
import { ElMessage } from 'element-plus'
import { toolsApi } from '@/api/tools'

const router = useRouter()
const toolStore = useToolCatalogStore()
const authStore = useAuthStore()
const { t } = useI18n()
toolStore.ensureLoaded()

const trendingTools = ref([])
const newFeatureTools = ref([])

const showFeaturedSections = computed(() => {
  return toolStore.activeCategory === 'all' && !toolStore.search
})

onMounted(async () => {
  try {
    const [trending, newTools] = await Promise.all([
      toolsApi.trending(),
      toolsApi.newTools()
    ])
    trendingTools.value = trending
    newFeatureTools.value = newTools
  } catch (e) {
    console.error('Failed to load stats', e)
  }
})

const filteredTools = computed(() => toolStore.filteredTools)

function handleToolClick(slug) {
  if (!authStore.isAuthed) {
    ElMessage.warning('请先登录后使用功能')
    router.push({ query: { ...router.currentRoute.value.query, login: '1' } })
  } else {
    router.push(`/tools/${slug}`)
  }
}
</script>

<style scoped>
.animate-pulse-slow {
  animation: pulse 8s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.15; }
}
</style>
