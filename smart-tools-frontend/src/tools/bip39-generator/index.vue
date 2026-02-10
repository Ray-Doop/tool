<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/bip39-generator/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、element-plus、bip39、buffer
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="space-y-6">
    <!-- Config & Generate -->
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <div class="flex flex-wrap gap-4 mb-4 items-end">
        <div class="w-48">
          <label class="block text-sm font-medium text-slate-700 mb-1">语言 / Language</label>
          <el-select v-model="language" @change="updateWordlist">
            <el-option label="English" value="english" />
            <el-option label="简体中文" value="chinese_simplified" />
            <el-option label="繁體中文" value="chinese_traditional" />
            <el-option label="日本語" value="japanese" />
            <el-option label="한국어" value="korean" />
            <el-option label="Français" value="french" />
            <el-option label="Italiano" value="italian" />
            <el-option label="Español" value="spanish" />
          </el-select>
        </div>
        <div class="w-48">
          <label class="block text-sm font-medium text-slate-700 mb-1">长度 / Length</label>
          <el-select v-model="strength">
            <el-option label="12 words (128 bits)" :value="128" />
            <el-option label="15 words (160 bits)" :value="160" />
            <el-option label="18 words (192 bits)" :value="192" />
            <el-option label="21 words (224 bits)" :value="224" />
            <el-option label="24 words (256 bits)" :value="256" />
          </el-select>
        </div>
        <div class="flex-1">
           <el-button type="primary" @click="generateNew" class="w-full sm:w-auto">生成随机助记词</el-button>
        </div>
      </div>
    </div>

    <!-- Mnemonic Editor -->
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <div class="mb-4">
        <label class="block text-sm font-medium text-slate-700 mb-1">助记词 / Mnemonic</label>
        <el-input
          v-model="mnemonic"
          type="textarea"
          :rows="4"
          placeholder="输入助记词或生成新的..."
          @input="validateAndCalc"
        />
        <div class="mt-2 flex justify-between items-center">
          <div class="text-sm">
            <span v-if="isValid" class="text-green-600 font-medium flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
              有效 ({{ mnemonicWords.length }} 词)
            </span>
            <span v-else-if="mnemonic" class="text-red-600 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
              无效的助记词 ({{ mnemonicWords.length }} 词)
            </span>
            <span v-else class="text-slate-400">请输入或生成助记词</span>
          </div>
          <el-button size="small" @click="copy(mnemonic)">复制助记词</el-button>
        </div>
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium text-slate-700 mb-1">Passphrase (可选 / Optional)</label>
        <el-input 
          v-model="passphrase" 
          type="password" 
          show-password 
          placeholder="可选的密码短语（Salt）" 
          @input="validateAndCalc" 
        />
        <p class="text-xs text-slate-500 mt-1">注意：不同的 Passphrase 会生成完全不同的 Seed。</p>
      </div>
    </div>

    <!-- Seed Output -->
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <div class="mb-2 flex justify-between items-center">
        <h3 class="text-lg font-medium text-slate-900">BIP39 Seed (Hex)</h3>
        <el-button size="small" @click="copy(seedHex)">复制 Seed</el-button>
      </div>
      <div class="font-mono text-sm break-all bg-slate-50 p-4 rounded border border-slate-200 text-slate-600 min-h-[4rem] flex items-center">
        {{ seedHex || '-' }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as bip39 from 'bip39'
import { Buffer } from 'buffer'

// Polyfill Buffer for browser environment
if (typeof window !== 'undefined' && !window.Buffer) {
  window.Buffer = Buffer
}

const language = ref('english')
const strength = ref(128)
const mnemonic = ref('')
const passphrase = ref('')
const seedHex = ref('')
const isValid = ref(false)

const mnemonicWords = computed(() => {
  return mnemonic.value.trim().split(/\s+/).filter(w => w)
})

function updateWordlist() {
  bip39.setDefaultWordlist(language.value)
}

function generateNew() {
  updateWordlist()
  // Generate mnemonic
  mnemonic.value = bip39.generateMnemonic(strength.value, undefined, bip39.wordlists[language.value])
  validateAndCalc()
}

function validateAndCalc() {
  if (!mnemonic.value.trim()) {
    isValid.value = false
    seedHex.value = ''
    return
  }
  
  // Validate against current language
  // Try to validate against all languages if current fails?
  // For now, strict validation against selected language to avoid confusion
  isValid.value = bip39.validateMnemonic(mnemonic.value, bip39.wordlists[language.value])
  
  if (isValid.value) {
    try {
      const seed = bip39.mnemonicToSeedSync(mnemonic.value, passphrase.value)
      seedHex.value = seed.toString('hex')
    } catch (e) {
      console.error(e)
      seedHex.value = 'Error generating seed'
    }
  } else {
    // Attempt auto-detect language if invalid in current?
    // Let's iterate all wordlists to find if valid in any
    let found = false
    for (const lang of Object.keys(bip39.wordlists)) {
      if (lang === language.value) continue
      if (bip39.validateMnemonic(mnemonic.value, bip39.wordlists[lang])) {
        // Found valid in another language
        // Should we switch language?
        // Maybe just mark valid and generate seed?
        // Let's prompt user or just switch
        // console.log('Found valid in', lang)
        // language.value = lang // This might trigger watcher loop if not careful
        // For now, keep it strict or allow generation if valid in any?
        // Let's stick to simple: user selects language.
        found = true
        break
      }
    }
    
    if (found) {
      // Allow generation if valid in *any* language, but warn?
      // Actually bip39.mnemonicToSeedSync doesn't care about language, it just needs the mnemonic string.
      // But validateMnemonic does.
      // Let's just generate seed if we found it valid in ANY language, but we need to know it's valid.
      // Update: Let's keep it simple. Only validate against selected language.
      seedHex.value = ''
    } else {
      seedHex.value = ''
    }
  }
}

function copy(text) {
  if (!text) return
  navigator.clipboard.writeText(text)
  ElMessage.success('已复制')
}

watch(language, () => {
  validateAndCalc()
})

watch(strength, () => {
  // Optional: Auto-regenerate if empty? No.
})

onMounted(() => {
  generateNew()
})
</script>
