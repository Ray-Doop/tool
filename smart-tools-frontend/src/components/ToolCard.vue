<!--
  @generated-file-note
  文件：smart-tools-frontend/src/components/ToolCard.vue
  用途：前端通用组件（可复用 UI 组件）
  归属：前端 components
  依赖：vue、vue-router、@/composables/useI18n
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  ToolCard：工具卡片组件。
  - 展示名称/描述/分类/slug
  - 点击跳转到工具详情页
-->
<template>
  <div 
    class="group relative flex flex-col justify-between overflow-hidden rounded-3xl bg-white p-6 transition-all duration-300 hover:-translate-y-1 cursor-pointer border border-slate-100 hover:border-indigo-100 hover:shadow-2xl hover:shadow-indigo-500/10"
    @click="go"
  >
    <!-- Background Gradient (Subtle) -->
    <div class="absolute inset-0 bg-gradient-to-br from-transparent via-transparent to-slate-50/50 opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
    
    <!-- Top Row: Icon + Category Badge -->
    <div class="relative z-10 flex items-start justify-between">
      <div class="relative flex h-14 w-14 items-center justify-center rounded-2xl bg-slate-50 text-slate-400 transition-all duration-300 group-hover:bg-indigo-600 group-hover:text-white group-hover:shadow-lg group-hover:shadow-indigo-500/30">
        <!-- Initials -->
        <span class="text-xl font-bold uppercase tracking-wider">{{ tool.name.charAt(0) }}</span>
        
        <!-- Decoration Dot -->
        <div class="absolute -right-1 -top-1 h-3 w-3 rounded-full border-2 border-white bg-green-400 opacity-0 transition-all group-hover:opacity-100"></div>
      </div>
      
      <!-- Minimal Badge -->
      <div class="flex flex-col items-end gap-2">
        <span v-if="badge" 
          class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-[10px] font-bold uppercase tracking-wide shadow-sm transition-colors"
          :class="badgeColorClass"
        >
          {{ badge.text }}
        </span>
        <span class="inline-flex items-center rounded-full border border-slate-100 bg-white px-2.5 py-0.5 text-[10px] font-bold uppercase tracking-wide text-slate-400 shadow-sm transition-colors group-hover:border-indigo-100 group-hover:text-indigo-500">
          {{ tool.category || t.tool }}
        </span>
      </div>
    </div>

    <!-- Content -->
    <div class="relative z-10 mt-6">
      <h3 class="text-lg font-bold text-slate-800 transition-colors group-hover:text-indigo-600">{{ tool.name }}</h3>
      <p class="mt-2 text-sm leading-relaxed text-slate-400 line-clamp-2 transition-colors group-hover:text-slate-500">
        {{ tool.description || t.defaultDescription }}
      </p>
    </div>

    <!-- Bottom Action Area -->
    <div class="relative z-10 mt-6 flex items-center justify-between border-t border-slate-50 pt-4 opacity-60 transition-opacity group-hover:opacity-100">
      <div class="flex items-center gap-1">
        <div class="h-1.5 w-1.5 rounded-full bg-slate-200 transition-colors group-hover:bg-indigo-400"></div>
        <span class="text-[10px] font-mono text-slate-400 transition-colors group-hover:text-indigo-400">
          {{ footerText || '/' + tool.slug }}
        </span>
      </div>
      
      <!-- Arrow Icon -->
      <div class="flex h-8 w-8 items-center justify-center rounded-full bg-slate-50 text-slate-400 transition-all group-hover:bg-indigo-50 group-hover:text-indigo-600">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor" class="h-3.5 w-3.5 transition-transform group-hover:translate-x-0.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12h15m0 0l-6.75-6.75M19.5 12l-6.75 6.75" />
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '@/composables/useI18n'

const props = defineProps({
  tool: {
    type: Object,
    required: true
  },
  badge: {
    type: Object, // { text: string, type: 'success' | 'primary' | 'warning' | 'danger' }
    default: null
  },
  footerText: {
    type: String,
    default: ''
  }
})

const router = useRouter()
const { t } = useI18n()

const badgeColorClass = computed(() => {
  const map = {
    success: 'border-green-100 bg-green-50 text-green-600',
    primary: 'border-blue-100 bg-blue-50 text-blue-600',
    warning: 'border-orange-100 bg-orange-50 text-orange-600',
    danger: 'border-red-100 bg-red-50 text-red-600'
  }
  return map[props.badge?.type] || map.success
})

function go() {
  router.push(`/tools/${props.tool.slug}`)
}
</script>

<style scoped>
/* Glassmorphism Shadow on Hover */
.group:hover {
  box-shadow: 0 20px 40px -10px rgba(79, 70, 229, 0.1), 0 0 15px -3px rgba(0, 0, 0, 0.05);
}
</style>
