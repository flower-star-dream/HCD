<template>
  <view class="feedback-container">
    <u--form ref="formRef" :model="feedbackForm" :rules="rules" label-width="160rpx">
      <u-form-item label="反馈类型" prop="type" border-bottom @click="showTypePicker = true">
        <view class="picker-wrapper">
          <text>{{ getTypeText(feedbackForm.type) }}</text>
          <u-icon name="arrow-right" color="#999" size="24" />
        </view>
      </u-form-item>

      <u-form-item label="反馈内容" prop="content" border-bottom>
        <u--textarea
          v-model="feedbackForm.content"
          placeholder="请详细描述您遇到的问题或建议..."
          border="none"
          maxlength="500"
          height="200"
          count
        />
      </u-form-item>

      <u-form-item label="联系方式" prop="contact" border-bottom>
        <u--input
          v-model="feedbackForm.contact"
          placeholder="手机号或邮箱（选填）"
          border="none"
          maxlength="50"
        />
      </u-form-item>

      <u-form-item label="上传图片" prop="images">
        <view class="image-upload">
          <u-upload
            :fileList="feedbackForm.images"
            @afterRead="afterRead"
            @delete="deleteImage"
            multiple
            :maxCount="5"
            width="160"
            height="160"
          />
        </view>
      </u-form-item>
    </u--form>

    <view class="submit-section">
      <u-button
        type="primary"
        text="提交反馈"
        size="large"
        :loading="loading"
        @click="handleSubmit"
      />
    </view>

    <!-- 反馈类型选择器 -->
    <u-picker
      :show="showTypePicker"
      :columns="typeColumns"
      @confirm="onTypeConfirm"
      @cancel="showTypePicker = false"
    />
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'

const formRef = ref(null)
const loading = ref(false)
const showTypePicker = ref(false)

const feedbackForm = reactive({
  type: 'BUG',
  content: '',
  contact: '',
  images: []
})

const rules = {
  type: [
    { required: true, message: '请选择反馈类型' }
  ],
  content: [
    { required: true, message: '请输入反馈内容' },
    { min: 10, message: '反馈内容不能少于10个字' }
  ],
  contact: [
    {
      validator: (rule, value, callback) => {
        if (value && !/^1[3-9]\d{9}$/.test(value) && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
          callback(new Error('请输入正确的手机号或邮箱'))
        } else {
          callback()
        }
      }
    }
  ]
}

const typeColumns = [[
  { label: '功能建议', value: 'FEATURE' },
  { label: '问题反馈', value: 'BUG' },
  { label: '界面优化', value: 'UI' },
  { label: '性能问题', value: 'PERFORMANCE' },
  { label: '其他', value: 'OTHER' }
]]

const getTypeText = (type) => {
  const typeMap = {
    'FEATURE': '功能建议',
    'BUG': '问题反馈',
    'UI': '界面优化',
    'PERFORMANCE': '性能问题',
    'OTHER': '其他'
  }
  return typeMap[type] || '请选择'
}

const onTypeConfirm = (e) => {
  feedbackForm.type = e.value[0].value
  showTypePicker.value = false
}

const afterRead = (event) => {
  const { file } = event
  // 上传图片
  uploadImage(file).then(url => {
    feedbackForm.images.push({
      url: url,
      status: 'success',
      message: ''
    })
  }).catch(() => {
    uni.showToast({
      title: '图片上传失败',
      icon: 'none'
    })
  })
}

const deleteImage = (event) => {
  const { index } = event
  feedbackForm.images.splice(index, 1)
}

const uploadImage = (file) => {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: 'http://localhost:8080/user/upload-feedback-image',
      filePath: file.url,
      name: 'file',
      header: {
        Authorization: `Bearer ${uni.getStorageSync('token')}`
      },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data.data.url)
        } else {
          reject(new Error(data.message || '上传失败'))
        }
      },
      fail: reject
    })
  })
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    // 提交反馈
    await submitFeedback({
      type: feedbackForm.type,
      content: feedbackForm.content,
      contact: feedbackForm.contact,
      images: feedbackForm.images.map(item => item.url)
    })

    uni.showToast({
      title: '反馈提交成功',
      icon: 'success'
    })

    // 清空表单
    feedbackForm.content = ''
    feedbackForm.contact = ''
    feedbackForm.images = []

    // 返回上一页
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

const submitFeedback = (data) => {
  // 模拟提交反馈
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve()
    }, 1000)
  })
}
</script>

<style scoped lang="scss">
.feedback-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.picker-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  color: #333;
}

.image-upload {
  width: 100%;
}

.submit-section {
  margin-top: 60rpx;
  padding: 0 20rpx;
}
</style>