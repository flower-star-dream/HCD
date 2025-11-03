import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Layout',
<<<<<<< HEAD
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
=======
    component: () => import('@/views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home/Home.vue'),
        meta: { title: '首页', icon: 'Home' }
      },
      // {
      //   path: '/dashboard',
      //   name: 'Dashboard',
      //   component: () => import('@/views/Dashboard/index.vue'),
      //   meta: { title: '仪表盘', icon: 'Odometer' }
      // },
      // {
      //   path: '/train',
      //   name: 'Train',
      //   component: () => import('@/views/Train/index.vue'),
>>>>>>> 7194a667e73e05f6f820be501adf75d935dc6a3c
      //   meta: { title: '车次管理', icon: 'Train' }
      // },
      // {
      //   path: '/order',
      //   name: 'Order',
<<<<<<< HEAD
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
=======
      //   component: () => import('@/views/Order/index.vue'),
      //   meta: { title: '订单管理', icon: 'Tickets' }
      // },
      {
        path: '/user',
        name: 'User',
        component: () => import('@/views/User/User-view.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      // {
      //   path: '/station',
      //   name: 'Station',
      //   component: () => import('@/views/Station/index.vue'),
>>>>>>> 7194a667e73e05f6f820be501adf75d935dc6a3c
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