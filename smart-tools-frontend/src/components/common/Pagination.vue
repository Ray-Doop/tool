<!--
  @generated-file-note
  文件：smart-tools-frontend/src/components/common/Pagination.vue
  用途：前端通用组件（可复用 UI 组件）
  归属：前端 components
  依赖：vue
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="flex flex-wrap items-center justify-between gap-4 text-xs text-slate-500">
    <div class="flex items-center gap-2">
      <span>共 {{ total }} 条</span>
      <select 
        :value="size" 
        @change="onSizeChange" 
        class="bg-white border border-slate-200 rounded-lg px-2 py-1 focus:outline-none focus:ring-2 focus:ring-slate-900/10"
      >
        <option :value="10">10 条/页</option>
        <option :value="20">20 条/页</option>
        <option :value="50">50 条/页</option>
        <option :value="100">100 条/页</option>
      </select>
    </div>
    
    <div class="flex items-center gap-1.5">
      <button 
        class="px-2 py-1 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-50 disabled:hover:bg-white transition" 
        :disabled="page <= 0" 
        @click="changePage(page - 1)"
      >
        上一页
      </button>
      
      <button 
        v-for="p in visiblePages" 
        :key="p" 
        class="w-8 py-1 rounded-lg border transition text-center"
        :class="p - 1 === page ? 'bg-slate-900 text-white border-slate-900' : 'border-slate-200 hover:bg-slate-50 text-slate-600'"
        @click="changePage(p - 1)"
      >
        {{ p }}
      </button>
      
      <button 
        class="px-2 py-1 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-50 disabled:hover:bg-white transition" 
        :disabled="page >= totalPages - 1" 
        @click="changePage(page + 1)"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CommonPagination'
}
</script>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  total: { type: Number, default: 0 },
  page: { type: Number, default: 0 },
  size: { type: Number, default: 20 }
})

const emit = defineEmits(['update:page', 'update:size', 'change'])

const totalPages = computed(() => Math.ceil(props.total / props.size))

const visiblePages = computed(() => {
  const current = props.page + 1
  const max = totalPages.value
  const delta = 2
  let start = Math.max(1, current - delta)
  let end = Math.min(max, current + delta)
  
  if (current - delta < 1) {
    end = Math.min(max, end + (1 - (current - delta)))
  }
  if (current + delta > max) {
    start = Math.max(1, start - ((current + delta) - max))
  }
  
  const pages = []
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

function changePage(newPage) {
  if (newPage < 0 || newPage >= totalPages.value) return
  emit('update:page', newPage)
  emit('change')
}

function onSizeChange(e) {
  const newSize = parseInt(e.target.value)
  emit('update:size', newSize)
  emit('update:page', 0) // Reset to first page
  emit('change')
}
</script>
