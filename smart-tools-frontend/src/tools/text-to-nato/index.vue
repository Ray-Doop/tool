<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/text-to-nato/index.vue
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
        <label class="block text-sm font-medium text-slate-700 mb-2">输入文本</label>
        <el-input 
          v-model="text" 
          type="textarea" 
          :rows="3" 
          placeholder="Hello World" 
          @input="convert" 
        />
      </div>

      <div v-if="result" class="bg-slate-50 p-4 rounded border border-slate-200">
        <div class="flex flex-wrap gap-2">
          <span v-for="(item, idx) in result" :key="idx" 
            class="px-2 py-1 bg-white border border-slate-200 rounded shadow-sm text-sm font-mono"
            :class="{'opacity-50': !item.word}"
          >
            <span class="font-bold text-primary">{{ item.char }}</span>
            <span v-if="item.word" class="text-slate-500 ml-1">{{ item.word }}</span>
          </span>
        </div>
        <div class="mt-4 pt-4 border-t border-slate-200">
          <p class="text-sm text-slate-500 font-mono select-all">{{ resultText }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const text = ref('')
const result = ref([])

const NATO_MAP = {
  a: 'Alpha', b: 'Bravo', c: 'Charlie', d: 'Delta', e: 'Echo', f: 'Foxtrot',
  g: 'Golf', h: 'Hotel', i: 'India', j: 'Juliett', k: 'Kilo', l: 'Lima',
  m: 'Mike', n: 'November', o: 'Oscar', p: 'Papa', q: 'Quebec', r: 'Romeo',
  s: 'Sierra', t: 'Tango', u: 'Uniform', v: 'Victor', w: 'Whiskey', x: 'X-ray',
  y: 'Yankee', z: 'Zulu',
  '0': 'Zero', '1': 'One', '2': 'Two', '3': 'Three', '4': 'Four',
  '5': 'Five', '6': 'Six', '7': 'Seven', '8': 'Eight', '9': 'Nine'
}

function convert() {
  if (!text.value) {
    result.value = []
    return
  }
  
  result.value = text.value.split('').map(char => {
    const key = char.toLowerCase()
    return {
      char: char,
      word: NATO_MAP[key] || ''
    }
  })
}

const resultText = computed(() => {
  return result.value.map(item => item.word || item.char).join(' ')
})
</script>
