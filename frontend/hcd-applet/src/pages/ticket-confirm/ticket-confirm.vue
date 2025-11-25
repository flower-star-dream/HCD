<template>
  <view class="ticket-confirm-container">
    <!-- 车次信息卡片 -->
    <view class="train-info-card">
      <view class="train-header">
        <text class="train-number">{{ trainInfo.trainNumber }}</text>
        <text class="train-type">{{ getTrainType(trainInfo.trainModel) }}</text>
      </view>
      <view class="train-route">
        <view class="route-station">
          <text class="station-time">{{ formatTime(trainInfo.startTime) }}</text>
          <text class="station-name">{{ trainInfo.startStation }}</text>
          <text class="station-date">{{ formatDate(trainInfo.startTime) }}</text>
        </view>
        <view class="route-arrow">
          <view class="route-duration">{{ formatDuration(trainInfo.startTime, trainInfo.endTime) }}</view>
          <u-icon name="arrow-right" color="#2979ff" size="32" />
        </view>
        <view class="route-station">
          <text class="station-time">{{ formatTime(trainInfo.endTime) }}</text>
          <text class="station-name">{{ trainInfo.endStation }}</text>
          <text class="station-date">{{ formatDate(trainInfo.endTime) }}</text>
        </view>
      </view>
    </view>
    
    <!-- 乘客选择 -->
    <view class="passenger-section">
      <view class="section-header">
        <text class="section-title">选择乘客</text>
        <text class="section-action" @click="addPassenger">+ 添加乘客</text>
      </view>
      <view class="passenger-list">
        <view 
          v-for="passenger in selectedPassengers" 
          :key="passenger.id"
          class="passenger-item"
        >
          <view class="passenger-info">
            <u-checkbox 
              v-model="passenger.checked" 
              :name="passenger.id"
              @change="updatePassengerSelection"
            />
            <view class="passenger-details">
              <text class="passenger-name">{{ passenger.realName }}</text>
              <text class="passenger-id">{{ passenger.cardType }} {{ formatIdCard(passenger.idCard) }}</text>
            </view>
          </view>
          <view class="passenger-type">
            <text class="type-label">票种</text>
            <text class="type-value">成人票</text>
          </view>
        </view>
        
        <view v-if="selectedPassengers.length === 0" class="empty-passengers">
          <text class="empty-text">暂无乘客信息，请先添加乘客</text>
          <u-button type="primary" text="添加乘客" size="small" @click="addPassenger" />
        </view>
      </view>
    </view>
    
    <!-- 座位选择 -->
    <view class="seat-section">
      <view class="section-header">
        <text class="section-title">选择座位</text>
        <text class="section-tips">选座服务仅支持部分车次</text>
      </view>
      <view class="seat-type-selection">
        <view 
          v-for="seatType in availableSeatTypes" 
          :key="seatType.type"
          class="seat-type-item"
          :class="{ active: selectedSeatType === seatType.type }"
          @click="selectSeatType(seatType)"
        >
          <text class="seat-type-name">{{ seatType.name }}</text>
          <text class="seat-type-price">¥{{ seatType.price }}</text>
          <text class="seat-type-availability" :class="getAvailabilityClass(seatType.available)">
            {{ getAvailabilityText(seatType.available) }}
          </text>
        </view>
      </view>
      
      <!-- 座位图选择（可选） -->
      <view v-if="showSeatMap && selectedSeatType" class="seat-map-section">
        <view class="seat-map-header">
          <text class="seat-map-title">选择座位</text>
          <text class="seat-map-tips">点击座位进行选择</text>
        </view>
        <view class="seat-map-container">
          <!-- 简化的座位图 -->
          <view class="seat-map">
            <view 
              v-for="seat in seatMap" 
              :key="seat.number"
              class="seat-item"
              :class="getSeatClass(seat)"
              @click="selectSeat(seat)"
            >
              {{ seat.number }}
            </view>
          </view>
          <view class="seat-legend">
            <view class="legend-item">
              <view class="legend-seat available"></view>
              <text>可选</text>
            </view>
            <view class="legend-item">
              <view class="legend-seat selected"></view>
              <text>已选</text>
            </view>
            <view class="legend-item">
              <view class="legend-seat occupied"></view>
              <text>已占</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 保险服务 -->
    <view class="insurance-section">
      <view class="section-header">
        <text class="section-title">保险服务</text>
      </view>
      <view class="insurance-options">
        <view class="insurance-item">
          <u-checkbox v-model="insuranceSelected" @change="toggleInsurance" />
          <view class="insurance-info">
            <text class="insurance-name">铁路乘意险</text>
            <text class="insurance-desc">最高保障30万元</text>
          </view>
          <text class="insurance-price">¥{{ insurancePrice }} × {{ passengerCount }}</text>
        </view>
      </view>
    </view>
    
    <!-- 联系人信息 -->
    <view class="contact-section">
      <view class="section-header">
        <text class="section-title">联系人信息</text>
      </view>
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
        <u-form-item label="邮箱" prop="contactEmail" border-bottom>
          <u--input
            v-model="contactForm.contactEmail"
            placeholder="选填，用于接收电子票据"
            border="none"
            maxlength="50"
          />
        </u-form-item>
      </u--form>
    </view>
    
    <!-- 费用明细 -->
    <view class="fee-section">
      <view class="section-header">
        <text class="section-title">费用明细</text>
      </view>
      <view class="fee-details">
        <view class="fee-item">
          <text class="fee-label">车票费用</text>
          <text class="fee-value">¥{{ ticketFee }}</text>
        </view>
        <view class="fee-item" v-if="insuranceSelected">
          <text class="fee-label">保险费用</text>
          <text class="fee-value">¥{{ insuranceTotalFee }}</text>
        </view>
        <view class="fee-total">
          <text class="total-label">总计</text>
          <text class="total-value">¥{{ totalFee }}</text>
        </view>
      </view>
    </view>
    
    <!-- 底部操作栏 -->
    <view class="bottom-actions">
      <view class="total-info">
        <text class="total-label">应付金额：</text>
        <text class="total-price">¥{{ totalFee }}</text>
      </view>
      <u-button
        type="primary"
        text="提交订单"
        size="large"
        :loading="submitting"
        :disabled="!canSubmit"
        @click="submitOrder"
      />
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { createOrder } from '@/api/order'
import { getTrainSeats } from '@/api/train'

// 车次信息
const trainInfo = ref({})
const selectedPassengers = ref([])
const selectedSeatType = ref('')
const selectedSeats = ref([])
const insuranceSelected = ref(false)
const showSeatMap = ref(false)
const submitting = ref(false)

// 表单引用
const formRef = ref(null)

// 联系人表单
const contactForm = reactive({
  contactName: '',
  contactPhone: '',
  contactEmail: ''
})

// 表单验证规则
const rules = {
  contactName: [
    { required: true, message: '请输入联系人姓名' },
    { max: 20, message: '姓名长度不能超过20位' }
  ],
  contactPhone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
  ],
  contactEmail: [
    { pattern: /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/, message: '请输入正确的邮箱格式' }
  ]
}

// 保险价格
const insurancePrice = 3

// 可用座位类型
const availableSeatTypes = ref([])

// 座位图（模拟数据）
const seatMap = ref([])

// 乘客数量
const passengerCount = computed(() => {
  return selectedPassengers.value.filter(p => p.checked).length
})

// 车票费用
const ticketFee = computed(() => {
  if (!selectedSeatType.value || passengerCount.value === 0) return 0
  const seatType = availableSeatTypes.value.find(s => s.type === selectedSeatType.value)
  return seatType ? seatType.price * passengerCount.value : 0
})

// 保险总费用
const insuranceTotalFee = computed(() => {
  return insuranceSelected.value ? insurancePrice * passengerCount.value : 0
})

// 总费用
const totalFee = computed(() => {
  return ticketFee.value + insuranceTotalFee.value
})

// 是否可以提交
const canSubmit = computed(() => {
  return passengerCount.value > 0 && 
         selectedSeatType.value && 
         contactForm.contactName && 
         contactForm.contactPhone
})

onMounted(() => {
  loadOrderData()
  loadSeatTypes()
  loadPassengers()
})

// 加载订单数据
const loadOrderData = () => {
  const orderData = uni.getStorageSync('selectedTrain')
  if (orderData) {
    trainInfo.value = orderData.trainInfo
  } else {
    // 模拟数据
    trainInfo.value = {
      id: 1,
      trainNumber: 'G1234',
      trainModel: 'G1234',
      startStation: '北京南',
      endStation: '上海虹桥',
      startTime: '2024-01-15T08:00:00',
      endTime: '2024-01-15T12:30:00'
    }
  }
}

// 加载座位类型
const loadSeatTypes = async () => {
  try {
    const response = await getTrainSeats(trainInfo.value.id)
    availableSeatTypes.value = response || []
    
    // 默认选择第一个有票的座位类型
    const availableSeat = availableSeatTypes.value.find(s => s.available > 0)
    if (availableSeat) {
      selectedSeatType.value = availableSeat.type
    }
  } catch (error) {
    console.error('加载座位类型失败:', error)
    // 模拟数据
    availableSeatTypes.value = [
      { type: 'SECOND_CLASS', name: '二等座', price: 553, available: 50 },
      { type: 'FIRST_CLASS', name: '一等座', price: 933, available: 20 },
      { type: 'BUSINESS', name: '商务座', price: 1748, available: 5 }
    ]
    selectedSeatType.value = 'SECOND_CLASS'
  }
}

// 加载乘客信息
const loadPassengers = () => {
  const passengers = uni.getStorageSync('passengers') || []
  selectedPassengers.value = passengers.map(p => ({
    ...p,
    checked: false
  }))
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 格式化日期
const formatDate = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

// 格式化时长
const formatDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return ''
  const duration = new Date(endTime) - new Date(startTime)
  const hours = Math.floor(duration / (1000 * 60 * 60))
  const minutes = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60))
  return `${hours}时${minutes}分`
}

// 格式化身份证号
const formatIdCard = (idCard) => {
  if (!idCard) return ''
  if (idCard.length === 18) {
    return idCard.substring(0, 4) + '**********' + idCard.substring(14)
  }
  return idCard
}

// 获取列车类型
const getTrainType = (model) => {
  if (model.includes('G')) return '高铁'
  if (model.includes('D')) return '动车'
  if (model.includes('C')) return '城际'
  return '列车'
}

// 获取座位类型名称
const getSeatTypeName = (type) => {
  const typeMap = {
    'BUSINESS': '商务座',
    'FIRST_CLASS': '一等座',
    'SECOND_CLASS': '二等座',
    'HARD_SEAT': '硬座',
    'SOFT_SEAT': '软座',
    'HARD_SLEEPER': '硬卧',
    'SOFT_SLEEPER': '软卧'
  }
  return typeMap[type] || type
}

// 获取可用性文本
const getAvailabilityText = (available) => {
  if (available > 20) return '有票'
  if (available > 0) return `${available}张`
  return '无票'
}

// 获取可用性样式类
const getAvailabilityClass = (available) => {
  if (available > 20) return 'available'
  if (available > 0) return 'limited'
  return 'none'
}

// 选择座位类型
const selectSeatType = (seatType) => {
  selectedSeatType.value = seatType.type
  // 加载座位图
  loadSeatMap()
}

// 加载座位图
const loadSeatMap = () => {
  // 模拟座位图数据
  seatMap.value = []
  for (let i = 1; i <= 50; i++) {
    seatMap.value.push({
      number: i,
      status: Math.random() > 0.7 ? 'occupied' : 'available',
      selected: false
    })
  }
  showSeatMap.value = true
}

// 获取座位样式类
const getSeatClass = (seat) => {
  if (seat.selected) return 'selected'
  if (seat.status === 'occupied') return 'occupied'
  return 'available'
}

// 选择座位
const selectSeat = (seat) => {
  if (seat.status === 'occupied') return
  
  if (seat.selected) {
    seat.selected = false
    selectedSeats.value = selectedSeats.value.filter(s => s !== seat.number)
  } else {
    if (selectedSeats.value.length < passengerCount.value) {
      seat.selected = true
      selectedSeats.value.push(seat.number)
    } else {
      uni.showToast({
        title: '已选择足够的座位',
        icon: 'none'
      })
    }
  }
}

// 更新乘客选择
const updatePassengerSelection = () => {
  // 重置座位选择
  selectedSeats.value = []
  seatMap.value.forEach(seat => {
    seat.selected = false
  })
}

// 添加乘客
const addPassenger = () => {
  uni.navigateTo({
    url: '/pages/passenger/passenger-add'
  })
}

// 切换保险选择
const toggleInsurance = () => {
  // 保险选择状态已在v-model中处理
}

// 提交订单
const submitOrder = async () => {
  try {
    // 表单验证
    await formRef.value.validate()
    
    if (passengerCount.value === 0) {
      uni.showToast({
        title: '请选择至少一名乘客',
        icon: 'none'
      })
      return
    }
    
    if (!selectedSeatType.value) {
      uni.showToast({
        title: '请选择座位类型',
        icon: 'none'
      })
      return
    }
    
    submitting.value = true
    
    const orderData = {
      trainId: trainInfo.value.id,
      scheduleId: trainInfo.value.scheduleId,
      departure: trainInfo.value.startStation,
      arrival: trainInfo.value.endStation,
      departureTime: trainInfo.value.startTime,
      arrivalTime: trainInfo.value.endTime,
      passengers: selectedPassengers.value
        .filter(p => p.checked)
        .map((passenger, index) => ({
          passengerId: passenger.id,
          realName: passenger.realName,
          idCard: passenger.idCard,
          cardType: passenger.cardType,
          seatType: selectedSeatType.value,
          seatNumber: selectedSeats.value[index] || null,
          ticketPrice: availableSeatTypes.value.find(s => s.type === selectedSeatType.value)?.price || 0
        })),
      contactName: contactForm.contactName,
      contactPhone: contactForm.contactPhone,
      contactEmail: contactForm.contactEmail,
      seatType: selectedSeatType.value,
      insurance: insuranceSelected.value,
      totalPrice: totalFee.value,
      orderDate: new Date().toISOString()
    }
    
    const response = await createOrder(orderData)
    
    uni.showToast({
      title: '订单提交成功',
      icon: 'success'
    })
    
    // 清除本地数据
    uni.removeStorageSync('selectedTrain')
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

<style lang="scss" scoped>
.ticket-confirm-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

.train-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx 30rpx;
  color: #fff;
  
  .train-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;
    
    .train-number {
      font-size: 48rpx;
      font-weight: bold;
    }
    
    .train-type {
      font-size: 28rpx;
      opacity: 0.9;
    }
  }
  
  .train-route {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .route-station {
      display: flex;
      flex-direction: column;
      align-items: center;
      flex: 1;
      
      .station-time {
        font-size: 40rpx;
        font-weight: bold;
        margin-bottom: 10rpx;
      }
      
      .station-name {
        font-size: 32rpx;
        margin-bottom: 5rpx;
      }
      
      .station-date {
        font-size: 24rpx;
        opacity: 0.8;
      }
    }
    
    .route-arrow {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0 20rpx;
      
      .route-duration {
        font-size: 24rpx;
        opacity: 0.8;
        margin-bottom: 10rpx;
      }
    }
  }
}

.passenger-section,
.seat-section,
.insurance-section,
.contact-section,
.fee-section {
  background: #fff;
  margin: 20rpx 0;
  padding: 30rpx;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    
    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
    
    .section-action {
      font-size: 28rpx;
      color: #2979ff;
    }
    
    .section-tips {
      font-size: 24rpx;
      color: #999;
    }
  }
}

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
      align-items: center;
      gap: 20rpx;
      flex: 1;
      
      .passenger-details {
        display: flex;
        flex-direction: column;
        
        .passenger-name {
          font-size: 32rpx;
          color: #333;
          margin-bottom: 5rpx;
        }
        
        .passenger-id {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
    
    .passenger-type {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      
      .type-label {
        font-size: 24rpx;
        color: #999;
        margin-bottom: 5rpx;
      }
      
      .type-value {
        font-size: 28rpx;
        color: #333;
      }
    }
  }
  
  .empty-passengers {
    text-align: center;
    padding: 60rpx 0;
    
    .empty-text {
      font-size: 28rpx;
      color: #999;
      margin-bottom: 20rpx;
      display: block;
    }
  }
}

.seat-type-selection {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  
  .seat-type-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border: 2rpx solid #e0e0e0;
    border-radius: 12rpx;
    
    &.active {
      border-color: #2979ff;
      background: #f0f7ff;
    }
    
    .seat-type-name {
      font-size: 32rpx;
      color: #333;
    }
    
    .seat-type-price {
      font-size: 32rpx;
      color: #ff6b35;
      font-weight: bold;
    }
    
    .seat-type-availability {
      font-size: 24rpx;
      
      &.available {
        color: #19be6b;
      }
      
      &.limited {
        color: #ff9900;
      }
      
      &.none {
        color: #999;
      }
    }
  }
}

.seat-map-section {
  margin-top: 30rpx;
  padding: 30rpx;
  background: #f8f8f8;
  border-radius: 12rpx;
  
  .seat-map-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    
    .seat-map-title {
      font-size: 28rpx;
      color: #333;
    }
    
    .seat-map-tips {
      font-size: 24rpx;
      color: #999;
    }
  }
  
  .seat-map-container {
    .seat-map {
      display: grid;
      grid-template-columns: repeat(5, 1fr);
      gap: 20rpx;
      margin-bottom: 20rpx;
      
      .seat-item {
        width: 80rpx;
        height: 80rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 8rpx;
        font-size: 24rpx;
        font-weight: bold;
        
        &.available {
          background: #19be6b;
          color: #fff;
        }
        
        &.selected {
          background: #2979ff;
          color: #fff;
        }
        
        &.occupied {
          background: #e0e0e0;
          color: #999;
        }
      }
    }
    
    .seat-legend {
      display: flex;
      justify-content: center;
      gap: 40rpx;
      
      .legend-item {
        display: flex;
        align-items: center;
        gap: 10rpx;
        
        .legend-seat {
          width: 30rpx;
          height: 30rpx;
          border-radius: 4rpx;
          
          &.available {
            background: #19be6b;
          }
          
          &.selected {
            background: #2979ff;
          }
          
          &.occupied {
            background: #e0e0e0;
          }
        }
        
        text {
          font-size: 24rpx;
          color: #666;
        }
      }
    }
  }
}

.insurance-options {
  .insurance-item {
    display: flex;
    align-items: center;
    gap: 20rpx;
    padding: 20rpx 0;
    
    .insurance-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      
      .insurance-name {
        font-size: 28rpx;
        color: #333;
        margin-bottom: 5rpx;
      }
      
      .insurance-desc {
        font-size: 24rpx;
        color: #999;
      }
    }
    
    .insurance-price {
      font-size: 28rpx;
      color: #ff6b35;
    }
  }
}

.fee-details {
  .fee-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    
    .fee-label {
      font-size: 28rpx;
      color: #666;
    }
    
    .fee-value {
      font-size: 28rpx;
      color: #333;
    }
  }
  
  .fee-total {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 0;
    
    .total-label {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
    
    .total-value {
      font-size: 40rpx;
      font-weight: bold;
      color: #ff6b35;
    }
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
      color: #ff6b35;
    }
  }
}
</style>