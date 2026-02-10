<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/json-to-yaml/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、js-yaml、element-plus
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="h-[calc(100vh-200px)] min-h-[500px] flex flex-col">
    <div class="flex-1 grid grid-cols-1 md:grid-cols-2 gap-4">
      <!-- Left: JSON -->
      <div class="flex flex-col border border-slate-200 rounded-lg bg-white overflow-hidden">
        <div class="px-4 py-2 border-b border-slate-100 bg-slate-50 flex justify-between items-center">
          <span class="font-medium text-slate-700">JSON</span>
          <div class="space-x-2">
            <el-button size="small" @click="formatJson">格式化</el-button>
            <el-button size="small" @click="copy(jsonVal)">复制</el-button>
          </div>
        </div>
        <textarea
          v-model="jsonVal"
          class="flex-1 p-4 w-full h-full resize-none focus:outline-none font-mono text-sm"
          placeholder="在此输入 JSON..."
          @input="onJsonChange"
        ></textarea>
      </div>

      <!-- Right: YAML -->
      <div class="flex flex-col border border-slate-200 rounded-lg bg-white overflow-hidden">
        <div class="px-4 py-2 border-b border-slate-100 bg-slate-50 flex justify-between items-center">
          <span class="font-medium text-slate-700">YAML</span>
          <div class="space-x-2">
            <el-button size="small" @click="copy(yamlVal)">复制</el-button>
          </div>
        </div>
        <textarea
          v-model="yamlVal"
          class="flex-1 p-4 w-full h-full resize-none focus:outline-none font-mono text-sm"
          placeholder="在此输入 YAML..."
          @input="onYamlChange"
        ></textarea>
      </div>
    </div>
    <div v-if="error" class="mt-4 p-3 bg-red-50 text-red-600 rounded border border-red-100 text-sm">
      {{ error }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import jsyaml from 'js-yaml'
import { ElMessage } from 'element-plus'

const jsonVal = ref('')
const yamlVal = ref('')
const error = ref('')

function onJsonChange() {
  error.value = ''
  if (!jsonVal.value.trim()) {
    yamlVal.value = ''
    return
  }
  try {
    const obj = JSON.parse(jsonVal.value)
    yamlVal.value = jsyaml.dump(obj)
  } catch (e) {
    // Don't clear YAML if JSON is invalid while typing
    // error.value = '无效的 JSON: ' + e.message
  }
}

function onYamlChange() {
  error.value = ''
  if (!yamlVal.value.trim()) {
    jsonVal.value = ''
    return
  }
  try {
    const obj = jsyaml.load(yamlVal.value)
    jsonVal.value = JSON.stringify(obj, null, 2)
  } catch (e) {
    // error.value = '无效的 YAML: ' + e.message
  }
}

function formatJson() {
  try {
    const obj = JSON.parse(jsonVal.value)
    jsonVal.value = JSON.stringify(obj, null, 2)
    error.value = ''
  } catch (e) {
    error.value = '无效的 JSON'
  }
}

function copy(text) {
  if (!text) return
  navigator.clipboard.writeText(text)
  ElMessage.success('已复制')
}
</script>
