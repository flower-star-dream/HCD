<template>
  <view class="change-password-container">
    <u--form ref="formRef" :model="passwordForm" :rules="rules" label-width="180rpx">
      <u-form-item label="当前密码" prop="oldPassword" border-bottom>
        <u--input
          v-model="passwordForm.oldPassword"
          placeholder="请输入当前密码"
          border="none"
          type="password"
          password
        />
      </u-form-item>

      <u-form-item label="新密码" prop="newPassword" border-bottom>
        <u--input
          v-model="passwordForm.newPassword"
          placeholder="请输入新密码"
          border="none"
          type="password"
          password
        />
      </u-form-item>

      <u-form-item label="确认新密码" prop="confirmPassword" border-bottom>
        <u--input
          v-model="passwordForm.confirmPassword"
          placeholder="请再次输入新密码"
          border="none"
          type="password"
          password
        />
      </u-form-item>
    </u--form>

    <view class="password-tips">
      <text class="tips-title">密码设置要求：</text>
      <view class="tips-content">
        <text>• 密码长度为6-20个字符</text>
        <text>• 建议使用字母、数字和符号组合</text>
        <text>• 不要使用过于简单的密码</text>
      </view>
    </view>

    <view class="submit-section">
      <u-button
        type="primary"
        text="确认修改"
        size="large"
        :loading="loading"
        @click="handleSubmit"
      />
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { changePassword } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  oldPassword: [
    { required: true, message: '请输入当前密码' },
    { min: 6, max: 20, message: '密码长度为6-20位' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, max: 20, message: '密码长度为6-20位' },
    {
      validator: (rule, value, callback) => {
        if (value === passwordForm.oldPassword) {
          callback(new Error('新密码不能与旧密码相同'))
        } else {
          callback()
        }
      }
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }
    }
  ]
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    uni.showToast({
      title: '密码修改成功',
      icon: 'success'
    })

    // 退出登录，重新登录
    setTimeout(() => {
      userStore.logoutAction()
      uni.reLaunch({
        url: '/pages/user/login'
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
</script>

<style scoped lang="scss">
.change-password-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.password-tips {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin: 20rpx 0;

  .tips-title {
    display: block;
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }

  .tips-content {
    display: flex;
    flex-direction: column;
    gap: 10rpx;

    text {
      font-size: 26rpx;
      color: #666;
      line-height: 1.6;
    }
  }
}

.submit-section {
  margin-top: 60rpx;
  padding: 0 20rpx;
}
</style>