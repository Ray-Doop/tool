<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/ProfilePage.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <AppLayout>
      <div class="mx-auto max-w-6xl px-6 md:px-8 lg:px-10 py-8">
        <div class="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
          <div class="bg-gradient-to-r from-indigo-50 via-white to-violet-50 px-6 md:px-8 lg:px-10 py-6">
            <div class="flex items-center justify-between gap-6">
              <div>
                <h1 class="text-2xl md:text-3xl font-extrabold tracking-tight text-slate-900">个人中心</h1>
                <p class="mt-1 text-sm text-slate-500">管理您的个人资料与偏好</p>
              </div>
              <div v-if="!loading && !error">
                <button
                  @click="startEdit"
                  class="inline-flex items-center justify-center rounded-xl bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 transition-all"
                >
                  编辑资料
                </button>
              </div>
            </div>
          </div>

        <div v-if="loading" class="flex h-64 items-center justify-center text-slate-400 px-6 md:px-8 lg:px-10 py-8">
          <svg class="mr-3 h-6 w-6 animate-spin text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          加载中...
        </div>

        <div v-else-if="error" class="mx-6 md:mx-8 lg:mx-10 my-8 rounded-2xl border border-red-100 bg-red-50 p-4 text-sm text-red-600">
          {{ error }}
        </div>

        <div v-else class="grid grid-cols-1 gap-8 px-6 md:px-8 lg:px-10 py-8 lg:grid-cols-3">
          <!-- Left Column: User Card -->
          <div class="lg:col-span-1">
            <div class="overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-900/5">
              <div class="h-32 bg-gradient-to-r from-indigo-500 to-purple-600"></div>
              <div class="relative px-6 pb-6">
                <div class="-mt-16 mb-4 flex justify-center">
                  <div class="h-32 w-32 overflow-hidden rounded-full border-4 border-white bg-white shadow-lg">
                    <img
                      :src="profile?.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(profile?.displayName || profile?.username || 'User')}&background=random`"
                      alt="Avatar"
                      class="h-full w-full object-cover"
                    />
                  </div>
                </div>
                <div class="text-center">
                  <h2 class="text-xl font-bold text-slate-900">{{ profile?.displayName || profile?.username }}</h2>
                  <p class="text-sm font-medium text-slate-500">@{{ profile?.username }}</p>
                  <p v-if="profile?.bio" class="mt-4 text-sm leading-relaxed text-slate-600">{{ profile.bio }}</p>
                </div>
                
                <div class="mt-8 border-t border-slate-100 pt-6 space-y-3">
                  <div class="flex items-center justify-between text-sm">
                    <span class="text-slate-500">注册时间</span>
                    <span class="font-medium text-slate-700">{{ formatDate(profile?.createdAt) }}</span>
                  </div>
                  <div class="flex items-center justify-between text-sm">
                    <span class="text-slate-500">ID</span>
                    <span class="font-mono text-xs text-slate-400 bg-slate-100 px-2 py-1 rounded">{{ profile?.id }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Stats Card -->
            <div class="overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-900/5 mt-6">
              <div class="px-6 py-5 border-b border-slate-100">
                <h3 class="text-base font-semibold text-slate-900">数据统计</h3>
              </div>
              <div class="px-6 py-6">
                <div class="grid grid-cols-3 gap-4 text-center">
                  <div>
                    <div class="text-2xl font-bold text-indigo-600">{{ stats?.totalVisits || 0 }}</div>
                    <div class="text-xs text-slate-500 mt-1">总访问</div>
                  </div>
                  <div>
                    <div class="text-2xl font-bold text-indigo-600">{{ stats?.uniqueToolsUsed || 0 }}</div>
                    <div class="text-xs text-slate-500 mt-1">使用工具</div>
                  </div>
                  <div>
                    <div class="text-2xl font-bold text-indigo-600">{{ stats?.favoritesCount || 0 }}</div>
                    <div class="text-xs text-slate-500 mt-1">收藏</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Right Column: Details -->
          <div class="lg:col-span-2 space-y-6">
            <!-- Activity Heatmap -->
            <div class="overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-900/5">
              <div class="border-b border-slate-100 px-6 py-5">
                <h3 class="text-base font-semibold leading-6 text-slate-900">活跃度</h3>
              </div>
              <div class="px-6 py-6">
                <div class="w-full overflow-x-auto">
                  <div class="grid grid-rows-7 grid-flow-col gap-1 w-max">
                     <div 
                       v-for="day in activityDays" 
                       :key="day.date"
                       :title="`${day.date}: ${day.count} 次访问`"
                       class="h-3 w-3 rounded-sm transition-colors"
                       :class="getColorClass(day.count)"
                     ></div>
                  </div>
                </div>
                <div class="mt-4 flex items-center justify-end gap-2 text-xs text-slate-500">
                  <span>Less</span>
                  <div class="h-3 w-3 rounded-sm bg-slate-100"></div>
                  <div class="h-3 w-3 rounded-sm bg-indigo-200"></div>
                  <div class="h-3 w-3 rounded-sm bg-indigo-400"></div>
                  <div class="h-3 w-3 rounded-sm bg-indigo-600"></div>
                  <div class="h-3 w-3 rounded-sm bg-indigo-800"></div>
                  <span>More</span>
                </div>
              </div>
            </div>

            <div class="space-y-6">
              <div class="overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-900/5">
                <div class="border-b border-slate-100 px-6 py-5">
                  <h3 class="text-base font-semibold leading-6 text-slate-900">基本信息</h3>
                </div>
                <div class="px-6 py-6">
                  <dl class="grid grid-cols-1 gap-x-4 gap-y-8 sm:grid-cols-2">
                    <div>
                      <dt class="text-xs font-medium text-slate-500 uppercase tracking-wider">邮箱</dt>
                      <dd class="mt-1 text-sm font-medium text-slate-900">{{ profile?.email || '未设置' }}</dd>
                    </div>
                    <div>
                      <dt class="text-xs font-medium text-slate-500 uppercase tracking-wider">手机号</dt>
                      <dd class="mt-1 text-sm font-medium text-slate-900">{{ profile?.phone || '未设置' }}</dd>
                    </div>
                    <div>
                      <dt class="text-xs font-medium text-slate-500 uppercase tracking-wider">微信绑定</dt>
                      <dd class="mt-1">
                        <span :class="{'bg-green-50 text-green-700 ring-green-600/20': profile?.wechatBound, 'bg-slate-50 text-slate-600 ring-slate-500/10': !profile?.wechatBound}" class="inline-flex items-center rounded-md px-2 py-1 text-xs font-medium ring-1 ring-inset">
                          {{ profile?.wechatBound ? '已绑定' : '未绑定' }}
                        </span>
                      </dd>
                    </div>
                    <div>
                      <dt class="text-xs font-medium text-slate-500 uppercase tracking-wider">更新时间</dt>
                      <dd class="mt-1 text-sm font-medium text-slate-900">{{ formatTime(profile?.updatedAt) }}</dd>
                    </div>
                  </dl>
                </div>
              </div>

              <!-- Favorites Section -->
              <div class="overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-900/5">
                <div class="border-b border-slate-100 px-6 py-5">
                  <h3 class="text-base font-semibold leading-6 text-slate-900">我的收藏</h3>
                </div>
                <div class="px-6 py-6">
                  <div v-if="favoriteTools.length === 0" class="flex flex-col items-center justify-center py-10 text-slate-400">
                    <svg class="mb-3 h-10 w-10 opacity-20" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                    <p class="text-sm">暂无收藏工具</p>
                  </div>
                  <div v-else class="grid grid-cols-1 gap-4 sm:grid-cols-2">
                     <router-link
                      v-for="tool in favoriteTools"
                      :key="tool.slug"
                      :to="`/tools/${tool.slug}`"
                      class="group relative flex items-center justify-between rounded-xl border border-slate-200 bg-white p-4 shadow-sm hover:border-indigo-300 hover:ring-1 hover:ring-indigo-300 transition-all"
                    >
                      <span class="text-sm font-medium text-slate-900 group-hover:text-indigo-600">{{ tool.name || tool.slug }}</span>
                      <div class="flex items-center gap-2">
                        <svg class="h-4 w-4 text-slate-400 group-hover:text-indigo-500 transform group-hover:translate-x-1 transition-all" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                        </svg>
                        <button 
                          @click.prevent="removeFavorite(tool.slug)" 
                          class="opacity-0 group-hover:opacity-100 p-1 text-slate-400 hover:text-red-600 transition-all"
                          title="取消收藏"
                        >
                          <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                          </svg>
                        </button>
                      </div>
                    </router-link>
                  </div>
                </div>
              </div>

              <!-- Recent Tools Section -->
              <div class="overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-900/5">
                <div class="border-b border-slate-100 px-6 py-5">
                  <h3 class="text-base font-semibold leading-6 text-slate-900">最近使用</h3>
                </div>
                <div class="px-6 py-6">
                  <div v-if="recentTools.length === 0" class="flex flex-col items-center justify-center py-10 text-slate-400">
                    <svg class="mb-3 h-10 w-10 opacity-20" fill="currentColor" viewBox="0 0 20 20">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd" />
                    </svg>
                    <p class="text-sm">暂无使用记录</p>
                  </div>
                  <div v-else class="grid grid-cols-1 gap-4 sm:grid-cols-2">
                     <router-link
                      v-for="tool in recentTools"
                      :key="tool.slug"
                      :to="`/tools/${tool.slug}`"
                      class="group relative flex items-center justify-between rounded-xl border border-slate-200 bg-white p-4 shadow-sm hover:border-indigo-300 hover:ring-1 hover:ring-indigo-300 transition-all"
                    >
                      <div class="flex flex-col">
                        <span class="text-sm font-medium text-slate-900 group-hover:text-indigo-600">{{ tool.name }}</span>
                        <span class="text-xs text-slate-400 mt-0.5">{{ formatTime(tool.lastUsedAt) }}</span>
                      </div>
                      <svg class="h-4 w-4 text-slate-400 group-hover:text-indigo-500 transform group-hover:translate-x-1 transition-all" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                      </svg>
                    </router-link>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        </div>
      </div>

      <!-- Douyin-style Edit Modal -->
      <transition
        enter-active-class="transition duration-300 ease-out"
        enter-from-class="opacity-0"
        enter-to-class="opacity-100"
        leave-active-class="transition duration-200 ease-in"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div v-if="isEditing" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6" @click.self="cancelEdit">
          <!-- Backdrop -->
          <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-sm transition-opacity" @click="cancelEdit"></div>

          <!-- Modal Panel -->
          <transition
            enter-active-class="transition duration-300 ease-out"
            enter-from-class="opacity-0 scale-95 translate-y-4"
            enter-to-class="opacity-100 scale-100 translate-y-0"
            leave-active-class="transition duration-200 ease-in"
            leave-from-class="opacity-100 scale-100 translate-y-0"
            leave-to-class="opacity-0 scale-95 translate-y-4"
          >
            <div class="relative w-full max-w-lg overflow-hidden rounded-2xl bg-white shadow-2xl ring-1 ring-slate-900/5" @click.stop>
              <!-- Modal Header -->
              <div class="flex items-center justify-between border-b border-slate-100 px-6 py-4">
                <h3 class="text-lg font-semibold text-slate-900">编辑资料</h3>
                <button @click="cancelEdit" class="rounded-full p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition-colors">
                  <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <!-- Modal Body -->
              <div class="px-6 py-6">
                <form @submit.prevent="saveProfile" class="space-y-6">
                  <!-- Avatar -->
                  <div class="flex flex-col items-center">
                    <div class="group relative h-24 w-24 cursor-pointer overflow-hidden rounded-full border-2 border-slate-100 bg-slate-50 transition-all hover:border-indigo-200" @click="handleAvatarClick">
                      <img
                        :src="editForm.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(editForm.displayName || 'User')}&background=random`"
                        alt="Avatar"
                        class="h-full w-full object-cover transition-opacity group-hover:opacity-75"
                      />
                      <div class="absolute inset-0 flex items-center justify-center bg-black/20 opacity-0 transition-opacity group-hover:opacity-100">
                        <svg class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                        </svg>
                      </div>
                    </div>
                    <input
                      type="file"
                      ref="fileInput"
                      class="hidden"
                      accept="image/png, image/jpeg, image/jpg, image/webp"
                      @change="handleFileChange"
                    />
                    <p class="mt-2 text-xs text-slate-500 cursor-pointer hover:text-indigo-600 transition-colors" @click="handleAvatarClick">点击修改头像</p>
                  </div>

                  <!-- Fields -->
                  <div class="space-y-5">
                    <div>
                      <label class="block text-xs font-medium text-slate-700 mb-1.5">名字</label>
                      <div class="relative">
                        <input
                          v-model="editForm.displayName"
                          type="text"
                          class="block w-full rounded-lg border-0 bg-slate-50 py-2.5 px-3 text-slate-900 placeholder:text-slate-400 focus:bg-white focus:ring-2 focus:ring-indigo-600/20 sm:text-sm transition-all"
                          placeholder="您的昵称"
                          maxlength="20"
                        />
                        <span class="absolute right-3 top-2.5 text-xs text-slate-400">{{ editForm.displayName?.length || 0 }}/20</span>
                      </div>
                    </div>

                    <div>
                      <label class="block text-xs font-medium text-slate-700 mb-1.5">简介</label>
                      <div class="relative">
                        <textarea
                          v-model="editForm.bio"
                          rows="4"
                          class="block w-full rounded-lg border-0 bg-slate-50 py-2.5 px-3 text-slate-900 placeholder:text-slate-400 focus:bg-white focus:ring-2 focus:ring-indigo-600/20 sm:text-sm transition-all resize-none"
                          placeholder="介绍一下自己..."
                          maxlength="100"
                        ></textarea>
                         <span class="absolute right-3 bottom-2.5 text-xs text-slate-400">{{ editForm.bio?.length || 0 }}/100</span>
                      </div>
                    </div>
                    
                     <div class="grid grid-cols-2 gap-4">
                        <div>
                          <label class="block text-xs font-medium text-slate-700 mb-1.5">邮箱</label>
                          <input
                            v-model="editForm.email"
                            type="email"
                            class="block w-full rounded-lg border-0 bg-slate-50 py-2.5 px-3 text-slate-900 placeholder:text-slate-400 focus:bg-white focus:ring-2 focus:ring-indigo-600/20 sm:text-sm transition-all"
                            placeholder="Email"
                          />
                        </div>
                        <div>
                           <label class="block text-xs font-medium text-slate-700 mb-1.5">手机号</label>
                          <input
                            v-model="editForm.phone"
                            type="tel"
                            class="block w-full rounded-lg border-0 bg-slate-50 py-2.5 px-3 text-slate-900 placeholder:text-slate-400 focus:bg-white focus:ring-2 focus:ring-indigo-600/20 sm:text-sm transition-all"
                            placeholder="Phone"
                          />
                        </div>
                    </div>
                  </div>
                </form>
              </div>

              <!-- Modal Footer -->
              <div class="flex items-center justify-end gap-3 border-t border-slate-100 px-6 py-4">
                <button
                  @click="cancelEdit"
                  class="rounded-lg border border-slate-200 bg-white px-5 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 transition-all"
                >
                  取消
                </button>
                <button
                  @click="saveProfile"
                  :disabled="saving"
                  class="rounded-lg bg-indigo-600 px-6 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-sm shadow-indigo-200"
                >
                  {{ saving ? '保存中...' : '保存' }}
                </button>
              </div>
            </div>
          </transition>
        </div>
      </transition>
  </AppLayout>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import AppLayout from '@/layouts/AppLayout.vue'
import { meApi } from '@/api/me'
import { useToolCatalogStore } from '@/store/toolCatalog'
import { useAuthStore } from '@/store/auth'

const toolCatalog = useToolCatalogStore()
const authStore = useAuthStore()
const loading = ref(true)
const saving = ref(false)
const error = ref('')
const profile = ref(null)
const favoriteSlugs = ref([])
const recentTools = ref([])
const stats = ref(null)
const isEditing = ref(false)
const editForm = ref({})
const fileInput = ref(null)

const favoriteTools = computed(() => {
  return favoriteSlugs.value.map(slug => {
    const tool = toolCatalog.tools.find(t => t.slug === slug)
    return tool || { slug, name: slug }
  })
})

const activityDays = computed(() => {
  const days = []
  // Show last 52 weeks (approx 1 year)
  // Calculate start date to align with grid (start on Sunday or align to today - 365)
  // Let's just show last 365 days
  const today = new Date()
  const startDate = new Date(today)
  startDate.setDate(today.getDate() - 364) // 365 days including today
  
  for (let i = 0; i < 365; i++) {
    const d = new Date(startDate)
    d.setDate(startDate.getDate() + i)
    // Adjust for timezone when formatting
    const dateStr = d.toLocaleDateString('en-CA') // YYYY-MM-DD
    const count = stats.value?.dailyVisits?.[dateStr] || 0
    days.push({ date: dateStr, count })
  }
  return days
})

function getColorClass(count) {
  if (count === 0) return 'bg-slate-100'
  if (count <= 2) return 'bg-indigo-200'
  if (count <= 5) return 'bg-indigo-400'
  if (count <= 10) return 'bg-indigo-600'
  return 'bg-indigo-800'
}

function formatDate(value) {
  if (!value) return '-'
  return new Date(value).toLocaleDateString()
}

function formatTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

function handleAvatarClick() {
  fileInput.value?.click()
}

async function handleFileChange(event) {
  const file = event.target.files[0]
  if (!file) return

  // Check file size (e.g., 2MB)
  if (file.size > 2 * 1024 * 1024) {
    alert('图片大小不能超过 2MB')
    return
  }

  try {
    const updated = await meApi.uploadAvatar(file)
    profile.value = updated
    editForm.value.avatarUrl = updated.avatarUrl
    // 同步更新全局 auth 状态，确保右上角头像实时刷新
    authStore.updateUser(updated)
  } catch (e) {
    alert(e.message || '上传头像失败')
  } finally {
    event.target.value = ''
  }
}

function startEdit() {
  editForm.value = {
    displayName: profile.value.displayName,
    avatarUrl: profile.value.avatarUrl,
    bio: profile.value.bio,
    email: profile.value.email,
    phone: profile.value.phone
  }
  isEditing.value = true
}

function cancelEdit() {
  isEditing.value = false
  editForm.value = {}
}



// eslint-disable-next-line no-unused-vars
async function removeFavorite(slug) {
  if (!confirm('确定要取消收藏吗？')) return
  try {
    await meApi.removeFavorite(slug)
    // 乐观更新
    favoriteSlugs.value = favoriteSlugs.value.filter(s => s !== slug)
    // 更新统计数据
    if (stats.value) {
      stats.value.favoritesCount = Math.max(0, (stats.value.favoritesCount || 0) - 1)
    }
  } catch (e) {
    alert(e.message || '操作失败')
  }
}

async function saveProfile() {
  saving.value = true
  try {
    const updated = await meApi.updateProfile(editForm.value)
    profile.value = updated
    isEditing.value = false
    // 同步更新全局 auth 状态，确保右上角信息实时刷新
    authStore.updateUser(updated)
    // 刷新页面或提示成功
  } catch (e) {
    alert(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  loading.value = true
  error.value = ''
  try {
    // 并行加载
    const [p, f, r, s] = await Promise.all([
      meApi.profile(),
      meApi.favorites(),
      meApi.recentTools(),
      meApi.stats()
    ])
    profile.value = p
    favoriteSlugs.value = f
    recentTools.value = r
    stats.value = s
    
    // 确保工具目录已加载，以便显示收藏工具名称
    if (toolCatalog.tools.length === 0) {
      toolCatalog.refreshFromServer() // 异步刷新，不阻塞
    }
  } catch (e) {
    error.value = e?.message || '加载失败'
  } finally {
    loading.value = false
  }
})
</script>
