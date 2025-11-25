<template>
  <view class="login-container">
    <view class="login-header">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="title">欢迎登录</text>
      <text class="subtitle">火车订票系统</text>
    </view>

    <view class="login-tabs">
      <view 
        class="tab-item" 
        :class="{ active: loginType === 'phone' }"
        @click="loginType = 'phone'"
      >
        手机号登录
      </view>
      <view 
        class="tab-item" 
        :class="{ active: loginType === 'wechat' }"
        @click="loginType = 'wechat'"
      >
        微信登录
      </view>
    </view>

    <!-- 手机号登录 -->
    <view class="login-form" v-if="loginType === 'phone'">
      <u--form ref="formRef" :model="loginForm" :rules="rules">
        <u-form-item label="手机号" prop="phone" border-bottom>
          <u--input
            v-model="loginForm.phone"
            placeholder="请输入手机号"
            border="none"
            type="number"
            maxlength="11"
          />
        </u-form-item>

        <u-form-item label="密码" prop="password" border-bottom>
          <u--input
            v-model="loginForm.password"
            placeholder="请输入密码"
            border="none"
            type="password"
            password
          />
        </u-form-item>
      </u--form>

      <view class="form-options">
        <text class="forget-pwd" @click="goToForgetPwd">忘记密码？</text>
      </view>

      <u-button
        type="primary"
        text="登录"
        size="large"
        :loading="loading"
        @click="handleLogin"
      />

      <view class="register-link">
        <text>还没有账号？</text>
        <text class="link-text" @click="goToRegister">立即注册</text>
      </view>
    </view>

    <!-- 微信登录 -->
    <view class="wechat-login" v-if="loginType === 'wechat'">
      <view class="wechat-tips">
        <text>微信一键登录，安全快捷</text>
      </view>
      <u-button
        type="success"
        text="微信登录"
        size="large"
        :loading="wechatLoading"
        @click="handleWechatLogin"
      />
      <view class="wechat-notice">
        <text>点击"微信登录"即表示您同意</text>
        <text class="link-text" @click="showAgreement">《用户协议》</text>
        <text>和</text>
        <text class="link-text" @click="showPrivacy">《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const loginType = ref('phone')

const formRef = ref(null)
const loading = ref(false)
const wechatLoading = ref(false)

const loginForm = reactive({
  phone: '',
  password: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 20, message: '密码长度为6-20位' }
  ]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    const response = await userStore.loginAction({
      phone: loginForm.phone,
      password: loginForm.password
    })

    uni.showToast({
      title: '登录成功',
      icon: 'success'
    })

    setTimeout(() => {
      uni.switchTab({
        url: '/pages/index/index'
      })
    }, 1500)
  } catch (error) {
    if (error.message) {
      uni.showToast({
        title: error.message,
        icon: 'none'
      })
    }
  } finally {
    loading.value = false
  }
}

const handleWechatLogin = async () => {
  try {
    wechatLoading.value = true
    
    // #ifdef MP-WEIXIN
    // 微信小程序登录
    const wxLoginRes = await new Promise((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: resolve,
        fail: reject
      })
    })

    if (wxLoginRes.code) {
      // 获取用户信息
      const userInfoRes = await new Promise((resolve, reject) => {
        uni.getUserProfile({
          desc: '用于完善用户资料',
          success: resolve,
          fail: reject
        })
      })

      // 调用后端微信登录接口
      const response = await userStore.wechatLoginAction({
        code: wxLoginRes.code,
        userInfo: userInfoRes.userInfo
      })

      uni.showToast({
        title: '登录成功',
        icon: 'success'
      })

      setTimeout(() => {
        uni.switchTab({
          url: '/pages/index/index'
        })
      }, 1500)
    } else {
      throw new Error('微信登录失败')
    }
    // #endif

    // #ifndef MP-WEIXIN
    uni.showToast({
      title: '请在微信小程序中使用此功能',
      icon: 'none'
    })
    // #endif
  } catch (error) {
    uni.showToast({
      title: error.message || '微信登录失败',
      icon: 'none'
    })
  } finally {
    wechatLoading.value = false
  }
}

const goToRegister = () => {
  uni.navigateTo({
    url: '/pages/user/register'
  })
}

const goToForgetPwd = () => {
  uni.showToast({
    title: '功能开发中...',
    icon: 'none'
  })
}

const showAgreement = () => {
  uni.showToast({
    title: '用户协议',
    icon: 'none'
  })
}

const showPrivacy = () => {
  uni.showToast({
    title: '隐私政策',
    icon: 'none'
  })
}
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60rpx 40rpx;
}

.login-header {
  text-align: center;
  margin-bottom: 60rpx;

  .logo {
    width: 120rpx;
    height: 120rpx;
    margin-bottom: 20rpx;
  }

  .title {
    display: block;
    font-size: 48rpx;
    font-weight: bold;
    color: #fff;
    margin-bottom: 10rpx;
  }

  .subtitle {
    display: block;
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.8);
  }
}

.login-tabs {
  display: flex;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20rpx;
  padding: 10rpx;
  margin-bottom: 40rpx;

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 20rpx;
    border-radius: 15rpx;
    color: rgba(255, 255, 255, 0.8);
    font-size: 28rpx;
    transition: all 0.3s ease;

    &.active {
      background: #fff;
      color: #333;
      box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
    }
  }
}

.login-form {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);

  .form-options {
    display: flex;
    justify-content: flex-end;
    margin: 20rpx 0 40rpx;

    .forget-pwd {
      font-size: 26rpx;
      color: #2979ff;
    }
  }

  .register-link {
    text-align: center;
    margin-top: 40rpx;
    font-size: 28rpx;
    color: #666;

    .link-text {
      color: #2979ff;
      margin-left: 10rpx;
    }
  }
}

.wechat-login {
  background: #fff;
  border-radius: 20rpx;
  padding: 80rpx 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
  text-align: center;

  .wechat-tips {
    margin-bottom: 60rpx;
    font-size: 32rpx;
    color: #666;
  }

  .wechat-notice {
    margin-top: 40rpx;
    font-size: 24rpx;
    color: #999;
    line-height: 1.6;

    .link-text {
      color: #2979ff;
    }
  }
}
</style>