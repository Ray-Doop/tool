<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/password-strength/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、zxcvbn
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-2xl mx-auto space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200 space-y-6">
      <div>
        <label class="block text-sm font-medium text-slate-700 mb-2">密码</label>
        <el-input v-model="password" type="password" show-password placeholder="输入密码进行分析..." @input="analyze" />
      </div>

      <div v-if="result" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-1">评分 (Score: {{ result.score }}/4)</label>
          <el-progress 
            :percentage="(result.score + 1) * 20" 
            :status="status" 
            :stroke-width="12"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="bg-slate-50 p-3 rounded">
            <div class="text-xs text-slate-500">破解时间 (离线)</div>
            <div class="font-bold">{{ result.crack_times_display.offline_slow_hashing_1e4_per_second }}</div>
          </div>
          <div class="bg-slate-50 p-3 rounded">
            <div class="text-xs text-slate-500">破解时间 (在线)</div>
            <div class="font-bold">{{ result.crack_times_display.online_no_throttling_10_per_second }}</div>
          </div>
        </div>

        <div v-if="result.feedback.warning" class="bg-orange-50 p-3 rounded border border-orange-100 text-orange-700 text-sm">
          <strong>警告:</strong> {{ result.feedback.warning }}
        </div>

        <div v-if="result.feedback.suggestions.length" class="bg-blue-50 p-3 rounded border border-blue-100 text-blue-700 text-sm">
          <strong>建议:</strong>
          <ul class="list-disc list-inside">
            <li v-for="(s, i) in result.feedback.suggestions" :key="i">{{ s }}</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import zxcvbn from 'zxcvbn'

const password = ref('')
const result = ref(null)

const status = computed(() => {
  if (!result.value) return ''
  const s = result.value.score
  if (s <= 1) return 'exception'
  if (s === 2) return 'warning'
  if (s >= 3) return 'success'
  return ''
})

function analyze() {
  if (!password.value) {
    result.value = null
    return
  }
  result.value = zxcvbn(password.value)
}
</script>
