<template>
  <view class="train-list-container">
    <!-- 筛选条件 -->
    <view class="filter-section">
      <view class="filter-item">
        <text class="filter-label">出发地</text>
        <text class="filter-value">{{ departure }}</text>
      </view>
      <view class="filter-item">
        <text class="filter-label">目的地</text>
        <text class="filter-value">{{ arrival }}</text>
      </view>
      <view class="filter-item">
        <text class="filter-label">日期</text>
        <text class="filter-value">{{ travelDate }}</text>
      </view>
    </view>

    <!-- 车次列表 -->
    <scroll-view scroll-y class="train-scroll">
      <view class="train-list">
        <view 
          v-for="train in trainList" 
          :key="train.id"
          class="train-item"
          @click="selectTrain(train)"
        >
          <view class="train-info">
            <view class="train-header">
              <text class="train-number">{{ train.trainNumber }}</text>
              <text class="train-type">{{ train.trainType }}</text>
            </view>
            <view class="train-route">
              <view class="station-info">
                <text class="station-name">{{ train.startStation }}</text>
                <text class="departure-time">{{ formatTime(train.startTime) }}</text>
              </view>
              <view class="route-arrow">
                <u-icon name="arrow-right" color="#2979ff" size="32" />
                <text class="duration">{{ train.duration }}</text>
              </view>
              <view class="station-info">
                <text class="station-name">{{ train.endStation }}</text>
                <text class="arrival-time">{{ formatTime(train.endTime) }}</text>
              </view>
            </view>
          </view>
          <view class="train-price">
            <text class="price">¥{{ train.minPrice }}</text>
            <text class="price-desc">起</text>
          </view>
        </view>
        
        <view v-if="trainList.length === 0" class="empty-state">
          <u-empty mode="data" text="暂无车次信息" />
        </view>
      </view>
    </scroll-view>

    <!-- 底部提示 -->
    <view class="bottom-tips">
      <text>共找到 {{ trainList.length }} 个车次</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { searchTrains } from '@/api/train'

const departure = ref('')
const arrival = ref('')
const travelDate = ref('')
const trainList = ref([])
const loading = ref(false)

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

const loadTrainList = async () => {
  loading.value = true
  try {
    const params = {
      departure: departure.value,
      arrival: arrival.value,
      date: travelDate.value
    }
    
    const response = await searchTrains(params)
    trainList.value = response || []
  } catch (error) {
    uni.showToast({
      title: '获取车次列表失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatDate = (date) => {
  const d = new Date(date)
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

const selectTrain = (train) => {
  uni.navigateTo({
    url: `/pages/train/train-detail?trainId=${train.id}`
  })
}
</script>

<style scoped lang="scss">
.train-list-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.filter-section {
  background: #fff;
  display: flex;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .filter-item {
    flex: 1;
    text-align: center;
    display: flex;
    flex-direction: column;
    gap: 10rpx;

    .filter-label {
      font-size: 24rpx;
      color: #999;
    }

    .filter-value {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }
}

.train-scroll {
  flex: 1;
  padding: 20rpx;
}

.train-list {
  .train-item {
    background: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

    .train-info {
      flex: 1;

      .train-header {
        display: flex;
        align-items: center;
        gap: 20rpx;
        margin-bottom: 20rpx;

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
      }

      .train-route {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .station-info {
          display: flex;
          flex-direction: column;
          align-items: center;
          flex: 1;

          .station-name {
            font-size: 28rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 8rpx;
          }

          .departure-time, .arrival-time {
            font-size: 24rpx;
            color: #666;
          }
        }

        .route-arrow {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 0 20rpx;

          .duration {
            font-size: 20rpx;
            color: #999;
            margin-top: 8rpx;
          }
        }
      }
    }

    .train-price {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 8rpx;

      .price {
        font-size: 40rpx;
        font-weight: bold;
        color: #f56c6c;
      }

      .price-desc {
        font-size: 24rpx;
        color: #999;
      }
    }
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 200rpx 0;
  }
}

.bottom-tips {
  background: #fff;
  padding: 30rpx;
  text-align: center;
  font-size: 26rpx;
  color: #999;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
}
</style>