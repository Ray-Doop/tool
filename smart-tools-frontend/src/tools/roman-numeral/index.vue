<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/roman-numeral/index.vue
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
        <label class="block text-sm font-medium text-slate-700 mb-2">数字 (Decimal)</label>
        <el-input v-model="decimal" placeholder="2023" @input="onDecimalChange" />
      </div>
      
      <div class="flex justify-center">
        <div class="p-2 bg-slate-100 rounded-full">
          <svg class="w-6 h-6 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"></path></svg>
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-slate-700 mb-2">罗马数字 (Roman)</label>
        <el-input v-model="roman" placeholder="MMXXIII" @input="onRomanChange" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const decimal = ref('')
const roman = ref('')

const ROMAN_MAP = [
  { val: 1000, char: 'M' },
  { val: 900, char: 'CM' },
  { val: 500, char: 'D' },
  { val: 400, char: 'CD' },
  { val: 100, char: 'C' },
  { val: 90, char: 'XC' },
  { val: 50, char: 'L' },
  { val: 40, char: 'XL' },
  { val: 10, char: 'X' },
  { val: 9, char: 'IX' },
  { val: 5, char: 'V' },
  { val: 4, char: 'IV' },
  { val: 1, char: 'I' }
]

function toRoman(num) {
  if (!num || isNaN(num)) return ''
  let n = parseInt(num)
  let res = ''
  for (const { val, char } of ROMAN_MAP) {
    while (n >= val) {
      res += char
      n -= val
    }
  }
  return res
}

function fromRoman(str) {
  if (!str) return ''
  const s = str.toUpperCase()
  let res = 0
  let i = 0
  while (i < s.length) {
    // Check 2 chars first
    let found = false
    if (i + 1 < s.length) {
      const two = s.substr(i, 2)
      const item = ROMAN_MAP.find(x => x.char === two)
      if (item) {
        res += item.val
        i += 2
        found = true
      }
    }
    if (!found) {
      const one = s.substr(i, 1)
      const item = ROMAN_MAP.find(x => x.char === one)
      if (item) {
        res += item.val
        i += 1
      } else {
        // Invalid char
        i++
      }
    }
  }
  return res.toString()
}

function onDecimalChange() {
  roman.value = toRoman(decimal.value)
}

function onRomanChange() {
  decimal.value = fromRoman(roman.value)
}
</script>
