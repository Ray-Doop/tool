<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/uuid-generator/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、element-plus
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  UUID Generator 工具页：
  - 使用浏览器 crypto.randomUUID 生成 v4 UUID
  - 支持复制到剪贴板
-->
<template>
  <div class="grid gap-4">
    <el-card>
      <div class="flex flex-wrap items-center gap-2">
        <el-button type="primary" @click="generate">生成</el-button>
        <el-button @click="copy">复制</el-button>
      </div>
    </el-card>

    <el-card>
      <div class="break-all font-mono text-sm">{{ value }}</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// UUID v4：直接使用浏览器端 crypto.randomUUID（无需后端参与）
const value = ref(crypto.randomUUID())

function generate() {
  value.value = crypto.randomUUID()
}

async function copy() {
  // 复制到剪贴板：需要 https 或 localhost 环境
  await navigator.clipboard.writeText(value.value)
  ElMessage.success('已复制')
}
</script>
