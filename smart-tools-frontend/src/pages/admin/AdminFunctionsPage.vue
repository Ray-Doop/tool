<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/admin/AdminFunctionsPage.vue
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
            <div class="text-sm font-semibold text-slate-900">工具与内容</div>
            <div class="text-xs text-slate-400 mt-1">维护前台工具页面、分类与排序</div>
          </div>
          <div class="text-xs text-slate-400">共 {{ items.length }} 个工具</div>
        </div>
        <div class="mt-4 grid grid-cols-1 md:grid-cols-6 gap-3">
          <input v-model.trim="form.slug" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="标识 slug" />
          <input v-model.trim="form.name" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="名称" />
          <input v-model.trim="form.category" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="分类" />
          <input v-model.number="form.sortOrder" type="number" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="排序" />
          <select v-model="form.enabled" class="px-3 py-2 rounded-xl border border-slate-200 bg-white">
            <option :value="true">启用</option>
            <option :value="false">禁用</option>
          </select>
          <button class="px-4 py-2 rounded-xl bg-slate-900 text-white" @click="submit">{{ editingId ? '更新工具' : '新增工具' }}</button>
        </div>
        <div class="mt-3 grid grid-cols-1 md:grid-cols-3 gap-3">
          <input v-model.trim="form.description" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="描述" />
          <input v-model.trim="form.keywords" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="关键词，逗号分隔" />
          <label class="px-3 py-2 rounded-xl border border-slate-200 flex items-center gap-2 text-sm text-slate-600 bg-white">
            <input v-model="generateFrontend" type="checkbox" class="rounded border-slate-300" />
            同步生成前台页面
          </label>
        </div>
      </div>

      <div class="flex flex-wrap items-center gap-3">
        <input v-model.trim="filterCategory" class="px-3 py-2 rounded-xl border border-slate-200" placeholder="分类筛选" />
        <button class="px-4 py-2 rounded-xl bg-slate-900 text-white" @click="load">筛选</button>
      </div>

      <div class="rounded-2xl bg-white shadow-sm border border-slate-200 overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-slate-50 text-slate-500">
            <tr>
              <th class="text-left px-4 py-3">工具</th>
              <th class="text-left px-4 py-3">分类</th>
              <th class="text-left px-4 py-3">排序</th>
              <th class="text-left px-4 py-3">状态</th>
              <th class="text-left px-4 py-3">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in items" :key="item.id" class="border-t border-slate-100">
              <td class="px-4 py-4">
                <div class="text-sm font-semibold text-slate-900">{{ item.name }}</div>
                <div class="text-xs text-slate-400">{{ item.slug }}</div>
              </td>
              <td class="px-4 py-4">{{ item.category || '-' }}</td>
              <td class="px-4 py-4">{{ item.sortOrder }}</td>
              <td class="px-4 py-4">
                <span :class="item.enabled ? 'text-emerald-600' : 'text-red-500'">{{ item.enabled ? '启用' : '禁用' }}</span>
              </td>
              <td class="px-4 py-4 flex items-center gap-2">
                <button class="text-xs px-3 py-1.5 rounded-lg bg-slate-100" @click="edit(item)">编辑</button>
                <button class="text-xs px-3 py-1.5 rounded-lg bg-slate-100" @click="toggle(item)">{{ item.enabled ? '禁用' : '启用' }}</button>
                <button class="text-xs px-3 py-1.5 rounded-lg bg-red-50 text-red-600" @click="remove(item)">删除并清理前端</button>
              </td>
            </tr>
            <tr v-if="items.length === 0">
              <td colspan="5" class="text-center text-slate-400 py-8">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { adminApi } from '@/api/admin'

const items = ref([])
const filterCategory = ref('')
const editingId = ref(null)
const generateFrontend = ref(true)
const form = reactive({
  slug: '',
  name: '',
  description: '',
  category: '',
  keywords: '',
  sortOrder: 0,
  enabled: true
})

async function load() {
  items.value = await adminApi.listTools()
  if (filterCategory.value) {
    items.value = items.value.filter(x => x.category === filterCategory.value)
  }
}

function edit(item) {
  editingId.value = item.id
  form.slug = item.slug
  form.name = item.name
  form.description = item.description || ''
  form.category = item.category || ''
  form.keywords = Array.isArray(item.keywords) ? item.keywords.join(',') : (item.keywords || '')
  form.sortOrder = item.sortOrder || 0
  form.enabled = item.enabled
}

async function submit() {
  const payload = { ...form }
  if (editingId.value) {
    await adminApi.updateTool(editingId.value, payload)
  } else {
    await adminApi.createTool(payload, { generateFrontend: generateFrontend.value })
  }
  editingId.value = null
  form.slug = ''
  form.name = ''
  form.description = ''
  form.category = ''
  form.keywords = ''
  form.sortOrder = 0
  form.enabled = true
  await load()
}

async function toggle(item) {
  await adminApi.updateTool(item.id, { ...item, enabled: !item.enabled })
  await load()
}

async function remove(item) {
  if (!confirm(`确定删除工具 ${item.name} 并清理前端代码吗？`)) return
  await adminApi.deleteTool(item.id, { deleteFrontend: true })
  await load()
}

onMounted(async () => {
  await load()
})
</script>
