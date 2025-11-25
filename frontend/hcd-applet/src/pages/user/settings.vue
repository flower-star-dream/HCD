<template>
  <view class="settings-container">
    <view class="settings-section">
      <u-cell-group>
        <u-cell title="消息通知" is-link @click="goToNotificationSettings" />
        <u-cell title="隐私设置" is-link @click="goToPrivacySettings" />
        <u-cell title="通用设置" is-link @click="goToGeneralSettings" />
      </u-cell-group>
    </view>

    <view class="settings-section">
      <u-cell-group>
        <u-cell title="清除缓存" :value="cacheSize" is-link @click="clearCache" />
        <u-cell title="检查更新" :value="version" is-link @click="checkUpdate" />
        <u-cell title="用户协议" is-link @click="showUserAgreement" />
        <u-cell title="隐私政策" is-link @click="showPrivacyPolicy" />
      </u-cell-group>
    </view>

    <view class="settings-section">
      <u-cell-group>
        <u-cell title="关于我们" is-link @click="showAbout" />
        <u-cell title="意见反馈" is-link @click="goToFeedback" />
        <u-cell title="联系客服" is-link @click="contactCustomerService" />
      </u-cell-group>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section">
      <u-button
        type="error"
        text="退出登录"
        size="large"
        @click="handleLogout"
      />
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const cacheSize = ref('0KB')
const version = ref('1.0.0')

onMounted(() => {
  getCacheSize()
  getAppVersion()
})

const getCacheSize = () => {
  // 获取缓存大小
  try {
    // #ifdef APP-PLUS
    plus.cache.calculate((size) => {
      cacheSize.value = formatFileSize(size)
    })
    // #endif
    
    // #ifndef APP-PLUS
    cacheSize.value = '0KB'
    // #endif
  } catch (error) {
    cacheSize.value = '0KB'
  }
}

const getAppVersion = () => {
  // 获取应用版本号
  try {
    // #ifdef APP-PLUS
    version.value = plus.runtime.version
    // #endif
    
    // #ifdef MP-WEIXIN
    const accountInfo = uni.getAccountInfoSync()
    version.value = accountInfo.miniProgram.version || '1.0.0'
    // #endif
    
    // #ifndef APP-PLUS && !MP-WEIXIN
    version.value = '1.0.0'
    // #endif
  } catch (error) {
    version.value = '1.0.0'
  }
}

const formatFileSize = (size) => {
  if (size < 1024) {
    return size + 'B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + 'KB'
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + 'MB'
  } else {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + 'GB'
  }
}

const goToNotificationSettings = () => {
  uni.navigateTo({
    url: '/pages/user/notification-settings'
  })
}

const goToPrivacySettings = () => {
  uni.navigateTo({
    url: '/pages/user/privacy-settings'
  })
}

const goToGeneralSettings = () => {
  uni.navigateTo({
    url: '/pages/user/general-settings'
  })
}

const clearCache = () => {
  uni.showModal({
    title: '提示',
    content: '确定要清除缓存吗？',
    success: (res) => {
      if (res.confirm) {
        try {
          // #ifdef APP-PLUS
          plus.cache.clear(() => {
            uni.showToast({
              title: '缓存清除成功',
              icon: 'success'
            })
            cacheSize.value = '0KB'
          })
          // #endif
          
          // #ifndef APP-PLUS
          uni.showToast({
            title: '缓存清除成功',
            icon: 'success'
          })
          cacheSize.value = '0KB'
          // #endif
        } catch (error) {
          uni.showToast({
            title: '清除失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

const checkUpdate = () => {
  uni.showToast({
    title: '当前已是最新版本',
    icon: 'success'
  })
}

const showUserAgreement = () => {
  uni.navigateTo({
    url: '/pages/user/user-agreement'
  })
}

const showPrivacyPolicy = () => {
  uni.navigateTo({
    url: '/pages/user/privacy-policy'
  })
}

const showAbout = () => {
  uni.navigateTo({
    url: '/pages/user/about'
  })
}

const goToFeedback = () => {
  uni.navigateTo({
    url: '/pages/user/feedback'
  })
}

const contactCustomerService = () => {
  // 联系客服
  uni.showActionSheet({
    itemList: ['拨打客服电话', '在线客服'],
    success: (res) => {
      if (res.tapIndex === 0) {
        // 拨打客服电话
        uni.makePhoneCall({
          phoneNumber: '400-123-4567'
        })
      } else {
        // 在线客服
        uni.showToast({
          title: '在线客服功能开发中...',
          icon: 'none'
        })
      }
    }
  })
}

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logoutAction()
        uni.showToast({
          title: '已退出登录',
          icon: 'success'
        })
        setTimeout(() => {
          uni.reLaunch({
            url: '/pages/user/login'
          })
        }, 1500)
      }
    }
  })
}
</script>

<style scoped lang="scss">
.settings-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx 0;
}

.settings-section {
  background: #fff;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.logout-section {
  padding: 40rpx 30rpx;
  background: #fff;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}
</style>