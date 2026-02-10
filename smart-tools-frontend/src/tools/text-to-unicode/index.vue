<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/text-to-unicode/index.vue
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
          placeholder="你好 World" 
          @input="onTextChange" 
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-slate-700 mb-2">Unicode Escaped</label>
        <el-input 
          v-model="unicode" 
          type="textarea" 
          :rows="4" 
          placeholder="\u4f60\u597d World" 
          readonly 
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const text = ref('')
const unicode = ref('')

function onTextChange() {
  if (!text.value) {
    unicode.value = ''
    return
  }
  unicode.value = text.value.split('').map(char => {
    const code = char.charCodeAt(0)
    if (code > 127) {
      return '\\u' + code.toString(16).padStart(4, '0')
    }
    return char
  }).join('')
}
</script>
