<!--
  @generated-file-note
  文件：smart-tools-frontend/src/components/WordCloudTool.vue
  用途：前端通用组件（可复用 UI 组件）
  归属：前端 components
  依赖：vue、element-plus
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  WordCloudTool：词云图工具
  - 使用 Canvas 渲染词云
  - 支持导出 PNG
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
          <el-button size="small" @click="resetData">重置数据</el-button>
          <el-button size="small" type="primary" @click="downloadPng">导出 PNG</el-button>
        </div>
      </div>
      <div class="mt-4 grid gap-3">
        <div class="text-sm text-slate-500">词语权重</div>
        <div class="grid gap-2">
          <div v-for="(row, idx) in wordRows" :key="idx" class="grid grid-cols-12 gap-2">
            <el-input v-model="row.word" class="col-span-7" placeholder="词语" />
            <el-input-number v-model="row.weight" class="col-span-4 w-full" :controls="false" placeholder="权重" />
            <el-button class="col-span-1" @click="removeWord(idx)">删</el-button>
          </div>
        </div>
        <div class="flex flex-wrap items-center gap-2">
          <el-button size="small" @click="addWord">添加一行</el-button>
        </div>
      </div>
    </el-card>

    <el-card>
      <div class="text-sm text-slate-500">词云预览</div>
      <div class="mt-3 flex items-center justify-center rounded-lg border border-slate-200 bg-white p-2">
        <canvas ref="canvasRef" class="h-[420px] w-full" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  title: { type: String, default: '在线制作词云图' },
  description: { type: String, default: '输入词语与权重生成词云图' },
  sample: { type: Object, default: () => ({}) }
})

const defaultRows = [
  { word: '智能', weight: 120 },
  { word: '分析', weight: 90 },
  { word: '增长', weight: 70 },
  { word: '用户', weight: 60 },
  { word: '转化', weight: 50 }
]

const wordRows = ref((props.sample.wordRows || defaultRows).map(row => ({ ...row })))
const canvasRef = ref(null)

function addWord() {
  wordRows.value.push({ word: '', weight: 10 })
}

function removeWord(index) {
  wordRows.value.splice(index, 1)
}

function resetData() {
  wordRows.value = defaultRows.map(row => ({ ...row }))
}

function drawCloud() {
  const canvas = canvasRef.value
  if (!canvas) return
  const width = canvas.parentElement?.clientWidth || 900
  const height = 420
  canvas.width = width
  canvas.height = height
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  ctx.clearRect(0, 0, width, height)

  const weights = wordRows.value.map(row => Number(row.weight) || 0)
  const maxWeight = Math.max(1, ...weights)
  const minWeight = Math.min(...weights, maxWeight)
  const colors = ['#1d4ed8', '#9333ea', '#0ea5e9', '#16a34a', '#f97316', '#e11d48']

  let x = 20
  let y = 60
  wordRows.value.forEach((row, idx) => {
    const weight = Number(row.weight) || 0
    const scale = maxWeight === minWeight ? 0.5 : (weight - minWeight) / (maxWeight - minWeight)
    const fontSize = 16 + scale * 44
    ctx.font = `${Math.round(fontSize)}px sans-serif`
    const text = row.word || '词语'
    const metrics = ctx.measureText(text)
    if (x + metrics.width > width - 20) {
      x = 20
      y += fontSize + 18
    }
    if (y > height - 30) {
      y = 60
      x = 20
    }
    ctx.save()
    ctx.fillStyle = colors[idx % colors.length]
    ctx.translate(x, y)
    ctx.rotate(((idx % 3) - 1) * 0.2)
    ctx.fillText(text, 0, 0)
    ctx.restore()
    x += metrics.width + 20
  })
}

function downloadPng() {
  const canvas = canvasRef.value
  if (!canvas) {
    ElMessage.warning('词云未生成')
    return
  }
  const url = canvas.toDataURL('image/png')
  const link = document.createElement('a')
  link.href = url
  link.download = 'word-cloud.png'
  link.click()
}

// 初次绘制与尺寸监听
onMounted(() => {
  drawCloud()
  window.addEventListener('resize', drawCloud)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', drawCloud)
})

watch(wordRows, drawCloud, { deep: true })
</script>
