<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/WechatLoginPage.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  依赖：vue、vue-router、element-plus、@/api/auth、@/store/auth
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  WechatLoginPage：独立的微信扫码登录页（不依赖弹窗）。
  - 页面加载时向后端请求二维码并开始轮询
  - 轮询成功后写入 token 并跳转回首页
-->
<template>
  <div class="min-h-screen bg-[#f8fafc]">
    <div class="mx-auto flex min-h-screen max-w-5xl items-center justify-center px-6 md:px-8 lg:px-10 py-10">
      <div class="w-full overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
        <div class="bg-gradient-to-r from-indigo-50 via-white to-violet-50 px-6 md:px-8 lg:px-10 py-6 text-center">
          <div class="text-2xl font-extrabold tracking-tight text-slate-900">微信扫码登录</div>
          <div class="mt-1 text-sm text-slate-500">打开微信扫一扫，扫码后本页会自动登录</div>
        </div>

        <div class="grid gap-8 px-6 md:px-8 lg:px-10 py-8 sm:grid-cols-2">
          <div class="flex flex-col items-center justify-center gap-4">
            <div class="flex h-[260px] w-[260px] items-center justify-center rounded-3xl border border-slate-200 bg-white shadow-sm">
            <img
              v-if="qrContent && qrContent.startsWith('http')"
              :src="qrContent"
              alt="wechat qr"
              class="h-[230px] w-[230px] rounded-2xl object-contain"
            />
            <div v-else class="text-xs text-slate-400">{{ qrContent || '加载中…' }}</div>
          </div>
          <div class="text-xs text-slate-400">状态：{{ status }}</div>
          <div class="flex gap-3">
            <button
              class="rounded-full border border-slate-200 px-4 py-2 text-xs text-slate-600 hover:bg-slate-50 transition-all"
              type="button"
              :disabled="loading"
              @click="refresh"
            >
              刷新二维码
            </button>
            <button
              class="rounded-full border border-slate-200 px-4 py-2 text-xs text-slate-600 hover:bg-slate-50 transition-all"
              type="button"
              @click="goHome"
            >
              返回首页
            </button>
          </div>
        </div>

          <div class="space-y-3 text-sm text-slate-700">
            <div class="font-semibold text-slate-900">使用说明</div>
            <div class="text-slate-500">
              1）确保测试号后台接口 URL 配置为：<span class="font-mono text-slate-700">{{ verifyUrl }}</span>
            </div>
            <div class="text-slate-500">2）确保后端已配置：WXLOGIN_APP_ID / WXLOGIN_APP_SECRET</div>
            <div class="text-slate-500">3）扫码后等待 1-2 秒，页面会自动跳转</div>
            <div class="mt-4 rounded-2xl bg-slate-50 p-4 text-xs text-slate-500 ring-1 ring-slate-200">
              提示：如果二维码一直加载不出来，通常是后端未配置 appId/appSecret 或者无法访问微信接口。
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const auth = useAuthStore()

const loading = ref(false)
const state = ref('')
const qrContent = ref('')
const status = ref('pending')
let pollTimer = null

const verifyUrl = computed(() => `${window.location.origin.replace(/:\\d+$/, ':9090')}/wxverify`)

function stopPoll() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

function startPoll() {
  stopPoll()
  if (!state.value) return
  pollTimer = setInterval(async () => {
    try {
      const res = await authApi.wechatPoll(state.value)
      status.value = res.status
      if (res.status === 'authed' && res.token) {
        await auth.applyToken(res.token, null)
        ElMessage.success('登录成功')
        stopPoll()
        router.replace('/')
      }
      if (res.status === 'need_register') {
        ElMessage.info('扫码成功，请绑定或注册账号')
        stopPoll()
        router.replace({ path: '/', query: { wechatRegisterState: state.value } })
      }
    } catch (e) {
      status.value = 'error'
    }
  }, 1500)
}

async function refresh() {
  loading.value = true
  try {
    const qr = await authApi.wechatQr()
    state.value = qr.state
    qrContent.value = qr.qrContent
    status.value = 'pending'
    startPoll()
  } catch (e) {
    ElMessage.error(e?.message || '获取二维码失败')
  } finally {
    loading.value = false
  }
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  void refresh()
})

onBeforeUnmount(() => {
  stopPoll()
})
</script>
