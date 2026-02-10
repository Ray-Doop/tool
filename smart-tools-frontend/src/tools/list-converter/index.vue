<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/list-converter/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-4xl mx-auto space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200 grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="space-y-4">
        <label class="block text-sm font-medium text-slate-700">输入列表 (每行一项)</label>
        <el-input v-model="input" type="textarea" :rows="10" placeholder="A\nB\nC" @input="process" />
        <div class="text-xs text-slate-500">行数: {{ inputCount }}</div>
      </div>

      <div class="space-y-4">
        <label class="block text-sm font-medium text-slate-700">输出列表</label>
        <el-input v-model="output" type="textarea" :rows="10" readonly />
        <div class="text-xs text-slate-500">行数: {{ outputCount }}</div>
      </div>
    </div>

    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200 space-y-4">
      <div class="flex flex-wrap gap-4">
        <el-checkbox v-model="config.sort">排序 (A-Z)</el-checkbox>
        <el-checkbox v-model="config.reverse">反转</el-checkbox>
        <el-checkbox v-model="config.dedup">去重</el-checkbox>
        <el-checkbox v-model="config.trim">去除首尾空格</el-checkbox>
        <el-checkbox v-model="config.removeEmpty">去除空行</el-checkbox>
      </div>
      
      <div class="flex items-center gap-4">
        <span class="text-sm font-medium">连接符:</span>
        <el-radio-group v-model="config.joiner" @change="process">
          <el-radio label="\n">换行符</el-radio>
          <el-radio label=",">逗号</el-radio>
          <el-radio label=";">分号</el-radio>
          <el-radio label=" ">空格</el-radio>
        </el-radio-group>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'

const input = ref('')
const output = ref('')
const config = reactive({
  sort: false,
  reverse: false,
  dedup: false,
  trim: true,
  removeEmpty: true,
  joiner: '\n'
})

const inputCount = computed(() => input.value ? input.value.split('\n').length : 0)
const outputCount = computed(() => output.value ? output.value.split(config.joiner === '\n' ? '\n' : config.joiner).length : 0)

function process() {
  if (!input.value) {
    output.value = ''
    return
  }

  // Always split by newline first for processing
  let list = input.value.split('\n')

  if (config.trim) {
    list = list.map(x => x.trim())
  }

  if (config.removeEmpty) {
    list = list.filter(x => x)
  }

  if (config.dedup) {
    list = [...new Set(list)]
  }

  if (config.sort) {
    list.sort((a, b) => a.localeCompare(b))
  }

  if (config.reverse) {
    list.reverse()
  }

  output.value = list.join(config.joiner)
}

watch(config, process)
</script>
