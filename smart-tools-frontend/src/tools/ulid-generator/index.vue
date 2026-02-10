<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/ulid-generator/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、ulid、element-plus、vue-clipboard3
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-2xl mx-auto space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <div class="flex items-center space-x-4 mb-6">
        <el-input-number v-model="count" :min="1" :max="50" label="数量" />
        <el-button type="primary" @click="generate">重新生成</el-button>
        <el-button @click="copyAll">复制全部</el-button>
      </div>
      
      <div class="space-y-2">
        <div v-for="(id, idx) in list" :key="idx" class="flex items-center justify-between p-3 bg-slate-50 rounded border border-slate-200 font-mono text-sm">
          <span>{{ id }}</span>
          <el-button type="primary" link size="small" @click="copy(id)">复制</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ulid } from 'ulid'
import { ElMessage } from 'element-plus'
import useClipboard from 'vue-clipboard3'

const { toClipboard } = useClipboard()
const count = ref(5)
const list = ref([])

function generate() {
  list.value = Array.from({ length: count.value }, () => ulid())
}

async function copy(text) {
  try {
    await toClipboard(text)
    ElMessage.success('已复制')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}

async function copyAll() {
  try {
    await toClipboard(list.value.join('\n'))
    ElMessage.success('已复制全部')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}

onMounted(generate)
</script>
