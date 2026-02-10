<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/hash-text/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、crypto-js、element-plus
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <label class="block text-sm font-medium text-slate-700 mb-2">输入文本</label>
      <el-input
        v-model="input"
        type="textarea"
        :rows="4"
        placeholder="输入要计算哈希的内容..."
        @input="calculate"
      />
    </div>

    <div class="grid grid-cols-1 gap-4">
      <div v-for="(val, algo) in results" :key="algo" class="bg-white p-4 rounded-lg shadow-sm border border-slate-200">
        <div class="flex items-center justify-between mb-2">
          <span class="text-sm font-bold text-slate-500 uppercase">{{ algo }}</span>
          <el-button type="info" link @click="copy(val)">复制</el-button>
        </div>
        <div class="font-mono text-sm text-slate-700 break-all select-all">
          {{ val || '-' }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import CryptoJS from 'crypto-js'
import { ElMessage } from 'element-plus'

const input = ref('')
const results = reactive({
  md5: '',
  sha1: '',
  sha256: '',
  sha512: '',
  ripemd160: ''
})

function calculate() {
  if (!input.value) {
    Object.keys(results).forEach(k => results[k] = '')
    return
  }
  
  const text = input.value
  results.md5 = CryptoJS.MD5(text).toString()
  results.sha1 = CryptoJS.SHA1(text).toString()
  results.sha256 = CryptoJS.SHA256(text).toString()
  results.sha512 = CryptoJS.SHA512(text).toString()
  results.ripemd160 = CryptoJS.RIPEMD160(text).toString()
}

function copy(text) {
  if (!text) return
  navigator.clipboard.writeText(text)
  ElMessage.success('已复制')
}
</script>
