import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/pages/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/pages/Dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      // {
      //   path: '/train',
      //   name: 'Train',
      //   component: () => import('@/pages/Train/index.vue'),
      //   meta: { title: '车次管理', icon: 'Train' }
      // },
      // {
      //   path: '/order',
      //   name: 'Order',
      //   component: () => import('@/pages/Order/index.vue'),
      //   meta: { title: '订单管理', icon: 'Tickets' }
      // },
      // {
      //   path: '/user',
      //   name: 'User',
      //   component: () => import('@/pages/User/index.vue'),
      //   meta: { title: '用户管理', icon: 'User' }
      // },
      // {
      //   path: '/station',
      //   name: 'Station',
      //   component: () => import('@/pages/Station/index.vue'),
      //   meta: { title: '站点管理', icon: 'Location' }
      // }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/pages/404.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || ''} - 火车订票系统后台`

  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router