<template>
  <div class="layout-container">
    <el-container class="layout">
      <el-aside :width="sidebarCollapsed ? '64px' : '200px'" class="sidebar">
        <div class="sidebar-header">
          <div class="logo">
            <img :src="`${ossUrl}/assets/hcd/logo.png`" alt="logo" />
            <span v-if="!sidebarCollapsed">火车订票系统</span>
          </div>
        </div>

        <el-menu
          :default-active="$route.path"
          class="sidebar-menu"
          :collapse="sidebarCollapsed"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item
            v-for="item in menuList"
            :key="item.path"
            :index="item.path"
          >
              <el-icon>
                <component :is="isCustomIcon(item.icon)" />
              </el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-icon
              class="collapse-icon"
              @click="toggleSidebar"
            >
              <Expand v-if="sidebarCollapsed" />
              <Fold v-else />
            </el-icon>
          </div>

          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userInfo?.avatar" />
                <span class="username">{{ userInfo?.nickname || userInfo?.username }}</span>
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, defineAsyncComponent, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore, useAppStore, ossUrl } from '@/stores'
import { getUserInfoService } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const userInfo = computed(() => userStore.userInfo)
const sidebarCollapsed = computed(() => appStore.sidebarCollapsed)
const menuList = computed(() => appStore.menuList)

// 定义一个标志变量，用于标记是否是第一次加载
const isFirstLoad = ref(true);

const isCustomIcon = (icon: any) => {
  // 动态加载 components 目录下的同名组件，文件不存在时返回 icon
  if (icon.endsWith('Icon')) {
    // 组件存在
    return defineAsyncComponent(() =>import(`@/components/${icon}/${icon}.vue`))
  }
  return icon
}

const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const handleCommand = (command: string) => {
  switch (command) {
    case 'logout':
      handleLogout()
      break
    case 'profile':
      router.push('/profile')
      break
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logoutAction()
    router.push('/login')
    ElMessage.success('已退出登录')
  }).catch(() => {
    // 取消退出
  })
}

onMounted(async () => {
    // 确保userInfo和id都存在时才获取用户详情
    try {
      if (isFirstLoad.value) {
        let res = await getUserInfoService(userStore.userInfo?.id)
        userStore.setUserInfo(res)
        isFirstLoad.value = false
      }
    } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败，请重新登录')
    }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #1f2d3d;
}

.logo {
  display: flex;
  align-items: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 8px;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  transition: color 0.3s;
}

.collapse-icon:hover {
  color: #409EFF;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  margin-left: 8px;
  margin-right: 4px;
}

.main {
  background-color: #f5f5f5;
  padding: 20px;
}
</style>