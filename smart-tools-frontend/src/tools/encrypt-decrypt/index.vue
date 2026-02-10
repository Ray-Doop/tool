<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/encrypt-decrypt/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、crypto-js、element-plus、vue-clipboard3
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-4xl mx-auto space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <el-tabs v-model="mode">
        <el-tab-pane label="加密 (Encrypt)" name="encrypt" />
        <el-tab-pane label="解密 (Decrypt)" name="decrypt" />
      </el-tabs>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mt-4">
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">算法</label>
            <el-select v-model="algo" class="w-full">
              <el-option label="AES" value="AES" />
              <el-option label="DES" value="DES" />
              <el-option label="TripleDES" value="TripleDES" />
              <el-option label="RC4" value="RC4" />
              <el-option label="Rabbit" value="Rabbit" />
            </el-select>
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">密钥 (Key)</label>
            <el-input v-model="key" type="password" show-password />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">输入文本</label>
            <el-input v-model="input" type="textarea" :rows="6" placeholder="输入内容..." />
          </div>
          <el-button type="primary" class="w-full" @click="process">
            {{ mode === 'encrypt' ? '加密' : '解密' }}
          </el-button>
        </div>

        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">结果</label>
            <el-input v-model="output" type="textarea" :rows="14" readonly />
          </div>
          <el-button @click="copy(output)" class="w-full">复制结果</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CryptoJS from 'crypto-js'
import { ElMessage } from 'element-plus'
import useClipboard from 'vue-clipboard3'

const { toClipboard } = useClipboard()

const mode = ref('encrypt')
const algo = ref('AES')
const key = ref('')
const input = ref('')
const output = ref('')

function process() {
  if (!input.value || !key.value) {
    output.value = ''
    return
  }

  try {
    const k = key.value
    const txt = input.value
    let res = null

    if (mode.value === 'encrypt') {
      res = CryptoJS[algo.value].encrypt(txt, k).toString()
    } else {
      const bytes = CryptoJS[algo.value].decrypt(txt, k)
      res = bytes.toString(CryptoJS.enc.Utf8)
      if (!res) throw new Error('解密失败')
    }
    output.value = res
  } catch (e) {
    output.value = 'Error: ' + e.message
  }
}

async function copy(text) {
  if (!text) return
  try {
    await toClipboard(text)
    ElMessage.success('已复制')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}
</script>
