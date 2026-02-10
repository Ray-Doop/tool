<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/admin/AdminDashboard.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  依赖：vue、vue-router、@/layouts/AdminLayout.vue、@/api/admin
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <AdminLayout>
    <div class="space-y-6">
      <div class="rounded-3xl bg-gradient-to-r from-indigo-600 via-violet-600 to-fuchsia-600 text-white p-6 shadow-sm">
        <div class="text-sm text-white/80">欢迎进入管理中心</div>
        <div class="text-2xl font-semibold mt-2">今天也一起把体验做得更好</div>
        <div class="mt-4 flex flex-wrap gap-3">
          <button class="px-4 py-2 rounded-xl bg-white text-slate-900 text-sm font-medium" @click="go('/admin/users')">用户与账号</button>
          <button class="px-4 py-2 rounded-xl bg-white/10 border border-white/30 text-sm" @click="go('/admin/tools')">工具与内容</button>
          <button class="px-4 py-2 rounded-xl bg-white/10 border border-white/30 text-sm" @click="go('/admin/errors')">错误与告警</button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="rounded-2xl bg-white p-5 shadow-sm border border-slate-200">
          <div class="text-xs text-slate-400">用户规模</div>
          <div class="text-2xl font-semibold text-slate-900 mt-2">{{ stats.users }}</div>
          <div class="text-xs text-slate-400 mt-1">已接入账号数量</div>
        </div>
        <div class="rounded-2xl bg-white p-5 shadow-sm border border-slate-200">
          <div class="text-xs text-slate-400">工具数量</div>
          <div class="text-2xl font-semibold text-slate-900 mt-2">{{ stats.tools }}</div>
          <div class="text-xs text-slate-400 mt-1">在线工具与页面</div>
        </div>
        <div class="rounded-2xl bg-white p-5 shadow-sm border border-slate-200">
          <div class="text-xs text-slate-400">错误与告警</div>
          <div class="text-2xl font-semibold text-slate-900 mt-2">{{ stats.errors }}</div>
          <div class="text-xs text-slate-400 mt-1">近 24 小时记录</div>
        </div>
        <div class="rounded-2xl bg-white p-5 shadow-sm border border-slate-200">
          <div class="text-xs text-slate-400">服务健康度</div>
          <div class="text-2xl font-semibold text-slate-900 mt-2">{{ health.status }}</div>
          <div class="text-xs text-slate-400 mt-1">最新采样 {{ health.time }}</div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div class="rounded-2xl bg-white p-6 shadow-sm border border-slate-200 lg:col-span-2">
          <div class="flex items-center justify-between">
            <div class="text-sm font-semibold text-slate-900">系统概览</div>
            <button class="text-xs text-slate-500 hover:text-slate-700" @click="go('/admin/monitor')">查看监控</button>
          </div>
          <div class="mt-4 grid grid-cols-1 md:grid-cols-3 gap-3">
            <div class="rounded-xl bg-slate-50 border border-slate-200 p-4">
              <div class="text-xs text-slate-400">CPU 使用率</div>
              <div class="text-lg font-semibold text-slate-900 mt-2">{{ health.cpu }}</div>
            </div>
            <div class="rounded-xl bg-slate-50 border border-slate-200 p-4">
              <div class="text-xs text-slate-400">内存使用率</div>
              <div class="text-lg font-semibold text-slate-900 mt-2">{{ health.mem }}</div>
            </div>
            <div class="rounded-xl bg-slate-50 border border-slate-200 p-4">
              <div class="text-xs text-slate-400">请求 P95</div>
              <div class="text-lg font-semibold text-slate-900 mt-2">{{ health.p95 }}</div>
            </div>
          </div>
        </div>

        <div class="rounded-2xl bg-white p-6 shadow-sm border border-slate-200">
          <div class="text-sm font-semibold text-slate-900">最近错误</div>
          <div class="mt-4 space-y-3">
            <div v-for="log in recentErrors" :key="log.id" class="rounded-xl border border-slate-200 p-3">
              <div class="text-xs text-slate-400">{{ log.source || 'unknown' }}</div>
              <div class="text-sm font-medium text-slate-900 mt-1">{{ log.message }}</div>
              <div class="text-xs text-slate-400 mt-1">{{ formatTime(log.createdAt) }}</div>
            </div>
            <div v-if="recentErrors.length === 0" class="text-xs text-slate-400">暂无错误记录</div>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { adminApi } from '@/api/admin'

const router = useRouter()
const stats = ref({ users: 0, tools: 0, errors: 0 })
const recentErrors = ref([])
const health = ref({ status: '正常', time: '-', cpu: '-', mem: '-', p95: '-' })

function go(path) {
  router.push(path)
}

function formatTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

onMounted(async () => {
  try {
    const [users, tools, errors, metrics] = await Promise.all([
      adminApi.listUsers({ page: 0, size: 1 }),
      adminApi.listTools(),
      adminApi.listErrorLogs({ page: 0, size: 5, level: 'error' }),
      adminApi.listMetrics({ page: 0, size: 1 })
    ])
    stats.value = {
      users: users?.total ?? 0,
      tools: Array.isArray(tools) ? tools.length : 0,
      errors: errors?.total ?? 0
    }
    recentErrors.value = errors?.items || []
    const metric = metrics?.items?.[0]
    if (metric) {
      health.value = {
        status: metric.cpuUsage > 0.85 || metric.memUsage > 0.85 ? '告警' : '正常',
        time: metric.collectedAt ? new Date(metric.collectedAt).toLocaleString() : '-',
        cpu: metric.cpuUsage === undefined ? '-' : `${(metric.cpuUsage * 100).toFixed(1)}%`,
        mem: metric.memUsage === undefined ? '-' : `${(metric.memUsage * 100).toFixed(1)}%`,
        p95: metric.rtP95 === undefined ? '-' : `${Number(metric.rtP95).toFixed(2)} ms`
      }
    }
  } catch {
    stats.value = { users: 0, tools: 0, errors: 0 }
    recentErrors.value = []
    health.value = { status: '正常', time: '-', cpu: '-', mem: '-', p95: '-' }
  }
})
</script>
