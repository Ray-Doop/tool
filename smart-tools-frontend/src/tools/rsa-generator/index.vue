<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/rsa-generator/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、jsencrypt、element-plus、vue-clipboard3
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-4xl mx-auto space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <div class="flex items-center space-x-4 mb-6">
        <el-select v-model="keySize" label="密钥长度">
          <el-option label="1024 bit" :value="1024" />
          <el-option label="2048 bit" :value="2048" />
          <el-option label="4096 bit" :value="4096" />
        </el-select>
        <el-button type="primary" :loading="loading" @click="generate">生成密钥对</el-button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">私钥 (Private Key)</label>
          <el-input v-model="privateKey" type="textarea" :rows="15" readonly class="font-mono text-xs" />
          <el-button class="mt-2 w-full" @click="copy(privateKey)">复制私钥</el-button>
        </div>
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">公钥 (Public Key)</label>
          <el-input v-model="publicKey" type="textarea" :rows="15" readonly class="font-mono text-xs" />
          <el-button class="mt-2 w-full" @click="copy(publicKey)">复制公钥</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import JSEncrypt from 'jsencrypt'
import { ElMessage } from 'element-plus'
import useClipboard from 'vue-clipboard3'

const { toClipboard } = useClipboard()
const keySize = ref(2048)
const privateKey = ref('')
const publicKey = ref('')
const loading = ref(false)

function generate() {
  loading.value = true
  // setTimeout to allow UI to update before heavy computation
  setTimeout(() => {
    try {
      const crypt = new JSEncrypt({ default_key_size: keySize.value })
      crypt.getKey(() => {
        privateKey.value = crypt.getPrivateKey()
        publicKey.value = crypt.getPublicKey()
        loading.value = false
      })
    } catch (e) {
      ElMessage.error('生成失败: ' + e.message)
      loading.value = false
    }
  }, 100)
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
