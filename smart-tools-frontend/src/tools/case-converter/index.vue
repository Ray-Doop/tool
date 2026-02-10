<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/case-converter/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、element-plus
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
        placeholder="helloWorld or hello_world..."
        @input="convert"
      />
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div v-for="(val, type) in results" :key="type" class="bg-white p-4 rounded-lg shadow-sm border border-slate-200">
        <div class="flex items-center justify-between mb-2">
          <span class="text-sm font-bold text-slate-500 uppercase">{{ labels[type] }}</span>
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
import { ElMessage } from 'element-plus'

const input = ref('')
const results = reactive({
  lower: '',
  upper: '',
  camel: '',
  pascal: '',
  snake: '',
  kebab: '',
  constant: ''
})

const labels = {
  lower: 'lowercase',
  upper: 'UPPERCASE',
  camel: 'camelCase',
  pascal: 'PascalCase',
  snake: 'snake_case',
  kebab: 'kebab-case',
  constant: 'CONSTANT_CASE'
}

function convert() {
  const text = input.value.trim()
  if (!text) {
    Object.keys(results).forEach(k => results[k] = '')
    return
  }

  // Split words by space, underscore, hyphen, or camelCase
  const words = text
    .replace(/([a-z])([A-Z])/g, '$1 $2')
    .replace(/[_-]/g, ' ')
    .toLowerCase()
    .split(/\s+/)
    .filter(w => w)

  results.lower = words.join('')
  results.upper = words.join('').toUpperCase()
  
  results.camel = words
    .map((w, i) => i === 0 ? w : w[0].toUpperCase() + w.slice(1))
    .join('')
    
  results.pascal = words
    .map(w => w[0].toUpperCase() + w.slice(1))
    .join('')
    
  results.snake = words.join('_')
  results.kebab = words.join('-')
  results.constant = words.join('_').toUpperCase()
}

function copy(text) {
  if (!text) return
  navigator.clipboard.writeText(text)
  ElMessage.success('已复制')
}
</script>
