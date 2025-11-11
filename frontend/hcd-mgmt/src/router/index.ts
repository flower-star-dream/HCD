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
        path: '/employee',
        name: 'Employee',
        meta: { title: '员工管理', icon: 'Employee' },
        children: [
          {
            path: '/employee/list',
            name: 'EmployeeList',
            component: () => import('@/views/Employee/Employee-list-view/Employee-list-view.vue'),
            meta: { title: '员工列表' }
          },
          {
            path: '/employee/profile',
            name: 'Profile',
            component: () => import('@/views/Employee/Employee-info-view/Employee-info-view.vue'),
            meta: { title: '个人信息'}
          },
          {
            path: '/employee/resetPassword',
            name: 'ResetPassword',
            component: () => import('@/views/Employee/Employee-resetPwd-view/Employee-resetPwd-view.vue'),
            meta: { title: '重置密码'}
          }
        ]
      },
      {
        path: '/user',
        name: 'User',
        meta: { title: '用户管理', icon: 'User' },
        children: [
          {
            path: '/user/list',
            name: 'UserList',
            component: () => import('@/views/User/User-list-view/User-list-view.vue'),
            meta: { title: '用户列表' }
          }
        ]
      },
      {
        path: '/passenger',
        name: 'Passenger',
        meta: { title: '乘车人管理', icon: 'User' },
        children: [
          {
            path: '/passenger/list',
            name: 'PassengerList',
            component: () => import('@/views/Passenger/Passenger-list-view/Passenger-list-view.vue'),
            meta: { title: '乘车人列表' }
          }
        ]
      }
      // {
      //   path: '/station',
      //   name: 'Station',
      //   component: () => import('@/views/Station/index.vue'),
>>>>>>> 7194a667e73e05f6f820be501adf75d935dc6a3c
      //   meta: { title: '站点管理', icon: 'Location' }
      // },
      
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