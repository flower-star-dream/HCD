<template>
  <view class="order-confirm-container">
    <!-- 车次信息 -->
    <view class="train-info-section">
      <view class="train-header">
        <text class="train-number">{{ trainInfo.trainNumber }}</text>
        <text class="train-type">{{ trainInfo.trainType }}</text>
      </view>
      <view class="route-info">
        <view class="station-info">
          <text class="station-name">{{ trainInfo.startStation }}</text>
          <text class="departure-time">{{ formatTime(trainInfo.startTime) }}</text>
        </view>
        <view class="route-arrow">
          <u-icon name="arrow-right" color="#2979ff" size="32" />
          <text class="duration">{{ trainInfo.duration }}</text>
        </view>
        <view class="station-info">
          <text class="station-name">{{ trainInfo.endStation }}</text>
          <text class="arrival-time">{{ formatTime(trainInfo.endTime) }}</text>
        </view>
      </view>
    </view>

    <!-- 乘客信息 -->
    <view class="passenger-section">
      <view class="section-title">乘客信息</view>
      <view class="passenger-list">
        <view 
          v-for="(passenger, index) in passengers" 
          :key="index"
          class="passenger-item"
        >
          <view class="passenger-info">
            <text class="name">{{ passenger.realName }}</text>
            <text class="id-card">{{ passenger.cardType }} {{ formatIdCard(passenger.idCard) }}</text>
          </view>
          <view class="seat-info" v-if="seatNumbers[index]">
            <text class="seat-type">{{ seatTypeName }}</text>
            <text class="seat-number">{{ seatNumbers[index] }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 费用明细 -->
    <view class="fee-section">
      <view class="section-title">费用明细</view>
      <u-cell-group>
        <u-cell title="票价">
          <template #value>
            <text class="price">¥{{ unitPrice }}</text>
          </template>
        </u-cell>
        <u-cell title="数量" :value="`${passengers.length}人`" />
        <u-cell title="总计">
          <template #value>
            <text class="total-price">¥{{ totalPrice }}</text>
          </template>
        </u-cell>
      </u-cell-group>
    </view>

    <!-- 联系人信息 -->
    <view class="contact-section">
      <view class="section-title">联系人信息</view>
      <u--form ref="formRef" :model="contactForm" :rules="rules" label-width="160rpx">
        <u-form-item label="联系人" prop="contactName" border-bottom>
          <u--input
            v-model="contactForm.contactName"
            placeholder="请输入联系人姓名"
            border="none"
            maxlength="20"
          />
        </u-form-item>
        <u-form-item label="手机号" prop="contactPhone" border-bottom>
          <u--input
            v-model="contactForm.contactPhone"
            placeholder="请输入手机号"
            border="none"
            type="number"
            maxlength="11"
          />
        </u-form-item>
        <u-form-item label="备注" prop="remarks" border-bottom>
          <u--input
            v-model="contactForm.remarks"
            placeholder="选填"
            border="none"
            maxlength="100"
          />
        </u-form-item>
      </u--form>
    </view>

    <!-- 底部操作 -->
    <view class="bottom-actions">
      <view class="total-info">
        <text class="total-label">总计：</text>
        <text class="total-price">¥{{ totalPrice }}</text>
      </view>
      <u-button
        type="primary"
        text="提交订单"
        size="large"
        :loading="submitting"
        @click="submitOrder"
      />
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { createOrder } from '@/api/order'

const trainInfo = ref({})
const passengers = ref([])
const seatType = ref('')
const seatNumbers = ref([])
const unitPrice = ref('0.00')
const submitting = ref(false)

const formRef = ref(null)
const contactForm = reactive({
  contactName: '',
  contactPhone: '',
  remarks: ''
})

const rules = {
  contactName: [
    { required: true, message: '请输入联系人姓名' },
    { max: 20, message: '姓名长度不能超过20位' }
  ],
  contactPhone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
  ]
}

onMounted(() => {
  loadOrderData()
})

const loadOrderData = () => {
  // 从本地存储获取订单数据
  const orderData = uni.getStorageSync('orderData')
  if (orderData) {
    trainInfo.value = orderData.trainInfo || {}
    passengers.value = orderData.passengers || []
    seatType.value = orderData.seatType || ''
    seatNumbers.value = orderData.seatNumbers || []
    unitPrice.value = orderData.unitPrice || '0.00'
  } else {
    // 模拟数据
    trainInfo.value = {
      trainNumber: 'G1234',
      trainType: '高铁',
      startStation: '北京南',
      endStation: '上海虹桥',
      startTime: '2024-01-15T08:00:00',
      endTime: '2024-01-15T12:30:00',
      duration: '4小时30分'
    }
    
    passengers.value = [
      {
        realName: '张三',
        cardType: '身份证',
        idCard: '110101199001011234'
      }
    ]
    
    seatType.value = 'SECOND_CLASS'
    unitPrice.value = '553.00'
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatIdCard = (idCard) => {
  if (!idCard) return ''
  if (idCard.length === 18) {
    return idCard.substring(0, 4) + '**********' + idCard.substring(14)
  }
  return idCard
}

const seatTypeName = computed(() => {
  const typeMap = {
    'BUSINESS': '商务座',
    'FIRST_CLASS': '一等座',
    'SECOND_CLASS': '二等座',
    'HARD_SEAT': '硬座',
    'SOFT_SEAT': '软座',
    'HARD_SLEEPER': '硬卧',
    'SOFT_SLEEPER': '软卧'
  }
  return typeMap[seatType.value] || '未知座位'
})

const totalPrice = computed(() => {
  const total = parseFloat(unitPrice.value) * passengers.value.length
  return total.toFixed(2)
})

const submitOrder = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true

    const orderData = {
      trainId: trainInfo.value.id,
      passengers: passengers.value.map((passenger, index) => ({
        passengerId: passenger.id,
        realName: passenger.realName,
        idCard: passenger.idCard,
        cardType: passenger.cardType,
        seatType: seatType.value,
        seatNumber: seatNumbers.value[index] || null
      })),
      contactName: contactForm.contactName,
      contactPhone: contactForm.contactPhone,
      remarks: contactForm.remarks,
      totalPrice: totalPrice.value
    }

    const response = await createOrder(orderData)

    uni.showToast({
      title: '订单提交成功',
      icon: 'success'
    })

    // 清除本地数据
    uni.removeStorageSync('orderData')
    uni.removeStorageSync('selectedPassengers')

    // 跳转到支付页面
    setTimeout(() => {
      uni.redirectTo({
        url: `/pages/order/payment?orderId=${response.orderId}`
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
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.order-confirm-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

.train-info-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
  color: #fff;
  border-radius: 0 0 30rpx 30rpx;
  margin-bottom: 20rpx;

  .train-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;

    .train-number {
      font-size: 48rpx;
      font-weight: bold;
    }

    .train-type {
      font-size: 28rpx;
      opacity: 0.9;
    }
  }

  .route-info {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .station-info {
      display: flex;
      flex-direction: column;
      align-items: center;
      flex: 1;

      .station-name {
        font-size: 32rpx;
        font-weight: bold;
        margin-bottom: 10rpx;
      }

      .departure-time, .arrival-time {
        font-size: 28rpx;
        opacity: 0.9;
      }
    }

    .route-arrow {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0 20rpx;

      .duration {
        font-size: 24rpx;
        opacity: 0.8;
        margin-top: 10rpx;
      }
    }
  }
}

.passenger-section,
.fee-section,
.contact-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin: 20rpx 0;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }
}

.passenger-section {
  .passenger-list {
    .passenger-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .passenger-info {
        display: flex;
        flex-direction: column;
        gap: 8rpx;

        .name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
        }

        .id-card {
          font-size: 24rpx;
          color: #666;
        }
      }

      .seat-info {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        gap: 8rpx;

        .seat-type {
          font-size: 24rpx;
          color: #666;
        }

        .seat-number {
          font-size: 28rpx;
          font-weight: bold;
          color: #2979ff;
        }
      }
    }
  }
}

.fee-section {
  .price {
    color: #f56c6c;
    font-weight: bold;
  }

  .total-price {
    color: #f56c6c;
    font-size: 36rpx;
    font-weight: bold;
  }
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 30rpx;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;

  .total-info {
    display: flex;
    align-items: center;
    gap: 10rpx;

    .total-label {
      font-size: 28rpx;
      color: #666;
    }

    .total-price {
      font-size: 40rpx;
      font-weight: bold;
      color: #f56c6c;
    }
  }
}
</style>