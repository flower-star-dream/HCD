<template>
  <view class="app-container">
    <u-toast ref="uToast" />
    <u-loading-page
      :loading="loading"
      loading-text="加载中..."
      bg-color="#f8f8f8"
    />
    <router-view />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const loading = ref(false)
const uToast = ref(null)
const userStore = useUserStore()

onMounted(() => {
  // 初始化用户信息
  userStore.initUserInfo()
})
</script>

<style lang="scss">
@import 'uview-plus/index.scss';

.app-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f8f8f8;
}

/* 全局样式 */
page {
  background-color: #f8f8f8;
  font-size: 28rpx;
  color: #333;
}

.container {
  padding: 20rpx;
}

.flex {
  display: flex;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.text-primary {
  color: #2979ff;
}

.text-success {
  color: #19be6b;
}

.text-warning {
  color: #ff9900;
}

.text-error {
  color: #fa3534;
}

.text-gray {
  color: #999;
}

.text-light {
  color: #c0c0c0;
}

.bg-white {
  background-color: #fff;
}

.margin-top {
  margin-top: 20rpx;
}

.margin-bottom {
  margin-bottom: 20rpx;
}

.padding {
  padding: 20rpx;
}

.border-bottom {
  border-bottom: 1rpx solid #eee;
}
</style>