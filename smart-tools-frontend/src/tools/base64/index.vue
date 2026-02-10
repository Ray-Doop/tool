<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/base64/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、element-plus
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  Base64 工具页：
  - 纯前端实现（btoa/atob）
  - 提供编码/解码/复制/清空
-->
<template>
  <div class="grid gap-4">
    <el-card>
      <div class="grid gap-3">
        <el-input v-model="input" type="textarea" :rows="6" placeholder="输入文本" />
        <div class="flex flex-wrap items-center gap-2">
          <el-button type="primary" @click="encode">编码</el-button>
          <el-button type="success" @click="decode">解码</el-button>
          <el-button @click="copy">复制输出</el-button>
          <el-button @click="clearAll">清空</el-button>
        </div>
      </div>
    </el-card>

    <el-card>
      <div class="grid gap-2">
        <div class="text-sm text-slate-500">输出</div>
        <div class="break-all font-mono text-sm">{{ output }}</div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// 输入/输出的响应式状态
const input = ref('')
const output = ref('')

// UTF-8 安全的 Base64 编码（避免直接 btoa 遇到中文报错）
function toBase64Utf8(text) {
  const bytes = new TextEncoder().encode(text)
  let binary = ''
  for (const b of bytes) binary += String.fromCharCode(b)
  return btoa(binary)
}

// UTF-8 安全的 Base64 解码
function fromBase64Utf8(base64) {
  const binary = atob(base64)
  const bytes = new Uint8Array(binary.length)
  for (let i = 0; i < binary.length; i += 1) bytes[i] = binary.charCodeAt(i)
  return new TextDecoder().decode(bytes)
}

function encode() {
  output.value = toBase64Utf8(input.value)
}

function decode() {
  try {
    output.value = fromBase64Utf8(input.value.trim())
  } catch {
    ElMessage.error('不是有效的 Base64')
  }
}

async function copy() {
  await navigator.clipboard.writeText(output.value)
  ElMessage.success('已复制')
}

function clearAll() {
  input.value = ''
  output.value = ''
}
</script>
