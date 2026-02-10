<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/token-generator/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、element-plus
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-2xl mx-auto space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">生成选项</label>
          <div class="flex flex-wrap gap-4">
            <el-checkbox v-model="options.uppercase">大写字母 (A-Z)</el-checkbox>
            <el-checkbox v-model="options.lowercase">小写字母 (a-z)</el-checkbox>
            <el-checkbox v-model="options.numbers">数字 (0-9)</el-checkbox>
            <el-checkbox v-model="options.symbols">特殊符号 (!@#$)</el-checkbox>
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">长度: {{ options.length }}</label>
          <el-slider v-model="options.length" :min="4" :max="128" show-input />
        </div>

        <div class="pt-2">
          <el-button type="primary" @click="generate" class="w-full">生成 Token</el-button>
        </div>
      </div>
    </div>

    <div v-if="result" class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <div class="flex items-center justify-between mb-2">
        <h3 class="text-lg font-medium text-slate-900">生成结果</h3>
        <el-button type="info" link @click="copy">复制</el-button>
      </div>
      <div class="bg-slate-50 p-4 rounded border border-slate-200 break-all font-mono text-lg text-slate-700">
        {{ result }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const options = reactive({
  length: 32,
  uppercase: true,
  lowercase: true,
  numbers: true,
  symbols: false
})

const result = ref('')

const chars = {
  uppercase: 'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
  lowercase: 'abcdefghijklmnopqrstuvwxyz',
  numbers: '0123456789',
  symbols: '!@#$%^&*()_+-=[]{}|;:,.<>?'
}

function generate() {
  let charset = ''
  if (options.uppercase) charset += chars.uppercase
  if (options.lowercase) charset += chars.lowercase
  if (options.numbers) charset += chars.numbers
  if (options.symbols) charset += chars.symbols

  if (!charset) {
    ElMessage.warning('请至少选择一种字符类型')
    return
  }

  let token = ''
  const array = new Uint32Array(options.length)
  window.crypto.getRandomValues(array)
  
  for (let i = 0; i < options.length; i++) {
    token += charset[array[i] % charset.length]
  }
  
  result.value = token
}

function copy() {
  if (!result.value) return
  navigator.clipboard.writeText(result.value)
  ElMessage.success('已复制')
}

// Initial generate
generate()
</script>
