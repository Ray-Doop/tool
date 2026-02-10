<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/json-to-toml/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、@iarna/toml
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-4xl mx-auto space-y-6">
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 h-[600px]">
      <div class="flex flex-col">
        <label class="block text-sm font-medium text-slate-700 mb-2">JSON</label>
        <el-input v-model="json" type="textarea" class="flex-1" :rows="20" @input="convert" />
      </div>
      <div class="flex flex-col">
        <label class="block text-sm font-medium text-slate-700 mb-2">TOML</label>
        <el-input v-model="tomlStr" type="textarea" class="flex-1" :rows="20" readonly />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import TOML from '@iarna/toml'

const json = ref('{\n  "name": "example",\n  "version": "1.0.0"\n}')
const tomlStr = ref('')

function convert() {
  try {
    const obj = JSON.parse(json.value)
    tomlStr.value = TOML.stringify(obj)
  } catch (e) {
    tomlStr.value = 'Error: ' + e.message
  }
}

convert()
</script>
