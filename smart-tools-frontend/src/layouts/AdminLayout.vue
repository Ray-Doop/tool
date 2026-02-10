<!--
  @generated-file-note
  文件：smart-tools-frontend/src/layouts/AdminLayout.vue
  用途：前端 Vue 单文件组件
  归属：前端 frontend
  依赖：vue、vue-router、@/store/auth
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="min-h-screen bg-slate-50 text-slate-700">
    <div class="flex">
      <aside class="w-72 bg-slate-950 text-slate-100 min-h-screen px-5 py-6 flex flex-col gap-6">
        <div class="px-2">
          <div class="text-lg font-semibold tracking-wide">Smart Tools</div>
          <div class="text-xs text-slate-400 mt-1">管理中心</div>
        </div>

        <div class="rounded-2xl bg-slate-900/60 border border-slate-800 px-4 py-4">
          <div class="text-xs text-slate-400">当前账号</div>
          <div class="text-sm font-semibold text-white mt-1">{{ auth.user?.username || '管理员' }}</div>
          <div class="text-xs text-slate-400 mt-1">{{ auth.user?.email || auth.user?.phone || '已通过权限验证' }}</div>
        </div>

        <nav class="flex-1 space-y-5">
          <div v-for="section in navSections" :key="section.title" class="space-y-2">
            <div class="text-xs uppercase tracking-widest text-slate-500 px-2">{{ section.title }}</div>
            <div class="space-y-1">
              <button
                v-for="item in section.items"
                :key="item.path"
                class="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm transition-colors"
                :class="isActive(item.path) ? 'bg-indigo-500 text-white' : 'text-slate-300 hover:bg-slate-800 hover:text-white'"
                @click="go(item.path)"
              >
                <span class="h-2 w-2 rounded-full" :class="isActive(item.path) ? 'bg-white' : 'bg-slate-600'"></span>
                {{ item.label }}
              </button>
            </div>
          </div>
        </nav>

      </aside>

      <div class="flex-1">
        <header class="border-b border-slate-200 bg-white">
          <div class="h-20 px-6 flex items-center justify-between">
            <div>
              <div class="text-xs uppercase tracking-widest text-slate-400">管理中心</div>
              <div class="text-lg font-semibold text-slate-900">{{ currentTitle }}</div>
            </div>
            <div class="flex items-center gap-3">
              <div class="hidden md:block text-xs text-slate-400">权限级别：管理员</div>
              <div class="group relative cursor-pointer">
                <div class="h-10 w-10 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-500 p-[2px] shadow-md hover:shadow-lg transition-shadow">
                  <div class="h-full w-full rounded-full bg-white p-[2px]">
                    <img
                      :src="auth.user?.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(auth.user?.displayName || auth.user?.username || 'Admin')}&background=random`"
                      alt="avatar"
                      class="h-full w-full rounded-full object-cover"
                    />
                  </div>
                </div>
                <div class="absolute right-0 top-full mt-3 w-44 origin-top-right rounded-2xl bg-white p-1 shadow-xl ring-1 ring-black/5 focus:outline-none opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 z-50 transform translate-y-2 group-hover:translate-y-0">
                  <button class="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors" @click="go('/')">返回前台</button>
                  <button class="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors" @click="logout">退出登录</button>
                </div>
              </div>
            </div>
          </div>
        </header>
        <main class="p-6">
          <slot />
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const navSections = [
  {
    title: '总览',
    items: [{ path: '/admin', label: '仪表盘' }]
  },
  {
    title: '运营',
    items: [
      { path: '/admin/users', label: '用户与账号' },
      { path: '/admin/tools', label: '工具与内容' }
    ]
  },
  {
    title: '安全',
    items: [
      { path: '/admin/roles', label: '角色管理' },
      { path: '/admin/access', label: '访问控制' }
    ]
  },
  {
    title: '观测',
    items: [
      { path: '/admin/errors', label: '错误与告警' },
      { path: '/admin/audit', label: '审计日志' },
      { path: '/admin/monitor', label: '系统监控' }
    ]
  }
]

function isActive(path) {
  return route.path === path
}

function go(path) {
  router.push(path)
}

const currentTitle = computed(() => {
  const flat = navSections.flatMap(section => section.items)
  const hit = flat.find(item => item.path === route.path)
  return hit?.label || '管理中心'
})

async function logout() {
  await auth.logout()
  router.push('/')
}

async function ensureAuthed() {
  if (auth.isAuthed) return
  await auth.silentRefresh()
  if (!auth.isAuthed) {
    router.replace({ name: 'admin-login', query: { redirect: route.fullPath } })
  }
}

onMounted(ensureAuthed)
watch(() => auth.isAuthed, authed => {
  if (!authed) {
    router.replace({ name: 'admin-login', query: { redirect: route.fullPath } })
  }
})
</script>
