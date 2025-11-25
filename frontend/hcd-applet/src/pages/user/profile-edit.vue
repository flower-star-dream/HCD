<template>
  <view class="profile-edit-container">
    <u--form ref="formRef" :model="profileForm" :rules="rules" label-width="160rpx">
      <u-form-item label="头像" prop="avatar" @click="changeAvatar">
        <view class="avatar-wrapper">
          <u-avatar 
            :src="profileForm.avatar || '/static/default-avatar.png'" 
            size="80" 
            mode="aspectFill"
          />
          <u-icon name="arrow-right" color="#999" size="24" />
        </view>
      </u-form-item>

      <u-form-item label="昵称" prop="nickname" border-bottom>
        <u--input
          v-model="profileForm.nickname"
          placeholder="请输入昵称"
          border="none"
          maxlength="20"
        />
      </u-form-item>

      <u-form-item label="真实姓名" prop="realName" border-bottom>
        <u--input
          v-model="profileForm.realName"
          placeholder="请输入真实姓名"
          border="none"
          maxlength="20"
        />
      </u-form-item>

      <u-form-item label="身份证号" prop="idCard" border-bottom>
        <u--input
          v-model="profileForm.idCard"
          placeholder="请输入身份证号"
          border="none"
          maxlength="18"
        />
      </u-form-item>

      <u-form-item label="邮箱" prop="email" border-bottom>
        <u--input
          v-model="profileForm.email"
          placeholder="请输入邮箱地址"
          border="none"
          maxlength="50"
        />
      </u-form-item>

      <u-form-item label="性别" prop="gender" border-bottom @click="showGenderPicker = true">
        <view class="picker-wrapper">
          <text>{{ genderText }}</text>
          <u-icon name="arrow-right" color="#999" size="24" />
        </view>
      </u-form-item>

      <u-form-item label="生日" prop="birthday" border-bottom @click="showDatePicker = true">
        <view class="picker-wrapper">
          <text>{{ profileForm.birthday || '请选择生日' }}</text>
          <u-icon name="arrow-right" color="#999" size="24" />
        </view>
      </u-form-item>
    </u--form>

    <view class="submit-section">
      <u-button
        type="primary"
        text="保存"
        size="large"
        :loading="loading"
        @click="handleSubmit"
      />
    </view>

    <!-- 性别选择器 -->
    <u-picker
      :show="showGenderPicker"
      :columns="genderColumns"
      @confirm="onGenderConfirm"
      @cancel="showGenderPicker = false"
    />

    <!-- 日期选择器 -->
    <u-datetime-picker
      :show="showDatePicker"
      v-model="birthdayValue"
      mode="date"
      :max-date="maxDate"
      @confirm="onDateConfirm"
      @cancel="showDatePicker = false"
    />
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { updateUserInfo } from '@/api/user'

const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const showGenderPicker = ref(false)
const showDatePicker = ref(false)
const birthdayValue = ref('')

const profileForm = reactive({
  avatar: '',
  nickname: '',
  realName: '',
  idCard: '',
  email: '',
  gender: '',
  birthday: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称' },
    { max: 20, message: '昵称长度不能超过20位' }
  ],
  realName: [
    { max: 20, message: '姓名长度不能超过20位' }
  ],
  idCard: [
    { 
      pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,
      message: '请输入正确的身份证号'
    }
  ],
  email: [
    { 
      pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: '请输入正确的邮箱地址'
    }
  ]
}

const genderColumns = [['男', '女', '保密']]

const genderText = computed(() => {
  const genderMap = {
    '1': '男',
    '2': '女',
    '0': '保密'
  }
  return genderMap[profileForm.gender] || '请选择'
})

const maxDate = computed(() => {
  const now = new Date()
  return now.getTime()
})

onMounted(() => {
  // 初始化表单数据
  if (userStore.userInfo) {
    Object.keys(profileForm).forEach(key => {
      if (userStore.userInfo[key] !== undefined) {
        profileForm[key] = userStore.userInfo[key]
      }
    })
    if (profileForm.birthday) {
      birthdayValue.value = profileForm.birthday
    }
  }
})

const changeAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      try {
        // 上传头像
        const uploadRes = await uploadAvatar(res.tempFilePaths[0])
        profileForm.avatar = uploadRes.url

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

const onGenderConfirm = (e) => {
  const genderMap = {
    '男': '1',
    '女': '2',
    '保密': '0'
  }
  profileForm.gender = genderMap[e.value[0]]
  showGenderPicker.value = false
}

const onDateConfirm = (e) => {
  const date = new Date(e.value)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  profileForm.birthday = `${year}-${month}-${day}`
  showDatePicker.value = false
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    await updateUserInfo(profileForm)

    // 更新本地用户信息
    await userStore.initUserInfo()

    uni.showToast({
      title: '保存成功',
      icon: 'success'
    })

    setTimeout(() => {
      uni.navigateBack()
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
.profile-edit-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.picker-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  color: #333;
}

.submit-section {
  margin-top: 60rpx;
  padding: 0 20rpx;
}
</style>