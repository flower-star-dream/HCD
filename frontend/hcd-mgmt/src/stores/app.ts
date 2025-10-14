import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface MenuItem {
  path: string
  title: string
  icon?: string
  children?: MenuItem[]
}

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const menuList = ref<MenuItem[]>([
    {
      path: '/dashboard',
      title: '仪表盘',
      icon: 'Odometer'
    },
    {
      path: '/train',
      title: '车次管理',
      icon: 'Train'
    },
    {
      path: '/order',
      title: '订单管理',
      icon: 'Tickets'
    },
    {
      path: '/user',
      title: '用户管理',
      icon: 'User'
    },
    {
      path: '/station',
      title: '站点管理',
      icon: 'Location'
    }
  ])

  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  return {
    sidebarCollapsed,
    menuList,
    toggleSidebar
  }
})