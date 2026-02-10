<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/base64-file/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、element-plus
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <div class="flex items-center justify-center w-full">
        <label class="flex flex-col items-center justify-center w-full h-32 border-2 border-slate-300 border-dashed rounded-lg cursor-pointer bg-slate-50 hover:bg-slate-100">
          <div class="flex flex-col items-center justify-center pt-5 pb-6">
            <svg class="w-8 h-8 mb-4 text-slate-500" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16">
              <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2"/>
            </svg>
            <p class="mb-2 text-sm text-slate-500"><span class="font-semibold">点击上传</span> 或拖拽文件到此处</p>
          </div>
          <input type="file" class="hidden" @change="handleFile" />
        </label>
      </div>
    </div>

    <div v-if="result" class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <div class="flex items-center justify-between mb-4">
        <div>
          <h3 class="text-lg font-medium text-slate-900">转换结果</h3>
          <p class="text-sm text-slate-500">{{ fileName }} ({{ fileSize }})</p>
        </div>
        <el-button type="primary" @click="copy">复制 Base64</el-button>
      </div>
      
      <el-input
        v-model="result"
        type="textarea"
        :rows="8"
        readonly
        class="font-mono text-xs"
      />

      <div v-if="isImage" class="mt-4">
        <p class="text-sm font-medium text-slate-700 mb-2">预览</p>
        <img :src="result" class="max-h-64 rounded border border-slate-200" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const result = ref('')
const fileName = ref('')
const fileSize = ref('')
const fileType = ref('')

const isImage = computed(() => fileType.value.startsWith('image/'))

function handleFile(event) {
  const file = event.target.files[0]
  if (!file) return

  fileName.value = file.name
  fileSize.value = formatSize(file.size)
  fileType.value = file.type

  const reader = new FileReader()
  reader.onload = (e) => {
    result.value = e.target.result
  }
  reader.readAsDataURL(file)
}

function formatSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

function copy() {
  if (!result.value) return
  navigator.clipboard.writeText(result.value)
  ElMessage.success('已复制')
}
</script>
