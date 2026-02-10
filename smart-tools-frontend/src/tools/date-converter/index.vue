<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/date-converter/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、dayjs、dayjs/plugin/utc
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="space-y-6">
    <!-- Current Time -->
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200 flex justify-between items-center">
      <div>
        <div class="text-sm text-slate-500">当前时间</div>
        <div class="text-2xl font-mono mt-1">{{ nowStr }}</div>
      </div>
      <div class="text-right">
        <div class="text-sm text-slate-500">Unix 时间戳 (秒)</div>
        <div class="text-xl font-mono mt-1">{{ nowTs }}</div>
      </div>
    </div>

    <!-- Converter -->
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200 space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Input -->
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">输入时间 (时间戳/日期字符串)</label>
          <div class="flex gap-2">
            <el-input v-model="input" placeholder="例如: 1678888888 或 2023-03-15" @input="parse" />
            <el-button @click="setInputNow">当前</el-button>
          </div>
        </div>

        <!-- Output -->
        <div class="space-y-4">
          <div>
            <div class="text-xs text-slate-500 mb-1">格式化日期 (Local)</div>
            <div class="font-mono bg-slate-50 p-2 rounded border border-slate-200 select-all">{{ output.local || '-' }}</div>
          </div>
          <div>
            <div class="text-xs text-slate-500 mb-1">ISO 8601 (UTC)</div>
            <div class="font-mono bg-slate-50 p-2 rounded border border-slate-200 select-all">{{ output.iso || '-' }}</div>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <div class="text-xs text-slate-500 mb-1">时间戳 (秒)</div>
              <div class="font-mono bg-slate-50 p-2 rounded border border-slate-200 select-all">{{ output.tsSec || '-' }}</div>
            </div>
            <div>
              <div class="text-xs text-slate-500 mb-1">时间戳 (毫秒)</div>
              <div class="font-mono bg-slate-50 p-2 rounded border border-slate-200 select-all">{{ output.tsMs || '-' }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
dayjs.extend(utc)

const nowStr = ref('')
const nowTs = ref(0)
const input = ref('')
const output = reactive({
  local: '',
  iso: '',
  tsSec: '',
  tsMs: ''
})

// Update current time
const timer = setInterval(() => {
  const n = dayjs()
  nowStr.value = n.format('YYYY-MM-DD HH:mm:ss')
  nowTs.value = n.unix()
}, 1000)

onUnmounted(() => clearInterval(timer))

function setInputNow() {
  input.value = dayjs().unix().toString()
  parse()
}

function parse() {
  if (!input.value) {
    output.local = output.iso = output.tsSec = output.tsMs = ''
    return
  }

  let d
  const val = input.value.trim()
  
  // Try parsing as timestamp (number)
  if (/^\d+$/.test(val)) {
    // If length > 11, likely ms, else sec
    if (val.length > 11) {
      d = dayjs(parseInt(val))
    } else {
      d = dayjs.unix(parseInt(val))
    }
  } else {
    // Try parsing as string
    d = dayjs(val)
  }

  if (d.isValid()) {
    output.local = d.format('YYYY-MM-DD HH:mm:ss')
    output.iso = d.toISOString()
    output.tsSec = d.unix()
    output.tsMs = d.valueOf()
  } else {
    output.local = '无效日期'
    output.iso = '-'
    output.tsSec = '-'
    output.tsMs = '-'
  }
}

// Init
setInputNow()
</script>
