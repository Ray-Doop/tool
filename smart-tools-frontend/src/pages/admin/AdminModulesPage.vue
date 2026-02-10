<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/admin/AdminModulesPage.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  依赖：vue、element-plus、@/layouts/AdminLayout.vue、@/api/admin
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <AdminLayout>
    <div class="space-y-5">
      <div class="rounded-2xl bg-white p-5 shadow-sm border border-slate-200">
        <div class="text-sm font-medium text-slate-900">模块配置</div>
        <div class="mt-4 grid grid-cols-1 md:grid-cols-5 gap-3">
          <input v-model.trim="form.code" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="编码" />
          <input v-model.trim="form.name" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="名称" />
          <input v-model.trim="form.description" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="描述" />
          <input v-model.number="form.sortOrder" type="number" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="排序" />
          <button class="px-4 py-2 rounded-xl bg-indigo-600 text-white" @click="submit">{{ editingId ? '更新' : '新增' }}</button>
        </div>
      </div>

      <div class="rounded-2xl bg-white shadow-sm border border-slate-200 overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-slate-50 text-slate-500">
            <tr>
              <th class="text-left px-4 py-3">编码</th>
              <th class="text-left px-4 py-3">名称</th>
              <th class="text-left px-4 py-3">描述</th>
              <th class="text-left px-4 py-3">排序</th>
              <th class="text-left px-4 py-3">状态</th>
              <th class="text-left px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in items" :key="item.id" class="border-t border-slate-100">
              <td class="px-4 py-3">{{ item.code }}</td>
              <td class="px-4 py-3">{{ item.name }}</td>
              <td class="px-4 py-3">{{ item.description || '-' }}</td>
              <td class="px-4 py-3">{{ item.sortOrder }}</td>
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
    </div>
  </AdminLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { adminApi } from '@/api/admin'

const items = ref([])
const editingId = ref(null)
const form = reactive({ code: '', name: '', description: '', sortOrder: 0, enabled: true })

async function load() {
  items.value = await adminApi.listModules()
}

function edit(item) {
  editingId.value = item.id
  form.code = item.code
  form.name = item.name
  form.description = item.description || ''
  form.sortOrder = item.sortOrder || 0
  form.enabled = item.enabled
}

async function submit() {
  if (!form.code || !form.name) {
    ElMessage.warning('编码和名称不能为空')
    return
  }
  const payload = { ...form }
  if (editingId.value) {
    await adminApi.updateModule(editingId.value, payload)
  } else {
    await adminApi.createModule(payload)
  }
  editingId.value = null
  form.code = ''
  form.name = ''
  form.description = ''
  form.sortOrder = 0
  form.enabled = true
  await load()
}

async function toggle(item) {
  await adminApi.updateModule(item.id, { ...item, enabled: !item.enabled })
  await load()
}

async function remove(item) {
  if (!confirm(`确定删除模块 ${item.name} 吗？`)) return
  await adminApi.deleteModule(item.id)
  await load()
}

onMounted(load)
</script>
