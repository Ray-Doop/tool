<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/admin/AdminUsersPage.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  AdminUsersPage.vue
  用户管理页面

  功能：
  1. 用户列表展示（分页、搜索）
  2. 用户状态管理（启用/禁用）
  3. 密码重置
  4. 角色分配（内嵌表格选择）
  5. 用户详情展示（头像、绑定状态、权限概览）
-->
<template>
  <AdminLayout>
    <div class="space-y-5">
      <!-- 顶部统计与搜索栏 -->
      <div class="rounded-2xl bg-white p-6 shadow-sm border border-slate-200">
        <div class="flex flex-wrap items-center justify-between gap-4">
          <div>
            <div class="text-sm font-semibold text-slate-900">用户与账号</div>
            <div class="text-xs text-slate-400 mt-1">管理登录、状态与账号安全</div>
          </div>
          <div class="flex items-center gap-2">
            <div class="text-xs text-slate-400">当前规模</div>
            <div class="text-sm font-semibold text-slate-900">{{ total }}</div>
          </div>
        </div>
        <div class="mt-4 flex flex-wrap items-center gap-3">
          <input v-model.trim="query" class="flex-1 max-w-sm px-3 py-2 rounded-xl border border-slate-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/10" placeholder="搜索用户名、邮箱或手机号" />
          <select v-model="filterRoleId" class="px-3 py-2 rounded-xl border border-slate-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/10">
            <option :value="null">所有角色</option>
            <option v-for="role in roleOptions" :key="role.id" :value="role.id">{{ role.name }}</option>
          </select>
          <button class="px-4 py-2 rounded-xl bg-slate-900 text-white text-sm hover:bg-slate-800 transition" @click="load">筛选</button>
        </div>
      </div>

      <!-- 用户列表区域 -->
      <div class="space-y-4">
        <div v-for="item in items" :key="item.id" class="rounded-2xl bg-white shadow-sm border border-slate-200 p-5">
          <div class="flex flex-wrap items-start justify-between gap-4">
            <div class="flex items-center gap-4">
              <div class="h-12 w-12 rounded-full bg-slate-100 overflow-hidden border border-slate-200">
                <img
                  :src="resolveAvatar(item.avatarUrl) || `https://ui-avatars.com/api/?name=${encodeURIComponent(item.displayName || item.username || 'User')}&background=random`"
                  class="h-full w-full object-cover"
                  @error="e => e.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(item.displayName || item.username || 'User')}&background=random`"
                />
              </div>
              <div>
                <div class="flex items-center gap-2">
                  <div class="text-sm font-semibold text-slate-900">{{ item.displayName || item.username }}</div>
                  <span class="text-[10px] px-2 py-0.5 rounded-full border" :class="item.isAdmin ? 'border-indigo-200 bg-indigo-50 text-indigo-600' : 'border-slate-200 bg-slate-50 text-slate-500'">
                    {{ item.isAdmin ? '管理员' : '普通用户' }}
                  </span>
                  <span class="text-[10px] px-2 py-0.5 rounded-full border" :class="item.enabled ? 'border-emerald-200 bg-emerald-50 text-emerald-600' : 'border-red-200 bg-red-50 text-red-600'">
                    {{ item.enabled ? '已启用' : '已禁用' }}
                  </span>
                  <span class="text-[10px] px-2 py-0.5 rounded-full border" :class="item.wechatBound ? 'border-emerald-200 bg-emerald-50 text-emerald-600' : 'border-slate-200 bg-slate-50 text-slate-500'">
                    {{ item.wechatBound ? '微信已绑定' : '微信未绑定' }}
                  </span>
                </div>
                <div class="text-xs text-slate-400 mt-1">用户名 {{ item.username }} · ID {{ item.id }}</div>
              </div>
            </div>
            <div class="flex flex-wrap items-center gap-2">
              <button class="text-xs px-3 py-1.5 rounded-lg text-white transition" :class="item.enabled ? 'bg-amber-500 hover:bg-amber-600' : 'bg-emerald-500 hover:bg-emerald-600'" @click="toggle(item)">
                {{ item.enabled ? '禁用' : '启用' }}
              </button>
              <button class="text-xs px-3 py-1.5 rounded-lg bg-indigo-50 text-indigo-600 border border-indigo-100 hover:bg-indigo-100 transition" @click="openUserRoles(item)">
                {{ roleEditingId === item.id ? '收起角色' : '设置角色' }}
              </button>
              <button class="text-xs px-3 py-1.5 rounded-lg bg-slate-50 text-slate-600 border border-slate-200 hover:bg-slate-100 transition" @click="resetPassword(item)">重置密码</button>
              <button class="text-xs px-3 py-1.5 rounded-lg bg-red-50 text-red-600 border border-red-100 hover:bg-red-100 transition" @click="remove(item)">删除</button>
            </div>
          </div>

          <div class="mt-4 grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
            <div>
              <div class="text-[11px] text-slate-400">联系方式</div>
              <div class="text-sm text-slate-700 mt-1">{{ item.email || '-' }}</div>
              <div class="text-xs text-slate-400">{{ item.phone || '-' }}</div>
            </div>
            <div>
              <div class="text-[11px] text-slate-400">用户角色</div>
              <div class="mt-2 flex flex-wrap gap-2">
                <span v-for="role in item.roles || []" :key="role.id" class="text-[10px] px-2 py-1 rounded-full bg-indigo-50 text-indigo-600 border border-indigo-100">
                  {{ role.name }}
                </span>
                <span v-if="!item.roles || item.roles.length === 0" class="text-[11px] text-slate-400">未分配角色</span>
              </div>
            </div>
            <div>
              <div class="text-[11px] text-slate-400">用户权限</div>
              <div class="mt-2 flex flex-wrap gap-2">
                <span v-for="perm in (item.permissions || []).slice(0, 6)" :key="perm.id" class="text-[10px] px-2 py-1 rounded-full bg-slate-50 text-slate-600 border border-slate-200">
                  {{ perm.name }}
                </span>
                <span v-if="(item.permissions || []).length > 6" class="text-[10px] px-2 py-1 rounded-full bg-slate-100 text-slate-500 border border-slate-200">
                  +{{ (item.permissions || []).length - 6 }}
                </span>
                <span v-if="!item.permissions || item.permissions.length === 0" class="text-[11px] text-slate-400">未分配权限</span>
              </div>
            </div>
          </div>

          <div v-if="roleEditingId === item.id" class="mt-4 border-t border-slate-100 pt-4">
            <div class="flex flex-wrap items-center justify-between gap-3">
              <div>
                <div class="text-xs text-slate-400">角色分配</div>
                <div class="text-sm font-medium text-slate-900">{{ item.displayName || item.username }}</div>
              </div>
              <div class="flex items-center gap-2">
                <button class="px-3 py-1.5 rounded-lg border border-slate-200 text-xs hover:bg-slate-50 transition" :disabled="roleLoading" @click="loadUserRoles(item)">刷新</button>
                <button class="px-3 py-1.5 rounded-lg bg-slate-900 text-white text-xs hover:bg-slate-800 transition" :disabled="roleSaving" @click="saveUserRoles(item)">保存角色</button>
                <button class="px-3 py-1.5 rounded-lg border border-slate-200 text-xs hover:bg-slate-50 transition" @click="clearUserRoles">取消</button>
              </div>
            </div>
            <div class="mt-3 overflow-hidden border border-slate-100 rounded-xl">
              <table class="w-full text-xs">
                <thead class="bg-slate-50 text-slate-500">
                  <tr>
                    <th class="text-left px-3 py-2">选择</th>
                    <th class="text-left px-3 py-2">角色名称</th>
                    <th class="text-left px-3 py-2">编码</th>
                    <th class="text-left px-3 py-2">状态</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="role in roleOptions" :key="role.id" class="border-t border-slate-100">
                    <td class="px-3 py-2">
                      <input v-model="roleEditingIds" type="checkbox" :value="role.id" />
                    </td>
                    <td class="px-3 py-2 text-slate-800">{{ role.name }}</td>
                    <td class="px-3 py-2 text-slate-500">{{ role.code }}</td>
                    <td class="px-3 py-2">
                      <span class="text-[10px] px-2 py-0.5 rounded-full border" :class="role.enabled ? 'border-emerald-200 bg-emerald-50 text-emerald-600' : 'border-slate-200 bg-slate-50 text-slate-500'">
                        {{ role.enabled ? '可用' : '禁用' }}
                      </span>
                    </td>
                  </tr>
                  <tr v-if="roleOptions.length === 0">
                    <td colspan="4" class="text-center text-slate-400 py-6">暂无角色可分配</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
        <div v-if="items.length === 0" class="rounded-2xl bg-white shadow-sm border border-slate-200 text-center text-slate-400 py-10">暂无数据</div>
      </div>

      <div class="mt-4">
        <Pagination 
          :total="total" 
          v-model:page="page" 
          v-model:size="size" 
          @change="load" 
        />
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import Pagination from '@/components/common/Pagination.vue'
import { adminApi } from '@/api/admin'

const items = ref([])
const roleOptions = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(20)
const query = ref('')
const filterRoleId = ref(null)
const roleEditingId = ref(null)
const roleEditingIds = ref([])

const roleLoading = ref(false)
const roleSaving = ref(false)

function resolveAvatar(path) {
  if (!path) return null
  if (path.startsWith('http')) return path
  return `/api${path}` // Proxy will handle /api -> backend
}

async function load() {
  const res = await adminApi.listUsers({
    page: page.value,
    size: size.value,
    keyword: query.value || undefined,
    roleId: filterRoleId.value || undefined
  })
  items.value = res.items || []
  total.value = res.total || 0
}

async function loadRoleOptions() {
  // 传入 onlyEnabled: true 以获取纯列表数组，而非分页对象
  roleOptions.value = await adminApi.listRoles({ onlyEnabled: true })
}

async function toggle(item) {
  await adminApi.updateUserStatus(item.id, !item.enabled)
  await load()
}

async function resetPassword(item) {
  const newPassword = prompt(`为 ${item.username} 设置新密码`)
  if (!newPassword) return
  await adminApi.resetUserPassword(item.id, newPassword)
}

async function remove(item) {
  if (!confirm(`确定删除用户 ${item.username} 吗？`)) return
  await adminApi.deleteUser(item.id)
  await load()
}

async function openUserRoles(item) {
  if (roleEditingId.value === item.id) {
    clearUserRoles()
    return
  }
  roleEditingId.value = item.id
  await loadUserRoles(item)
}

async function loadUserRoles(item) {
  if (!item) return
  roleLoading.value = true
  try {
    const res = await adminApi.listUserRoles(item.id)
    roleEditingIds.value = res.roleIds || []
  } finally {
    roleLoading.value = false
  }
}

async function saveUserRoles(item) {
  if (!item) return
  roleSaving.value = true
  try {
    await adminApi.updateUserRoles(item.id, roleEditingIds.value)
    await load()
    clearUserRoles()
  } finally {
    roleSaving.value = false
  }
}

function clearUserRoles() {
  roleEditingId.value = null
  roleEditingIds.value = []
}

onMounted(async () => {
  await Promise.all([load(), loadRoleOptions()])
})
</script>
