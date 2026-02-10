/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/router/index.js
 * 用途：前端路由配置
 * 归属：前端 router
 * 依赖：vue-router、@/pages/HomePage.vue、@/pages/ToolPage.vue、@/pages/WechatLoginPage.vue、@/pages/ProfilePage.vue、@/pages/SettingsPage.vue、@/pages/admin/AdminDashboard.vue、@/pages/admin/AdminUsersPage.vue、@/pages/admin/AdminFunctionsPage.vue、@/pages/admin/AdminRolesPage.vue、@/pages/admin/AdminErrorTipsPage.vue、@/pages/admin/AdminOperationLogsPage.vue、@/pages/admin/AdminMonitorPage.vue、@/pages/admin/AdminLoginPage.vue
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
// 路由定义：首页（工具列表）与工具详情页
import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import ToolPage from '@/pages/ToolPage.vue'
import WechatLoginPage from '@/pages/WechatLoginPage.vue'
import ProfilePage from '@/pages/ProfilePage.vue'
import SettingsPage from '@/pages/SettingsPage.vue'
import AdminDashboard from '@/pages/admin/AdminDashboard.vue'
import AdminUsersPage from '@/pages/admin/AdminUsersPage.vue'
import AdminFunctionsPage from '@/pages/admin/AdminFunctionsPage.vue'
import AdminRolesPage from '@/pages/admin/AdminRolesPage.vue'
import AdminErrorTipsPage from '@/pages/admin/AdminErrorTipsPage.vue'
import AdminOperationLogsPage from '@/pages/admin/AdminOperationLogsPage.vue'
import AdminMonitorPage from '@/pages/admin/AdminMonitorPage.vue'
import AdminLoginPage from '@/pages/admin/AdminLoginPage.vue'
import { useAuthStore } from '@/store/auth'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomePage },
    { path: '/wechat-login', name: 'wechat-login', component: WechatLoginPage },
    { 
      path: '/profile', 
      name: 'profile', 
      component: ProfilePage,
      meta: { requiresAuth: true }
    },
    { 
      path: '/settings', 
      name: 'settings', 
      component: SettingsPage,
      meta: { requiresAuth: true }
    },
    { 
      path: '/admin', 
      name: 'admin',
      component: AdminDashboard,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/login',
      name: 'admin-login',
      component: AdminLoginPage
    },
    { 
      path: '/admin/users', 
      name: 'admin-users',
      component: AdminUsersPage,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    { 
      path: '/admin/tools', 
      name: 'admin-tools',
      component: AdminFunctionsPage,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    { 
      path: '/admin/roles', 
      name: 'admin-roles',
      component: AdminRolesPage,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    { 
      path: '/admin/access', 
      name: 'admin-access',
      component: AdminRolesPage,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    { 
      path: '/admin/errors', 
      name: 'admin-errors',
      component: AdminErrorTipsPage,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    { 
      path: '/admin/audit', 
      name: 'admin-audit',
      component: AdminOperationLogsPage,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    { 
      path: '/admin/monitor', 
      name: 'admin-monitor',
      component: AdminMonitorPage,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    // 所有工具页都需要登录
    { 
      path: '/tools/:slug', 
      name: 'tool', 
      component: ToolPage, 
      props: true,
      meta: { requiresAuth: true }
    }
  ]
})

// 全局路由守卫
router.beforeEach(async (to, from, next) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.isAuthed) {
    await auth.silentRefresh()
  }
  if (to.meta.requiresAuth && !auth.isAuthed) {
    if (to.path.startsWith('/admin')) {
      next({ name: 'admin-login', query: { redirect: to.fullPath } })
    } else {
      next({ name: 'home', query: { login: '1', redirect: to.fullPath } })
    }
    return
  }
  if (to.meta.requiresAdmin && !auth.isAdmin) {
    next({ name: 'admin-login', query: { redirect: to.fullPath } })
    return
  }
  next()
})
