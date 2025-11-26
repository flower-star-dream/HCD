<template>
  <view class="app-container">
    <!-- <u-toast ref="uToast" /> -->
    <!-- <u-loading-page
      :loading="loading"
      loading-text="加载中..."
      bg-color="#f8f8f8"
    /> -->
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
/* 暂时注释uview-plus样式导入，避免Sass变量作用域问题 */
/* 
// 定义uview-plus所需的Sass变量
$u-primary: #3c9cff;
$u-info: #909399;
$u-default: #909399;
$u-warning: #f9ae3d;
$u-error: #f56c6c;
$u-success: #5ac725;
$u-main-color: #303133;
$u-content-color: #606266;
$u-tips-color: #909399;
$u-light-color: #c0c4cc;
$u-border-color: #e4e7ed;
$u-primary-light: rgba(60, 156, 255, 0.1);
$u-warning-light: rgba(249, 174, 61, 0.1);
$u-success-light: rgba(90, 199, 37, 0.1);
$u-error-light: rgba(245, 108, 108, 0.1);
$u-info-light: rgba(144, 147, 153, 0.1);
$u-primary-dark: #3688e6;
$u-warning-dark: #e09628;
$u-success-dark: #4fb81f;
$u-error-dark: #dd6161;
$u-info-dark: #7d8087;
$u-primary-disabled: rgba(60, 156, 255, 0.5);
$u-warning-disabled: rgba(249, 174, 61, 0.5);
$u-success-disabled: rgba(90, 199, 37, 0.5);
$u-error-disabled: rgba(245, 108, 108, 0.5);
$u-info-disabled: rgba(144, 147, 153, 0.5);

// 导入uview-plus样式
@import 'uview-plus/index.scss';
*/

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