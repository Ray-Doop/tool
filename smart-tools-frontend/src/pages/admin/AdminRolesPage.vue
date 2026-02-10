<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/admin/AdminRolesPage.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  依赖：vue
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  AdminRolesPage.vue
  角色管理页面

  功能：
  1. 角色列表展示（分页）
  2. 角色增删改查
  3. 角色权限分配
-->
<template>
  <AdminLayout>
    <div class="space-y-5">
      <!-- 顶部操作栏 -->
      <div class="rounded-2xl bg-white p-6 shadow-sm border border-slate-200">
        <div class="flex flex-wrap items-center justify-between gap-4">
          <div>
            <div class="text-sm font-semibold text-slate-900">角色管理</div>
            <div class="text-xs text-slate-400 mt-1">系统角色定义与权限分配</div>
          </div>
          <div class="flex items-center gap-2">
            <button class="px-4 py-2 rounded-xl bg-slate-900 text-white text-sm hover:bg-slate-800 transition" @click="openCreate">新建角色</button>
          </div>
        </div>
      </div>

      <!-- 角色列表区域 -->
      <div class="rounded-2xl bg-white p-5 shadow-sm border border-slate-200">
        <div class="overflow-hidden rounded-xl border border-slate-100">
          <table class="w-full text-sm">
            <thead class="bg-slate-50 text-slate-500">
              <tr>
                <th class="text-left px-4 py-3 font-medium">角色名称</th>
                <th class="text-left px-4 py-3 font-medium">编码</th>
                <th class="text-left px-4 py-3 font-medium">描述</th>
                <th class="text-left px-4 py-3 font-medium">状态</th>
                <th class="text-left px-4 py-3 font-medium">创建时间</th>
                <th class="text-right px-4 py-3 font-medium">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
              <tr v-for="role in roles" :key="role.id" class="hover:bg-slate-50/50 transition">
                <td class="px-4 py-3 font-medium text-slate-900">{{ role.name }}</td>
                <td class="px-4 py-3 text-slate-500 font-mono text-xs">{{ role.code }}</td>
                <td class="px-4 py-3 text-slate-500">{{ role.description || '-' }}</td>
                <td class="px-4 py-3">
                  <span class="text-[10px] px-2 py-0.5 rounded-full border" :class="role.enabled ? 'border-emerald-200 bg-emerald-50 text-emerald-600' : 'border-slate-200 bg-slate-50 text-slate-500'">
                    {{ role.enabled ? '已启用' : '已禁用' }}
                  </span>
                </td>
                <td class="px-4 py-3 text-slate-400 text-xs">{{ new Date(role.createdAt).toLocaleString() }}</td>
                <td class="px-4 py-3 text-right">
                  <div class="flex items-center justify-end gap-2">
                    <button class="text-xs px-2 py-1 rounded-lg text-slate-600 hover:bg-slate-100 transition" @click="openEdit(role)">编辑</button>
                    <button class="text-xs px-2 py-1 rounded-lg text-slate-600 hover:bg-slate-100 transition" @click="openPermissions(role)">权限</button>
                    <button class="text-xs px-2 py-1 rounded-lg text-red-600 hover:bg-red-50 transition" @click="remove(role)">删除</button>
                  </div>
                </td>
              </tr>
              <tr v-if="roles.length === 0">
                <td colspan="6" class="text-center text-slate-400 py-10">暂无角色数据</td>
              </tr>
            </tbody>
          </table>
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
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/20 backdrop-blur-sm p-4">
      <div class="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl border border-slate-100">
        <h3 class="text-lg font-semibold text-slate-900 mb-4">{{ isEditing ? '编辑角色' : '新建角色' }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-xs font-medium text-slate-700 mb-1">角色名称</label>
            <input v-model.trim="form.name" class="w-full px-3 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/10" placeholder="如：超级管理员" />
          </div>
          <div>
            <label class="block text-xs font-medium text-slate-700 mb-1">角色编码</label>
            <input v-model.trim="form.code" class="w-full px-3 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/10" placeholder="如：ROLE_ADMIN" :disabled="isEditing" />
            <div class="text-[10px] text-slate-400 mt-1">唯一标识符，通常以 ROLE_ 开头</div>
          </div>
          <div>
            <label class="block text-xs font-medium text-slate-700 mb-1">描述</label>
            <textarea v-model.trim="form.description" class="w-full px-3 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/10" rows="3" placeholder="角色的职责描述..."></textarea>
          </div>
          <div class="flex items-center gap-2">
            <input v-model="form.enabled" type="checkbox" id="enabled" class="rounded border-slate-300 text-slate-900 focus:ring-slate-900" />
            <label for="enabled" class="text-sm text-slate-700">启用此角色</label>
          </div>
        </div>
        <div class="mt-6 flex justify-end gap-3">
          <button class="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50 transition" @click="closeModal">取消</button>
          <button class="px-4 py-2 rounded-xl bg-slate-900 text-white text-sm hover:bg-slate-800 transition" :disabled="submitting" @click="submit">
            {{ submitting ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Permissions Modal -->
    <div v-if="showPermModal" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/20 backdrop-blur-sm p-4">
      <div class="w-full max-w-2xl rounded-2xl bg-white p-6 shadow-xl border border-slate-100 flex flex-col max-h-[80vh]">
        <div class="flex items-center justify-between mb-4">
          <div>
            <h3 class="text-lg font-semibold text-slate-900">权限配置</h3>
            <div class="text-xs text-slate-400 mt-1">当前角色：{{ currentRole?.name }} ({{ currentRole?.code }})</div>
          </div>
          <button class="text-slate-400 hover:text-slate-600" @click="closePermModal">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>
        
        <div class="flex-1 overflow-y-auto min-h-0 border rounded-xl border-slate-100 p-4">
          <div v-if="loadingPerms" class="text-center py-10 text-slate-400">加载中...</div>
          <div v-else-if="allPermissions.length === 0" class="text-center py-10 text-slate-400">暂无权限定义</div>
          <div v-else class="space-y-6">
            <!-- Group permissions by parent or category if available, simplified here -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <label v-for="perm in allPermissions" :key="perm.id" class="flex items-start gap-3 p-3 rounded-xl border border-slate-100 hover:border-slate-200 hover:bg-slate-50 transition cursor-pointer">
                <input v-model="selectedPermIds" type="checkbox" :value="perm.id" class="mt-1 rounded border-slate-300 text-slate-900 focus:ring-slate-900" />
                <div>
                  <div class="text-sm font-medium text-slate-900">{{ perm.name }}</div>
                  <div class="text-xs text-slate-400 font-mono mt-0.5">{{ perm.code }}</div>
                </div>
              </label>
            </div>
          </div>
        </div>

        <div class="mt-6 flex justify-end gap-3 pt-4 border-t border-slate-100">
          <button class="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm hover:bg-slate-50 transition" @click="closePermModal">取消</button>
          <button class="px-4 py-2 rounded-xl bg-slate-900 text-white text-sm hover:bg-slate-800 transition" :disabled="savingPerms" @click="savePerms">
            {{ savingPerms ? '保存中...' : '保存配置' }}
          </button>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { onMounted, ref, reactive } from 'vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import Pagination from '@/components/common/Pagination.vue'
import { adminApi } from '@/api/admin'

const roles = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(20)

const showModal = ref(false)
const isEditing = ref(false)
const submitting = ref(false)
const form = reactive({
  id: null,
  name: '',
  code: '',
  description: '',
  enabled: true
})

const showPermModal = ref(false)
const currentRole = ref(null)
const allPermissions = ref([])
const selectedPermIds = ref([])
const loadingPerms = ref(false)
const savingPerms = ref(false)

async function load() {
  try {
    const res = await adminApi.listRoles({ page: page.value, size: size.value })
    if (res.items) {
      roles.value = res.items
      total.value = res.total || 0
    } else {
      // Fallback for non-paginated API if any
      roles.value = Array.isArray(res) ? res : []
      total.value = roles.value.length
    }
  } catch (e) {
    console.error(e)
    alert(e.message || '加载失败')
  }
}

function openCreate() {
  isEditing.value = false
  form.id = null
  form.name = ''
  form.code = ''
  form.description = ''
  form.enabled = true
  showModal.value = true
}

function openEdit(role) {
  isEditing.value = true
  form.id = role.id
  form.name = role.name
  form.code = role.code
  form.description = role.description
  form.enabled = role.enabled
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

async function submit() {
  if (!form.name || !form.code) {
    alert('请填写名称和编码')
    return
  }
  submitting.value = true
  try {
    if (isEditing.value) {
      await adminApi.updateRole(form.id, {
        name: form.name,
        code: form.code,
        description: form.description,
        enabled: form.enabled
      })
    } else {
      await adminApi.createRole({
        name: form.name,
        code: form.code,
        description: form.description,
        enabled: form.enabled
      })
    }
    await load()
    closeModal()
  } catch (e) {
    alert(e.message)
  } finally {
    submitting.value = false
  }
}

async function remove(role) {
  if (!confirm(`确定删除角色 ${role.name} 吗？`)) return
  try {
    await adminApi.deleteRole(role.id)
    await load()
  } catch (e) {
    alert(e.message)
  }
}

async function openPermissions(role) {
  currentRole.value = role
  showPermModal.value = true
  loadingPerms.value = true
  try {
    const [perms, rolePerms] = await Promise.all([
      adminApi.listPermissions(),
      adminApi.listRolePermissions(role.id)
    ])
    allPermissions.value = perms
    selectedPermIds.value = rolePerms.permissionIds || []
  } catch (e) {
    alert(e.message)
  } finally {
    loadingPerms.value = false
  }
}

function closePermModal() {
  showPermModal.value = false
  currentRole.value = null
  selectedPermIds.value = []
}

async function savePerms() {
  if (!currentRole.value) return
  savingPerms.value = true
  try {
    await adminApi.updateRolePermissions(currentRole.value.id, selectedPermIds.value)
    closePermModal()
  } catch (e) {
    alert(e.message)
  } finally {
    savingPerms.value = false
  }
}

onMounted(() => {
  load()
})
</script>
