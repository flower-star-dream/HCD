<template>
  <div class="home">
    <div class="container">
      <!-- 头部欢迎区域 -->
      <div class="welcome-section">
        <div class="welcome-content">
          <h1 class="welcome-title">
            <span class="icon-train">🚄</span>
            欢迎使用火车订票后台管理系统
          </h1>
          <p class="welcome-subtitle">高效管理票务，为用户提供优质出行体验</p>
          <div class="user-info" v-if="userInfo">
            <span class="greeting">您好，{{ userInfo.name || '管理员' }}</span>
            <span class="current-time">{{ currentTime }}</span>
          </div>
        </div>
      </div>
      
      <!-- 快捷操作区域 -->
      <div class="quick-actions">
        <h2 class="section-title">快捷操作</h2>
        <div class="action-grid">
          <div class="action-card" @click="navigateTo('/orders')">
            <div class="action-icon">📋</div>
            <div class="action-text">订单管理</div>
          </div>
          <div class="action-card" @click="navigateTo('/users')">
            <div class="action-icon">👥</div>
            <div class="action-text">员工管理</div>
          </div>
          <div class="action-card" @click="navigateTo('/trains')">
            <div class="action-icon">🚆</div>
            <div class="action-text">车次管理</div>
          </div>
          <div class="action-card" @click="navigateTo('/settings')">
            <div class="action-icon">⚙️</div>
            <div class="action-text">系统设置</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useEmployeeStore } from '@/stores/employee'

const router = useRouter()
const employeeStore = useEmployeeStore()
const currentTime = ref('')

// 获取员工信息
const userInfo = computed(() => employeeStore.employeeInfo)

// 格式化当前时间
const formatTime = () => {
  const now = new Date()
  const options = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }
  currentTime.value = now.toLocaleString('zh-CN', options)
}

// 导航到指定路径
const navigateTo = (path) => {
  router.push(path)
}

// 页面加载时执行
onMounted(() => {
  formatTime()
  // 每秒更新时间
  setInterval(formatTime, 1000)
})
</script>

<style scoped lang="scss">
@use '@/styles/global.scss';

.home {
  width: 100%;
  min-height: 100%;
  background-color: #f5f7fa;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.container {
  width: 100%;
  max-width: 1000px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 欢迎区域样式 */
.welcome-section {
  background: #ffffff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  animation: fadeInUp 0.6s ease-out;
}

.welcome-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.welcome-title {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 0;
}

.icon-train {
  font-size: 36px;
}

.welcome-subtitle {
  font-size: 16px;
  color: #666;
  text-align: center;
  margin: 0;
}

.user-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.greeting {
  font-size: 16px;
  color: #555;
}

.current-time {
  font-size: 14px;
  color: #888;
}

/* 快捷操作区域样式 */
.quick-actions {
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  animation: fadeInUp 0.6s ease-out 0.3s;
  animation-fill-mode: both;
}

.section-title {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin: 0 0 24px 0;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.action-card {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  border-color: #409eff;
  background: #ecf5ff;
}

.action-icon {
  font-size: 32px;
}

.action-text {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home {
    padding: 16px;
  }
  
  .welcome-section {
    padding: 24px;
  }
  
  .welcome-title {
    font-size: 24px;
  }
  
  .action-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 480px) {
  .action-grid {
    grid-template-columns: 1fr;
  }
  
  .user-info {
    flex-direction: column;
    gap: 8px;
    align-items: center;
  }
}
</style>
