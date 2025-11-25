<template>
  <view class="profile-container">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="avatar-section">
        <u-avatar 
          :src="userInfo?.avatar || '/static/default-avatar.png'" 
          size="120" 
          mode="aspectFill"
          @click="changeAvatar"
        />
        <view class="user-info">
          <text class="nickname">{{ userInfo?.nickname || '未设置昵称' }}</text>
          <text class="phone">{{ userInfo?.phone || '未绑定手机号' }}</text>
        </view>
      </view>
      <u-icon name="arrow-right" color="#999" size="24" @click="goToProfileEdit" />
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <u-cell-group>
        <u-cell
          title="我的订单"
          icon="order"
          is-link
          @click="goToOrderList"
        />
        <u-cell
          title="常用乘客"
          icon="account"
          is-link
          @click="goToPassengerList"
        />
        <u-cell
          title="我的车票"
          icon="coupon"
          is-link
          @click="goToMyTickets"
        />
        <u-cell
          title="个人资料"
          icon="edit-pen"
          is-link
          @click="goToProfileEdit"
        />
        <u-cell
          title="修改密码"
          icon="lock"
          is-link
          @click="goToChangePassword"
        />
      </u-cell-group>
    </view>

    <!-- 设置和帮助 -->
    <view class="menu-section">
      <u-cell-group>
        <u-cell
          title="设置"
          icon="setting"
          is-link
          @click="goToSettings"
        />
        <u-cell
          title="帮助中心"
          icon="question-circle"
          is-link
          @click="goToHelp"
        />
        <u-cell
          title="意见反馈"
          icon="edit-pen"
          is-link
          @click="goToFeedback"
        />
        <u-cell
          title="关于我们"
          icon="info-circle"
          is-link
          @click="goToAbout"
        />
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
import { computed } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

const changeAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      try {
        // 上传头像
        const uploadRes = await uploadAvatar(res.tempFilePaths[0])
        
        // 更新用户信息
        await userStore.updateUserInfo({
          avatar: uploadRes.url
        })

        uni.showToast({
          title: '头像更新成功',
          icon: 'success'
        })
      } catch (error) {
        uni.showToast({
          title: '头像更新失败',
          icon: 'none'
        })
      }
    }
  })
}

const uploadAvatar = (filePath) => {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: 'http://localhost:8080/user/upload-avatar',
      filePath,
      name: 'file',
      header: {
        Authorization: `Bearer ${userStore.token}`
      },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data.data)
        } else {
          reject(new Error(data.message || '上传失败'))
        }
      },
      fail: reject
    })
  })
}

const goToOrderList = () => {
  uni.switchTab({
    url: '/pages/order/order-list'
  })
}

const goToPassengerList = () => {
  uni.navigateTo({
    url: '/pages/passenger/passenger-list'
  })
}

const goToMyTickets = () => {
  uni.navigateTo({
    url: '/pages/ticket/ticket-list'
  })
}

const goToProfileEdit = () => {
  uni.navigateTo({
    url: '/pages/user/profile-edit'
  })
}

const goToChangePassword = () => {
  uni.navigateTo({
    url: '/pages/user/change-password'
  })
}

const goToSettings = () => {
  uni.navigateTo({
    url: '/pages/user/settings'
  })
}

const goToHelp = () => {
  uni.showToast({
    title: '帮助中心',
    icon: 'none'
  })
}

const goToFeedback = () => {
  uni.navigateTo({
    url: '/pages/user/feedback'
  })
}

const goToAbout = () => {
  uni.navigateTo({
    url: '/pages/user/about'
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
.profile-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

.user-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: 0 0 30rpx 30rpx;

  .avatar-section {
    display: flex;
    align-items: center;

    .user-info {
      margin-left: 30rpx;
      display: flex;
      flex-direction: column;

      .nickname {
        font-size: 36rpx;
        font-weight: bold;
        color: #fff;
        margin-bottom: 8rpx;
      }

      .phone {
        font-size: 28rpx;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

.menu-section {
  margin: 20rpx 0;
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.logout-section {
  padding: 40rpx;
  background: #fff;
  border-radius: 20rpx;
  margin: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}
</style>