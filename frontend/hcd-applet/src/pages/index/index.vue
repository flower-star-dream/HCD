<template>
  <view class="home-container">
    <!-- 搜索栏 -->
    <view class="search-section">
      <view class="search-form">
        <view class="form-item">
          <text class="label">出发地</text>
          <u--input
            v-model="searchForm.departure"
            placeholder="请选择出发地"
            border="none"
            @click="showStationPicker = true"            readonly
          />
        </view>

        <view class="exchange-btn" @click="exchangeStations">
          <u-icon name="swap" size="24" color="#2979ff" />
        </view>

        <view class="form-item">
          <text class="label">目的地</text>
          <u--input
            v-model="searchForm.arrival"
            placeholder="请选择目的地"
            border="none"
            @click="showStationPicker = true"            readonly
          />
        </view>

        <view class="form-item">
          <text class="label">出发日期</text>
          <u--input
            v-model="searchForm.date"
            placeholder="请选择日期"
            border="none"
            @click="showDatePicker = true"            readonly
          />
        </view>

        <u-button type="primary" text="查询车票" @click="searchTickets" />
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-actions">
      <view class="action-item" @click="goToOrderList">
        <u-icon name="order" size="32" color="#2979ff" />
        <text>我的订单</text>
      </view>
      <view class="action-item" @click="goToPassenger">
        <u-icon name="account" size="32" color="#19be6b" />
        <text>常用乘客</text>
      </view>
      <view class="action-item" @click="goToProfile">
        <u-icon name="setting" size="32" color="#ff9900" />
        <text>个人中心</text>
      </view>
    </view>

    <!-- 推荐车次 -->
    <view class="recommend-section">
      <view class="section-title">
        <text>热门车次</text>
        <text class="more" @click="goToSearch">查看更多</text>
      </view>

      <view class="train-list">
        <view
          v-for="train in recommendTrains"          :key="train.id"          class="train-item"          @click="selectTrain(train)"        >
          <view class="train-info">
            <text class="train-number">{{ train.trainNumber }}</text>
            <view class="train-route">
              <text>{{ train.departureStation }}</text>
              <u-icon name="arrow-right" size="16" color="#999" />
              <text>{{ train.arrivalStation }}</text>
            </view>
            <view class="train-time">
              <text>{{ train.departureTime }}</text>
              <text class="duration">{{ train.duration }}</text>
              <text>{{ train.arrivalTime }}</text>
            </view>
          </view>
          <view class="train-price">
            <text class="price">¥{{ train.price }}</text>
            <text class="seat-info">{{ train.seatInfo }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 站点选择器 -->
    <u-picker
      :show="showStationPicker"
      :columns="stationColumns"
      @confirm="handleStationSelect"
      @cancel="showStationPicker = false"    />

    <!-- 日期选择器 -->
    <u-datetime-picker
      :show="showDatePicker"
      mode="date"
      :min-date="minDate"
      :max-date="maxDate"      @confirm="handleDateSelect"
      @cancel="showDatePicker = false"
    />
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getStations } from '@/api/train'

// 搜索表单
const searchForm = ref({
  departure: '北京',
  arrival: '上海',
  date: ''
})

// 显示控制
const showStationPicker = ref(false)
const showDatePicker = ref(false)
const stationColumns = ref([[]])
const stations = ref([])

// 日期范围
const minDate = computed(() => Date.now())
const maxDate = computed(() => Date.now() + 30 * 24 * 60 * 60 * 1000)

// 推荐车次
const recommendTrains = ref([
  {
    id: 1,
    trainNumber: 'G123',
    departureStation: '北京',
    arrivalStation: '上海',
    departureTime: '08:00',
    arrivalTime: '12:30',
    duration: '4小时30分',
    price: 553,
    seatInfo: '二等座有票'
  },
  {
    id: 2,
    trainNumber: 'G456',
    departureStation: '北京',
    arrivalStation: '广州',
    departureTime: '09:00',
    arrivalTime: '14:30',
    duration: '5小时30分',
    price: 862,
    seatInfo: '二等座有票'
  },
  {
    id: 3,
    trainNumber: 'G789',
    departureStation: '上海',
    arrivalStation: '深圳',
    departureTime: '10:00',
    arrivalTime: '15:30',
    duration: '5小时30分',
    price: 737,
    seatInfo: '二等座有票'
  }
])

// 初始化日期
const initDate = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  searchForm.value.date = `${year}-${month}-${day}`
}

// 加载站点数据
const loadStations = async () => {
  try {
    const response = await getStations()
    stations.value = response
    stationColumns.value[0] = response.map(item => item.name)
  } catch (error) {
    console.error('加载站点失败:', error)
  }
}

// 交换站点
const exchangeStations = () => {
  const temp = searchForm.value.departure
  searchForm.value.departure = searchForm.value.arrival
  searchForm.value.arrival = temp
}

// 处理站点选择
const handleStationSelect = (e) => {
  const selectedStation = e.value[0]
  // 这里需要判断是选择出发地还是目的地
  showStationPicker.value = false
}

// 处理日期选择
const handleDateSelect = (e) => {
  const date = new Date(e.value)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  searchForm.value.date = `${year}-${month}-${day}`
  showDatePicker.value = false
}

// 搜索车票
const searchTickets = () => {
  if (!searchForm.value.departure || !searchForm.value.arrival || !searchForm.value.date) {
    uni.showToast({
      title: '请完善搜索信息',
      icon: 'none'
    })
    return
  }

  uni.navigateTo({
    url: `/pages/train-list/train-list?departure=${searchForm.value.departure}&arrival=${searchForm.value.arrival}&date=${searchForm.value.date}`
  })
}

// 跳转到订单列表
const goToOrderList = () => {
  uni.switchTab({
    url: '/pages/order/order-list'
  })
}

// 跳转到乘客管理
const goToPassenger = () => {
  uni.navigateTo({
    url: '/pages/passenger/passenger-list'
  })
}

// 跳转到个人中心
const goToProfile = () => {
  uni.switchTab({
    url: '/pages/user/profile'
  })
}

// 跳转到搜索页
const goToSearch = () => {
  uni.navigateTo({
    url: '/pages/search/search'
  })
}

// 选择车次
const selectTrain = (train) => {
  searchForm.value.departure = train.departureStation
  searchForm.value.arrival = train.arrivalStation
  searchTickets()
}

onShow(() => {
  initDate()
  loadStations()
})
</script>

<style lang="scss" scoped>
.home-container {
  padding: 20rpx;
  background-color: #f8f8f8;
  min-height: 100vh;
}

.search-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
}

.search-form {
  .form-item {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #eee;

    &:last-child {
      border-bottom: none;
    }

    .label {
      width: 120rpx;
      font-size: 28rpx;
      color: #666;
    }

    .u-input {
      flex: 1;
    }
  }

  .exchange-btn {
    width: 80rpx;
    height: 80rpx;
    margin: 20rpx auto;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;
    border-radius: 50%;
  }

  .u-button {
    margin-top: 30rpx;
  }
}

.quick-actions {
  display: flex;
  justify-content: space-around;
  background: #fff;
  border-radius: 16rpx;
  padding: 40rpx 0;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);

  .action-item {
    display: flex;
    flex-direction: column;
    align-items: center;

    text {
      margin-top: 10rpx;
      font-size: 24rpx;
      color: #666;
    }
  }
}

.recommend-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);

  .section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    text {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }

    .more {
      font-size: 24rpx;
      color: #999;
    }
  }

  .train-list {
    .train-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .train-info {
        flex: 1;

        .train-number {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 10rpx;
        }

        .train-route {
          display: flex;
          align-items: center;
          font-size: 28rpx;
          color: #666;
          margin-bottom: 10rpx;

          text {
            margin: 0 10rpx;
          }
        }

        .train-time {
          display: flex;
          align-items: center;
          font-size: 24rpx;
          color: #999;

          .duration {
            margin: 0 20rpx;
            color: #666;
          }
        }
      }

      .train-price {
        text-align: right;

        .price {
          font-size: 32rpx;
          font-weight: bold;
          color: #ff6b35;
        }

        .seat-info {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
  }
}
</style>