<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/hmac-generator/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、crypto-js、element-plus、vue-clipboard3
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-2xl mx-auto space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200 space-y-4">
      <div>
        <label class="block text-sm font-medium text-slate-700 mb-2">哈希算法</label>
        <el-radio-group v-model="algo">
          <el-radio-button label="MD5" />
          <el-radio-button label="SHA1" />
          <el-radio-button label="SHA256" />
          <el-radio-button label="SHA512" />
        </el-radio-group>
      </div>
      
      <div>
        <label class="block text-sm font-medium text-slate-700 mb-2">密钥 (Secret Key)</label>
        <el-input v-model="key" placeholder="secret" @input="calculate" />
      </div>

      <div>
        <label class="block text-sm font-medium text-slate-700 mb-2">消息 (Message)</label>
        <el-input v-model="message" type="textarea" :rows="4" placeholder="Message to sign" @input="calculate" />
      </div>

      <div v-if="result">
        <label class="block text-sm font-medium text-slate-700 mb-2">HMAC Result</label>
        <el-input v-model="result" readonly>
          <template #append>
            <el-button @click="copy(result)">复制</el-button>
          </template>
        </el-input>
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

const algo = ref('SHA256')
const key = ref('')
const message = ref('')
const result = ref('')

function calculate() {
  if (!key.value || !message.value) {
    result.value = ''
    return
  }
  const fn = CryptoJS['Hmac' + algo.value]
  if (fn) {
    result.value = fn(message.value, key.value).toString()
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
