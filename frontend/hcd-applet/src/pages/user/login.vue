<template>
  <view class="login-container">
    <view class="login-header">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="title">欢迎登录</text>
      <text class="subtitle">火车订票系统</text>
    </view>

    <view class="login-form">
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
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

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
</script>

<style lang="scss" scoped>
.login-container {
  padding: 40rpx;
  background-color: #f8f8f8;
  min-height: 100vh;
}

.login-header {
  text-align: center;
  margin-bottom: 80rpx;
  padding-top: 60rpx;

  .logo {
    width: 120rpx;
    height: 120rpx;
    margin: 0 auto 30rpx;
  }

  .title {
    display: block;
    font-size: 48rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 10rpx;
  }

  .subtitle {
    display: block;
    font-size: 28rpx;
    color: #999;
  }
}

.login-form {
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);

  .form-options {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 40rpx;

    .forget-pwd {
      font-size: 26rpx;
      color: #2979ff;
    }
  }

  .register-link {
    text-align: center;
    margin-top: 40rpx;
    font-size: 26rpx;
    color: #666;

    .link-text {
      color: #2979ff;
      margin-left: 10rpx;
    }
  }
}
</style>