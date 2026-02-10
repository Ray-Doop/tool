<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/text-to-binary/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-2xl mx-auto space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200 space-y-6">
      <div>
        <label class="block text-sm font-medium text-slate-700 mb-2">文本 (Text)</label>
        <el-input 
          v-model="text" 
          type="textarea" 
          :rows="4" 
          placeholder="Type something..." 
          @input="onTextChange" 
        />
      </div>

      <div class="flex justify-center">
        <div class="p-2 bg-slate-100 rounded-full">
          <svg class="w-6 h-6 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"></path></svg>
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-slate-700 mb-2">二进制 (Binary)</label>
        <el-input 
          v-model="binary" 
          type="textarea" 
          :rows="6" 
          placeholder="01001000 01100101 01101100 01101100 01101111" 
          @input="onBinaryChange" 
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const text = ref('')
const binary = ref('')

function onTextChange() {
  if (!text.value) {
    binary.value = ''
    return
  }
  binary.value = text.value.split('')
    .map(char => char.charCodeAt(0).toString(2).padStart(8, '0'))
    .join(' ')
}

function onBinaryChange() {
  if (!binary.value) {
    text.value = ''
    return
  }
  try {
    text.value = binary.value.trim().split(/\s+/)
      .map(bin => String.fromCharCode(parseInt(bin, 2)))
      .join('')
  } catch (e) {
    // ignore parsing errors
  }
}
</script>
