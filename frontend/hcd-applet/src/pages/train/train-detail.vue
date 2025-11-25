<template>
  <view class="train-detail-container">
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
          <u-icon name="arrow-right" color="#2979ff" size="40" />
          <text class="duration">{{ trainInfo.duration }}</text>
        </view>
        <view class="station-info">
          <text class="station-name">{{ trainInfo.endStation }}</text>
          <text class="arrival-time">{{ formatTime(trainInfo.endTime) }}</text>
        </view>
      </view>
      <view class="train-date">
        <text>{{ formatDate(trainInfo.startTime) }}</text>
      </view>
    </view>

    <!-- 座位类型选择 -->
    <view class="seat-selection-section">
      <view class="section-title">选择座位类型</view>
      <view class="seat-list">
        <view 
          v-for="seat in seatList" 
          :key="seat.type"
          class="seat-item"
          :class="{ active: selectedSeatType === seat.type }"
          @click="selectSeatType(seat)"
        >
          <view class="seat-type">
            <text class="type-name">{{ seat.name }}</text>
            <text class="type-desc">{{ seat.desc }}</text>
          </view>
          <view class="seat-price">
            <text class="price">¥{{ seat.price }}</text>
            <text class="remaining">余{{ seat.remaining }}张</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 乘客选择 -->
    <view class="passenger-section">
      <view class="section-header">
        <text class="section-title">选择乘客</text>
        <text class="add-passenger" @click="addPassenger">+ 添加乘客</text>
      </view>
      <view class="passenger-list">
        <view 
          v-for="(passenger, index) in selectedPassengers" 
          :key="index"
          class="passenger-item"
        >
          <view class="passenger-info">
            <text class="name">{{ passenger.realName }}</text>
            <text class="id-card">{{ formatIdCard(passenger.idCard) }}</text>
          </view>
          <u-icon name="close" color="#999" size="24" @click="removePassenger(index)" />
        </view>
        <view v-if="selectedPassengers.length === 0" class="empty-passenger" @click="selectPassenger">
          <text>请选择乘客</text>
          <u-icon name="arrow-right" color="#999" size="24" />
        </view>
      </view>
    </view>

    <!-- 底部订单信息 -->
    <view class="order-summary">
      <view class="summary-info">
        <text class="passenger-count">{{ selectedPassengers.length }}位乘客</text>
        <text class="total-price">¥{{ totalPrice }}</text>
      </view>
      <u-button
        type="primary"
        text="提交订单"
        size="large"
        :disabled="!canSubmit"
        :loading="submitting"
        @click="submitOrder"
      />
    </view>

    <!-- 座位图弹窗 -->
    <u-popup v-model="showSeatMap" mode="bottom" height="80%">
      <view class="seat-map-popup">
        <view class="popup-header">
          <text class="popup-title">选择座位</text>
          <u-icon name="close" size="24" @click="showSeatMap = false" />
        </view>
        <view class="seat-map-container">
          <view class="seat-legend">
            <view class="legend-item">
              <view class="seat available"></view>
              <text>可选</text>
            </view>
            <view class="legend-item">
              <view class="seat selected"></view>
              <text>已选</text>
            </view>
            <view class="legend-item">
              <view class="seat occupied"></view>
              <text>已售</text>
            </view>
          </view>
          <view class="seat-map">
            <!-- 座位图实现 -->
            <view class="seat-row" v-for="row in seatLayout" :key="row.row">
              <text class="row-number">{{ row.row }}</text>
              <view class="seat-group">
                <view 
                  v-for="seat in row.seats" 
                  :key="seat.number"
                  class="seat"
                  :class="getSeatClass(seat)"
                  @click="selectSeat(seat)"
                >
                  {{ seat.number }}
                </view>
              </view>
            </view>
          </view>
        </view>
        <view class="popup-footer">
          <view class="selected-seats">
            <text>已选座位：{{ selectedSeats.join(', ') }}</text>
          </view>
          <u-button
            type="primary"
            text="确认选择"
            size="medium"
            :disabled="selectedSeats.length === 0"
            @click="confirmSeatSelection"
          />
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getTrainDetail, getTrainSeats } from '@/api/train'

const trainId = ref('')
const trainInfo = ref({
  trainNumber: '',
  trainType: '',
  startStation: '',
  endStation: '',
  startTime: '',
  endTime: '',
  duration: ''
})

const seatList = ref([])
const selectedSeatType = ref('')
const selectedPassengers = ref([])
const selectedSeats = ref([])
const showSeatMap = ref(false)
const seatLayout = ref([])
const submitting = ref(false)

onMounted(() => {
  // 获取车次ID
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  trainId.value = currentPage.options.trainId
  
  if (trainId.value) {
    loadTrainDetail()
    loadPassengers()
  }
})

const loadTrainDetail = async () => {
  try {
    const response = await getTrainDetail(trainId.value)
    trainInfo.value = response
    
    // 加载座位信息
    const seatResponse = await getTrainSeats(trainId.value)
    seatList.value = seatResponse || []
    
    // 默认选择第一个有座的座位类型
    const availableSeat = seatList.value.find(seat => seat.remaining > 0)
    if (availableSeat) {
      selectedSeatType.value = availableSeat.type
    }
  } catch (error) {
    uni.showToast({
      title: '获取车次信息失败',
      icon: 'none'
    })
  }
}

const loadPassengers = () => {
  // 从全局事件或本地存储获取已选择的乘客
  const passengers = uni.getStorageSync('selectedPassengers') || []
  selectedPassengers.value = passengers
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatDate = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const formatIdCard = (idCard) => {
  if (!idCard) return ''
  if (idCard.length === 18) {
    return idCard.substring(0, 4) + '**********' + idCard.substring(14)
  }
  return idCard
}

const selectSeatType = (seat) => {
  selectedSeatType.value = seat.type
  // 显示座位图
  generateSeatLayout(seat.type)
  showSeatMap.value = true
}

const generateSeatLayout = (seatType) => {
  // 模拟座位布局数据
  const rows = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']
  const seatNumbers = seatType === 'BUSINESS' ? ['1', '2'] : 
                     seatType === 'FIRST_CLASS' ? ['1', '2', '3', '4'] :
                     ['1', '2', '3', '4', '5']
  
  seatLayout.value = rows.map(row => ({
    row,
    seats: seatNumbers.map(number => ({
      number: `${row}${number}`,
      status: Math.random() > 0.7 ? 'occupied' : 'available',
      selected: selectedSeats.value.includes(`${row}${number}`)
    }))
  }))
}

const getSeatClass = (seat) => {
  if (seat.status === 'occupied') return 'occupied'
  if (seat.selected) return 'selected'
  return 'available'
}

const selectSeat = (seat) => {
  if (seat.status === 'occupied') return
  
  const seatNumber = seat.number
  if (seat.selected) {
    // 取消选择
    selectedSeats.value = selectedSeats.value.filter(s => s !== seatNumber)
  } else {
    // 选择座位，但不能超过乘客数量
    if (selectedSeats.value.length < selectedPassengers.value.length) {
      selectedSeats.value.push(seatNumber)
    } else {
      uni.showToast({
        title: '座位数量不能超过乘客数量',
        icon: 'none'
      })
    }
  }
  
  // 更新座位状态
  seat.selected = !seat.selected
}

const confirmSeatSelection = () => {
  showSeatMap.value = false
}

const addPassenger = () => {
  uni.navigateTo({
    url: '/pages/passenger/passenger-add'
  })
}

const selectPassenger = () => {
  uni.navigateTo({
    url: '/pages/passenger/passenger-list?mode=select'
  })
}

const removePassenger = (index) => {
  selectedPassengers.value.splice(index, 1)
  // 同时移除对应的座位选择
  if (selectedSeats.value.length > selectedPassengers.value.length) {
    selectedSeats.value.splice(selectedPassengers.value.length)
  }
}

const totalPrice = computed(() => {
  const seat = seatList.value.find(s => s.type === selectedSeatType.value)
  if (!seat) return '0.00'
  return (seat.price * selectedPassengers.value.length).toFixed(2)
})

const canSubmit = computed(() => {
  return selectedPassengers.value.length > 0 && selectedSeatType.value
})

const submitOrder = async () => {
  if (submitting.value) return
  
  submitting.value = true
  
  try {
    // 构建订单数据
    const orderData = {
      trainId: trainId.value,
      passengers: selectedPassengers.value.map((passenger, index) => ({
        passengerId: passenger.id,
        seatType: selectedSeatType.value,
        seatNumber: selectedSeats.value[index] || null
      }))
    }
    
    // 跳转到订单确认页面
    uni.setStorageSync('orderData', orderData)
    uni.navigateTo({
      url: '/pages/order/order-confirm'
    })
  } catch (error) {
    uni.showToast({
      title: '提交失败',
      icon: 'none'
    })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.train-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

.train-info-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
  color: #fff;
  border-radius: 0 0 30rpx 30rpx;

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

  .route-info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20rpx;

    .station-info {
      display: flex;
      flex-direction: column;
      align-items: center;
      flex: 1;

      .station-name {
        font-size: 36rpx;
        font-weight: bold;
        margin-bottom: 10rpx;
      }

      .departure-time, .arrival-time {
        font-size: 32rpx;
        margin-bottom: 5rpx;
      }
    }

    .route-arrow {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0 20rpx;

      .duration {
        font-size: 24rpx;
        opacity: 0.9;
        margin-top: 10rpx;
      }
    }
  }

  .train-date {
    text-align: center;
    font-size: 28rpx;
    opacity: 0.9;
  }
}

.seat-selection-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  margin: 20rpx 0;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 30rpx;
  }

  .seat-list {
    .seat-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 30rpx;
      border: 2rpx solid #f0f0f0;
      border-radius: 16rpx;
      margin-bottom: 20rpx;
      transition: all 0.3s ease;

      &.active {
        border-color: #2979ff;
        background: #f0f7ff;
      }

      .seat-type {
        display: flex;
        flex-direction: column;
        gap: 8rpx;

        .type-name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
        }

        .type-desc {
          font-size: 24rpx;
          color: #666;
        }
      }

      .seat-price {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        gap: 8rpx;

        .price {
          font-size: 36rpx;
          font-weight: bold;
          color: #f56c6c;
        }

        .remaining {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
  }
}

.passenger-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  margin: 20rpx 0;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

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

    .add-passenger {
      font-size: 28rpx;
      color: #2979ff;
    }
  }

  .passenger-list {
    .passenger-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 30rpx;
      border: 2rpx solid #f0f0f0;
      border-radius: 16rpx;
      margin-bottom: 20rpx;

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
    }

    .empty-passenger {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 40rpx 30rpx;
      border: 2rpx dashed #ddd;
      border-radius: 16rpx;
      color: #999;
      font-size: 28rpx;
    }
  }
}

.order-summary {
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

  .summary-info {
    display: flex;
    flex-direction: column;
    gap: 8rpx;

    .passenger-count {
      font-size: 24rpx;
      color: #666;
    }

    .total-price {
      font-size: 40rpx;
      font-weight: bold;
      color: #f56c6c;
    }
  }
}

.seat-map-popup {
  background: #fff;
  height: 100%;
  display: flex;
  flex-direction: column;

  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .popup-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }

  .seat-map-container {
    flex: 1;
    padding: 30rpx;
    overflow-y: auto;

    .seat-legend {
      display: flex;
      justify-content: center;
      gap: 40rpx;
      margin-bottom: 30rpx;

      .legend-item {
        display: flex;
        align-items: center;
        gap: 10rpx;

        .seat {
          width: 40rpx;
          height: 40rpx;
          border-radius: 8rpx;

          &.available {
            background: #e8f4fd;
            border: 2rpx solid #2979ff;
          }

          &.selected {
            background: #2979ff;
          }

          &.occupied {
            background: #f0f0f0;
            border: 2rpx solid #ddd;
          }
        }

        text {
          font-size: 24rpx;
          color: #666;
        }
      }
    }

    .seat-map {
      .seat-row {
        display: flex;
        align-items: center;
        margin-bottom: 20rpx;

        .row-number {
          width: 60rpx;
          text-align: center;
          font-size: 28rpx;
          color: #666;
          margin-right: 20rpx;
        }

        .seat-group {
          display: flex;
          gap: 20rpx;

          .seat {
            width: 80rpx;
            height: 80rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 12rpx;
            font-size: 24rpx;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;

            &.available {
              background: #e8f4fd;
              border: 2rpx solid #2979ff;
              color: #2979ff;

              &:active {
                background: #d0ebff;
              }
            }

            &.selected {
              background: #2979ff;
              border: 2rpx solid #2979ff;
              color: #fff;
            }

            &.occupied {
              background: #f0f0f0;
              border: 2rpx solid #ddd;
              color: #999;
              cursor: not-allowed;
            }
          }
        }
      }
    }
  }

  .popup-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-top: 1rpx solid #f0f0f0;

    .selected-seats {
      font-size: 28rpx;
      color: #333;
    }
  }
}
</style>