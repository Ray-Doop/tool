<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/admin/AdminOperationLogsPage.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  依赖：vue、@/layouts/AdminLayout.vue、@/api/admin
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <AdminLayout>
    <div class="space-y-5">
      <div class="rounded-2xl bg-white p-6 shadow-sm border border-slate-200">
        <div class="flex flex-wrap items-center justify-between gap-4">
          <div>
            <div class="text-sm font-semibold text-slate-900">审计日志</div>
            <div class="text-xs text-slate-400 mt-1">追踪关键操作与权限变更</div>
          </div>
          <div class="text-xs text-slate-400">共 {{ total }} 条</div>
        </div>
        <div class="mt-4 flex flex-wrap items-center gap-3">
          <input v-model.trim="module" class="px-3 py-2 rounded-xl border border-slate-200 bg-white text-sm" placeholder="模块筛选" />
          <button class="px-4 py-2 rounded-xl bg-slate-900 text-white text-sm" @click="load">筛选</button>
        </div>
      </div>

      <div class="rounded-2xl bg-white shadow-sm border border-slate-200 overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-slate-50 text-slate-500">
            <tr>
              <th class="text-left px-4 py-3">用户</th>
              <th class="text-left px-4 py-3">模块</th>
              <th class="text-left px-4 py-3">动作</th>
              <th class="text-left px-4 py-3">目标</th>
              <th class="text-left px-4 py-3">结果</th>
              <th class="text-left px-4 py-3">时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in items" :key="item.id" class="border-t border-slate-100">
              <td class="px-4 py-4">{{ item.userId || '-' }}</td>
              <td class="px-4 py-4">{{ item.module || '-' }}</td>
              <td class="px-4 py-4">{{ item.action || '-' }}</td>
              <td class="px-4 py-4">{{ item.target || '-' }}</td>
              <td class="px-4 py-4">{{ item.result || '-' }}</td>
              <td class="px-4 py-4">{{ formatTime(item.createdAt) }}</td>
            </tr>
            <tr v-if="items.length === 0">
              <td colspan="6" class="text-center text-slate-400 py-8">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="flex items-center justify-between text-xs text-slate-500">
        <div>共 {{ total }} 条</div>
        <div class="flex items-center gap-2">
          <button class="px-3 py-1.5 rounded-lg border border-slate-200" :disabled="page === 0" @click="prev">上一页</button>
          <button class="px-3 py-1.5 rounded-lg border border-slate-200" :disabled="(page + 1) * size >= total" @click="next">下一页</button>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { adminApi } from '@/api/admin'

const items = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(20)
const module = ref('')

function formatTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

async function load() {
  const res = await adminApi.listOperationLogs({ page: page.value, size: size.value, module: module.value || undefined })
  items.value = res.items || []
  total.value = res.total || 0
}

function prev() {
  if (page.value > 0) {
    page.value -= 1
    load()
  }
}

function next() {
  if ((page.value + 1) * size.value < total.value) {
    page.value += 1
    load()
  }
}

onMounted(load)
</script>
