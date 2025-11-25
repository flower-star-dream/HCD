<template>
  <view class="train-detail-container">
    <!-- 顶部车次信息卡片 -->
    <view class="train-header-card">
      <view class="train-basic-info">
        <view class="train-number-section">
          <text class="train-number">{{ scheduleInfo.trainNumber }}</text>
          <text class="train-model">{{ trainInfo.trainModel }}</text>
        </view>
        <view class="train-status">
          <u-tag 
            :text="getStatusText(scheduleInfo.status)" 
            :type="getStatusType(scheduleInfo.status)"
            size="mini"
          />
        </view>
      </view>
      
      <view class="route-overview">
        <view class="station-info">
          <text class="station-name">{{ scheduleInfo.startStation }}</text>
          <text class="departure-time">{{ formatTime(scheduleInfo.startTime) }}</text>
          <text class="departure-date">{{ formatDate(scheduleInfo.startTime) }}</text>
        </view>
        <view class="route-middle">
          <view class="duration-info">
            <text class="duration-time">{{ calculateDuration(scheduleInfo.startTime, scheduleInfo.endTime) }}</text>
            <text class="duration-text">历时</text>
          </view>
          <view class="route-line">
            <view class="start-dot"></view>
            <view class="line"></view>
            <view class="end-dot"></view>
          </view>
        </view>
        <view class="station-info">
          <text class="station-name">{{ scheduleInfo.endStation }}</text>
          <text class="arrival-time">{{ formatTime(scheduleInfo.endTime) }}</text>
          <text class="arrival-date">{{ formatDate(scheduleInfo.endTime) }}</text>
        </view>
      </view>
    </view>

    <!-- 选项卡 -->
    <view class="detail-tabs">
      <u-tabs 
        :list="tabs" 
        :current="currentTab" 
        @change="handleTabChange"
        line-color="#2979ff"
        active-color="#2979ff"
      />
    </view>

    <!-- 选项卡内容 -->
    <scroll-view scroll-y class="tab-content">
      <!-- 班次信息 -->
      <view v-if="currentTab === 0" class="tab-panel">
        <!-- 列车信息 -->
        <view class="info-section">
          <view class="section-title">
            <u-icon name="train-fill" color="#2979ff" size="24" />
            <text>列车信息</text>
          </view>
          <u-cell-group>
            <u-cell title="列车名称" :value="trainInfo.trainName" />
            <u-cell title="列车型号" :value="trainInfo.trainModel" />
            <u-cell title="座位总数" :value="trainInfo.seatNum + '个'" />
            <u-cell title="服务年数" :value="trainInfo.serviceYears + '年'" />
          </u-cell-group>
        </view>

        <!-- 班次信息 -->
        <view class="info-section">
          <view class="section-title">
            <u-icon name="calendar-fill" color="#2979ff" size="24" />
            <text>班次信息</text>
          </view>
          <u-cell-group>
            <u-cell title="班次编号" :value="scheduleInfo.id" />
            <u-cell title="列车长" :value="scheduleInfo.conductor || '暂无信息'" />
            <u-cell title="可售票数" :value="scheduleInfo.availableTickets + '张'" />
            <u-cell title="出发时间" :value="formatDateTime(scheduleInfo.startTime)" />
            <u-cell title="到达时间" :value="formatDateTime(scheduleInfo.endTime)" />
          </u-cell-group>
        </view>

        <!-- 线路信息 -->
        <view class="info-section">
          <view class="section-title">
            <u-icon name="map-fill" color="#2979ff" size="24" />
            <text>线路信息</text>
          </view>
          <u-cell-group>
            <u-cell title="线路名称" :value="routeInfo.routeName" />
            <u-cell title="起点站" :value="routeInfo.startStation" />
            <u-cell title="终点站" :value="routeInfo.endStation" />
            <u-cell title="站点数量" :value="routeInfo.stationCount + '个'" />
          </u-cell-group>
        </view>
      </view>

      <!-- 经停站点 -->
      <view v-if="currentTab === 1" class="tab-panel">
        <view class="stations-timeline">
          <view 
            v-for="(station, index) in stationsList" 
            :key="station.id"
            class="timeline-item"
            :class="{ 'current-station': isCurrentStation(station) }"
          >
            <view class="timeline-marker">
              <view class="station-dot" :class="getStationDotClass(station, index)"></view>
              <view v-if="index < stationsList.length - 1" class="timeline-line"></view>
            </view>
            <view class="station-content">
              <view class="station-header">
                <text class="station-name">{{ station.stationName }}</text>
                <text v-if="station.isStart" class="station-tag start-tag">始发</text>
                <text v-else-if="station.isEnd" class="station-tag end-tag">终点</text>
                <text v-else class="station-tag middle-tag">经停</text>
              </view>
              <view class="station-time">
                <text class="arrival-time" v-if="station.arrivalTime">
                  到达: {{ formatTime(station.arrivalTime) }}
                </text>
                <text class="departure-time" v-if="station.departureTime">
                  发车: {{ formatTime(station.departureTime) }}
                </text>
                <text class="stop-time" v-if="station.stopDuration">
                  停车: {{ station.stopDuration }}分钟
                </text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 座位价格 -->
      <view v-if="currentTab === 2" class="tab-panel">
        <view class="seat-price-section">
          <view class="price-list">
            <view 
              v-for="seat in seatPriceList" 
              :key="seat.seatType"
              class="seat-price-item"
              :class="{ 'has-discount': seat.discount < 1 }"
            >
              <view class="seat-type-info">
                <text class="seat-type-name">{{ getSeatTypeName(seat.seatType) }}</text>
                <text class="seat-type-desc">{{ getSeatTypeDesc(seat.seatType) }}</text>
                <text v-if="seat.discount < 1" class="discount-tag">
                  {{ (seat.discount * 10).toFixed(1) }}折
                </text>
              </view>
              <view class="seat-price-info">
                <view class="price-main">
                  <text class="price-symbol">¥</text>
                  <text class="price-amount">{{ seat.price }}</text>
                </view>
                <view class="price-extra">
                  <text v-if="seat.originalPrice > seat.price" class="original-price">
                    ¥{{ seat.originalPrice }}
                  </text>
                  <text class="remaining-seats">余{{ seat.remaining }}张</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-actions">
      <view class="action-info">
        <text class="price-range">¥{{ minPrice }} - ¥{{ maxPrice }}</text>
        <text class="ticket-count">{{ scheduleInfo.availableTickets }}张可售</text>
      </view>
      <u-button
        type="primary"
        text="立即预订"
        size="large"
        :disabled="scheduleInfo.availableTickets <= 0"
        @click="goToBooking"
      />
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getScheduleDetail, getScheduleStations, getScheduleSeatPrices, getTrainInfo, getRouteDetail } from '@/api/schedule'

const scheduleId = ref('')
const currentTab = ref(0)
const tabs = [
  { name: '班次信息' },
  { name: '经停站点' },
  { name: '座位价格' }
]

// 班次信息
const scheduleInfo = ref({
  id: '',
  trainNumber: '',
  trainId: '',
  routeId: '',
  conductor: '',
  startStation: '',
  endStation: '',
  startTime: '',
  endTime: '',
  availableTickets: 0,
  status: 1
})

// 列车信息
const trainInfo = ref({
  trainName: '',
  trainModel: '',
  seatNum: 0,
  serviceYears: 0
})

// 线路信息
const routeInfo = ref({
  routeName: '',
  startStation: '',
  endStation: '',
  stationCount: 0
})

// 经停站点列表
const stationsList = ref([])

// 座位价格列表
const seatPriceList = ref([])

onMounted(() => {
  // 获取班次ID
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  scheduleId.value = currentPage.options.scheduleId || currentPage.options.trainId
  
  if (scheduleId.value) {
    loadScheduleDetail()
  }
})

const loadScheduleDetail = async () => {
  try {
    uni.showLoading({ title: '加载中...' })
    
    // 加载班次详情
    const scheduleResponse = await getScheduleDetail(scheduleId.value)
    scheduleInfo.value = scheduleResponse
    
    // 并行加载其他信息
    const [trainResponse, routeResponse, stationsResponse, seatPricesResponse] = await Promise.all([
      getTrainInfo(scheduleResponse.trainId),
      getRouteDetail(scheduleResponse.routeId),
      getScheduleStations(scheduleId.value),
      getScheduleSeatPrices(scheduleId.value)
    ])
    
    trainInfo.value = trainResponse
    routeInfo.value = routeResponse
    stationsList.value = processStations(stationsResponse)
    seatPriceList.value = seatPricesResponse || []
    
  } catch (error) {
    uni.showToast({
      title: '获取班次详情失败',
      icon: 'none'
    })
  } finally {
    uni.hideLoading()
  }
}

const processStations = (stations) => {
  return stations.map((station, index) => ({
    ...station,
    isStart: index === 0,
    isEnd: index === stations.length - 1,
    stopDuration: station.stopDuration || 0
  }))
}

const handleTabChange = (index) => {
  currentTab.value = index
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

const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const calculateDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return ''
  const start = new Date(startTime)
  const end = new Date(endTime)
  const duration = end - start
  const hours = Math.floor(duration / (1000 * 60 * 60))
  const minutes = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60))
  return `${hours}小时${minutes}分钟`
}

const getStatusText = (status) => {
  const statusMap = {
    1: '正常',
    2: '停运',
    3: '晚点'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    1: 'success',
    2: 'danger',
    3: 'warning'
  }
  return typeMap[status] || 'info'
}

const isCurrentStation = (station) => {
  // 这里可以根据当前时间判断是否为当前站点
  return false
}

const getStationDotClass = (station, index) => {
  if (station.isStart) return 'start-dot'
  if (station.isEnd) return 'end-dot'
  return 'middle-dot'
}

const getSeatTypeName = (seatType) => {
  const typeMap = {
    'BUSINESS': '商务座',
    'FIRST_CLASS': '一等座',
    'SECOND_CLASS': '二等座',
    'HARD_SEAT': '硬座',
    'SOFT_SEAT': '软座',
    'HARD_SLEEPER': '硬卧',
    'SOFT_SLEEPER': '软卧'
  }
  return typeMap[seatType] || '未知座位'
}

const getSeatTypeDesc = (seatType) => {
  const descMap = {
    'BUSINESS': '豪华舒适，商务首选',
    'FIRST_CLASS': '宽敞舒适，性价比高',
    'SECOND_CLASS': '经济实惠，出行首选',
    'HARD_SEAT': '基础座位，价格实惠',
    'SOFT_SEAT': '软座舒适，价格适中',
    'HARD_SLEEPER': '硬卧卧铺，夜间出行',
    'SOFT_SLEEPER': '软卧卧铺，舒适睡眠'
  }
  return descMap[seatType] || ''
}

const minPrice = computed(() => {
  if (seatPriceList.value.length === 0) return '0'
  return Math.min(...seatPriceList.value.map(seat => seat.price))
})

const maxPrice = computed(() => {
  if (seatPriceList.value.length === 0) return '0'
  return Math.max(...seatPriceList.value.map(seat => seat.price))
})

const goToBooking = () => {
  uni.navigateTo({
    url: `/pages/order-confirm/order-confirm?scheduleId=${scheduleId.value}`
  })
}
</script>

<style scoped lang="scss">
.train-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.train-header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
  color: #fff;
  border-radius: 0 0 30rpx 30rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);

  .train-basic-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;

    .train-number-section {
      display: flex;
      flex-direction: column;
      gap: 10rpx;

      .train-number {
        font-size: 48rpx;
        font-weight: bold;
      }

      .train-model {
        font-size: 28rpx;
        opacity: 0.9;
      }
    }
  }

  .route-overview {
    display: flex;
    align-items: center;
    justify-content: space-between;

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

      .departure-date, .arrival-date {
        font-size: 24rpx;
        opacity: 0.8;
      }
    }

    .route-middle {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0 20rpx;
      min-width: 120rpx;

      .duration-info {
        text-align: center;
        margin-bottom: 20rpx;

        .duration-time {
          display: block;
          font-size: 28rpx;
          font-weight: bold;
        }

        .duration-text {
          font-size: 24rpx;
          opacity: 0.8;
        }
      }

      .route-line {
        display: flex;
        align-items: center;
        width: 100%;

        .start-dot, .end-dot {
          width: 16rpx;
          height: 16rpx;
          border-radius: 50%;
          background: #fff;
        }

        .line {
          flex: 1;
          height: 2rpx;
          background: rgba(255, 255, 255, 0.6);
          margin: 0 10rpx;
        }
      }
    }
  }
}

.detail-tabs {
  background: #fff;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.tab-content {
  flex: 1;
  padding: 20rpx;
}

.tab-panel {
  .info-section {
    background: #fff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

    .section-title {
      display: flex;
      align-items: center;
      gap: 10rpx;
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 20rpx;
    }
  }
}

.stations-timeline {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .timeline-item {
    display: flex;
    position: relative;
    padding: 20rpx 0;

    &.current-station {
      .station-content {
        background: #f0f7ff;
      }
    }

    .timeline-marker {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-right: 30rpx;
      position: relative;

      .station-dot {
        width: 24rpx;
        height: 24rpx;
        border-radius: 50%;
        z-index: 2;

        &.start-dot {
          background: #4caf50;
        }

        &.end-dot {
          background: #f44336;
        }

        &.middle-dot {
          background: #2979ff;
        }
      }

      .timeline-line {
        position: absolute;
        top: 44rpx;
        bottom: -20rpx;
        width: 2rpx;
        background: #e0e0e0;
        z-index: 1;
      }
    }

    .station-content {
      flex: 1;
      background: #f8f8f8;
      border-radius: 16rpx;
      padding: 20rpx;

      .station-header {
        display: flex;
        align-items: center;
        gap: 20rpx;
        margin-bottom: 10rpx;

        .station-name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
        }

        .station-tag {
          padding: 4rpx 12rpx;
          border-radius: 12rpx;
          font-size: 24rpx;

          &.start-tag {
            background: #e8f5e8;
            color: #4caf50;
          }

          &.end-tag {
            background: #ffebee;
            color: #f44336;
          }

          &.middle-tag {
            background: #e3f2fd;
            color: #2979ff;
          }
        }
      }

      .station-time {
        display: flex;
        flex-wrap: wrap;
        gap: 20rpx;

        text {
          font-size: 24rpx;
          color: #666;
        }
      }
    }

    &:last-child {
      .timeline-line {
        display: none;
      }
    }
  }
}

.seat-price-section {
  .price-list {
    .seat-price-item {
      background: #fff;
      border-radius: 16rpx;
      padding: 30rpx;
      margin-bottom: 20rpx;
      box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
      display: flex;
      justify-content: space-between;
      align-items: center;

      &.has-discount {
        border: 2rpx solid #ff9800;
      }

      .seat-type-info {
        display: flex;
        flex-direction: column;
        gap: 8rpx;

        .seat-type-name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
        }

        .seat-type-desc {
          font-size: 24rpx;
          color: #666;
        }

        .discount-tag {
          background: #ff9800;
          color: #fff;
          padding: 4rpx 12rpx;
          border-radius: 12rpx;
          font-size: 24rpx;
          align-self: flex-start;
        }
      }

      .seat-price-info {
        text-align: right;

        .price-main {
          display: flex;
          align-items: baseline;
          gap: 4rpx;

          .price-symbol {
            font-size: 24rpx;
            color: #f56c6c;
          }

          .price-amount {
            font-size: 48rpx;
            font-weight: bold;
            color: #f56c6c;
          }
        }

        .price-extra {
          display: flex;
          flex-direction: column;
          gap: 4rpx;

          .original-price {
            font-size: 24rpx;
            color: #999;
            text-decoration: line-through;
          }

          .remaining-seats {
            font-size: 24rpx;
            color: #666;
          }
        }
      }
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

  .action-info {
    display: flex;
    flex-direction: column;
    gap: 8rpx;

    .price-range {
      font-size: 32rpx;
      font-weight: bold;
      color: #f56c6c;
    }

    .ticket-count {
      font-size: 24rpx;
      color: #666;
    }
  }
}
</style>