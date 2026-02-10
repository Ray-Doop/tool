<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/admin/AdminErrorTipsPage.vue
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
            <div class="text-sm font-semibold text-slate-900">错误与告警</div>
            <div class="text-xs text-slate-400 mt-1">统一管理错误提示模板与系统异常追踪</div>
          </div>
          <div class="flex items-center gap-2">
            <button
              v-for="tab in tabs"
              :key="tab.value"
              class="text-xs px-3 py-1.5 rounded-full border transition-colors"
              :class="activeTab === tab.value ? 'bg-slate-900 text-white border-slate-900' : 'bg-white text-slate-600 border-slate-200 hover:border-slate-300'"
              @click="activeTab = tab.value"
            >
              {{ tab.label }}
            </button>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'tips'" class="space-y-5">
        <div class="rounded-2xl bg-white p-5 shadow-sm border border-slate-200">
          <div class="text-sm font-semibold text-slate-900">错误提示配置</div>
          <div class="mt-4 grid grid-cols-1 md:grid-cols-6 gap-3">
            <input v-model.trim="form.code" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="编码" />
            <input v-model.trim="form.title" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="标题" />
            <input v-model.trim="form.severity" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="级别" />
            <input v-model.trim="form.message" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="提示内容" />
            <input v-model.trim="form.solution" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="解决方案" />
            <button class="px-4 py-2 rounded-xl bg-indigo-600 text-white" @click="submit">{{ editingId ? '更新' : '新增' }}</button>
          </div>
        </div>

        <div class="rounded-2xl bg-white shadow-sm border border-slate-200 overflow-hidden">
          <table class="w-full text-sm">
            <thead class="bg-slate-50 text-slate-500">
              <tr>
                <th class="text-left px-4 py-3">编码</th>
                <th class="text-left px-4 py-3">标题</th>
                <th class="text-left px-4 py-3">级别</th>
                <th class="text-left px-4 py-3">提示内容</th>
                <th class="text-left px-4 py-3">状态</th>
                <th class="text-left px-4 py-3">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in items" :key="item.id" class="border-t border-slate-100">
                <td class="px-4 py-3">{{ item.code }}</td>
                <td class="px-4 py-3">{{ item.title }}</td>
                <td class="px-4 py-3">{{ item.severity }}</td>
                <td class="px-4 py-3">{{ item.message }}</td>
                <td class="px-4 py-3">
                  <span :class="item.enabled ? 'text-emerald-600' : 'text-red-500'">{{ item.enabled ? '启用' : '禁用' }}</span>
                </td>
                <td class="px-4 py-3 flex items-center gap-2">
                  <button class="text-xs px-3 py-1 rounded-lg bg-slate-100" @click="edit(item)">编辑</button>
                  <button class="text-xs px-3 py-1 rounded-lg bg-slate-100" @click="toggle(item)">{{ item.enabled ? '禁用' : '启用' }}</button>
                  <button class="text-xs px-3 py-1 rounded-lg bg-red-50 text-red-600" @click="remove(item)">删除</button>
                </td>
              </tr>
              <tr v-if="items.length === 0">
                <td colspan="6" class="text-center text-slate-400 py-6">暂无数据</td>
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

      <div v-else class="space-y-4">
        <div class="rounded-2xl bg-white p-4 shadow-sm border border-slate-200 flex flex-wrap items-center gap-3">
          <select v-model="filters.source" class="px-3 py-2 rounded-xl border border-slate-200 text-sm bg-white">
            <option value="">来源</option>
            <option value="backend">后端</option>
            <option value="frontend">前端</option>
          </select>
          <select v-model="filters.level" class="px-3 py-2 rounded-xl border border-slate-200 text-sm bg-white">
            <option value="">级别</option>
            <option value="error">错误</option>
            <option value="warn">告警</option>
          </select>
          <input v-model.trim="filters.keyword" class="px-3 py-2 rounded-xl border border-slate-200 text-sm" placeholder="关键词/路径" />
          <button class="px-4 py-2 rounded-xl bg-slate-900 text-white text-sm" @click="loadLogs">筛选</button>
        </div>

        <div class="rounded-2xl bg-white shadow-sm border border-slate-200 overflow-hidden">
          <table class="w-full text-sm">
            <thead class="bg-slate-50 text-slate-500">
              <tr>
                <th class="text-left px-4 py-3">来源</th>
                <th class="text-left px-4 py-3">级别</th>
                <th class="text-left px-4 py-3">信息</th>
                <th class="text-left px-4 py-3">路径</th>
                <th class="text-left px-4 py-3">时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="log in logs" :key="log.id" class="border-t border-slate-100">
                <td class="px-4 py-3">{{ log.source }}</td>
                <td class="px-4 py-3">
                  <span :class="log.level === 'error' ? 'text-red-600' : 'text-amber-600'">{{ log.level }}</span>
                </td>
                <td class="px-4 py-3">
                  <div class="text-slate-900">{{ log.message }}</div>
                  <div v-if="log.detail" class="text-xs text-slate-400 mt-1">{{ log.detail }}</div>
                </td>
                <td class="px-4 py-3">
                  <div class="text-slate-600">{{ log.path || log.pageUrl || '-' }}</div>
                  <div class="text-xs text-slate-400">{{ log.method || '-' }}</div>
                </td>
                <td class="px-4 py-3 text-slate-500">{{ formatTime(log.createdAt) }}</td>
              </tr>
              <tr v-if="logs.length === 0">
                <td colspan="5" class="text-center text-slate-400 py-6">暂无数据</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="flex items-center justify-between">
          <div class="text-xs text-slate-400">共 {{ logTotal }} 条</div>
          <div class="flex items-center gap-2">
            <button class="px-3 py-1.5 text-xs rounded-full border border-slate-200" :disabled="logPage === 0" @click="prevLogPage">上一页</button>
            <button class="px-3 py-1.5 text-xs rounded-full border border-slate-200" :disabled="(logPage + 1) * logSize >= logTotal" @click="nextLogPage">下一页</button>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { adminApi } from '@/api/admin'

const activeTab = ref('tips')
const tabs = [
  { label: '错误提示配置', value: 'tips' },
  { label: '异常日志追踪', value: 'logs' }
]

// Tips
const items = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(20)
const editingId = ref(null)
const form = reactive({
  code: '',
  title: '',
  severity: 'error',
  message: '',
  solution: '',
  enabled: true
})

// Logs
const logs = ref([])
const logTotal = ref(0)
const logPage = ref(0)
const logSize = ref(20)
const filters = reactive({
  source: '',
  level: '',
  keyword: ''
})

async function load() {
  const res = await adminApi.listErrorTips({ page: page.value, size: size.value })
  if (res.items) {
    items.value = res.items
    total.value = res.total || 0
  } else {
    items.value = Array.isArray(res) ? res : []
    total.value = items.value.length
  }
}

async function loadLogs() {
  const res = await adminApi.listErrorLogs({
    page: logPage.value,
    size: logSize.value,
    source: filters.source || undefined,
    level: filters.level || undefined,
    keyword: filters.keyword || undefined
  })
  logs.value = res.items || []
  logTotal.value = res.total || 0
}

function nextLogPage() {
  if ((logPage.value + 1) * logSize.value >= logTotal.value) return
  logPage.value += 1
  loadLogs()
}

function prevLogPage() {
  if (logPage.value === 0) return
  logPage.value -= 1
  loadLogs()
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

watch(activeTab, (val) => {
  if (val === 'tips') load()
  else loadLogs()
})

function edit(item) {
  editingId.value = item.id
  form.code = item.code
  form.title = item.title
  form.message = item.message
  form.severity = item.severity
  form.solution = item.solution || ''
  form.enabled = item.enabled
}

async function submit() {
  const payload = { ...form }
  if (editingId.value) {
    await adminApi.updateErrorTip(editingId.value, payload)
  } else {
    await adminApi.createErrorTip(payload)
  }
  editingId.value = null
  form.code = ''
  form.title = ''
  form.message = ''
  form.severity = ''
  form.solution = ''
  form.enabled = true
  await load()
}

async function toggle(item) {
  await adminApi.updateErrorTip(item.id, { ...item, enabled: !item.enabled })
  await load()
}

async function remove(item) {
  if (!confirm(`确定删除错误提示 ${item.title} 吗？`)) return
  await adminApi.deleteErrorTip(item.id)
  await load()
}

function formatTime(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString()
}

onMounted(async () => {
  await load()
  await loadLogs()
})
</script>
