<!--
  @generated-file-note
  文件：smart-tools-frontend/src/tools/code-generator/index.vue
  用途：前端工具组件（具体工具的 UI 与交互逻辑）
  归属：前端 tools
  依赖：vue、@/api/http、element-plus
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="space-y-6">
    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <h3 class="text-lg font-medium text-slate-900 mb-4">项目配置</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-1">Group ID</label>
          <el-input v-model="form.groupId" placeholder="com.example" />
        </div>
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-1">Artifact ID</label>
          <el-input v-model="form.artifactId" placeholder="demo" />
        </div>
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-1">Package Name</label>
          <el-input v-model="form.packageName" placeholder="com.example.demo" />
        </div>
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-1">Java Version</label>
          <el-select v-model="form.javaVersion" class="w-full">
            <el-option label="17" value="17" />
            <el-option label="21" value="21" />
          </el-select>
        </div>
      </div>

      <div class="mt-6">
        <label class="block text-sm font-medium text-slate-700 mb-2">依赖选择</label>
        <el-checkbox-group v-model="form.dependencies">
          <el-checkbox label="web">Spring Web</el-checkbox>
          <el-checkbox label="jpa">Spring Data JPA</el-checkbox>
          <el-checkbox label="mysql">MySQL Driver</el-checkbox>
          <el-checkbox label="lombok">Lombok</el-checkbox>
          <el-checkbox label="security">Spring Security</el-checkbox>
          <el-checkbox label="validation">Validation</el-checkbox>
        </el-checkbox-group>
      </div>
    </div>

    <div class="bg-white p-6 rounded-lg shadow-sm border border-slate-200">
      <h3 class="text-lg font-medium text-slate-900 mb-4">实体生成 (可选)</h3>
      <div class="mb-4">
        <label class="block text-sm font-medium text-slate-700 mb-1">实体名称 (Entity Name)</label>
        <el-input v-model="form.entityName" placeholder="User (Leave empty to skip CRUD)" />
      </div>
      
      <div v-if="form.entityName" class="space-y-4">
        <div v-for="(field, idx) in form.fields" :key="idx" class="flex gap-2 items-start">
          <el-input v-model="field.name" placeholder="fieldName" class="flex-1" />
          <el-select v-model="field.type" placeholder="Type" class="w-32">
            <el-option label="String" value="String" />
            <el-option label="Long" value="Long" />
            <el-option label="Integer" value="Integer" />
            <el-option label="Boolean" value="Boolean" />
            <el-option label="LocalDateTime" value="LocalDateTime" />
            <el-option label="BigDecimal" value="BigDecimal" />
          </el-select>
          <el-button type="danger" circle @click="removeField(idx)">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </el-button>
        </div>
        <el-button type="dashed" class="w-full" @click="addField">+ 添加字段</el-button>
      </div>
    </div>

    <div class="flex justify-end">
      <el-button type="primary" size="large" :loading="loading" @click="generate">
        生成并下载项目 (.zip)
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { http } from '@/api/http'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const form = reactive({
  groupId: 'com.example',
  artifactId: 'demo',
  packageName: 'com.example.demo',
  javaVersion: '17',
  dependencies: ['web', 'lombok'],
  entityName: '',
  fields: [
    { name: 'id', type: 'Long' },
    { name: 'name', type: 'String' }
  ]
})

// Auto update package name
watch(() => [form.groupId, form.artifactId], () => {
  form.packageName = `${form.groupId}.${form.artifactId}`.replace(/-/g, '_')
})

function addField() {
  form.fields.push({ name: '', type: 'String' })
}

function removeField(idx) {
  form.fields.splice(idx, 1)
}

async function generate() {
  loading.value = true
  try {
    const response = await http.post('/api/tools/codegen/generate', form, {
      responseType: 'blob'
    })
    
    // Download file
    const blob = new Blob([response.data], { type: 'application/zip' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${form.artifactId}.zip`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('生成成功')
  } catch (e) {
    ElMessage.error('生成失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>
