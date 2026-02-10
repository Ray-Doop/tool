<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/SettingsPage.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <AppLayout>
      <div class="mx-auto max-w-5xl px-6 md:px-8 lg:px-10 py-8">
        <div class="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
          <div class="bg-gradient-to-r from-indigo-50 via-white to-violet-50 px-6 md:px-8 lg:px-10 py-6 text-center">
            <h1 class="text-2xl md:text-3xl font-extrabold tracking-tight text-slate-900 sm:text-4xl">设置</h1>
            <p class="mt-1 text-sm md:text-base text-slate-500">管理您的账号安全与偏好设置</p>
          </div>

          <div class="px-6 md:px-8 lg:px-10 py-8">
            <div class="mb-8 flex justify-center">
              <nav class="inline-flex rounded-2xl bg-slate-100 p-1.5" aria-label="Tabs">
                <button
                  v-for="tab in tabs"
                  :key="tab.name"
                  @click="currentTab = tab.name"
                  :class="[
                    currentTab === tab.name
                      ? 'bg-white text-slate-900 shadow-md'
                      : 'text-slate-500 hover:text-slate-700',
                    'flex min-w-[110px] items-center justify-center rounded-xl px-6 py-2.5 text-sm font-medium transition-all duration-200 ease-out'
                  ]"
                >
                  {{ tab.label }}
                </button>
              </nav>
            </div>

        <div v-if="loading" class="flex h-64 items-center justify-center text-slate-400">
          <svg class="mr-3 h-6 w-6 animate-spin text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          加载中...
        </div>

        <div v-else-if="error" class="rounded-2xl border border-red-100 bg-red-50 p-4 text-sm text-red-600">
          {{ error }}
        </div>

        <div v-else class="space-y-6">
          <!-- General Settings -->
          <transition
            enter-active-class="transition duration-300 ease-out"
            enter-from-class="transform opacity-0 scale-95"
            enter-to-class="transform opacity-100 scale-100"
            leave-active-class="transition duration-200 ease-in"
            leave-from-class="transform opacity-100 scale-100"
            leave-to-class="transform opacity-0 scale-95"
            mode="out-in"
          >
            <div v-if="currentTab === 'general'" key="general" class="space-y-6">
              <div class="overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-900/5">
                <div class="border-b border-slate-100 px-6 py-5">
                  <h3 class="text-base font-semibold leading-6 text-slate-900">界面偏好</h3>
                  <p class="mt-1 text-sm text-slate-500">自定义您的浏览体验</p>
                </div>
                <div class="px-6 py-6 space-y-6">
                  <div class="flex items-center justify-between">
                    <div class="flex flex-col">
                      <span class="text-sm font-medium text-slate-900">深色模式</span>
                      <span class="text-sm text-slate-500">在界面中使用深色主题</span>
                    </div>
                    <button
                      @click="togglePreference('darkMode')"
                      :class="preferences.darkMode ? 'bg-indigo-600' : 'bg-slate-200'"
                      class="relative inline-flex h-7 w-12 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:ring-offset-2"
                    >
                      <span
                        :class="preferences.darkMode ? 'translate-x-5' : 'translate-x-0'"
                        class="pointer-events-none inline-block h-6 w-6 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out"
                      ></span>
                    </button>
                  </div>
                  <div class="flex items-center justify-between">
                    <div class="flex flex-col">
                      <span class="text-sm font-medium text-slate-900">紧凑模式</span>
                      <span class="text-sm text-slate-500">缩小列表间距，显示更多内容</span>
                    </div>
                    <button
                      @click="togglePreference('compactMode')"
                      :class="preferences.compactMode ? 'bg-indigo-600' : 'bg-slate-200'"
                      class="relative inline-flex h-7 w-12 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:ring-offset-2"
                    >
                      <span
                        :class="preferences.compactMode ? 'translate-x-5' : 'translate-x-0'"
                        class="pointer-events-none inline-block h-6 w-6 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out"
                      ></span>
                    </button>
                  </div>
                </div>
                <div class="bg-slate-50 px-6 py-4 flex justify-end">
                  <button
                    @click="savePreferences"
                    :disabled="saving"
                    class="inline-flex items-center justify-center rounded-xl bg-indigo-600 px-5 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                  >
                    {{ saving ? '保存中...' : '保存设置' }}
                  </button>
                </div>
              </div>
            </div>

            <!-- Security Settings -->
            <div v-else-if="currentTab === 'security'" key="security" class="space-y-6">
              <div class="overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-900/5">
                 <div class="border-b border-slate-100 px-6 py-5">
                  <h3 class="text-base font-semibold leading-6 text-slate-900">安全设置</h3>
                  <p class="mt-1 text-sm text-slate-500">修改密码与账号绑定</p>
                </div>
                
                <div class="px-6 py-6 space-y-8">
                  <!-- Password Change -->
                  <div>
                    <h4 class="text-sm font-medium text-slate-900 mb-4">修改密码</h4>
                    <form @submit.prevent="changePassword" class="space-y-4 max-w-lg">
                      <div>
                        <label class="block text-sm font-medium leading-6 text-slate-900">当前密码</label>
                        <div class="mt-2">
                          <input
                            v-model="passwordForm.oldPassword"
                            type="password"
                            required
                          class="block w-full rounded-lg border-0 py-2 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 transition-all"
                          />
                        </div>
                      </div>
                      <div>
                        <label class="block text-sm font-medium leading-6 text-slate-900">新密码</label>
                        <div class="mt-2">
                          <input
                            v-model="passwordForm.newPassword"
                            type="password"
                            required
                            minlength="8"
                          class="block w-full rounded-lg border-0 py-2 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 transition-all"
                          />
                          <p class="mt-1 text-xs text-slate-500">密码长度至少 8 位</p>
                        </div>
                      </div>
                      <div>
                        <label class="block text-sm font-medium leading-6 text-slate-900">确认新密码</label>
                        <div class="mt-2">
                          <input
                            v-model="passwordForm.confirmPassword"
                            type="password"
                            required
                            minlength="8"
                          class="block w-full rounded-lg border-0 py-2 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 transition-all"
                          />
                        </div>
                      </div>
                      
                      <div v-if="passwordMessage" :class="{'text-green-600': passwordSuccess, 'text-red-600': !passwordSuccess}" class="text-sm font-medium">
                        {{ passwordMessage }}
                      </div>

                      <div class="pt-2">
                        <button
                          type="submit"
                          :disabled="changingPassword"
                          class="inline-flex items-center justify-center rounded-xl bg-indigo-600 px-5 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                        >
                          {{ changingPassword ? '提交中...' : '修改密码' }}
                        </button>
                      </div>
                    </form>
                  </div>
                  
                  <div class="relative">
                    <div class="absolute inset-0 flex items-center" aria-hidden="true">
                      <div class="w-full border-t border-slate-200"></div>
                    </div>
                    <div class="relative flex justify-start">
                      <span class="bg-white pr-3 text-sm font-medium text-slate-500">第三方账号</span>
                    </div>
                  </div>

                  <!-- Bindings -->
                  <div>
                     <div class="flex items-center justify-between p-4 rounded-xl border border-slate-200 bg-slate-50/50">
                        <div class="flex items-center space-x-4">
                          <div class="flex h-12 w-12 items-center justify-center rounded-full bg-white shadow-sm text-green-600 ring-1 ring-slate-900/5">
                            <svg class="h-7 w-7" fill="currentColor" viewBox="0 0 24 24">
                              <path d="M8.69,14.61C3.39,12.56,2.26,9.6,2.26,9.6l0-0.03c0,0-0.34-0.89-0.08-1.58C2.39,7.4,2.94,7.24,3.37,7.36 c0.77,0.22,4.86,1.45,4.86,1.45L8.69,14.61z M17.65,11.39c-1.25,2.78-4.48,4.72-8.31,4.72c-0.12,0-0.24,0-0.35,0l-0.3-6.52 l-3.96-1.15c0,0-0.21-0.64-0.12-1.29c0.1-0.69,0.59-1.2,0.59-1.2l0.04-0.03c0,0,2.15-0.74,5.92-0.74c3.81,0,7.31,0.78,7.31,0.78 s0.64,0.44,0.72,1.15C19.26,7.74,19.04,8.38,17.65,11.39z M15.31,14.61l0.46-5.8l4.86-1.45c0.43-0.13,0.98,0.04,1.19,0.63 c0.26,0.69-0.08,1.58-0.08,1.58l0,0.03C21.74,9.6,20.61,12.56,15.31,14.61z"/>
                            </svg>
                          </div>
                          <div>
                            <div class="text-sm font-medium text-slate-900">微信</div>
                            <div class="text-xs text-slate-500">绑定后可使用微信快捷登录</div>
                          </div>
                        </div>
                        <span
                          class="inline-flex items-center rounded-md px-2 py-1 text-xs font-medium ring-1 ring-inset"
                          :class="settings?.wechatBound ? 'bg-green-50 text-green-700 ring-green-600/20' : 'bg-slate-50 text-slate-600 ring-slate-500/10'"
                        >
                          {{ settings?.wechatBound ? '已绑定' : '未绑定' }}
                        </span>
                      </div>
                      
                      <div class="mt-2 text-right">
                         <button
                          v-if="!settings?.wechatBound"
                          @click="handleBindWechat"
                          class="text-sm font-medium text-indigo-600 hover:text-indigo-500 transition-colors"
                        >
                          立即绑定 &rarr;
                        </button>
                        <button
                          v-else
                          @click="handleUnbindWechat"
                          class="text-sm font-medium text-red-600 hover:text-red-500 transition-colors"
                        >
                          解除绑定
                        </button>
                      </div>
                  </div>
                  
                  <!-- Login History -->
                  <div>
                    <h4 class="text-sm font-medium text-slate-900 mb-4">登录历史</h4>
                    <div class="rounded-xl border border-slate-200 bg-white overflow-hidden">
                      <div v-if="loginHistory.length === 0" class="p-6 text-center text-sm text-slate-500">
                        暂无记录
                      </div>
                      <div v-else>
                        <div
                          v-for="(log, idx) in loginHistory"
                          :key="idx"
                          class="flex items-center justify-between border-b border-slate-100 px-4 py-3 last:border-0"
                        >
                          <div class="flex flex-col">
                            <span class="text-sm font-medium text-slate-900">{{ log.ip }}</span>
                            <span class="text-xs text-slate-500">{{ log.userAgent }}</span>
                          </div>
                          <span class="text-xs text-slate-400">{{ formatTime(log.loginAt) }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Privacy & Data Settings -->
            <div v-else-if="currentTab === 'privacy'" key="privacy" class="space-y-6">
              <div class="overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-900/5">
                <div class="border-b border-slate-100 px-6 py-5">
                  <h3 class="text-base font-semibold leading-6 text-slate-900">隐私与数据</h3>
                  <p class="mt-1 text-sm text-slate-500">管理您的数据导出与账号删除</p>
                </div>
                <div class="px-6 py-6 space-y-8">
                  <!-- Export Data -->
                  <div>
                    <h4 class="text-sm font-medium text-slate-900 mb-4">导出数据</h4>
                    <div class="rounded-2xl border border-slate-200 bg-slate-50 p-4 flex items-center justify-between">
                      <div>
                        <div class="text-sm font-medium text-slate-900">下载个人数据副本</div>
                        <div class="text-xs text-slate-500 mt-1">包含您的个人资料、设置、收藏夹及访问记录</div>
                      </div>
                      <button
                        @click="handleExportData"
                        class="px-4 py-2 text-sm font-medium text-indigo-700 bg-white border border-indigo-200 rounded-xl hover:bg-indigo-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all"
                      >
                        导出 JSON
                      </button>
                    </div>
                  </div>

                  <!-- Danger Zone -->
                  <div>
                    <h4 class="text-sm font-medium text-red-600 mb-4">危险区域</h4>
                    <div class="rounded-2xl border border-red-200 bg-red-50 p-4 flex items-center justify-between">
                      <div>
                        <div class="text-sm font-medium text-red-900">注销账号</div>
                        <div class="text-xs text-red-700 mt-1">此操作不可恢复，您的所有数据将被永久删除</div>
                      </div>
                      <button
                        @click="handleDeleteAccount"
                        class="px-4 py-2 text-sm font-medium text-red-700 bg-white border border-red-300 rounded-xl hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-all"
                      >
                        注销账号
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </transition>
        </div>
      </div>
    </div>
    </div>
  </AppLayout>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'
import { meApi } from '@/api/me'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(true)
const saving = ref(false)
const changingPassword = ref(false)
const error = ref('')
const settings = ref(null)
const preferences = ref({})
const currentTab = ref('general')
const loginHistory = ref([])

const tabs = [
  { name: 'general', label: '常规设置' },
  { name: 'notifications', label: '通知设置' },
  { name: 'security', label: '安全设置' },
  { name: 'privacy', label: '隐私与数据' }
]

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordMessage = ref('')
const passwordSuccess = ref(false)

function formatTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

function togglePreference(key) {
  preferences.value[key] = !preferences.value[key]
}

// eslint-disable-next-line no-unused-vars
function handleBindWechat() {
  alert('请关注我们的微信公众号，并发送“绑定+您的用户名”进行绑定（模拟功能）')
}

// eslint-disable-next-line no-unused-vars
async function handleUnbindWechat() {
  if (!confirm('确定要解除微信绑定吗？')) return
  
  // 这里暂时模拟解除绑定，实际应该调用 API
  // await meApi.unbindWechat() 
  // 为了演示，我们更新本地状态
  if (settings.value) {
    settings.value.wechatBound = false
    alert('解除绑定成功（模拟）')
  }
}

async function savePreferences() {
  saving.value = true
  try {
    const updated = await meApi.updateSettings({
      preferences: preferences.value
    })
    settings.value = updated
    preferences.value = updated.preferences || {}
    alert('设置已保存')
  } catch (e) {
    alert(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function changePassword() {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordMessage.value = '两次输入的密码不一致'
    passwordSuccess.value = false
    return
  }
  
  changingPassword.value = true
  passwordMessage.value = ''
  
  try {
    await meApi.changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    passwordMessage.value = '密码修改成功'
    passwordSuccess.value = true
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (e) {
    passwordMessage.value = e.message || '修改失败'
    passwordSuccess.value = false
  } finally {
    changingPassword.value = false
  }
}

async function handleDeleteAccount() {
  if (!confirm('确定要注销账号吗？此操作无法撤销！')) return
  
  const userInput = prompt('请输入您的用户名以确认注销：')
  if (userInput !== authStore.username) {
    alert('用户名输入错误，操作取消')
    return
  }

  try {
    await meApi.deleteAccount()
    authStore.logout()
    alert('账号已注销')
    router.push('/')
  } catch (e) {
    alert(e.message || '注销失败')
  }
}

async function handleExportData() {
  try {
    const data = await meApi.exportData()
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `smart-tools-export-${new Date().toISOString().split('T')[0]}.json`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  } catch (e) {
    alert(e.message || '导出失败')
  }
}

onMounted(async () => {
  loading.value = true
  error.value = ''
  try {
    const [s, h] = await Promise.all([
      meApi.settings(),
      meApi.loginHistory()
    ])
    settings.value = s
    loginHistory.value = h
    preferences.value = settings.value.preferences || {}
  } catch (e) {
    error.value = e?.message || '加载失败'
  } finally {
    loading.value = false
  }
})
</script>
