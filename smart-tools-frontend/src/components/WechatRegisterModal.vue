<!--
  @generated-file-note
  文件：smart-tools-frontend/src/components/WechatRegisterModal.vue
  用途：前端通用组件（可复用 UI 组件）
  归属：前端 components
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <Teleport to="body">
    <div v-if="modelValue" class="fixed inset-0 z-[4000] flex items-center justify-center p-4 sm:p-6" role="dialog">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="close"></div>
      <div class="relative w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-8 text-left shadow-2xl transition-all animate-fade-in-up">
      <!-- Close Button -->
      <button 
        class="absolute right-4 top-4 rounded-full p-2 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition-colors"
        type="button"
        @click.prevent="close"
      >
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="h-5 w-5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>

      <!-- Header -->
      <div class="mb-8 text-center">
        <div class="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-indigo-50 text-indigo-600 mb-4 shadow-sm ring-4 ring-indigo-50/50">
           <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-8 h-8">
             <path stroke-linecap="round" stroke-linejoin="round" d="M19 7.5v3m0 0v3m0-3h3m-3 0h-3m-2.25-4.125a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zM4 19.235v-.11a6.375 6.375 0 0112.75 0v.109A12.318 12.318 0 0110.374 21c-2.331 0-4.512-.645-6.374-1.766z" />
           </svg>
        </div>
        <h2 class="text-2xl font-bold text-slate-900 tracking-tight">{{ headerTitle }}</h2>
        <p class="mt-2 text-sm text-slate-500">{{ headerSubtitle }}</p>
      </div>

      <!-- Form -->
      <div class="space-y-5">
          <div v-if="!isEmailComplete" class="flex justify-center gap-3">
            <button
              type="button"
              class="rounded-full px-4 py-1.5 text-xs font-semibold transition-colors"
              :class="bindMode === 'register' ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
              @click="bindMode = 'register'"
            >
              注册新账号
            </button>
            <button
              type="button"
              class="rounded-full px-4 py-1.5 text-xs font-semibold transition-colors"
              :class="bindMode === 'bind' ? 'bg-indigo-600 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
              @click="bindMode = 'bind'"
            >
              绑定已有账号
            </button>
          </div>
          <div
            v-if="noticeMessage"
            class="rounded-xl px-4 py-3 text-sm font-medium"
            :class="noticeType === 'success' ? 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-200' : 'bg-rose-50 text-rose-700 ring-1 ring-rose-200'"
          >
            {{ noticeMessage }}
          </div>

          <!-- Email Input -->
          <input
            v-if="!isBindMode"
            v-model.trim="email"
            :class="[
              baseInputClass,
              fieldErrors.email ? errorInputClass : okInputClass,
              isEmailComplete ? 'opacity-70 cursor-not-allowed' : ''
            ]"
            placeholder="请输入您的邮箱"
            :disabled="isEmailComplete"
          />

          <!-- Email OTP -->
          <div v-if="!isEmailComplete && !isBindMode" class="relative">
             <input
               v-model.trim="otpCode"
               :class="[baseInputClass, fieldErrors.otpCode ? errorInputClass : okInputClass]"
               placeholder="邮箱验证码"
             />
             <button
               type="button"
               class="absolute right-2 top-1.5 bottom-1.5 px-3 rounded-lg text-xs font-semibold transition-colors"
               :class="sendingOtp || otpCountdown > 0 ? 'bg-slate-100 text-slate-400 cursor-not-allowed' : 'bg-indigo-50 text-indigo-600 hover:bg-indigo-100'"
               :disabled="sendingOtp || otpCountdown > 0"
               @click.prevent="openSliderVerify"
             >
               {{ otpCountdown > 0 ? `${otpCountdown}s` : '获取验证码' }}
             </button>
          </div>

          <!-- Username -->
          <input
            v-if="!isBindMode"
            v-model.trim="username"
            :class="[baseInputClass, fieldErrors.username ? errorInputClass : okInputClass]"
            placeholder="请设置用户名 (3-32位)"
          />

          <input
            v-if="isBindMode"
            v-model.trim="identifier"
            :class="[baseInputClass, fieldErrors.identifier ? errorInputClass : okInputClass]"
            placeholder="邮箱/手机号/用户名"
          />

          <!-- Password -->
          <input
            v-model="password"
            type="password"
            :class="[baseInputClass, fieldErrors.password ? errorInputClass : okInputClass]"
            :placeholder="isBindMode ? '账号密码' : '请设置登录密码 (6位以上)'"
          />

          <!-- Confirm Password -->
          <input
            v-if="!isBindMode"
            v-model="confirmPassword"
            type="password"
            :class="[baseInputClass, fieldErrors.confirmPassword ? errorInputClass : okInputClass]"
            placeholder="确认密码"
          />

          <!-- Captcha -->
          <div v-if="!isEmailComplete" class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <input
                v-model.trim="captchaCode"
                :class="[baseInputClass, fieldErrors.captchaCode ? errorInputClass : okInputClass]"
                placeholder="图形验证码"
                maxlength="8"
              />
            </div>
            <div class="flex items-end">
               <div 
                 class="h-[46px] w-full rounded-xl border bg-white overflow-hidden cursor-pointer flex items-center justify-center transition-colors"
                 :class="fieldErrors.captchaCode ? 'border-rose-300 hover:border-rose-400' : 'border-slate-200 hover:border-slate-300'"
                 @click="refreshCaptcha"
               >
                 <img v-if="captchaImage" :src="captchaImage" class="h-full w-full object-contain" />
                 <span v-else class="text-xs text-slate-400">点击加载</span>
               </div>
            </div>
          </div>

          <!-- Submit Button -->
          <button
            class="w-full mt-4 rounded-xl bg-indigo-600 py-3.5 text-sm font-bold text-white shadow-lg shadow-indigo-500/20 hover:bg-indigo-700 hover:-translate-y-0.5 transition-all disabled:opacity-70 disabled:cursor-not-allowed"
            type="button"
            :disabled="loading"
            @click.prevent="doRegister"
          >
            {{ loading ? '提交中...' : (isBindMode ? '绑定并登录' : '完成注册并登录') }}
          </button>
      </div>
      </div>
    </div>
  </Teleport>

  <Teleport to="body">
    <div v-if="sliderVisible" class="fixed inset-0 z-[5000] flex items-center justify-center p-4 sm:p-6" role="dialog">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="closeSlider"></div>
      <div class="relative w-full max-w-sm overflow-hidden rounded-2xl bg-white p-6 text-left shadow-2xl transition-all animate-fade-in-up">
        <div class="flex items-center justify-between">
          <div class="text-sm font-bold text-slate-900">滑动验证</div>
          <div class="flex items-center gap-1">
            <button class="rounded-full p-2 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition-colors" @click="reloadSlider">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="h-4 w-4">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0l3.181 3.183a8.25 8.25 0 0013.803-3.7M4.031 9.865a8.25 8.25 0 0113.803-3.7l3.181 3.182m0-4.991v4.99" />
              </svg>
            </button>
            <button type="button" class="rounded-full p-2 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition-colors" @click.prevent="closeSlider">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="h-4 w-4">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <div class="mt-4">
          <div class="relative w-full overflow-hidden rounded-xl border border-slate-200 bg-slate-50">
              <div ref="sliderPuzzleRef" class="relative w-full" :style="{ height: `${sliderPuzzleHeight}px` }">
                <img v-if="sliderPuzzleBgUrl" :src="sliderPuzzleBgUrl" class="absolute inset-0 h-full w-full object-cover" />
                <img
                  v-if="sliderPuzzlePieceUrl"
                  :src="sliderPuzzlePieceUrl"
                  class="absolute inset-0 h-full w-full pointer-events-none"
                :style="{ transform: `translateX(${sliderPuzzleCurrentX}px)` }"
              />
              <div v-if="!sliderPuzzleBgUrl" class="absolute inset-0 flex items-center justify-center text-xs text-slate-400">加载中...</div>
            </div>
          </div>

          <div class="mt-4">
            <div class="mb-2 text-xs text-slate-500">{{ sliderVerified ? '验证通过' : '拖动下方滑块完成拼图' }}</div>
            <div ref="sliderTrackRef" class="relative h-11 w-full rounded-xl bg-slate-100 overflow-hidden select-none">
              <div class="absolute inset-y-0 left-0 bg-indigo-500/20" :style="{ width: `${sliderProgress}%` }"></div>
              <div class="absolute inset-0 flex items-center justify-center text-xs font-semibold" :class="sliderVerified ? 'text-green-700' : 'text-slate-500'">
                {{ sliderVerified ? '验证通过' : '按住滑块拖动' }}
              </div>
              <div
                class="absolute top-1 bottom-1 w-12 rounded-lg bg-white shadow ring-1 ring-slate-200 flex items-center justify-center cursor-grab active:cursor-grabbing"
                :style="{ left: `${sliderX}px` }"
                @pointerdown="onSliderDown"
              >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="h-4 w-4 text-slate-400">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M13.5 4.5L21 12l-7.5 7.5M3 12h18" />
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { computed, nextTick, ref, watch } from 'vue'
import { useAuthStore } from '@/store/auth'
import { authApi } from '@/api/auth'

const props = defineProps({
  modelValue: Boolean,
  wechatState: String,
  mode: { type: String, default: 'wechat' },
  email: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue'])

const authStore = useAuthStore()

const isEmailComplete = computed(() => props.mode === 'email')
const bindMode = ref('register')
const isBindMode = computed(() => !isEmailComplete.value && bindMode.value === 'bind')
const headerTitle = computed(() => {
  if (isEmailComplete.value) return '完善注册信息'
  return isBindMode.value ? '绑定已有账号' : '补全账号信息'
})
const headerSubtitle = computed(() => {
  if (isEmailComplete.value) return '请设置用户名和登录密码'
  return isBindMode.value ? '使用已注册账号完成微信绑定' : '为了保障您的账号安全，首次微信登录请绑定邮箱'
})

const email = ref('')
const identifier = ref('')
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const otpCode = ref('')
const captchaCode = ref('')
const captchaId = ref('')
const captchaImage = ref('')

const noticeType = ref('error')
const noticeMessage = ref('')

const fieldErrors = ref({
  email: '',
  identifier: '',
  otpCode: '',
  username: '',
  password: '',
  confirmPassword: '',
  captchaCode: ''
})

const baseInputClass =
  'w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 placeholder:text-slate-400 ring-1 outline-none focus:ring-2 focus:bg-white transition-all'
const okInputClass = 'ring-slate-200 focus:ring-indigo-500/20'
const errorInputClass = 'ring-rose-300 focus:ring-rose-500/20'

const loading = ref(false)
const sendingOtp = ref(false)
const otpCountdown = ref(0)
let otpTimer = null

const sliderVisible = ref(false)
const sliderX = ref(0)
const sliderMaxX = ref(0)
const sliderVerified = ref(false)
const sliderTrackRef = ref(null)
const sliderPuzzleRef = ref(null)
let sliderDragging = false
let sliderStartX = 0

const sliderPuzzleBgUrl = ref('')
const sliderPuzzlePieceUrl = ref('')
const sliderPuzzleTargetX = ref(0)
const sliderPuzzleMaxX = ref(0)
const sliderPuzzleHeight = 155
const sliderPuzzleTolerancePx = 8

const sliderProgress = computed(() => {
  if (!sliderMaxX.value) return 0
  return Math.max(0, Math.min(100, (sliderX.value / sliderMaxX.value) * 100))
})

const sliderPuzzleCurrentX = computed(() => {
  if (!sliderMaxX.value || !sliderPuzzleMaxX.value) return 0
  const ratio = Math.max(0, Math.min(1, sliderX.value / sliderMaxX.value))
  return Math.round(ratio * sliderPuzzleMaxX.value)
})

// Captcha Logic
async function refreshCaptcha() {
  if (isEmailComplete.value) return
  try {
    const res = await authApi.captcha()
    captchaId.value = res.captchaId
    captchaImage.value = res.image
    captchaCode.value = ''
  } catch (e) {
    console.error('Failed to load captcha', e)
  }
}

function setNotice(type, message) {
  noticeType.value = type
  noticeMessage.value = message || ''
}

function clearErrors() {
  fieldErrors.value = {
    email: '',
    identifier: '',
    otpCode: '',
    username: '',
    password: '',
    confirmPassword: '',
    captchaCode: ''
  }
}

// Watch visibility to refresh captcha
watch(() => props.modelValue, (val) => {
  if (val) {
    setNotice('error', '')
    clearErrors()
    bindMode.value = 'register'
    identifier.value = ''
    username.value = ''
    password.value = ''
    confirmPassword.value = ''
    otpCode.value = ''
    captchaCode.value = ''
    if (isEmailComplete.value) {
      email.value = props.email || ''
    } else {
      email.value = ''
      refreshCaptcha()
    }
  }
})

watch(() => props.email, (val) => {
  if (isEmailComplete.value) {
    email.value = val || ''
  }
})

watch(email, () => {
  otpCode.value = ''
  otpCountdown.value = 0
  stopOtpCountdown()
  fieldErrors.value.email = ''
  fieldErrors.value.otpCode = ''
})

watch(identifier, () => {
  fieldErrors.value.identifier = ''
})

watch(bindMode, () => {
  setNotice('error', '')
  clearErrors()
  captchaCode.value = ''
  if (!isEmailComplete.value) refreshCaptcha()
})

function close() {
  emit('update:modelValue', false)
}

function stopOtpCountdown() {
  if (otpTimer) {
    clearInterval(otpTimer)
    otpTimer = null
  }
}

function startOtpCountdown(seconds) {
  stopOtpCountdown()
  otpCountdown.value = Number.isFinite(seconds) ? Math.max(0, Math.floor(seconds)) : 0
  if (otpCountdown.value <= 0) return
  otpTimer = setInterval(() => {
    if (otpCountdown.value <= 1) {
      otpCountdown.value = 0
      stopOtpCountdown()
    } else {
      otpCountdown.value--
    }
  }, 1000)
}

async function sendRegisterOtp() {
  if (isEmailComplete.value || isBindMode.value) return
  if (!email.value) {
    clearErrors()
    fieldErrors.value.email = '请填写邮箱'
    setNotice('error', '请先填写邮箱')
    return
  }

  sendingOtp.value = true
  try {
    clearErrors()
    await authStore.otpSend('email', email.value, null, null, 'register')
    setNotice('success', '验证码已发送')
  } catch (e) {
    otpCountdown.value = 0
    stopOtpCountdown()
    if (e?.code === 'EMAIL_ALREADY_REGISTERED') {
      fieldErrors.value.email = '邮箱已注册'
    }
    setNotice('error', e?.message || '发送失败')
  } finally {
    sendingOtp.value = false
  }
}

async function openSliderVerify() {
  if (sendingOtp.value || otpCountdown.value > 0) return
  if (isBindMode.value) return
  if (!email.value) {
    clearErrors()
    fieldErrors.value.email = '请填写邮箱'
    setNotice('error', '请先填写邮箱')
    return
  }
  sliderVisible.value = true
  await nextTick()
  updateSliderMax()
  await reloadSlider()
}

function closeSlider() {
  sliderVisible.value = false
  sliderDragging = false
  sliderVerified.value = false
  sliderX.value = 0
  window.removeEventListener('pointermove', onSliderMove)
  window.removeEventListener('pointerup', onSliderUp)
}

async function reloadSlider() {
  sliderVerified.value = false
  sliderX.value = 0
  updateSliderMax()
  const w = Math.round(sliderPuzzleRef.value?.getBoundingClientRect?.().width || 310)
  const seed = Date.now() % 2147483647
  const bg = await createPuzzleBackgroundDataUrl(w, sliderPuzzleHeight, seed)
  const puzzle = await buildPuzzle(bg, w, sliderPuzzleHeight)
  sliderPuzzleBgUrl.value = puzzle.bgUrl
  sliderPuzzlePieceUrl.value = puzzle.pieceUrl
  sliderPuzzleTargetX.value = puzzle.targetX
  sliderPuzzleMaxX.value = puzzle.maxX
}

function onSliderDown(e) {
  if (sliderVerified.value) return
  updateSliderMax()
  sliderDragging = true
  sliderStartX = e.clientX
  sliderStartKnobX = sliderX.value
  window.addEventListener('pointermove', onSliderMove)
  window.addEventListener('pointerup', onSliderUp)
}

let sliderStartKnobX = 0

function onSliderMove(e) {
  if (!sliderDragging || !sliderTrackRef.value) return
  const rect = sliderTrackRef.value.getBoundingClientRect()
  const deltaPx = e.clientX - sliderStartX
  const knobWidth = 48
  const maxX = Math.max(0, rect.width - knobWidth - 8)
  sliderMaxX.value = maxX
  sliderX.value = Math.max(0, Math.min(maxX, sliderStartKnobX + deltaPx))
}

function onSliderUp() {
  sliderDragging = false
  window.removeEventListener('pointermove', onSliderMove)
  window.removeEventListener('pointerup', onSliderUp)
  if (sliderVerified.value) return
  const pass = Math.abs(sliderPuzzleCurrentX.value - sliderPuzzleTargetX.value) <= sliderPuzzleTolerancePx
  if (!pass) {
    sliderX.value = 0
    return
  }
  sliderVerified.value = true
  startOtpCountdown(60)
  setTimeout(async () => {
    closeSlider()
    await sendRegisterOtp()
  }, 160)
}

async function doRegister() {
  clearErrors()
  setNotice('error', '')
  if (isBindMode.value) {
    if (!identifier.value) fieldErrors.value.identifier = '必填'
    if (!password.value) fieldErrors.value.password = '必填'
    if (!captchaCode.value) fieldErrors.value.captchaCode = '必填'
    if (fieldErrors.value.identifier || fieldErrors.value.password || fieldErrors.value.captchaCode) {
      setNotice('error', '请填写所有必填项')
      return
    }
  } else if (isEmailComplete.value) {
    if (!username.value) fieldErrors.value.username = '必填'
    if (!password.value) fieldErrors.value.password = '必填'
    if (!confirmPassword.value) fieldErrors.value.confirmPassword = '必填'
    if (fieldErrors.value.username || fieldErrors.value.password || fieldErrors.value.confirmPassword) {
      setNotice('error', '请填写所有必填项')
      return
    }
  } else {
    if (!email.value) fieldErrors.value.email = '必填'
    if (!otpCode.value) fieldErrors.value.otpCode = '必填'
    if (!username.value) fieldErrors.value.username = '必填'
    if (!password.value) fieldErrors.value.password = '必填'
    if (!confirmPassword.value) fieldErrors.value.confirmPassword = '必填'
    if (!captchaCode.value) fieldErrors.value.captchaCode = '必填'
    if (
      fieldErrors.value.email ||
      fieldErrors.value.otpCode ||
      fieldErrors.value.username ||
      fieldErrors.value.password ||
      fieldErrors.value.confirmPassword ||
      fieldErrors.value.captchaCode
    ) {
      setNotice('error', '请填写所有必填项')
      return
    }
  }
  if (!isBindMode.value) {
    if (password.value.length < 6) {
      fieldErrors.value.password = '密码至少 6 位'
      setNotice('error', '密码至少 6 位')
      return
    }
    if (password.value !== confirmPassword.value) {
      fieldErrors.value.confirmPassword = '两次密码不一致'
      setNotice('error', '两次密码不一致')
      return
    }
  }
  
  loading.value = true
  try {
    if (isBindMode.value) {
      await authStore.wechatBind(props.wechatState, identifier.value, password.value, captchaId.value, captchaCode.value)
      setNotice('success', '绑定成功，欢迎回来！')
      close()
    } else if (isEmailComplete.value) {
      await authStore.completeRegistration(username.value, password.value, confirmPassword.value)
      setNotice('success', '注册信息已完善')
      close()
    } else {
      await authStore.wechatRegister(
        props.wechatState,
        'email',
        email.value,
        null,
        username.value,
        password.value,
        captchaId.value,
        captchaCode.value,
        otpCode.value
      )
      setNotice('success', '注册成功，欢迎回来！')
      close()
    }
  } catch (e) {
    if (isBindMode.value) {
      if (e?.code === 'INVALID_CREDENTIALS') {
        fieldErrors.value.identifier = '账号或密码错误'
        fieldErrors.value.password = '账号或密码错误'
      }
      if (e?.code === 'CAPTCHA_INVALID') fieldErrors.value.captchaCode = '验证码错误'
      setNotice('error', e?.message || '绑定失败')
      captchaCode.value = ''
      await refreshCaptcha()
    } else if (isEmailComplete.value) {
      if (e?.code === 'PASSWORD_MISMATCH') fieldErrors.value.confirmPassword = '两次密码不一致'
      if (e?.code === 'USERNAME_ALREADY_EXISTS') fieldErrors.value.username = '用户名已存在'
      if (e?.code === 'USERNAME_REQUIRED') fieldErrors.value.username = '必填'
      setNotice('error', e?.message || '注册失败')
    } else {
      if (e?.code === 'OTP_INVALID') fieldErrors.value.otpCode = '验证码错误或已过期'
      if (e?.code === 'CAPTCHA_INVALID') fieldErrors.value.captchaCode = '验证码错误'
      if (e?.code === 'PASSWORD_MISMATCH') fieldErrors.value.confirmPassword = '两次密码不一致'
      if (e?.code === 'EMAIL_ALREADY_REGISTERED') fieldErrors.value.email = '邮箱已注册'
      setNotice('error', e?.message || '注册失败')
      captchaCode.value = ''
      await refreshCaptcha()
    }
  } finally {
    loading.value = false
  }
}

function updateSliderMax() {
  if (!sliderTrackRef.value) return
  const rect = sliderTrackRef.value.getBoundingClientRect()
  const knobWidth = 48
  sliderMaxX.value = Math.max(0, rect.width - knobWidth - 8)
}

function seededRandom(seed) {
  let t = seed % 2147483647
  if (t <= 0) t += 2147483646
  return () => (t = (t * 16807) % 2147483647) / 2147483647
}

async function createPuzzleBackgroundDataUrl(w, h, seed) {
  const rand = seededRandom(seed)
  const canvas = document.createElement('canvas')
  canvas.width = w
  canvas.height = h
  const ctx = canvas.getContext('2d')
  const g = ctx.createLinearGradient(0, 0, w, h)
  const c1 = `hsl(${Math.floor(rand() * 360)}, 70%, 60%)`
  const c2 = `hsl(${Math.floor(rand() * 360)}, 70%, 45%)`
  g.addColorStop(0, c1)
  g.addColorStop(1, c2)
  ctx.fillStyle = g
  ctx.fillRect(0, 0, w, h)

  for (let i = 0; i < 18; i++) {
    const x = rand() * w
    const y = rand() * h
    const r = 12 + rand() * 46
    ctx.fillStyle = `rgba(255,255,255,${0.08 + rand() * 0.12})`
    ctx.beginPath()
    ctx.arc(x, y, r, 0, Math.PI * 2)
    ctx.fill()
  }

  for (let i = 0; i < 1600; i++) {
    const x = (rand() * w) | 0
    const y = (rand() * h) | 0
    ctx.fillStyle = `rgba(0,0,0,${rand() * 0.06})`
    ctx.fillRect(x, y, 1, 1)
  }

  return canvas.toDataURL('image/png')
}

function loadImage(src) {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => resolve(img)
    img.onerror = reject
    img.src = src
  })
}

function buildPuzzlePath(ctx, x, y, size, r) {
  ctx.beginPath()
  ctx.rect(x, y, size, size)
  ctx.arc(x + size, y + size / 2, r, -Math.PI / 2, Math.PI / 2, false)
  ctx.closePath()
}

async function buildPuzzle(src, w, h) {
  const img = await loadImage(src)
  const pieceSize = 46
  const r = 8
  const padding = 10
  const maxTargetX = w - pieceSize - r - padding
  const minTargetX = Math.max(80, pieceSize + padding)
  const rand = seededRandom((Date.now() + w + h) % 2147483647)
  const targetX = Math.floor(minTargetX + rand() * Math.max(1, maxTargetX - minTargetX))
  const y = Math.floor(padding + rand() * Math.max(1, h - pieceSize - padding * 2))

  const bgCanvas = document.createElement('canvas')
  bgCanvas.width = w
  bgCanvas.height = h
  const bgCtx = bgCanvas.getContext('2d')
  bgCtx.drawImage(img, 0, 0, w, h)
  bgCtx.save()
  bgCtx.fillStyle = 'rgba(0,0,0,0.28)'
  buildPuzzlePath(bgCtx, targetX, y, pieceSize, r)
  bgCtx.fill()
  bgCtx.strokeStyle = 'rgba(255,255,255,0.75)'
  bgCtx.lineWidth = 2
  bgCtx.stroke()
  bgCtx.restore()

  const pieceCanvas = document.createElement('canvas')
  pieceCanvas.width = w
  pieceCanvas.height = h
  const pieceCtx = pieceCanvas.getContext('2d')
  pieceCtx.save()
  pieceCtx.shadowColor = 'rgba(0,0,0,0.25)'
  pieceCtx.shadowBlur = 10
  pieceCtx.shadowOffsetY = 4
  buildPuzzlePath(pieceCtx, 0, y, pieceSize, r)
  pieceCtx.clip()
  pieceCtx.drawImage(img, -targetX, 0, w, h)
  pieceCtx.restore()
  pieceCtx.save()
  pieceCtx.strokeStyle = 'rgba(255,255,255,0.75)'
  pieceCtx.lineWidth = 2
  buildPuzzlePath(pieceCtx, 0, y, pieceSize, r)
  pieceCtx.stroke()
  pieceCtx.restore()

  return {
    bgUrl: bgCanvas.toDataURL('image/png'),
    pieceUrl: pieceCanvas.toDataURL('image/png'),
    targetX,
    maxX: w - pieceSize - r - padding
  }
}
</script>
