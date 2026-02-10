<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/pdf-signature-checker/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、@/api/http、element-plus
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="space-y-6">
    <!-- Upload Area -->
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <div 
        class="border-2 border-dashed border-slate-300 rounded-lg p-12 text-center hover:border-indigo-500 transition-colors cursor-pointer"
        @click="triggerUpload"
        @dragover.prevent
        @drop.prevent="handleDrop"
      >
        <input 
          type="file" 
          ref="fileInput" 
          class="hidden" 
          accept=".pdf" 
          @change="handleFileChange" 
        />
        <div v-if="!file">
          <svg class="mx-auto h-12 w-12 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
          </svg>
          <p class="mt-4 text-sm text-slate-600">点击或拖拽上传 PDF 文件</p>
          <p class="mt-1 text-xs text-slate-500">仅支持 PDF 格式，文件仅用于解析签名，不保存</p>
        </div>
        <div v-else class="flex items-center justify-center gap-3">
          <svg class="h-8 w-8 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <div class="text-left">
            <p class="text-sm font-medium text-slate-900">{{ file.name }}</p>
            <p class="text-xs text-slate-500">{{ formatSize(file.size) }}</p>
          </div>
          <button 
            @click.stop="clearFile"
            class="ml-4 p-1 rounded-full hover:bg-slate-100 text-slate-400 hover:text-red-500"
          >
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
      
      <div v-if="file" class="mt-4 flex justify-end">
        <el-button type="primary" :loading="loading" @click="analyze">开始验证签名</el-button>
      </div>
    </div>

    <!-- Results -->
    <div v-if="analyzed" class="space-y-4">
      <div v-if="signatures.length === 0" class="bg-yellow-50 border border-yellow-200 rounded-lg p-4 flex items-center gap-3">
        <svg class="h-5 w-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <span class="text-sm text-yellow-700">该文档未包含任何数字签名信息。</span>
      </div>

      <div v-else class="bg-white rounded-lg shadow-sm border border-slate-200 overflow-hidden">
        <div class="px-6 py-4 border-b border-slate-100 flex justify-between items-center bg-slate-50">
          <h3 class="font-medium text-slate-900">检测到 {{ signatures.length }} 个签名</h3>
        </div>
        <ul class="divide-y divide-slate-100">
          <li v-for="(sig, idx) in signatures" :key="idx" class="p-6 hover:bg-slate-50 transition-colors">
            <div class="flex items-start gap-4">
              <div class="flex-shrink-0 mt-1">
                <div class="h-10 w-10 rounded-full bg-green-100 flex items-center justify-center text-green-600">
                  <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
              </div>
              <div class="flex-1 space-y-2">
                <div class="flex flex-wrap justify-between gap-2">
                  <h4 class="text-base font-semibold text-slate-900">{{ sig.signerName || '未知签署者' }}</h4>
                  <span class="text-xs bg-slate-100 text-slate-600 px-2 py-1 rounded">{{ sig.signatureDate }}</span>
                </div>
                
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-2 text-sm">
                  <div class="flex gap-2">
                    <span class="text-slate-500 w-12 flex-shrink-0">位置:</span>
                    <span class="text-slate-900">{{ sig.location || '-' }}</span>
                  </div>
                  <div class="flex gap-2">
                    <span class="text-slate-500 w-12 flex-shrink-0">原因:</span>
                    <span class="text-slate-900">{{ sig.reason || '-' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { http } from '@/api/http'
import { ElMessage } from 'element-plus'

const fileInput = ref(null)
const file = ref(null)
const loading = ref(false)
const analyzed = ref(false)
const signatures = ref([])

function triggerUpload() {
  fileInput.value.click()
}

function handleFileChange(e) {
  const selected = e.target.files[0]
  if (selected) setFile(selected)
}

function handleDrop(e) {
  const dropped = e.dataTransfer.files[0]
  if (dropped && dropped.type === 'application/pdf') {
    setFile(dropped)
  } else {
    ElMessage.warning('请上传 PDF 文件')
  }
}

function setFile(f) {
  file.value = f
  analyzed.value = false
  signatures.value = []
}

function clearFile() {
  file.value = null
  if (fileInput.value) fileInput.value.value = ''
  analyzed.value = false
  signatures.value = []
}

function formatSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

async function analyze() {
  if (!file.value) return
  
  loading.value = true
  const formData = new FormData()
  formData.append('file', file.value)
  
  try {
    const { data } = await http.post('/api/tools/pdf/signature/check', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    
    if (data.success) {
      signatures.value = data.data
      analyzed.value = true
    } else {
      ElMessage.error(data.error?.message || '验证失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '网络请求失败')
  } finally {
    loading.value = false
  }
}
</script>
