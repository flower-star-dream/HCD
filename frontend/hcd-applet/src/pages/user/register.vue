<template>
  <view class="register-container">
    <view class="register-header">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="title">欢迎注册</text>
      <text class="subtitle">火车订票系统</text>
    </view>

    <view class="register-form">
      <u--form ref="formRef" :model="registerForm" :rules="rules">
        <u-form-item label="手机号" prop="phone" border-bottom>
          <u--input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            border="none"
            type="number"
            maxlength="11"
          />
        </u-form-item>

        <u-form-item label="验证码" prop="code" border-bottom>
          <view class="code-input-wrapper">
            <u--input
              v-model="registerForm.code"
              placeholder="请输入验证码"
              border="none"
              type="number"
              maxlength="6"
            />
            <u-button
              type="primary"
              size="mini"
              :disabled="codeSending || countdown > 0"
              @click="sendCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </u-button>
          </view>
        </u-form-item>

        <u-form-item label="密码" prop="password" border-bottom>
          <u--input
            v-model="registerForm.password"
            placeholder="请输入密码"
            border="none"
            type="password"
            password
          />
        </u-form-item>

        <u-form-item label="确认密码" prop="confirmPassword" border-bottom>
          <u--input
            v-model="registerForm.confirmPassword"
            placeholder="请再次输入密码"
            border="none"
            type="password"
            password
          />
        </u-form-item>

        <u-form-item label="昵称" prop="nickname" border-bottom>
          <u--input
            v-model="registerForm.nickname"
            placeholder="请输入昵称"
            border="none"
            maxlength="20"
          />
        </u-form-item>
      </u--form>

      <u-button
        type="primary"
        text="注册"
        size="large"
        :loading="loading"
        @click="handleRegister"
      />

      <view class="login-link">
        <text>已有账号？</text>
        <text class="link-text" @click="goToLogin">立即登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { register } from '@/api/user'
import { useUserStore } from '@/store/user'

const formRef = ref(null)
const loading = ref(false)
const codeSending = ref(false)
const countdown = ref(0)

const registerForm = reactive({
  phone: '',
  code: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
  ],
  code: [
    { required: true, message: '请输入验证码' },
    { pattern: /^\d{6}$/, message: '验证码为6位数字' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 20, message: '密码长度为6-20位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }
    }
  ],
  nickname: [
    { required: true, message: '请输入昵称' },
    { max: 20, message: '昵称长度不能超过20位' }
  ]
}

const sendCode = async () => {
  try {
    // 验证手机号格式
    const phoneValid = /^1[3-9]\d{9}$/.test(registerForm.phone)
    if (!phoneValid) {
      uni.showToast({
        title: '请输入正确的手机号',
        icon: 'none'
      })
      return
    }

    codeSending.value = true
    
    // 调用发送验证码接口
    // await sendSmsCode({
    //   phone: registerForm.phone,
    //   type: 'register'
    // })

    uni.showToast({
      title: '验证码已发送',
      icon: 'success'
    })

    // 开始倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    uni.showToast({
      title: error.message || '发送失败',
      icon: 'none'
    })
  } finally {
    codeSending.value = false
  }
}

const handleRegister = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    const response = await register({
      phone: registerForm.phone,
      code: registerForm.code,
      password: registerForm.password,
      nickname: registerForm.nickname
    })

    uni.showToast({
      title: '注册成功',
      icon: 'success'
    })

    // 注册成功后自动登录
    const userStore = useUserStore()
    await userStore.loginAction({
      phone: registerForm.phone,
      password: registerForm.password
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

const goToLogin = () => {
  uni.navigateBack()
}
</script>

<style scoped lang="scss">
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60rpx 40rpx;
}

.register-header {
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

.register-form {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);

  .code-input-wrapper {
    display: flex;
    align-items: center;
    gap: 20rpx;

    .u-button {
      min-width: 180rpx;
    }
  }

  .login-link {
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
</style>