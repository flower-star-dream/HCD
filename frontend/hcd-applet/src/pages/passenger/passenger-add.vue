<template>
  <view class="passenger-add-container">
    <u--form ref="formRef" :model="passengerForm" :rules="rules" label-width="160rpx">
      <u-form-item label="姓名" prop="realName" border-bottom>
        <u--input
          v-model="passengerForm.realName"
          placeholder="请输入乘客真实姓名"
          border="none"
          maxlength="20"
        />
      </u-form-item>

      <u-form-item label="证件类型" prop="cardType" border-bottom @click="showCardTypePicker = true">
        <view class="picker-wrapper">
          <text>{{ getCardTypeText(passengerForm.cardType) }}</text>
          <u-icon name="arrow-right" color="#999" size="24" />
        </view>
      </u-form-item>

      <u-form-item label="证件号码" prop="idCard" border-bottom>
        <u--input
          v-model="passengerForm.idCard"
          placeholder="请输入证件号码"
          border="none"
          maxlength="18"
        />
      </u-form-item>

      <u-form-item label="手机号" prop="phone" border-bottom>
        <u--input
          v-model="passengerForm.phone"
          placeholder="请输入手机号"
          border="none"
          type="number"
          maxlength="11"
        />
      </u-form-item>

      <u-form-item label="乘客类型" prop="passengerType" border-bottom @click="showPassengerTypePicker = true">
        <view class="picker-wrapper">
          <text>{{ getPassengerTypeText(passengerForm.passengerType) }}</text>
          <u-icon name="arrow-right" color="#999" size="24" />
        </view>
      </u-form-item>

      <u-form-item label="设为默认" border-bottom>
        <view class="switch-wrapper">
          <u-switch v-model="passengerForm.isDefault" />
        </view>
      </u-form-item>
    </u--form>

    <view class="tips-section">
      <text class="tips-title">温馨提示：</text>
      <view class="tips-content">
        <text>• 请确保填写的姓名与证件上的姓名完全一致</text>
        <text>• 证件号码必须真实有效，否则可能影响购票</text>
        <text>• 手机号用于接收订单通知，请保持畅通</text>
      </view>
    </view>

    <view class="submit-section">
      <u-button
        type="primary"
        text="保存"
        size="large"
        :loading="loading"
        @click="handleSubmit"
      />
    </view>

    <!-- 证件类型选择器 -->
    <u-picker
      :show="showCardTypePicker"
      :columns="cardTypeColumns"
      @confirm="onCardTypeConfirm"
      @cancel="showCardTypePicker = false"
    />

    <!-- 乘客类型选择器 -->
    <u-picker
      :show="showPassengerTypePicker"
      :columns="passengerTypeColumns"
      @confirm="onPassengerTypeConfirm"
      @cancel="showPassengerTypePicker = false"
    />
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { addPassenger } from '@/api/user'

const formRef = ref(null)
const loading = ref(false)
const showCardTypePicker = ref(false)
const showPassengerTypePicker = ref(false)

const passengerForm = reactive({
  realName: '',
  cardType: 'ID_CARD',
  idCard: '',
  phone: '',
  passengerType: 'ADULT',
  isDefault: false
})

const rules = {
  realName: [
    { required: true, message: '请输入乘客姓名' },
    { max: 20, message: '姓名长度不能超过20位' },
    { 
      pattern: /^[\u4e00-\u9fa5·]+$/,
      message: '姓名只能包含中文和·符号'
    }
  ],
  cardType: [
    { required: true, message: '请选择证件类型' }
  ],
  idCard: [
    { required: true, message: '请输入证件号码' },
    { 
      validator: (rule, value, callback) => {
        if (passengerForm.cardType === 'ID_CARD') {
          const idCardRegex = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
          if (!idCardRegex.test(value)) {
            callback(new Error('请输入正确的身份证号'))
            return
          }
        } else if (passengerForm.cardType === 'PASSPORT') {
          const passportRegex = /^[a-zA-Z0-9]{5,17}$/
          if (!passportRegex.test(value)) {
            callback(new Error('请输入正确的护照号码'))
            return
          }
        }
        callback()
      }
    }
  ],
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
  ],
  passengerType: [
    { required: true, message: '请选择乘客类型' }
  ]
}

const cardTypeColumns = [[
  { label: '身份证', value: 'ID_CARD' },
  { label: '护照', value: 'PASSPORT' },
  { label: '港澳通行证', value: 'HK_MACAO_PASS' },
  { label: '台湾通行证', value: 'TAIWAN_PASS' }
]]

const passengerTypeColumns = [[
  { label: '成人', value: 'ADULT' },
  { label: '儿童', value: 'CHILD' },
  { label: '学生', value: 'STUDENT' },
  { label: '军人', value: 'SOLDIER' }
]]

const getCardTypeText = (cardType) => {
  const typeMap = {
    'ID_CARD': '身份证',
    'PASSPORT': '护照',
    'HK_MACAO_PASS': '港澳通行证',
    'TAIWAN_PASS': '台湾通行证'
  }
  return typeMap[cardType] || '请选择'
}

const getPassengerTypeText = (passengerType) => {
  const typeMap = {
    'ADULT': '成人',
    'CHILD': '儿童',
    'STUDENT': '学生',
    'SOLDIER': '军人'
  }
  return typeMap[passengerType] || '请选择'
}

const onCardTypeConfirm = (e) => {
  passengerForm.cardType = e.value[0].value
  showCardTypePicker.value = false
  // 清空证件号码
  passengerForm.idCard = ''
}

const onPassengerTypeConfirm = (e) => {
  passengerForm.passengerType = e.value[0].value
  showPassengerTypePicker.value = false
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    await addPassenger(passengerForm)

    uni.showToast({
      title: '添加成功',
      icon: 'success'
    })

    // 返回上一页并刷新
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
.passenger-add-container {
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

.switch-wrapper {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.tips-section {
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