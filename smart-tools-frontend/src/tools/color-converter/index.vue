<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/color-converter/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、element-plus
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="space-y-6">
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- Input & Preview -->
      <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200 space-y-6">
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">颜色预览</label>
          <div 
            class="h-32 w-full rounded-lg shadow-inner border border-slate-200 transition-colors"
            :style="{ backgroundColor: cssColor }"
          ></div>
        </div>
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">输入颜色</label>
          <el-input v-model="input" placeholder="#FFFFFF or rgb(255, 255, 255)" @input="parse" />
        </div>
      </div>

      <!-- Outputs -->
      <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200 space-y-4">
        <div v-for="(val, type) in outputs" :key="type">
          <div class="flex items-center justify-between mb-1">
            <span class="text-xs font-medium text-slate-500 uppercase">{{ type }}</span>
            <el-button v-if="val" type="info" link size="small" @click="copy(val)">复制</el-button>
          </div>
          <div class="font-mono bg-slate-50 p-2 rounded border border-slate-200 select-all text-sm">
            {{ val || '-' }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const input = ref('#3b82f6')
const cssColor = ref('#3b82f6')
const outputs = reactive({
  hex: '#3b82f6',
  rgb: 'rgb(59, 130, 246)',
  hsl: 'hsl(217, 91%, 60%)'
})

function parse() {
  const color = input.value.trim()
  if (!color) return

  // Create a temporary element to let browser parse color
  const temp = document.createElement('div')
  temp.style.color = color
  temp.style.display = 'none' // Avoid layout
  document.body.appendChild(temp)
  
  // Check if valid
  const computed = window.getComputedStyle(temp).color
  document.body.removeChild(temp)

  if (!computed) {
    // Invalid
    return
  }

  cssColor.value = computed
  
  // Parse RGB
  const rgbMatch = computed.match(/\d+/g)
  if (!rgbMatch || rgbMatch.length < 3) return
  
  const r = parseInt(rgbMatch[0])
  const g = parseInt(rgbMatch[1])
  const b = parseInt(rgbMatch[2])
  const a = rgbMatch[3] ? parseFloat(rgbMatch[3]) : 1

  outputs.rgb = a === 1 ? `rgb(${r}, ${g}, ${b})` : `rgba(${r}, ${g}, ${b}, ${a})`
  outputs.hex = rgbToHex(r, g, b, a)
  outputs.hsl = rgbToHsl(r, g, b, a)
}

function rgbToHex(r, g, b, a) {
  const toHex = (n) => {
    const hex = n.toString(16)
    return hex.length === 1 ? '0' + hex : hex
  }
  let hex = '#' + toHex(r) + toHex(g) + toHex(b)
  if (a !== 1) {
    hex += toHex(Math.round(a * 255))
  }
  return hex
}

function rgbToHsl(r, g, b, a) {
  r /= 255
  g /= 255
  b /= 255
  const max = Math.max(r, g, b)
  const min = Math.min(r, g, b)
  let h, s, l = (max + min) / 2

  if (max === min) {
    h = s = 0
  } else {
    const d = max - min
    s = l > 0.5 ? d / (2 - max - min) : d / (max + min)
    switch (max) {
      case r: h = (g - b) / d + (g < b ? 6 : 0); break
      case g: h = (b - r) / d + 2; break
      case b: h = (r - g) / d + 4; break
    }
    h /= 6
  }

  h = Math.round(h * 360)
  s = Math.round(s * 100)
  l = Math.round(l * 100)

  return a === 1 
    ? `hsl(${h}, ${s}%, ${l}%)` 
    : `hsla(${h}, ${s}%, ${l}%, ${a})`
}

function copy(text) {
  if (!text) return
  navigator.clipboard.writeText(text)
  ElMessage.success('已复制')
}

// Init
parse()
</script>
