<template>
  <view class="train-list-container">
    <!-- 顶部筛选条件 -->
    <view class="filter-header">
      <view class="route-info">
        <view class="route-stations">
          <text class="station">{{ departure }}</text>
          <u-icon name="arrow-right" color="#2979ff" size="28" />
          <text class="station">{{ arrival }}</text>
        </view>
        <text class="travel-date">{{ formatDate(travelDate) }}</text>
      </view>
      <view class="filter-actions">
        <view class="filter-btn" @click="showTimeFilter = true">
          <u-icon name="clock" color="#666" size="24" />
          <text>时间</text>
        </view>
        <view class="filter-btn" @click="showPriceFilter = true">
          <u-icon name="list" color="#666" size="24" />
          <text>排序</text>
        </view>
      </view>
    </view>
    
    <!-- 车次列表 -->
    <scroll-view 
      scroll-y 
      class="train-scroll"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view class="train-list">
        <view 
          v-for="train in displayTrains" 
          :key="train.id"
          class="train-card"
          @click="selectTrain(train)"
        >
          <!-- 车次头部信息 -->
          <view class="train-header">
            <view class="train-basic">
              <text class="train-number">{{ train.trainNumber }}</text>
              <text class="train-type">{{ getTrainType(train.trainModel) }}</text>
              <text class="available-tickets" v-if="train.availableTickets >= 0">
                余票: {{ train.availableTickets }}
              </text>
            </view>
            <view class="train-duration">
              <text>{{ formatDuration(train.startTime, train.endTime) }}</text>
            </view>
          </view>
          
          <!-- 车次时间线路 -->
          <view class="train-route">
            <view class="station-info departure">
              <text class="station-time">{{ formatTime(train.startTime) }}</text>
              <text class="station-name">{{ train.startStation }}</text>
            </view>
            <view class="route-middle">
              <view class="route-line"></view>
              <u-icon name="arrow-right" color="#2979ff" size="32" />
            </view>
            <view class="station-info arrival">
              <text class="station-time">{{ formatTime(train.endTime) }}</text>
              <text class="station-name">{{ train.endStation }}</text>
            </view>
          </view>
          
          <!-- 座位价格信息 -->
          <view class="seat-prices" v-if="train.seatPrices && train.seatPrices.length > 0">
            <view 
              v-for="seat in train.seatPrices.slice(0, 3)" 
              :key="seat.type"
              class="seat-price-item"
            >
              <text class="seat-type">{{ getSeatTypeName(seat.type) }}</text>
              <text class="seat-price">¥{{ seat.price }}</text>
              <text class="seat-availability" :class="getAvailabilityClass(seat.available)">
                {{ getAvailabilityText(seat.available) }}
              </text>
            </view>
          </view>
          
          <!-- 无票提示 -->
          <view v-else class="no-tickets">
            <text class="no-tickets-text">暂无余票</text>
          </view>
        </view>
        
        <!-- 空状态 -->
        <view v-if="displayTrains.length === 0 && !loading" class="empty-state">
          <u-empty mode="data" text="暂无符合条件的车次" />
        </view>
        
        <!-- 加载更多 -->
        <view v-if="loading" class="loading-more">
          <u-loading-icon text="加载中..." />
        </view>
      </view>
    </scroll-view>
    
    <!-- 时间筛选弹窗 -->
    <u-popup :show="showTimeFilter" mode="bottom" @close="showTimeFilter = false">
      <view class="filter-popup">
        <view class="popup-header">
          <text class="popup-title">时间筛选</text>
          <u-icon name="close" color="#999" size="24" @click="showTimeFilter = false" />
        </view>
        <view class="time-filter-content">
          <view class="filter-section">
            <text class="filter-label">出发时间段</text>
            <view class="time-range">
              <u-button 
                v-for="range in timeRanges" 
                :key="range.value"
                :text="range.label"
                :type="selectedTimeRange === range.value ? 'primary' : 'default'"
                size="small"
                @click="selectedTimeRange = range.value"
              />
            </view>
          </view>
          <view class="filter-actions">
            <u-button text="重置" @click="resetTimeFilter" />
            <u-button type="primary" text="确定" @click="applyTimeFilter" />
          </view>
        </view>
      </view>
    </u-popup>
    
    <!-- 价格排序弹窗 -->
    <u-popup :show="showPriceFilter" mode="bottom" @close="showPriceFilter = false">
      <view class="filter-popup">
        <view class="popup-header">
          <text class="popup-title">排序方式</text>
          <u-icon name="close" color="#999" size="24" @click="showPriceFilter = false" />
        </view>
        <view class="sort-content">
          <u-cell-group>
            <u-cell 
              v-for="option in sortOptions" 
              :key="option.value"
              :title="option.label"
              :value="selectedSort === option.value ? '✓' : ''"
              @click="selectSort(option.value)"
            />
          </u-cell-group>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { searchSchedules } from '@/api/train'

// 查询参数
const departure = ref('')
const arrival = ref('')
const travelDate = ref('')

// 数据列表
const trainList = ref([])
const loading = ref(false)
const refreshing = ref(false)

// 筛选状态
const showTimeFilter = ref(false)
const showPriceFilter = ref(false)
const selectedTimeRange = ref('')
const selectedSort = ref('departureTime')

// 分页
const pageInfo = reactive({
  page: 1,
  pageSize: 10,
  hasMore: true
})

// 时间范围选项
const timeRanges = [
  { value: '', label: '不限' },
  { value: 'morning', label: '06:00-12:00' },
  { value: 'afternoon', label: '12:00-18:00' },
  { value: 'evening', label: '18:00-24:00' }
]

// 排序选项
const sortOptions = [
  { value: 'departureTime', label: '出发时间最早' },
  { value: 'arrivalTime', label: '到达时间最早' },
  { value: 'duration', label: '用时最短' },
  { value: 'priceAsc', label: '价格最低' },
  { value: 'priceDesc', label: '价格最高' }
]

// 显示的车次列表（经过筛选和排序）
const displayTrains = computed(() => {
  let filtered = filterTrainsByTime(trainList.value)
  return sortTrains(filtered)
})

onMounted(() => {
  // 获取查询参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options
  
  departure.value = options.departure || '北京'
  arrival.value = options.arrival || '上海'
  travelDate.value = options.date || formatDate(new Date())
  
  loadTrainList()
})

// 加载车次列表
const loadTrainList = async (isRefresh = false) => {
  if (loading.value) return
  
  loading.value = true
  try {
    const params = {
      departure: departure.value,
      arrival: arrival.value,
      date: travelDate.value,
      page: isRefresh ? 1 : pageInfo.page,
      pageSize: pageInfo.pageSize
    }
    
    const response = await searchSchedules(params)
    
    if (isRefresh) {
      trainList.value = response.list || []
      pageInfo.page = 1
    } else {
      trainList.value.push(...(response.list || []))
    }
    
    pageInfo.hasMore = response.hasMore || false
  } catch (error) {
    console.error('加载车次列表失败:', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  refreshing.value = true
  loadTrainList(true)
}

// 加载更多
const loadMore = () => {
  if (pageInfo.hasMore && !loading.value) {
    pageInfo.page++
    loadTrainList()
  }
}

// 时间筛选
const filterTrainsByTime = (trains) => {
  if (!selectedTimeRange.value) return trains
  
  return trains.filter(train => {
    const hour = new Date(train.startTime).getHours()
    switch (selectedTimeRange.value) {
      case 'morning':
        return hour >= 6 && hour < 12
      case 'afternoon':
        return hour >= 12 && hour < 18
      case 'evening':
        return hour >= 18 && hour < 24
      default:
        return true
    }
  })
}

// 排序
const sortTrains = (trains) => {
  const sorted = [...trains]
  
  switch (selectedSort.value) {
    case 'departureTime':
      return sorted.sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
    case 'arrivalTime':
      return sorted.sort((a, b) => new Date(a.endTime) - new Date(b.endTime))
    case 'duration':
      return sorted.sort((a, b) => {
        const durationA = new Date(a.endTime) - new Date(a.startTime)
        const durationB = new Date(b.endTime) - new Date(b.startTime)
        return durationA - durationB
      })
    case 'priceAsc':
      return sorted.sort((a, b) => {
        const minPriceA = Math.min(...(a.seatPrices || []).map(s => s.price))
        const minPriceB = Math.min(...(b.seatPrices || []).map(s => s.price))
        return minPriceA - minPriceB
      })
    case 'priceDesc':
      return sorted.sort((a, b) => {
        const maxPriceA = Math.max(...(a.seatPrices || []).map(s => s.price))
        const maxPriceB = Math.max(...(b.seatPrices || []).map(s => s.price))
        return maxPriceB - maxPriceA
      })
    default:
      return sorted
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  const today = new Date()
  const tomorrow = new Date(today)
  tomorrow.setDate(tomorrow.getDate() + 1)
  
  if (date.toDateString() === today.toDateString()) {
    return '今天'
  } else if (date.toDateString() === tomorrow.toDateString()) {
    return '明天'
  } else {
    const month = date.getMonth() + 1
    const day = date.getDate()
    const weekDays = ['日', '一', '二', '三', '四', '五', '六']
    const weekDay = weekDays[date.getDay()]
    return `${month}月${day}日 周${weekDay}`
  }
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 格式化时长
const formatDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return ''
  const duration = new Date(endTime) - new Date(startTime)
  const hours = Math.floor(duration / (1000 * 60 * 60))
  const minutes = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60))
  return `${hours}小时${minutes}分`
}

// 获取列车类型
const getTrainType = (model) => {
  if (model.includes('G')) return '高铁'
  if (model.includes('D')) return '动车'
  if (model.includes('C')) return '城际'
  if (model.includes('Z')) return '直达'
  if (model.includes('T')) return '特快'
  if (model.includes('K')) return '快速'
  return '普速'
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
    'SOFT_SLEEPER': '软卧',
    'NO_SEAT': '无座'
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

// 选择车次
const selectTrain = (train) => {
  // 存储选择的车次信息
  const orderData = {
    trainInfo: train,
    departure: departure.value,
    arrival: arrival.value,
    date: travelDate.value
  }
  uni.setStorageSync('selectedTrain', orderData)
  
  // 跳转到车次详情页
  uni.navigateTo({
    url: `/pages/train-detail/train-detail?trainId=${train.id}&date=${travelDate.value}`
  })
}

// 重置时间筛选
const resetTimeFilter = () => {
  selectedTimeRange.value = ''
}

// 应用时间筛选
const applyTimeFilter = () => {
  showTimeFilter.value = false
}

// 选择排序方式
const selectSort = (value) => {
  selectedSort.value = value
  showPriceFilter.value = false
}
</script>

<style lang="scss" scoped>
.train-list-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.filter-header {
  background: #fff;
  padding: 20rpx 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  
  .route-info {
    flex: 1;
    
    .route-stations {
      display: flex;
      align-items: center;
      gap: 10rpx;
      margin-bottom: 8rpx;
      
      .station {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }
    }
    
    .travel-date {
      font-size: 24rpx;
      color: #666;
    }
  }
  
  .filter-actions {
    display: flex;
    gap: 20rpx;
    
    .filter-btn {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4rpx;
      padding: 10rpx 20rpx;
      background: #f8f8f8;
      border-radius: 12rpx;
      
      text {
        font-size: 22rpx;
        color: #666;
      }
    }
  }
}

.train-scroll {
  flex: 1;
  padding: 20rpx;
}

.train-list {
  .train-card {
    background: #fff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
    
    .train-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20rpx;
      
      .train-basic {
        display: flex;
        align-items: center;
        gap: 15rpx;
        
        .train-number {
          font-size: 36rpx;
          font-weight: bold;
          color: #333;
        }
        
        .train-type {
          font-size: 24rpx;
          color: #666;
          background: #f0f0f0;
          padding: 4rpx 12rpx;
          border-radius: 12rpx;
        }
        
        .available-tickets {
          font-size: 24rpx;
          color: #ff6b35;
          font-weight: 500;
        }
      }
      
      .train-duration {
        font-size: 24rpx;
        color: #999;
      }
    }
    
    .train-route {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20rpx;
      
      .station-info {
        display: flex;
        flex-direction: column;
        align-items: center;
        flex: 1;
        
        &.departure {
          align-items: flex-start;
        }
        
        &.arrival {
          align-items: flex-end;
        }
        
        .station-time {
          font-size: 36rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 8rpx;
        }
        
        .station-name {
          font-size: 28rpx;
          color: #666;
        }
      }
      
      .route-middle {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 0 20rpx;
        position: relative;
        
        .route-line {
          width: 2rpx;
          height: 40rpx;
          background: #e0e0e0;
          margin-bottom: 10rpx;
        }
      }
    }
    
    .seat-prices {
      display: flex;
      justify-content: space-between;
      gap: 20rpx;
      padding-top: 20rpx;
      border-top: 1rpx solid #f0f0f0;
      
      .seat-price-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        flex: 1;
        
        .seat-type {
          font-size: 24rpx;
          color: #666;
          margin-bottom: 8rpx;
        }
        
        .seat-price {
          font-size: 32rpx;
          font-weight: bold;
          color: #ff6b35;
          margin-bottom: 4rpx;
        }
        
        .seat-availability {
          font-size: 20rpx;
          
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
    
    .no-tickets {
      text-align: center;
      padding-top: 20rpx;
      border-top: 1rpx solid #f0f0f0;
      
      .no-tickets-text {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
  
  .empty-state {
    padding: 200rpx 0;
  }
  
  .loading-more {
    text-align: center;
    padding: 40rpx 0;
  }
}

.filter-popup {
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  
  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 2rpx solid #f0f0f0;
    
    .popup-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }
  
  .time-filter-content {
    padding: 30rpx;
    
    .filter-section {
      margin-bottom: 40rpx;
      
      .filter-label {
        font-size: 28rpx;
        color: #333;
        margin-bottom: 20rpx;
        display: block;
      }
      
      .time-range {
        display: flex;
        flex-wrap: wrap;
        gap: 20rpx;
      }
    }
    
    .filter-actions {
      display: flex;
      gap: 20rpx;
      
      .u-button {
        flex: 1;
      }
    }
  }
  
  .sort-content {
    padding: 0 30rpx 30rpx;
  }
}
</style>