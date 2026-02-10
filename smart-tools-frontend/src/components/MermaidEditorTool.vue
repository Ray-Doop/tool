<!--
  @generated-file-note
  文件：smart-tools-frontend/src/components/MermaidEditorTool.vue
  用途：前端通用组件（可复用 UI 组件）
  归属：前端 components
  依赖：vue、element-plus、mermaid
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  MermaidEditorTool：Mermaid 在线编辑器
  - 支持输入 Mermaid 语法并实时渲染 SVG
  - 提供复制与下载 SVG
-->
<template>
  <div class="grid gap-4">
    <el-card>
      <div class="flex flex-wrap items-start justify-between gap-3">
        <div class="min-w-0">
          <div class="truncate text-lg font-semibold">{{ title }}</div>
          <div class="text-sm text-slate-500">{{ description }}</div>
        </div>
        <div class="flex flex-wrap items-center gap-2">
          <el-button size="small" @click="renderMermaid">渲染预览</el-button>
          <el-button size="small" @click="copySvg">复制 SVG</el-button>
          <el-button size="small" type="primary" @click="downloadSvg">下载 SVG</el-button>
        </div>
      </div>
      <div class="mt-4 grid gap-3">
        <div class="text-sm text-slate-500">Mermaid 源码</div>
        <el-input v-model="code" type="textarea" :rows="10" placeholder="输入 Mermaid 语法" />
      </div>
    </el-card>

    <el-card>
      <div class="text-sm text-slate-500">预览结果</div>
      <div class="mt-3 overflow-auto rounded-lg border border-slate-200 bg-white p-4" v-html="svgContent" />
      <div v-if="errorMessage" class="mt-2 text-sm text-rose-500">{{ errorMessage }}</div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import mermaid from 'mermaid'

const props = defineProps({
  title: { type: String, default: 'Mermaid 图表' },
  description: { type: String, default: '输入 Mermaid 语法生成流程图、时序图等' },
  defaultCode: { type: String, default: '' }
})

const code = ref(props.defaultCode || 'flowchart LR\n  A[开始] --> B{判断}\n  B -->|是| C[通过]\n  B -->|否| D[结束]')
const svgContent = ref('')
const errorMessage = ref('')
const renderCounter = ref(0)

// 初始化 Mermaid 配置
onMounted(() => {
  mermaid.initialize({ startOnLoad: false })
  renderMermaid()
})

// 渲染 Mermaid SVG
async function renderMermaid() {
  try {
    const id = `mermaid-${Date.now()}-${renderCounter.value++}`
    const { svg } = await mermaid.render(id, code.value)
    svgContent.value = svg
    errorMessage.value = ''
  } catch (err) {
    errorMessage.value = 'Mermaid 语法解析失败，请检查输入'
  }
}

async function copySvg() {
  if (!svgContent.value) {
    ElMessage.warning('暂无可复制内容')
    return
  }
  await navigator.clipboard.writeText(svgContent.value)
  ElMessage.success('已复制 SVG')
}

function downloadSvg() {
  if (!svgContent.value) {
    ElMessage.warning('暂无可下载内容')
    return
  }
  const blob = new Blob([svgContent.value], { type: 'image/svg+xml' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = 'mermaid.svg'
  link.click()
  URL.revokeObjectURL(url)
}

// 输入变化后自动刷新预览
let debounceTimer = 0
watch(code, () => {
  window.clearTimeout(debounceTimer)
  debounceTimer = window.setTimeout(renderMermaid, 400)
})
</script>
