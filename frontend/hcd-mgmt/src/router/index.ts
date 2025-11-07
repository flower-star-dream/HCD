import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Layout',
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
      //   meta: { title: '车次管理', icon: 'Train' }
      // },
      // {
      //   path: '/order',
      //   name: 'Order',
      //   component: () => import('@/views/Order/index.vue'),
      //   meta: { title: '订单管理', icon: 'Tickets' }
      // },
      {
        path: '/user',
        name: 'User',
        meta: { title: '用户管理', icon: 'User' },
        children: [
          {
            path: '/user/list',
            name: 'UserList',
            component: () => import('@/views/User/User-list/User-list-view.vue'),
            meta: { title: '用户列表' }
          }
        ]
      },
      // {
      //   path: '/station',
      //   name: 'Station',
      //   component: () => import('@/views/Station/index.vue'),
      //   meta: { title: '站点管理', icon: 'Location' }
      // },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/User/User-info/User-info-view.vue'),
        meta: { title: '个人信息', icon: 'User' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/404.vue'),
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