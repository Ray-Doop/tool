<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/bcrypt-generator/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、bcryptjs、element-plus、vue-clipboard3
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-3xl mx-auto space-y-8">
    <!-- Generator Section -->
    <div class="bg-slate-50/50 rounded-xl p-6 border border-slate-100/50">
      <div class="flex items-center gap-2 mb-6">
        <div class="w-1 h-5 bg-indigo-500 rounded-full"></div>
        <h3 class="text-base font-bold text-slate-900">Hash 生成</h3>
      </div>
      
      <div class="space-y-6">
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">明文 (Plain Text)</label>
          <el-input 
            v-model="input" 
            placeholder="请输入要加密的文本，例如：password123" 
            size="large"
            class="shadow-sm"
            @input="hash" 
          />
        </div>
        
        <div class="bg-white p-4 rounded-lg border border-slate-100 shadow-sm">
          <div class="flex justify-between items-center mb-2">
            <label class="text-sm font-medium text-slate-700">强度设置 (Salt Rounds)</label>
            <span class="text-xs font-mono bg-slate-100 px-2 py-0.5 rounded text-slate-600">{{ rounds }}</span>
          </div>
          <el-slider v-model="rounds" :min="4" :max="12" @change="hash" />
          <p class="text-xs text-slate-400 mt-1">数值越大计算越慢，安全性越高。建议范围 10-12。</p>
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">Bcrypt Hash 结果</label>
          <div class="relative group">
            <el-input 
              v-model="hashed" 
              readonly 
              type="textarea" 
              :rows="3"
              resize="none"
              class="font-mono text-sm"
            />
            <div class="absolute bottom-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity">
              <el-button size="small" type="primary" plain @click="copy(hashed)">复制</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Verify Section -->
    <div class="bg-slate-50/50 rounded-xl p-6 border border-slate-100/50">
      <div class="flex items-center gap-2 mb-6">
        <div class="w-1 h-5 bg-emerald-500 rounded-full"></div>
        <h3 class="text-base font-bold text-slate-900">Hash 校验</h3>
      </div>

      <div class="space-y-4">
        <div class="grid md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">明文</label>
            <el-input v-model="checkInput" placeholder="输入原始密码" @input="compare" />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">Hash 值</label>
            <el-input v-model="checkHash" placeholder="$2a$10$..." @input="compare" />
          </div>
        </div>

        <div 
          v-if="checkInput && checkHash" 
          class="flex items-center gap-3 p-4 rounded-xl border transition-all duration-300"
          :class="match ? 'bg-emerald-50 border-emerald-200 text-emerald-700' : 'bg-rose-50 border-rose-200 text-rose-700'"
        >
          <div class="flex-shrink-0 flex items-center justify-center w-8 h-8 rounded-full" :class="match ? 'bg-emerald-100' : 'bg-rose-100'">
            <svg v-if="match" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
            <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </div>
          <div>
            <div class="font-bold text-sm">{{ match ? '校验通过' : '校验失败' }}</div>
            <div class="text-xs opacity-80">{{ match ? '明文与 Hash 匹配。' : '明文与 Hash 不匹配。' }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import bcrypt from 'bcryptjs'
import { ElMessage } from 'element-plus'
import useClipboard from 'vue-clipboard3'

const { toClipboard } = useClipboard()

const input = ref('')
const rounds = ref(10)
const hashed = ref('')

const checkInput = ref('')
const checkHash = ref('')
const match = ref(false)

function hash() {
  if (!input.value) {
    hashed.value = ''
    return
  }
  // Sync version blocks UI, but for small inputs it's OK. 
  // For better UX, use async but it requires callback/promise
  hashed.value = bcrypt.hashSync(input.value, rounds.value)
}

function compare() {
  if (!checkInput.value || !checkHash.value) {
    match.value = false
    return
  }
  try {
    match.value = bcrypt.compareSync(checkInput.value, checkHash.value)
  } catch (e) {
    match.value = false
  }
}

async function copy(text) {
  try {
    await toClipboard(text)
    ElMessage.success('已复制')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}
</script>
