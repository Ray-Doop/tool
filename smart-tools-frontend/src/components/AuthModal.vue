<!--
  @generated-file-note
  文件：smart-tools-frontend/src/components/AuthModal.vue
  用途：前端通用组件（可复用 UI 组件）
  归属：前端 components
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <Teleport to="body">
    <div v-if="modelValue" class="fixed inset-0 z-[3000] flex items-center justify-center p-4 sm:p-6">
      <!-- Backdrop with Blur -->
      <div 
        class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" 
        @click="close" 
      />
      
      <!-- Modal Content -->
      <div class="relative w-full max-w-4xl overflow-hidden rounded-3xl bg-white shadow-2xl ring-1 ring-black/5 flex flex-col md:flex-row h-auto md:h-[600px] animate-fade-in-up">
        
        <!-- Left Side: Visual / QR Code -->
        <div class="relative w-full md:w-[400px] bg-slate-50 border-b md:border-b-0 md:border-r border-slate-100 p-8 flex flex-col justify-between overflow-hidden">
          <!-- Decor Background -->
          <div class="absolute inset-0 bg-grid-slate-100 [mask-image:linear-gradient(0deg,white,rgba(255,255,255,0.6))]"></div>
          <div class="absolute -top-24 -left-24 h-64 w-64 rounded-full bg-indigo-500/10 blur-3xl"></div>
          <div class="absolute -bottom-24 -right-24 h-64 w-64 rounded-full bg-purple-500/10 blur-3xl"></div>

          <div class="relative z-10">
            <h2 class="text-2xl font-bold text-slate-800">欢迎回来</h2>
            <p class="mt-2 text-sm text-slate-500">扫码安全登录 / Scan to login</p>
          </div>

          <!-- QR Code Container -->
          <div class="relative z-10 flex-1 flex flex-col items-center justify-center py-8">
            <div class="relative group cursor-pointer" @click="active = 'wechat'; refreshWechatQr()">
              <div class="absolute -inset-0.5 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-2xl opacity-20 group-hover:opacity-40 blur transition duration-500"></div>
              <div class="relative h-64 w-64 rounded-xl bg-white p-4 shadow-sm flex items-center justify-center border border-slate-100">
                <!-- Loading State -->
                <div v-if="wechatLoading" class="flex flex-col items-center gap-3">
                  <svg class="animate-spin h-8 w-8 text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                  <span class="text-xs text-slate-400">加载中...</span>
                </div>
                
                <!-- QR Image (Generated or Image URL) -->
                <img
                  v-else-if="qrDataUrl"
                  :src="qrDataUrl"
                  alt="Scan to Login"
                  class="h-full w-full object-contain rounded-lg"
                />
                
                <!-- Placeholder / Error -->
                <div v-else class="text-center p-4">
                  <div class="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-slate-100 text-slate-400 mb-3">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6"><path stroke-linecap="round" stroke-linejoin="round" d="M3.75 4.875c0-.621.504-1.125 1.125-1.125h4.5c.621 0 1.125.504 1.125 1.125v4.5c0 .621-.504 1.125-1.125 1.125h-4.5A1.125 1.125 0 013.75 9.375v-4.5zM3.75 14.625c0-.621.504-1.125 1.125-1.125h4.5c.621 0 1.125.504 1.125 1.125v4.5c0 .621-.504 1.125-1.125 1.125h-4.5a1.125 1.125 0 01-1.125-1.125v-4.5zM13.5 4.875c0-.621.504-1.125 1.125-1.125h4.5c.621 0 1.125.504 1.125 1.125v4.5c0 .621-.504 1.125-1.125 1.125h-4.5A1.125 1.125 0 0113.5 9.375v-4.5z" /><path stroke-linecap="round" stroke-linejoin="round" d="M6.75 6.75h.75v.75h-.75v-.75zM6.75 16.5h.75v.75h-.75v-.75zM16.5 6.75h.75v.75h-.75v-.75zM13.5 13.5h.75v.75h-.75v-.75zM13.5 19.5h.75v.75h-.75v-.75zM19.5 13.5h.75v.75h-.75v-.75zM19.5 19.5h.75v.75h-.75v-.75zM16.5 16.5h.75v.75h-.75v-.75z" /></svg>
                  </div>
                  <p class="text-xs text-slate-500">{{ t.auth.clickToLoad }}</p>
                </div>
              </div>
            </div>
            
            <p class="mt-6 text-xs text-slate-400 text-center max-w-[200px]">
              {{ t.auth.scanTipWechat }}
            </p>
            
            <button
               class="mt-4 text-xs font-medium text-indigo-600 hover:text-indigo-700 hover:underline"
               type="button"
               :disabled="wechatLoading"
               @click="refreshWechatQr"
             >
               {{ t.auth.refreshQr }}
             </button>
             
             <!-- Dev Mode Simulation -->
             <div v-if="wechatQrContent && wechatQrContent.startsWith('smarttools://')" class="mt-4 w-full max-w-[200px]">
               <div class="flex gap-2">
                  <input
                    v-model.trim="wechatUsername"
                    class="flex-1 min-w-0 bg-white border border-slate-200 rounded-lg px-2 py-1.5 text-xs outline-none focus:border-indigo-500"
                    placeholder="Dev User"
                  />
                  <button
                    class="px-3 py-1.5 bg-slate-900 text-white text-xs font-medium rounded-lg hover:bg-slate-800"
                    :disabled="wechatConfirming"
                    @click="confirmWechatDev"
                  >
                    {{ t.auth.simulateScan }}
                  </button>
               </div>
             </div>
          </div>
          
          <div class="relative z-10 text-xs text-slate-400">
            © Smart Tools Inc.
          </div>
        </div>

        <!-- Right Side: Forms -->
        <div class="flex-1 flex flex-col p-8 md:p-12 overflow-y-auto custom-scrollbar">
          <!-- Header Actions -->
          <div class="flex items-center justify-between mb-8">
            <h3 class="text-xl font-bold text-slate-800">{{ active === 'register' ? t.auth.registerTitle : t.auth.loginTitle }}</h3>
            <button 
              class="h-8 w-8 rounded-full bg-slate-50 flex items-center justify-center text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition-colors"
              @click="close"
            >
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
            </button>
          </div>

          <!-- Tabs -->
          <div v-if="active !== 'register'" class="flex p-1 bg-slate-50 rounded-xl mb-8">
            <button
              v-for="tab in ['email', 'password']"
              :key="tab"
              class="flex-1 py-2 text-sm font-medium rounded-lg transition-all duration-200"
              :class="active === tab ? 'bg-white text-slate-800 shadow-sm ring-1 ring-black/5' : 'text-slate-500 hover:text-slate-700'"
              @click="active = tab"
            >
              {{ tab === 'email' ? '验证码登录' : '密码登录' }}
            </button>
          </div>

          <!-- Forms -->
          <div class="flex-1">
            <!-- Email Form -->
            <div v-if="active === 'email'" class="space-y-5 animate-fade-in-up">
              <div class="space-y-1.5">
                <label class="text-xs font-semibold text-slate-500 uppercase tracking-wider">邮箱地址</label>
                <input
                  v-model.trim="otpTarget"
                  class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-indigo-500/20 focus:bg-white transition-all outline-none"
                  placeholder="name@example.com"
                />
              </div>

              <div class="space-y-1.5">
                <label class="text-xs font-semibold text-slate-500 uppercase tracking-wider">验证码</label>
                <div class="relative">
                  <input
                    v-model.trim="otpCode"
                    class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-indigo-500/20 focus:bg-white transition-all outline-none"
                    placeholder="6位验证码"
                  />
                  <button
                    type="button"
                    class="absolute right-2 top-1.5 bottom-1.5 px-3 rounded-lg text-xs font-semibold transition-colors"
                    :class="sendingOtp || otpCountdown > 0 ? 'bg-slate-100 text-slate-400' : 'bg-indigo-50 text-indigo-600 hover:bg-indigo-100'"
                    :disabled="sendingOtp || otpCountdown > 0"
                    @click="sendOtp"
                  >
                    {{ otpCountdown > 0 ? `${otpCountdown}s` : '获取验证码' }}
                  </button>
                </div>
              </div>

              <button
                class="w-full mt-2 rounded-xl bg-slate-900 py-3.5 text-sm font-bold text-white shadow-lg shadow-slate-900/20 hover:bg-slate-800 hover:shadow-xl hover:shadow-slate-900/30 hover:-translate-y-0.5 transition-all active:translate-y-0 active:shadow-sm disabled:opacity-70 disabled:pointer-events-none"
                :disabled="otpLoading"
                @click="loginByOtp"
              >
                {{ otpLoading ? '登录中...' : '登录 / 注册' }}
              </button>
            </div>

            <!-- Password Form -->
            <div v-else-if="active === 'password'" class="space-y-5 animate-fade-in-up">
              <div class="space-y-1.5">
                <label class="text-xs font-semibold text-slate-500 uppercase tracking-wider">账号</label>
                <input
                  v-model.trim="identifier"
                  class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-indigo-500/20 focus:bg-white transition-all outline-none"
                  placeholder="用户名 / 邮箱 / 手机号"
                />
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div class="space-y-1.5">
                  <label class="text-xs font-semibold text-slate-500 uppercase tracking-wider">图形验证码</label>
                  <input
                    v-model.trim="captchaCode"
                    class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-indigo-500/20 focus:bg-white transition-all outline-none"
                    placeholder="输入图形码"
                    maxlength="8"
                  />
                </div>
                <div class="flex items-end">
                   <div 
                     class="h-[46px] w-full rounded-xl border border-slate-200 bg-white overflow-hidden cursor-pointer flex items-center justify-center hover:border-slate-300 transition-colors"
                     @click="refreshCaptcha"
                   >
                     <img v-if="captchaImage" :src="captchaImage" class="h-full w-full object-contain" />
                     <span v-else class="text-xs text-slate-400">点击加载</span>
                   </div>
                </div>
              </div>

              <div class="space-y-1.5">
                <label class="text-xs font-semibold text-slate-500 uppercase tracking-wider">密码</label>
                <input
                  v-model="password"
                  type="password"
                  class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-indigo-500/20 focus:bg-white transition-all outline-none"
                  placeholder="请输入密码"
                />
              </div>

              <button
                class="w-full mt-2 rounded-xl bg-slate-900 py-3.5 text-sm font-bold text-white shadow-lg shadow-slate-900/20 hover:bg-slate-800 hover:shadow-xl hover:shadow-slate-900/30 hover:-translate-y-0.5 transition-all active:translate-y-0 active:shadow-sm disabled:opacity-70 disabled:pointer-events-none"
                :disabled="passwordLoading"
                @click="loginByPassword"
              >
                {{ passwordLoading ? '登录中...' : '立即登录' }}
              </button>
              
              <div class="text-center">
                <button class="text-xs font-medium text-indigo-600 hover:text-indigo-700" @click="active = 'register'">
                  注册新账号
                </button>
              </div>
            </div>

            <!-- Register Form (Simplified) -->
            <div v-else-if="active === 'register'" class="space-y-5 animate-fade-in-up">
              <!-- Mode Switch -->
              <div v-if="wechatStatus !== 'need_register'" class="flex gap-4 mb-2">
                <label class="flex items-center gap-2 cursor-pointer">
                  <input type="radio" v-model="registerMode" value="email" class="text-indigo-600 focus:ring-indigo-500" />
                  <span class="text-sm text-slate-700">{{ t.auth.emailLogin }}</span>
                </label>
                <label class="flex items-center gap-2 cursor-pointer">
                  <input type="radio" v-model="registerMode" value="phone" class="text-indigo-600 focus:ring-indigo-500" />
                  <span class="text-sm text-slate-700">{{ t.auth.phoneLogin }}</span>
                </label>
              </div>
              <div v-else class="mb-4 p-3 bg-indigo-50 text-indigo-700 rounded-xl text-sm font-medium text-center">
                 请补全账号信息以完成微信登录
              </div>

              <div v-if="registerMode === 'email' || wechatStatus === 'need_register'" class="space-y-1.5">
                <input
                  v-model.trim="registerEmail"
                  class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 outline-none"
                  :placeholder="t.auth.emailPlaceholder"
                />
              </div>
              <input
                v-else
                v-model.trim="registerPhone"
                class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 outline-none"
                :placeholder="t.auth.phonePlaceholder"
              />

              <div v-if="registerMode === 'email' || wechatStatus === 'need_register'" class="space-y-1.5">
                <div class="relative">
                  <input
                    v-model.trim="registerEmailCode"
                    class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-indigo-500/20 focus:bg-white transition-all outline-none"
                    placeholder="邮箱验证码"
                  />
                  <button
                    type="button"
                    class="absolute right-2 top-1.5 bottom-1.5 px-3 rounded-lg text-xs font-semibold transition-colors"
                    :class="sendingOtp || otpCountdown > 0 ? 'bg-slate-100 text-slate-400' : 'bg-indigo-50 text-indigo-600 hover:bg-indigo-100'"
                    :disabled="sendingOtp || otpCountdown > 0"
                    @click="sendRegisterOtp"
                  >
                    {{ otpCountdown > 0 ? `${otpCountdown}s` : '获取验证码' }}
                  </button>
                </div>
                <div v-if="registerOtpChecking" class="text-xs text-slate-400">验证码校验中...</div>
                <div v-else-if="registerOtpValid === true" class="text-xs text-green-700">验证码正确</div>
                <div v-else-if="registerOtpValid === false" class="text-xs text-rose-700">验证码错误或已过期</div>
              </div>

              <input
                v-model.trim="registerUsername"
                class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 outline-none"
                :placeholder="t.auth.usernamePlaceholder"
              />
              
              <input
                v-model="registerPassword"
                type="password"
                class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 outline-none"
                :placeholder="t.auth.setPasswordPlaceholder"
              />

              <input
                v-model="registerPassword2"
                type="password"
                class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 outline-none"
                placeholder="确认密码"
              />

              <div v-if="registerMode === 'email' || wechatStatus === 'need_register'" class="grid grid-cols-2 gap-4">
                <input
                  v-model.trim="captchaCode"
                  class="w-full bg-slate-50 border-0 rounded-xl px-4 py-3 text-sm text-slate-800 ring-1 ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-indigo-500/20 focus:bg-white transition-all outline-none"
                  placeholder="图形验证码"
                  maxlength="8"
                />
                <div class="flex items-end">
                  <div
                    class="h-[46px] w-full rounded-xl border border-slate-200 bg-white overflow-hidden cursor-pointer flex items-center justify-center hover:border-slate-300 transition-colors"
                    @click="refreshCaptcha"
                  >
                    <img v-if="captchaImage" :src="captchaImage" class="h-full w-full object-contain" />
                    <span v-else class="text-xs text-slate-400">点击加载</span>
                  </div>
                </div>
              </div>
              
              <button
                type="button"
                class="w-full mt-2 rounded-xl bg-indigo-600 py-3.5 text-sm font-bold text-white shadow-lg shadow-indigo-500/20 hover:bg-indigo-700 hover:-translate-y-0.5 transition-all"
                :disabled="registerLoading"
                @click="doRegister"
              >
                {{ registerLoading ? t.auth.submitting : t.auth.createAccount }}
              </button>
              
              <div class="text-center">
                <button class="text-xs font-medium text-slate-500 hover:text-slate-700" @click="active = 'password'">
                  {{ t.auth.backToLogin }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Teleport>

  <Teleport to="body">
    <div v-if="otpSliderVisible" class="fixed inset-0 z-[3500] flex items-center justify-center p-4 sm:p-6" role="dialog">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="closeOtpSlider" />
      <div class="relative w-full max-w-sm overflow-hidden rounded-2xl bg-white p-6 text-left shadow-2xl transition-all animate-fade-in-up">
        <div class="flex items-center justify-between">
          <div class="text-sm font-bold text-slate-900">滑动验证</div>
          <div class="flex items-center gap-1">
            <button class="rounded-full p-2 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition-colors" @click="reloadOtpSlider">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="h-4 w-4">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0l3.181 3.183a8.25 8.25 0 0013.803-3.7M4.031 9.865a8.25 8.25 0 0113.803-3.7l3.181 3.182m0-4.991v4.99" />
              </svg>
            </button>
            <button class="rounded-full p-2 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition-colors" @click="closeOtpSlider">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="h-4 w-4">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <div class="mt-4">
          <div class="relative w-full overflow-hidden rounded-xl border border-slate-200 bg-slate-50">
            <div ref="otpPuzzleRef" class="relative w-full" :style="{ height: `${otpPuzzleHeight}px` }">
              <img v-if="otpPuzzleBgUrl" :src="otpPuzzleBgUrl" class="absolute inset-0 h-full w-full object-cover" />
              <img
                v-if="otpPuzzlePieceUrl"
                :src="otpPuzzlePieceUrl"
                class="absolute inset-0 h-full w-full pointer-events-none"
                :style="{ transform: `translateX(${otpPuzzleCurrentX}px)` }"
              />
              <div v-if="!otpPuzzleBgUrl" class="absolute inset-0 flex items-center justify-center text-xs text-slate-400">加载中...</div>
            </div>
          </div>

          <div class="mt-4">
            <div class="mb-2 text-xs text-slate-500">{{ otpSliderVerified ? '验证通过' : '拖动下方滑块完成拼图' }}</div>
            <div ref="otpSliderTrackRef" class="relative h-11 w-full rounded-xl bg-slate-100 overflow-hidden select-none">
              <div class="absolute inset-y-0 left-0 bg-indigo-500/20" :style="{ width: `${otpSliderProgress}%` }"></div>
              <div class="absolute inset-0 flex items-center justify-center text-xs font-semibold" :class="otpSliderVerified ? 'text-green-700' : 'text-slate-500'">
                {{ otpSliderVerified ? '验证通过' : '按住滑块拖动' }}
              </div>
              <div
                class="absolute top-1 bottom-1 w-12 rounded-lg bg-white shadow ring-1 ring-slate-200 flex items-center justify-center cursor-grab active:cursor-grabbing"
                :style="{ left: `${otpSliderX}px` }"
                @pointerdown="onOtpSliderDown"
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
import { ElMessage } from 'element-plus'
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'
import QRCode from 'qrcode'
import { useAuthStore } from '@/store/auth'
import { authApi } from '@/api/auth'
import { useI18n } from '@/composables/useI18n'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'open-wechat-register'])
const auth = useAuthStore()
const { t } = useI18n()

// Tabs: email, password, register, wechat
const active = ref('email')

// Login Forms
const identifier = ref('')
const password = ref('')
const passwordLoading = ref(false)

const otpTarget = ref('')
const otpCode = ref('')
const otpLoading = ref(false)
const sendingOtp = ref(false)
const otpCountdown = ref(0)
let otpTimer = null

const otpSliderVisible = ref(false)
const otpSliderX = ref(0)
const otpSliderMaxX = ref(0)
const otpSliderVerified = ref(false)
const otpSliderTrackRef = ref(null)
const otpPuzzleRef = ref(null)
let otpSliderDragging = false
let otpSliderStartX = 0

const otpPuzzleBgUrl = ref('')
const otpPuzzlePieceUrl = ref('')
const otpPuzzleTargetX = ref(0)
const otpPuzzleMaxX = ref(0)
const otpPuzzleHeight = 155
const otpPuzzleTolerancePx = 8

const otpSliderProgress = computed(() => {
  if (!otpSliderMaxX.value) return 0
  return Math.max(0, Math.min(100, (otpSliderX.value / otpSliderMaxX.value) * 100))
})

const otpPuzzleCurrentX = computed(() => {
  if (!otpSliderMaxX.value || !otpPuzzleMaxX.value) return 0
  const ratio = Math.max(0, Math.min(1, otpSliderX.value / otpSliderMaxX.value))
  return Math.round(ratio * otpPuzzleMaxX.value)
})

const captchaId = ref('')
const captchaCode = ref('')
const captchaImage = ref('')
const captchaLoading = ref(false)

// Register
const registerMode = ref('email')
const registerEmail = ref('')
const registerEmailCode = ref('')
const registerPhone = ref('')
const registerUsername = ref('')
const registerPassword = ref('')
const registerPassword2 = ref('')
const registerLoading = ref(false)
const registerOtpChecking = ref(false)
const registerOtpValid = ref(null)
let registerOtpCheckTimer = null
let registerOtpCheckSeq = 0

// WeChat
const wechatLoading = ref(false)
const wechatConfirming = ref(false)
const wechatState = ref('')
const wechatQrContent = ref('')
const wechatStatus = ref('pending')
const wechatUsername = ref('')
let wechatPollTimer = null
const qrDataUrl = ref('') // For generated QR code

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
      return
    }
    otpCountdown.value -= 1
  }, 1000)
}

async function sendOtp() {
  if (!otpTarget.value || sendingOtp.value || otpCountdown.value > 0) return
  otpSliderVisible.value = true
  await nextTick()
  updateOtpSliderMax()
  await reloadOtpSlider()
}

function closeOtpSlider() {
  otpSliderVisible.value = false
  otpSliderDragging = false
  otpSliderVerified.value = false
  otpSliderX.value = 0
  window.removeEventListener('pointermove', onOtpSliderMove)
  window.removeEventListener('pointerup', onOtpSliderUp)
}

async function reloadOtpSlider() {
  otpSliderVerified.value = false
  otpSliderX.value = 0
  updateOtpSliderMax()
  const w = Math.round(otpPuzzleRef.value?.getBoundingClientRect?.().width || 310)
  const seed = Date.now() % 2147483647
  const bg = await createPuzzleBackgroundDataUrl(w, otpPuzzleHeight, seed)
  const puzzle = await buildPuzzle(bg, w, otpPuzzleHeight)
  otpPuzzleBgUrl.value = puzzle.bgUrl
  otpPuzzlePieceUrl.value = puzzle.pieceUrl
  otpPuzzleTargetX.value = puzzle.targetX
  otpPuzzleMaxX.value = puzzle.maxX
}

function onOtpSliderDown(e) {
  if (otpSliderVerified.value) return
  updateOtpSliderMax()
  otpSliderDragging = true
  otpSliderStartX = e.clientX
  const knob = otpSliderX.value
  otpSliderStartKnobX = knob
  window.addEventListener('pointermove', onOtpSliderMove)
  window.addEventListener('pointerup', onOtpSliderUp)
}

let otpSliderStartKnobX = 0

function onOtpSliderMove(e) {
  if (!otpSliderDragging || !otpSliderTrackRef.value) return
  const rect = otpSliderTrackRef.value.getBoundingClientRect()
  const knobWidth = 48
  const maxX = Math.max(0, rect.width - knobWidth - 8)
  otpSliderMaxX.value = maxX
  const deltaPx = e.clientX - otpSliderStartX
  otpSliderX.value = Math.max(0, Math.min(maxX, otpSliderStartKnobX + deltaPx))
}

function onOtpSliderUp() {
  otpSliderDragging = false
  window.removeEventListener('pointermove', onOtpSliderMove)
  window.removeEventListener('pointerup', onOtpSliderUp)
  if (otpSliderVerified.value) return
  const pass = Math.abs(otpPuzzleCurrentX.value - otpPuzzleTargetX.value) <= otpPuzzleTolerancePx
  if (!pass) {
    otpSliderX.value = 0
    return
  }
  otpSliderVerified.value = true
  setTimeout(async () => {
    closeOtpSlider()
    const expiresInSeconds = await doSendOtp()
    if (Number.isFinite(expiresInSeconds) && expiresInSeconds > 0) {
      startOtpCountdown(expiresInSeconds)
    }
  }, 160)
}

async function doSendOtp() {
  if (!otpTarget.value) return
  sendingOtp.value = true
  try {
    const scene = active.value === 'register' ? 'register' : 'login'
    const res = await auth.otpSend('email', otpTarget.value, null, null, scene)
    ElMessage.success({ message: t.value.auth.sentCaptcha, zIndex: 10000 })
    return res?.expiresInSeconds || 60
  } catch (e) {
    otpCountdown.value = 0
    stopOtpCountdown()
    if (e?.code === 'USER_NOT_FOUND') {
      ElMessage.warning({ message: t.value.auth.emailNotRegistered, zIndex: 10000 })
    } else {
      ElMessage.error({ message: e?.message || t.value.auth.sendFailed, zIndex: 10000 })
    }
  } finally {
    sendingOtp.value = false
  }
}

async function loginByOtp() {
  if (!otpTarget.value || !otpCode.value) return
  otpLoading.value = true
  try {
    const startAt = performance.now()
    const needsRegister = await auth.loginOtp('email', otpTarget.value, otpCode.value)
    const ms = Math.round(performance.now() - startAt)
    if (needsRegister) {
      ElMessage.success({ message: `登录成功，请补全账号信息（${ms}ms）`, zIndex: 10000 })
      emit('open-wechat-register', { mode: 'email', email: otpTarget.value })
      close()
      return
    }
    ElMessage.success({ message: `${t.value.auth.successLogin}（${ms}ms）`, zIndex: 10000 })
    close()
  } catch (e) {
    ElMessage.error({ message: e?.message || t.value.auth.loginFailed, zIndex: 10000 })
  } finally {
    otpLoading.value = false
  }
}

async function loginByPassword() {
  if (!identifier.value || !password.value || !captchaId.value || !captchaCode.value) return
  passwordLoading.value = true
  try {
    const startAt = performance.now()
    await auth.loginPassword(identifier.value, password.value, captchaId.value, captchaCode.value)
    const ms = Math.round(performance.now() - startAt)
    ElMessage.success({ message: `${t.value.auth.successLogin}（${ms}ms）`, zIndex: 10000 })
    close()
  } catch (e) {
    if (e?.code === 'USER_NOT_FOUND') {
      active.value = 'register'
      ElMessage.warning({ message: t.value.auth.accountNotRegistered, zIndex: 10000 })
    } else {
      ElMessage.error({ message: e?.message || t.value.auth.loginFailed, zIndex: 10000 })
    }
    captchaCode.value = ''
    await refreshCaptcha()
  } finally {
    passwordLoading.value = false
  }
}

async function sendRegisterOtp() {
  if (sendingOtp.value || otpCountdown.value > 0) return
  if (registerMode.value !== 'email') return
  if (!registerEmail.value) {
    ElMessage.warning({ message: '请填写邮箱', zIndex: 10000 })
    return
  }
  otpTarget.value = registerEmail.value
  await sendOtp()
}

async function doRegister() {
  if (!registerUsername.value) {
    ElMessage.warning({ message: '请填写用户名', zIndex: 10000 })
    return
  }
  if (!registerPassword.value || !registerPassword2.value) {
    ElMessage.warning({ message: '请填写密码与确认密码', zIndex: 10000 })
    return
  }
  if (registerPassword.value !== registerPassword2.value) {
    ElMessage.warning({ message: '两次输入的密码不一致', zIndex: 10000 })
    return
  }
  if (registerMode.value === 'email') {
    if (!registerEmail.value) {
      ElMessage.warning({ message: '请填写邮箱', zIndex: 10000 })
      return
    }
    if (!registerEmailCode.value) {
      ElMessage.warning({ message: '请填写邮箱验证码', zIndex: 10000 })
      return
    }
    if (!captchaId.value) {
      await refreshCaptcha()
      ElMessage.warning({ message: '请先输入图形验证码', zIndex: 10000 })
      return
    }
    if (!captchaCode.value) {
      ElMessage.warning({ message: '请填写图形验证码', zIndex: 10000 })
      return
    }
    if (registerOtpValid.value === false) {
      ElMessage.warning({ message: '邮箱验证码错误或已过期', zIndex: 10000 })
      return
    }
  } else {
    if (!registerPhone.value) {
      ElMessage.warning({ message: '请填写手机号', zIndex: 10000 })
      return
    }
  }

  registerLoading.value = true
  try {
    if (registerMode.value === 'email') {
      const startAt = performance.now()
      await auth.registerEmail(
        registerEmail.value,
        registerEmailCode.value,
        registerPassword.value,
        registerPassword2.value,
        registerUsername.value,
        captchaId.value,
        captchaCode.value
      )
      const ms = Math.round(performance.now() - startAt)
      ElMessage.success({ message: `${t.value.auth.successRegister}（${ms}ms）`, zIndex: 10000 })
    } else {
      const startAt = performance.now()
      await auth.registerPhone(registerPhone.value, registerPassword.value, registerPassword2.value, registerUsername.value)
      const ms = Math.round(performance.now() - startAt)
      ElMessage.success({ message: `${t.value.auth.successRegister}（${ms}ms）`, zIndex: 10000 })
    }
    close()
  } catch (e) {
    ElMessage.error({ message: e?.message || t.value.auth.registerFailed, zIndex: 10000 })
  } finally {
    registerLoading.value = false
  }
}

function stopWechatPoll() {
  if (wechatPollTimer) {
    clearInterval(wechatPollTimer)
    wechatPollTimer = null
  }
}

function startWechatPoll() {
  stopWechatPoll()
  if (!wechatState.value) return
  wechatPollTimer = setInterval(async () => {
    try {
      const prevStatus = wechatStatus.value
      const res = await authApi.wechatPoll(wechatState.value)
      wechatStatus.value = res.status
      if (res.status === 'authed' && res.token) {
        ElMessage.success({ message: '扫码成功，正在登录...', zIndex: 10000 })
        await auth.applyToken(res.token)
        close()
        // Reload page to reflect login state
        setTimeout(() => {
          window.location.reload()
        }, 500)
      }
      if (res.status === 'need_register') {
        ElMessage.success({ message: '扫码成功，请补全账号信息', zIndex: 10000 })
        stopWechatPoll()
        close()
        emit('open-wechat-register', wechatState.value)
      }
      if (res.status === 'scanned' && prevStatus !== 'scanned') {
        ElMessage.info({ message: '扫码成功，请在手机确认登录', zIndex: 10000 })
      }
    } catch {
      wechatStatus.value = 'error'
    }
  }, 1500)
}

async function generateQr(text) {
  try {
    qrDataUrl.value = await QRCode.toDataURL(text, { width: 200, margin: 1, color: { dark: '#1e293b', light: '#ffffff' } })
  } catch (e) {
    console.error('QR Gen Error:', e)
    qrDataUrl.value = ''
  }
}

async function refreshWechatQr() {
  wechatLoading.value = true
  qrDataUrl.value = ''
  try {
    const qr = await authApi.wechatQr()
    wechatState.value = qr.state
    wechatQrContent.value = qr.qrContent
    if (qr.qrImage && typeof qr.qrImage === 'string') {
      qrDataUrl.value = qr.qrImage
      wechatStatus.value = 'pending'
      startWechatPoll()
      return
    }
    
    // 智能判断：如果是微信图片链接(showqrcode)或常见图片格式，直接显示；否则(如URL链接、协议头)生成二维码图片
    const isImage = qr.qrContent && (qr.qrContent.includes('showqrcode') || /\.(png|jpg|jpeg|gif)$/i.test(qr.qrContent))
    
    if (isImage) {
       qrDataUrl.value = qr.qrContent
    } else {
       await generateQr(qr.qrContent)
    }

    wechatStatus.value = 'pending'
    startWechatPoll()
  } catch (e) {
    ElMessage.error({ message: e?.message || t.value.auth.loadQrFailed, zIndex: 10000 })
  } finally {
    wechatLoading.value = false
  }
}

async function confirmWechatDev() {
  if (!wechatState.value) return
  wechatConfirming.value = true
  try {
    await auth.wechatDevConfirm(wechatState.value, wechatUsername.value)
    ElMessage.success({ message: t.value.auth.scanSuccess, zIndex: 10000 })
  } catch (e) {
    ElMessage.error({ message: e?.message || t.value.auth.confirmFailed, zIndex: 10000 })
  } finally {
    wechatConfirming.value = false
  }
}

async function refreshCaptcha() {
  captchaLoading.value = true
  try {
    const c = await authApi.captcha()
    captchaId.value = c.captchaId
    captchaImage.value = c.image
  } catch (e) {
    captchaId.value = ''
    captchaImage.value = ''
  } finally {
    captchaLoading.value = false
  }
}

watch(
  () => props.modelValue,
  val => {
    if (!val) {
      stopOtpCountdown()
      stopWechatPoll()
      return
    }
    if (!captchaId.value) refreshCaptcha()
    // Always load QR when modal opens
    refreshWechatQr()
  }
)

watch(
  () => active.value,
  tab => {
    if (tab === 'email') {
      stopOtpCountdown()
      otpTarget.value = ''
      otpCode.value = ''
    }
    captchaCode.value = ''
    if (props.modelValue) refreshCaptcha()
  }
)

watch(otpTarget, () => {
  otpCode.value = ''
  otpCountdown.value = 0
  stopOtpCountdown()
})

watch(registerEmail, () => {
  registerEmailCode.value = ''
  registerOtpValid.value = null
  registerOtpChecking.value = false
  if (registerOtpCheckTimer) {
    clearTimeout(registerOtpCheckTimer)
    registerOtpCheckTimer = null
  }
})

watch(
  () => [registerEmail.value, registerEmailCode.value],
  ([emailVal, codeVal]) => {
    registerOtpValid.value = null
    if (registerOtpCheckTimer) {
      clearTimeout(registerOtpCheckTimer)
      registerOtpCheckTimer = null
    }
    if (!emailVal || !codeVal || codeVal.length < 4) return

    const seq = ++registerOtpCheckSeq
    registerOtpChecking.value = true
    registerOtpCheckTimer = setTimeout(async () => {
      try {
        const ok = await authApi.otpCheck('email', emailVal, codeVal)
        if (seq === registerOtpCheckSeq) registerOtpValid.value = ok
      } catch (e) {
        if (seq !== registerOtpCheckSeq) return
        if (e?.code === 'OTP_INVALID') {
          registerOtpValid.value = false
        } else {
          registerOtpValid.value = null
          ElMessage.error({ message: e?.message || '验证码校验失败', zIndex: 10000 })
        }
      } finally {
        if (seq === registerOtpCheckSeq) registerOtpChecking.value = false
      }
    }, 300)
  }
)

onBeforeUnmount(() => {
  stopOtpCountdown()
  stopWechatPoll()
  if (registerOtpCheckTimer) clearTimeout(registerOtpCheckTimer)
})

function updateOtpSliderMax() {
  if (!otpSliderTrackRef.value) return
  const rect = otpSliderTrackRef.value.getBoundingClientRect()
  const knobWidth = 48
  otpSliderMaxX.value = Math.max(0, rect.width - knobWidth - 8)
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

<style scoped>
.bg-grid-slate-100 {
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 32 32' width='32' height='32' fill='none' stroke='rgb(241 245 249)'%3e%3cpath d='M0 .5H31.5V32'/%3e%3c/svg%3e");
}
</style>
