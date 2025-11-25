<template>
  <view class="seat-selector-container">
    <!-- 座位类型选择 -->
    <view class="seat-type-section">
      <view class="section-title">选择座位类型</view>
      <view class="seat-type-list">
        <view 
          v-for="seatType in seatTypes" 
          :key="seatType.type"
          class="seat-type-item"
          :class="{ active: selectedSeatType === seatType.type }"
          @click="selectSeatType(seatType)"
        >
          <view class="seat-type-info">
            <text class="seat-type-name">{{ seatType.name }}</text>
            <text class="seat-type-price">¥{{ seatType.price }}</text>
          </view>
          <view class="seat-type-availability">
            <text class="availability-text" :class="getAvailabilityClass(seatType.available)">
              {{ getAvailabilityText(seatType.available) }}
            </text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 座位图选择 -->
    <view class="seat-map-section" v-if="showSeatMap && selectedSeatType">
      <view class="seat-map-header">
        <text class="seat-map-title">选择座位</text>
        <text class="selected-count">已选: {{ selectedSeats.length }}/{{ maxSeats }}</text>
      </view>
      
      <!-- 车厢示意图 -->
      <view class="carriage-layout">
        <view class="carriage-header">
          <text class="carriage-number">{{ currentCarriage }}号车厢</text>
          <view class="carriage-nav">
            <u-icon 
              name="arrow-left" 
              size="24" 
              :color="currentCarriage > 1 ? '#2979ff' : '#ccc'"
              @click="changeCarriage(-1)"
            />
            <text class="carriage-info">{{ currentCarriage }}/{{ totalCarriages }}</text>
            <u-icon 
              name="arrow-right" 
              size="24" 
              :color="currentCarriage < totalCarriages ? '#2979ff' : '#ccc'"
              @click="changeCarriage(1)"
            />
          </view>
        </view>
        
        <!-- 座位布局 -->
        <view class="seat-layout">
          <!-- 过道标识 -->
          <view class="aisle-marker">
            <text>过道</text>
          </view>
          
          <!-- 座位网格 -->
          <view class="seat-grid">
            <view 
              v-for="seat in currentSeats" 
              :key="seat.id"
              class="seat-item"
              :class="getSeatItemClass(seat)"
              @click="toggleSeatSelection(seat)"
            >
              <text class="seat-number">{{ seat.number }}</text>
              <text v-if="seat.status === 'occupied'" class="seat-status">×</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 座位图例 -->
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
        <view class="legend-item">
          <view class="legend-seat disabled"></view>
          <text>不可选</text>
        </view>
      </view>
    </view>
    
    <!-- 已选座位展示 -->
    <view class="selected-seats-section" v-if="selectedSeats.length > 0">
      <view class="section-title">已选座位</view>
      <view class="selected-seats-list">
        <view 
          v-for="seat in selectedSeats" 
          :key="seat.id"
          class="selected-seat-item"
        >
          <text class="seat-info">{{ seat.carriage }}车 {{ seat.number }}</text>
          <u-icon name="close" size="20" color="#999" @click="removeSeat(seat)" />
        </view>
      </view>
    </view>
    
    <!-- 确认按钮 -->
    <view class="confirm-section">
      <u-button 
        type="primary" 
        text="确认选择" 
        size="large"
        :disabled="!canConfirm"
        @click="confirmSelection"
      />
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'

// Props
const props = defineProps({
  seatTypes: {
    type: Array,
    default: () => []
  },
  scheduleId: {
    type: [String, Number],
    required: true
  },
  maxSeats: {
    type: Number,
    default: 1
  },
  showSeatMap: {
    type: Boolean,
    default: true
  }
})

// Emits
const emit = defineEmits(['confirm', 'cancel'])

// 状态
const selectedSeatType = ref('')
const selectedSeats = ref([])
const currentCarriage = ref(1)
const totalCarriages = ref(8)

// 座位数据
const seatMapData = reactive({})

// 计算属性
const currentSeats = computed(() => {
  const carriageKey = `${currentCarriage.value}_${selectedSeatType.value}`
  return seatMapData[carriageKey] || []
})

const canConfirm = computed(() => {
  return selectedSeatType.value && selectedSeats.value.length > 0
})

// 监听座位类型变化
watch(selectedSeatType, (newType) => {
  if (newType && props.showSeatMap) {
    loadSeatMap()
  }
})

onMounted(() => {
  // 默认选择第一个有票的座位类型
  const availableSeat = props.seatTypes.find(s => s.available > 0)
  if (availableSeat) {
    selectedSeatType.value = availableSeat.type
  }
})

// 加载座位图
const loadSeatMap = () => {
  // 模拟座位图数据
  const carriageKey = `${currentCarriage.value}_${selectedSeatType.value}`
  
  if (!seatMapData[carriageKey]) {
    // 生成座位数据
    const seats = []
    const seatLayout = getSeatLayout(selectedSeatType.value)
    
    seatLayout.forEach((row, rowIndex) => {
      row.forEach((seatConfig, colIndex) => {
        if (seatConfig) {
          const seatNumber = `${rowIndex + 1}${String.fromCharCode(65 + colIndex)}`
          seats.push({
            id: `${carriageKey}_${seatNumber}`,
            number: seatNumber,
            carriage: currentCarriage.value,
            type: selectedSeatType.value,
            row: rowIndex + 1,
            column: colIndex + 1,
            status: Math.random() > 0.7 ? 'occupied' : 'available',
            selected: false
          })
        }
      })
    })
    
    seatMapData[carriageKey] = seats
  }
}

// 获取座位布局
const getSeatLayout = (seatType) => {
  // 不同座位类型的布局
  const layouts = {
    'SECOND_CLASS': [
      [1, 1, 0, 1, 1], // A B | C D
      [1, 1, 0, 1, 1],
      [1, 1, 0, 1, 1],
      [1, 1, 0, 1, 1],
      [1, 1, 0, 1, 1]
    ],
    'FIRST_CLASS': [
      [1, 0, 1],
      [1, 0, 1],
      [1, 0, 1],
      [1, 0, 1]
    ],
    'BUSINESS': [
      [1, 0, 1],
      [1, 0, 1]
    ]
  }
  
  return layouts[seatType] || layouts['SECOND_CLASS']
}

// 选择座位类型
const selectSeatType = (seatType) => {
  selectedSeatType.value = seatType.type
  // 清空已选座位
  selectedSeats.value = []
}

// 切换座位选择
const toggleSeatSelection = (seat) => {
  if (seat.status === 'occupied' || seat.status === 'disabled') return
  
  if (seat.selected) {
    // 取消选择
    seat.selected = false
    selectedSeats.value = selectedSeats.value.filter(s => s.id !== seat.id)
  } else {
    // 选择座位
    if (selectedSeats.value.length < props.maxSeats) {
      seat.selected = true
      selectedSeats.value.push(seat)
    } else {
      uni.showToast({
        title: `最多可选择${props.maxSeats}个座位`,
        icon: 'none'
      })
    }
  }
}

// 获取座位项样式类
const getSeatItemClass = (seat) => {
  if (seat.selected) return 'selected'
  if (seat.status === 'occupied') return 'occupied'
  if (seat.status === 'disabled') return 'disabled'
  return 'available'
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

// 切换车厢
const changeCarriage = (delta) => {
  const newCarriage = currentCarriage.value + delta
  if (newCarriage >= 1 && newCarriage <= totalCarriages.value) {
    currentCarriage.value = newCarriage
    loadSeatMap()
  }
}

// 移除已选座位
const removeSeat = (seat) => {
  selectedSeats.value = selectedSeats.value.filter(s => s.id !== seat.id)
  // 更新座位图状态
  const carriageKey = `${seat.carriage}_${selectedSeatType.value}`
  const seatInMap = seatMapData[carriageKey]?.find(s => s.id === seat.id)
  if (seatInMap) {
    seatInMap.selected = false
  }
}

// 确认选择
const confirmSelection = () => {
  const selectedSeatTypeInfo = props.seatTypes.find(s => s.type === selectedSeatType.value)
  
  emit('confirm', {
    seatType: selectedSeatType.value,
    seatTypeName: selectedSeatTypeInfo?.name || '',
    seatTypePrice: selectedSeatTypeInfo?.price || 0,
    seats: selectedSeats.value,
    totalPrice: selectedSeatTypeInfo?.price * selectedSeats.value.length || 0
  })
}
</script>

<style lang="scss" scoped>
.seat-selector-container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.seat-type-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  
  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }
  
  .seat-type-list {
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
      background: #fff;
      
      &.active {
        border-color: #2979ff;
        background: #f0f7ff;
      }
      
      .seat-type-info {
        display: flex;
        flex-direction: column;
        gap: 8rpx;
        
        .seat-type-name {
          font-size: 32rpx;
          color: #333;
          font-weight: 500;
        }
        
        .seat-type-price {
          font-size: 28rpx;
          color: #ff6b35;
          font-weight: bold;
        }
      }
      
      .seat-type-availability {
        .availability-text {
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
  }
}

.seat-map-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  
  .seat-map-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    
    .seat-map-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
    
    .selected-count {
      font-size: 28rpx;
      color: #666;
    }
  }
  
  .carriage-layout {
    .carriage-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30rpx;
      padding: 20rpx;
      background: #f8f8f8;
      border-radius: 12rpx;
      
      .carriage-number {
        font-size: 28rpx;
        color: #333;
        font-weight: bold;
      }
      
      .carriage-nav {
        display: flex;
        align-items: center;
        gap: 20rpx;
        
        .carriage-info {
          font-size: 24rpx;
          color: #666;
        }
      }
    }
    
    .seat-layout {
      position: relative;
      
      .aisle-marker {
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        background: #e0e0e0;
        padding: 10rpx 20rpx;
        border-radius: 8rpx;
        font-size: 24rpx;
        color: #666;
        z-index: 1;
      }
      
      .seat-grid {
        display: grid;
        grid-template-columns: repeat(5, 1fr);
        gap: 20rpx;
        padding: 40rpx 0;
        
        .seat-item {
          position: relative;
          width: 100rpx;
          height: 100rpx;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          border-radius: 12rpx;
          font-size: 24rpx;
          font-weight: bold;
          transition: all 0.3s ease;
          
          &.available {
            background: #19be6b;
            color: #fff;
            
            &:active {
              transform: scale(0.95);
            }
          }
          
          &.selected {
            background: #2979ff;
            color: #fff;
            transform: scale(1.05);
          }
          
          &.occupied {
            background: #e0e0e0;
            color: #999;
          }
          
          &.disabled {
            background: #f5f5f5;
            color: #ccc;
          }
          
          .seat-number {
            font-size: 28rpx;
            margin-bottom: 4rpx;
          }
          
          .seat-status {
            font-size: 32rpx;
            font-weight: bold;
          }
        }
      }
    }
  }
  
  .seat-legend {
    display: flex;
    justify-content: center;
    gap: 40rpx;
    margin-top: 30rpx;
    padding-top: 30rpx;
    border-top: 2rpx solid #f0f0f0;
    
    .legend-item {
      display: flex;
      align-items: center;
      gap: 10rpx;
      
      .legend-seat {
        width: 30rpx;
        height: 30rpx;
        border-radius: 6rpx;
        
        &.available {
          background: #19be6b;
        }
        
        &.selected {
          background: #2979ff;
        }
        
        &.occupied {
          background: #e0e0e0;
        }
        
        &.disabled {
          background: #f5f5f5;
        }
      }
      
      text {
        font-size: 24rpx;
        color: #666;
      }
    }
  }
}

.selected-seats-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  
  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }
  
  .selected-seats-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20rpx;
    
    .selected-seat-item {
      display: flex;
      align-items: center;
      gap: 10rpx;
      padding: 15rpx 20rpx;
      background: #f0f7ff;
      border: 2rpx solid #2979ff;
      border-radius: 8rpx;
      
      .seat-info {
        font-size: 28rpx;
        color: #2979ff;
        font-weight: 500;
      }
    }
  }
}

.confirm-section {
  padding: 30rpx;
  background: #fff;
  border-radius: 20rpx;
  
  .u-button {
    width: 100%;
  }
}
</style>