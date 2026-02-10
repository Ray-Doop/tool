<!--
  @generated-file-note
  文件：smart-tools-frontend/src/pages/ToolPage.vue
  用途：前端页面组件（路由页面，页面级状态与布局）
  归属：前端 pages
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <AppLayout>
    <div class="min-h-screen bg-slate-50/50">
      <div class="relative mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-10 animate-slide-up">
        
        <!-- Breadcrumb & Header -->
          <div class="mb-8 flex flex-col md:flex-row md:items-center justify-between gap-4">
           <div class="flex flex-col gap-2">
             <div class="flex items-center gap-2 text-sm font-medium text-slate-500">
               <router-link to="/" class="hover:text-slate-900 transition-colors flex items-center gap-1">
                 <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                   <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                 </svg>
                 首页
               </router-link>
               <span class="text-slate-300">/</span>
               <span>{{ tool?.category || '工具' }}</span>
             </div>
             <h1 class="text-3xl md:text-4xl font-extrabold text-slate-900 tracking-tight">
               {{ tool?.name ?? slug }}
             </h1>
           </div>
           
          <div class="flex items-center gap-3">
            <button @click="toggleFavorite" class="group relative inline-flex items-center gap-2 px-5 py-2.5 rounded-full bg-white text-sm font-medium text-slate-600 hover:text-indigo-600 transition-all shadow-sm hover:shadow-md ring-1 ring-slate-200 hover:ring-indigo-100">
              <svg class="h-5 w-5 transition-transform group-active:scale-90" :class="{'text-indigo-500 fill-current': isFavorite}" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
              </svg>
              <span>{{ isFavorite ? '已收藏' : '收藏' }}</span>
              <span class="text-xs text-slate-400">{{ stats?.favoritesCount ?? 0 }}</span>
            </button>
            <button @click="toggleLike" class="group relative inline-flex items-center gap-2 px-5 py-2.5 rounded-full bg-white text-sm font-medium text-slate-600 hover:text-indigo-600 transition-all shadow-sm hover:shadow-md ring-1 ring-slate-200 hover:ring-indigo-100">
              <svg class="h-5 w-5 transition-transform group-active:scale-90" :class="{'text-indigo-500 fill-current': stats?.liked}" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 9l-3 8-3-8m9 0a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <span>{{ stats?.liked ? '已点赞' : '点赞' }}</span>
              <span class="text-xs text-slate-400">{{ stats?.likesCount ?? 0 }}</span>
            </button>
            <button @click="copyLink" class="group relative inline-flex items-center gap-2 px-5 py-2.5 rounded-full bg-white text-sm font-medium text-slate-600 hover:text-indigo-600 transition-all shadow-sm hover:shadow-md ring-1 ring-slate-200 hover:ring-indigo-100">
              <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
              </svg>
              <span>分享</span>
            </button>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
          <!-- Main Content -->
          <div class="lg:col-span-9 space-y-8">
            <!-- Tool Area Container -->
            <div class="relative bg-white rounded-[2rem] shadow-xl shadow-slate-200/60 ring-1 ring-slate-900/5 overflow-hidden min-h-[600px] transition-all duration-500">
              <!-- Decorative Header Line -->
              <div class="absolute top-0 left-0 w-full h-1.5 bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 opacity-90"></div>
              
              <!-- Content Wrapper -->
              <div class="p-8 md:p-10 lg:p-12">
                <component :is="resolvedComponent" v-if="resolvedComponent" />
                <div v-else class="h-full flex flex-col items-center justify-center py-20 text-slate-400">
                  <el-empty description="工具加载中或不存在" />
                </div>
              </div>
            </div>
            
            <!-- Description -->
            <div class="bg-white rounded-2xl p-8 shadow-sm border border-slate-100">
               <h3 class="text-lg font-bold text-slate-900 mb-4 flex items-center gap-2">
                 <span class="w-1 h-5 bg-indigo-500 rounded-full"></span>
                 关于本工具
               </h3>
               <p class="text-slate-600 leading-relaxed text-base">{{ tool?.description || '暂无描述' }}</p>
            </div>

            <!-- Usage Guide (Conditional) -->
            <div v-if="isFileTool" class="bg-white rounded-2xl p-8 shadow-sm border border-slate-100">
               <h3 class="text-lg font-bold text-slate-900 mb-8 flex items-center gap-2">
                 <span class="w-1 h-5 bg-indigo-500 rounded-full"></span>
                 使用指南
               </h3>
               <div class="grid gap-8 md:grid-cols-3">
                <div class="relative group">
                  <div class="absolute -left-4 -top-4 w-12 h-12 bg-indigo-50 rounded-full -z-10 group-hover:scale-110 transition-transform"></div>
                  <div class="text-sm font-bold text-indigo-600 uppercase tracking-wider mb-2">Step 01</div>
                  <h4 class="text-lg font-bold text-slate-900 mb-2">上传文件</h4>
                  <p class="text-slate-500 text-sm leading-relaxed">将您的文件拖拽到操作区域，或点击上传按钮选择本地文件。</p>
                </div>
                <div class="relative group">
                  <div class="absolute -left-4 -top-4 w-12 h-12 bg-indigo-50 rounded-full -z-10 group-hover:scale-110 transition-transform"></div>
                  <div class="text-sm font-bold text-indigo-600 uppercase tracking-wider mb-2">Step 02</div>
                  <h4 class="text-lg font-bold text-slate-900 mb-2">配置参数</h4>
                  <p class="text-slate-500 text-sm leading-relaxed">根据需求调整工具的各项设置，实时预览处理效果。</p>
                </div>
                <div class="relative group">
                  <div class="absolute -left-4 -top-4 w-12 h-12 bg-indigo-50 rounded-full -z-10 group-hover:scale-110 transition-transform"></div>
                  <div class="text-sm font-bold text-indigo-600 uppercase tracking-wider mb-2">Step 03</div>
                  <h4 class="text-lg font-bold text-slate-900 mb-2">下载保存</h4>
                  <p class="text-slate-500 text-sm leading-relaxed">处理完成后，一键将结果文件安全地下载保存到您的设备。</p>
                </div>
              </div>
            </div>

            <div class="bg-white rounded-2xl p-8 shadow-sm border border-slate-100">
              <div class="flex items-center justify-between gap-4 mb-6">
                <h3 class="text-lg font-bold text-slate-900 flex items-center gap-2">
                  <span class="w-1 h-5 bg-indigo-500 rounded-full"></span>
                  评论
                </h3>
                <span class="text-xs text-slate-400">共 {{ stats?.commentsCount ?? 0 }} 条</span>
              </div>
              <div class="flex items-start gap-4">
                <div class="h-10 w-10 rounded-full overflow-hidden bg-slate-100">
                  <img
                    :src="resolveAvatar(currentUser?.avatarUrl) || avatarFallback(currentUser?.displayName || currentUser?.username || 'User')"
                    class="h-full w-full object-cover"
                    @error="e => handleAvatarError(e, currentUser?.displayName || currentUser?.username || 'User')"
                  />
                </div>
                <div class="flex-1">
                  <textarea
                    v-model="commentContent"
                    rows="4"
                    class="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:bg-white"
                    placeholder="写下你的评论..."
                  ></textarea>
                  <div class="flex items-center justify-end mt-3">
                    <button
                      @click="submitComment"
                      :disabled="submittingComment || !commentContent.trim()"
                      class="rounded-lg bg-indigo-600 px-5 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                    >
                      {{ submittingComment ? '发布中...' : '发布评论' }}
                    </button>
                  </div>
                </div>
              </div>

              <div class="mt-8 space-y-6">
                <div v-if="loadingComments" class="text-center text-sm text-slate-400">加载中...</div>
                <div v-else-if="comments.length === 0" class="text-center text-sm text-slate-400">暂无评论</div>
                <div v-else class="space-y-6">
                  <div v-for="comment in comments" :key="comment.id" class="space-y-4">
                    <div class="flex items-start gap-4">
                      <div class="h-10 w-10 rounded-full overflow-hidden bg-slate-100">
                        <img
                          :src="resolveAvatar(comment.avatarUrl) || avatarFallback(comment.displayName || comment.username || 'User')"
                          class="h-full w-full object-cover"
                          @error="e => handleAvatarError(e, comment.displayName || comment.username || 'User')"
                        />
                      </div>
                      <div class="flex-1">
                        <div class="flex items-center justify-between">
                          <div class="text-sm font-semibold text-slate-800">{{ comment.displayName || comment.username }}</div>
                          <div class="text-xs text-slate-400">{{ formatTime(comment.createdAt) }}</div>
                        </div>
                        <div v-if="editingCommentId === comment.id" class="mt-3">
                          <textarea
                            v-model="editingContent"
                            rows="3"
                            class="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:bg-white"
                          ></textarea>
                          <div class="flex items-center justify-end gap-2 mt-3">
                            <button
                              @click="cancelEdit"
                              class="rounded-lg border border-slate-200 bg-white px-4 py-1.5 text-xs font-medium text-slate-600 hover:bg-slate-50"
                            >
                              取消
                            </button>
                            <button
                              @click="saveEdit(comment)"
                              :disabled="editingSaving"
                              class="rounded-lg bg-indigo-600 px-4 py-1.5 text-xs font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
                            >
                              保存
                            </button>
                          </div>
                        </div>
                        <p v-else class="mt-2 text-sm text-slate-600 leading-relaxed">{{ comment.content }}</p>
                        <div class="flex items-center gap-4 mt-3 text-xs text-slate-500">
                          <button @click="toggleCommentLike(comment)" class="flex items-center gap-1 hover:text-indigo-600">
                            <svg class="h-4 w-4" :class="{'text-indigo-500 fill-current': comment.liked}" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 9l-3 8-3-8m9 0a3 3 0 11-6 0 3 3 0 016 0z" />
                            </svg>
                            <span>{{ comment.likeCount }}</span>
                          </button>
                          <button @click="startReply(comment)" class="hover:text-indigo-600">回复</button>
                          <button v-if="comment.owner" @click="startEdit(comment)" class="hover:text-indigo-600">编辑</button>
                          <button v-if="comment.owner" @click="deleteComment(comment)" class="hover:text-red-600">删除</button>
                        </div>
                        <div v-if="replyingTo === comment.id" class="mt-4">
                          <textarea
                            v-model="replyContent"
                            rows="3"
                            class="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:bg-white"
                            placeholder="写下回复..."
                          ></textarea>
                          <div class="flex items-center justify-end gap-2 mt-3">
                            <button
                              @click="cancelReply"
                              class="rounded-lg border border-slate-200 bg-white px-4 py-1.5 text-xs font-medium text-slate-600 hover:bg-slate-50"
                            >
                              取消
                            </button>
                            <button
                              @click="submitReply(comment)"
                              :disabled="submittingReply || !replyContent.trim()"
                              class="rounded-lg bg-indigo-600 px-4 py-1.5 text-xs font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
                            >
                              {{ submittingReply ? '回复中...' : '发布回复' }}
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div v-if="comment.replies?.length" class="space-y-4 pl-14">
                      <div v-for="reply in comment.replies" :key="reply.id" class="flex items-start gap-3">
                        <div class="h-8 w-8 rounded-full overflow-hidden bg-slate-100">
                          <img
                            :src="resolveAvatar(reply.avatarUrl) || avatarFallback(reply.displayName || reply.username || 'User')"
                            class="h-full w-full object-cover"
                            @error="e => handleAvatarError(e, reply.displayName || reply.username || 'User')"
                          />
                        </div>
                        <div class="flex-1">
                          <div class="flex items-center justify-between">
                            <div class="text-xs font-semibold text-slate-800">{{ reply.displayName || reply.username }}</div>
                            <div class="text-xs text-slate-400">{{ formatTime(reply.createdAt) }}</div>
                          </div>
                          <p class="mt-1 text-xs text-slate-600 leading-relaxed">{{ reply.content }}</p>
                          <div class="flex items-center gap-4 mt-2 text-xs text-slate-500">
                            <button @click="toggleCommentLike(reply)" class="flex items-center gap-1 hover:text-indigo-600">
                              <svg class="h-3.5 w-3.5" :class="{'text-indigo-500 fill-current': reply.liked}" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 9l-3 8-3-8m9 0a3 3 0 11-6 0 3 3 0 016 0z" />
                              </svg>
                              <span>{{ reply.likeCount }}</span>
                            </button>
                            <button @click="startReply(reply)" class="hover:text-indigo-600">回复</button>
                            <button v-if="reply.owner" @click="deleteComment(reply)" class="hover:text-red-600">删除</button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Sidebar -->
          <div class="lg:col-span-3 space-y-6">
            <div class="bg-white rounded-2xl p-6 shadow-sm border border-slate-100 sticky top-6">
               <h3 class="font-bold text-slate-900 mb-6 text-sm uppercase tracking-wide flex items-center gap-2">
                 <svg class="w-4 h-4 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                   <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                 </svg>
                 相关推荐
               </h3>
               <div class="space-y-3">
                  <router-link v-for="t in relatedTools" :key="t.slug" :to="`/tools/${t.slug}`" class="block p-4 rounded-xl bg-slate-50 border border-slate-100 hover:border-indigo-200 hover:bg-indigo-50/50 hover:shadow-md transition-all group">
                    <div class="font-bold text-slate-800 text-sm group-hover:text-indigo-700 mb-1">{{ t.name }}</div>
                    <div class="text-xs text-slate-500 line-clamp-2 leading-relaxed">{{ t.description }}</div>
                  </router-link>
               </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
// 工具详情页：根据路由参数 slug 找到工具模块并渲染
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppLayout from '@/layouts/AppLayout.vue'
import { useToolCatalogStore } from '@/store/toolCatalog'
import { useFavoritesStore } from '@/store/favorites'
import { logApi } from '@/api/log'
import { toolsApi } from '@/api/tools'
import { useAuthStore } from '@/store/auth'

const route = useRoute()
const catalogStore = useToolCatalogStore()
const favoritesStore = useFavoritesStore()
const authStore = useAuthStore()

catalogStore.ensureLoaded()
favoritesStore.ensureLoaded()

const slug = computed(() => route.params.slug)
const tool = computed(() => catalogStore.bySlug(slug.value))
const relatedTools = ref([])
const stats = ref(null)
const comments = ref([])
const loadingComments = ref(false)
const submittingComment = ref(false)
const submittingReply = ref(false)
const commentContent = ref('')
const replyContent = ref('')
const replyingTo = ref(null)
const editingCommentId = ref(null)
const editingContent = ref('')
const editingSaving = ref(false)
const currentUser = computed(() => authStore.user)

// 判断是否为文件处理类工具（用于决定是否显示文件上传指南）
const isFileTool = computed(() => {
  if (!tool.value) return false
  // 简单判定：分类包含“图片”、“文件”、“PDF”等关键词，或者 mode 明确为文件操作
  const cat = tool.value.category || ''
  return ['文档处理', '图片处理', '音视频', '格式转换', '文件'].includes(cat)
})

// 监听工具变化，上报访问日志
watch(slug, (newSlug) => {
  if (newSlug) {
    logApi.visit(newSlug, route.fullPath)
    loadStats()
    loadComments()
  }
}, { immediate: true })

// 智能推荐算法：基于分类和关键词权重
watch(tool, (current) => {
  if (!current) {
    relatedTools.value = []
    return
  }
  
  const all = catalogStore.tools.filter(t => t.slug !== current.slug && t.enabled)
  
  // 计算每个工具的相关性得分
  const scored = all.map(t => {
    let score = 0
    // 1. 同分类权重最高 (+10)
    if (t.category === current.category) score += 10
    
    // 2. 关键词匹配 (+1 per match)
    if (Array.isArray(current.keywords) && Array.isArray(t.keywords)) {
      const common = t.keywords.filter(k => current.keywords.includes(k))
      score += common.length
    }
    
    // 3. 引入少量随机因子防止列表僵化 (0-1)
    score += Math.random()
    
    return { tool: t, score }
  })
  
  // 按得分降序排列并取前 4 个
  scored.sort((a, b) => b.score - a.score)
  // 确保唯一性
  const unique = []
  const seen = new Set()
  for (const item of scored) {
    if (!seen.has(item.tool.slug)) {
      seen.add(item.tool.slug)
      unique.push(item.tool)
    }
  }
  relatedTools.value = unique.slice(0, 4)
}, { immediate: true })

// 动态加载工具组件
const resolvedComponent = computed(() => {
  if (!tool.value) return null
  return tool.value.component
})

const isFavorite = computed(() => favoritesStore.isFavorite(slug.value))

async function toggleFavorite() {
  const wasFavorite = isFavorite.value
  try {
    await favoritesStore.toggle(slug.value)
    ElMessage.success(isFavorite.value ? '已添加到收藏' : '已取消收藏')
    if (stats.value) {
      const delta = wasFavorite ? -1 : 1
      stats.value.favoritesCount = Math.max(0, (stats.value.favoritesCount || 0) + delta)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function toggleLike() {
  if (!slug.value) return
  try {
    if (stats.value?.liked) {
      await toolsApi.unlike(slug.value)
      stats.value.liked = false
      stats.value.likesCount = Math.max(0, (stats.value.likesCount || 0) - 1)
    } else {
      await toolsApi.like(slug.value)
      if (!stats.value) stats.value = { favoritesCount: 0, likesCount: 0, commentsCount: 0, liked: true }
      stats.value.liked = true
      stats.value.likesCount = (stats.value.likesCount || 0) + 1
    }
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

function copyLink() {
  navigator.clipboard.writeText(window.location.href).then(() => {
    ElMessage.success('链接已复制')
  })
}

async function loadStats() {
  if (!slug.value) return
  try {
    stats.value = await toolsApi.stats(slug.value)
  } catch (e) {
    stats.value = stats.value || { favoritesCount: 0, likesCount: 0, commentsCount: 0, liked: false }
  }
}

async function loadComments() {
  if (!slug.value) return
  loadingComments.value = true
  try {
    comments.value = await toolsApi.comments(slug.value)
  } catch (e) {
    comments.value = []
  } finally {
    loadingComments.value = false
  }
}

async function submitComment() {
  if (!commentContent.value.trim() || !slug.value) return
  submittingComment.value = true
  try {
    await toolsApi.addComment(slug.value, { content: commentContent.value.trim(), parentId: null })
    commentContent.value = ''
    await Promise.all([loadComments(), loadStats()])
  } catch (e) {
    ElMessage.error(e?.message || '评论失败')
  } finally {
    submittingComment.value = false
  }
}

function startReply(comment) {
  replyingTo.value = comment.id
  replyContent.value = ''
}

function cancelReply() {
  replyingTo.value = null
  replyContent.value = ''
}

async function submitReply(comment) {
  if (!replyContent.value.trim() || !slug.value) return
  submittingReply.value = true
  try {
    await toolsApi.addComment(slug.value, { content: replyContent.value.trim(), parentId: comment.id })
    replyContent.value = ''
    replyingTo.value = null
    await Promise.all([loadComments(), loadStats()])
  } catch (e) {
    ElMessage.error(e?.message || '回复失败')
  } finally {
    submittingReply.value = false
  }
}

function startEdit(comment) {
  editingCommentId.value = comment.id
  editingContent.value = comment.content
}

function cancelEdit() {
  editingCommentId.value = null
  editingContent.value = ''
}

async function saveEdit(comment) {
  if (!editingContent.value.trim() || !slug.value) return
  editingSaving.value = true
  try {
    await toolsApi.updateComment(slug.value, comment.id, { content: editingContent.value.trim() })
    cancelEdit()
    await loadComments()
  } catch (e) {
    ElMessage.error(e?.message || '修改失败')
  } finally {
    editingSaving.value = false
  }
}

async function deleteComment(comment) {
  if (!slug.value) return
  if (!confirm('确定要删除这条评论吗？')) return
  try {
    await toolsApi.deleteComment(slug.value, comment.id)
    await Promise.all([loadComments(), loadStats()])
  } catch (e) {
    ElMessage.error(e?.message || '删除失败')
  }
}

async function toggleCommentLike(comment) {
  if (!slug.value) return
  try {
    if (comment.liked) {
      await toolsApi.unlikeComment(slug.value, comment.id)
      comment.liked = false
      comment.likeCount = Math.max(0, (comment.likeCount || 0) - 1)
    } else {
      await toolsApi.likeComment(slug.value, comment.id)
      comment.liked = true
      comment.likeCount = (comment.likeCount || 0) + 1
    }
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

function resolveAvatar(path) {
  if (!path) return null
  const cleaned = path.trim()
  if (!cleaned) return null
  if (cleaned.startsWith('http')) return cleaned
  if (cleaned.startsWith('/uploads/')) return cleaned
  if (cleaned.startsWith('uploads/')) return `/${cleaned}`
  if (cleaned.startsWith('/api/')) return cleaned
  if (!cleaned.startsWith('/')) return `/api/${cleaned}`
  return `/api${cleaned}`
}

function avatarFallback(name) {
  const display = name || 'User'
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(display)}&background=random`
}

function handleAvatarError(event, name) {
  if (!event?.target) return
  event.target.src = avatarFallback(name)
}

function formatTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}
</script>
