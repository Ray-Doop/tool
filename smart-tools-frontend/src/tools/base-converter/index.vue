<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/base-converter/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、element-plus、vue-clipboard3
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-2xl mx-auto space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200 grid grid-cols-1 gap-4">
      <div v-for="base in bases" :key="base.value">
        <label class="block text-sm font-medium text-slate-700 mb-1">{{ base.label }} (Base {{ base.value }})</label>
        <el-input 
          v-model="values[base.value]" 
          placeholder="输入数字..." 
          @input="val => update(val, base.value)"
        >
          <template #append>
            <el-button @click="copy(values[base.value])">复制</el-button>
          </template>
        </el-input>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import useClipboard from 'vue-clipboard3'

const { toClipboard } = useClipboard()

const bases = [
  { label: '二进制', value: 2 },
  { label: '八进制', value: 8 },
  { label: '十进制', value: 10 },
  { label: '十六进制', value: 16 },
  { label: 'Base 32', value: 32 },
  { label: 'Base 36', value: 36 }
]

const values = reactive({
  2: '',
  8: '',
  10: '',
  16: '',
  32: '',
  36: ''
})

function update(val, fromBase) {
  if (!val) {
    Object.keys(values).forEach(k => values[k] = '')
    return
  }

  try {
    // Remove invalid chars for the base? Simplified for now.
    // For hex, remove 0x prefix if present
    let cleanVal = val
    if (fromBase === 16 && val.startsWith('0x')) cleanVal = val.slice(2)
    
    const decimal = parseInt(cleanVal, fromBase)
    if (isNaN(decimal)) return // Or handle error

    bases.forEach(b => {
      if (b.value !== fromBase) {
        values[b.value] = decimal.toString(b.value).toUpperCase()
      }
    })
  } catch (e) {
    // ignore
  }
}

async function copy(text) {
  if (!text) return
  try {
    await toClipboard(text)
    ElMessage.success('已复制')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}
</script>
