<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/admin/AdminLoginPage.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  依赖：vue、vue-router、element-plus、@/api/auth、@/store/auth
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="min-h-screen bg-slate-950 text-white flex items-center justify-center px-6 py-10">
    <div class="w-full max-w-5xl grid grid-cols-1 lg:grid-cols-2 rounded-[32px] overflow-hidden border border-slate-800 bg-slate-900/60">
      <div class="p-10 flex flex-col justify-between gap-10 bg-gradient-to-br from-indigo-600 via-violet-600 to-fuchsia-600">
        <div>
          <div class="text-sm uppercase tracking-widest text-white/70">Smart Tools</div>
          <div class="text-3xl font-semibold mt-4">现代化后台管理</div>
          <div class="text-sm text-white/80 mt-3">安全、清晰、可持续扩展的管理体验</div>
        </div>
        <div class="space-y-3 text-sm text-white/80">
          <div class="flex items-center gap-3">
            <span class="h-2 w-2 rounded-full bg-white"></span>
            快速定位核心指标与异常
          </div>
          <div class="flex items-center gap-3">
            <span class="h-2 w-2 rounded-full bg-white"></span>
            统一管理用户与工具内容
          </div>
          <div class="flex items-center gap-3">
            <span class="h-2 w-2 rounded-full bg-white"></span>
            审计日志覆盖关键操作
          </div>
        </div>
      </div>
      <div class="p-10 bg-white text-slate-900">
        <div class="text-2xl font-extrabold">管理员登录</div>
        <div class="mt-2 text-sm text-slate-500">请输入账号与密码进入管理后台</div>

        <div class="mt-8 space-y-4">
          <div class="space-y-1.5">
            <label class="text-xs font-semibold text-slate-500 uppercase tracking-wider">账号</label>
            <input v-model.trim="identifier" class="w-full rounded-xl bg-slate-50 px-4 py-3 text-sm ring-1 ring-slate-200 focus:ring-2 focus:ring-indigo-500/20 outline-none" placeholder="用户名 / 邮箱 / 手机号" />
          </div>
          <div class="space-y-1.5">
            <label class="text-xs font-semibold text-slate-500 uppercase tracking-wider">密码</label>
            <input v-model="password" type="password" class="w-full rounded-xl bg-slate-50 px-4 py-3 text-sm ring-1 ring-slate-200 focus:ring-2 focus:ring-indigo-500/20 outline-none" placeholder="请输入密码" />
          </div>
          <div class="grid grid-cols-2 gap-4 items-end">
            <div class="space-y-1.5">
              <label class="text-xs font-semibold text-slate-500 uppercase tracking-wider">图形验证码</label>
              <input v-model.trim="captchaCode" class="w-full rounded-xl bg-slate-50 px-4 py-3 text-sm ring-1 ring-slate-200 focus:ring-2 focus:ring-indigo-500/20 outline-none" placeholder="输入图形码" maxlength="8" />
            </div>
            <button class="h-[46px] w-full rounded-xl border border-slate-200 bg-white overflow-hidden flex items-center justify-center hover:border-slate-300 transition-colors" type="button" @click="refreshCaptcha">
              <img v-if="captchaImage" :src="captchaImage" class="h-full w-full object-contain" />
              <span v-else class="text-xs text-slate-400">点击加载</span>
            </button>
          </div>
          <button class="w-full rounded-xl bg-slate-900 py-3.5 text-sm font-bold text-white shadow-lg shadow-slate-900/20 hover:bg-slate-800 transition-all disabled:opacity-70" :disabled="loading" @click="submit">
            {{ loading ? '登录中...' : '立即登录' }}
          </button>
          <div class="flex items-center justify-between text-xs text-slate-500">
            <button class="hover:text-slate-700" type="button" @click="goHome">返回首页</button>
            <button class="hover:text-slate-700" type="button" @click="goWechat">微信扫码登录</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const identifier = ref('')
const password = ref('')
const captchaCode = ref('')
const captchaId = ref('')
const captchaImage = ref('')
const loading = ref(false)

function goHome() {
  router.push('/')
}

function goWechat() {
  router.push('/wechat-login')
}

async function refreshCaptcha() {
  try {
    const c = await authApi.captcha()
    captchaId.value = c.captchaId
    captchaImage.value = c.image
  } catch {
    captchaId.value = ''
    captchaImage.value = ''
  }
}

async function submit() {
  if (!identifier.value || !password.value || !captchaId.value || !captchaCode.value) {
    ElMessage.warning('请完整填写登录信息')
    return
  }
  loading.value = true
  try {
    await auth.loginPassword(identifier.value, password.value, captchaId.value, captchaCode.value)
    if (!auth.isAdmin) {
      await auth.logout()
      ElMessage.error('当前账号没有后台权限')
      return
    }
    ElMessage.success('登录成功')
    const redirect = route.query?.redirect
    if (typeof redirect === 'string' && redirect) {
      router.replace(redirect)
    } else {
      router.replace('/admin')
    }
  } catch (e) {
    ElMessage.error(e?.message || '登录失败')
    refreshCaptcha()
    captchaCode.value = ''
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (auth.isAuthed) {
    if (auth.isAdmin) {
      router.replace('/admin')
      return
    }
    await auth.logout()
  }
  refreshCaptcha()
})
</script>
