<template>
  <view class="ticket-search-container">
    <!-- 搜索表单 -->
    <view class="search-form-section">
      <view class="form-card">
        <!-- 出发地和目的地 -->
        <view class="station-section">
          <view class="station-item" @click="selectDeparture">
            <text class="station-label">出发地</text>
            <text class="station-name">{{ searchForm.departure || '请选择出发地' }}</text>
            <u-icon name="arrow-right" color="#999" size="24" />
          </view>
          
          <view class="exchange-btn" @click="exchangeStations">
            <u-icon name="swap" color="#2979ff" size="32" />
          </view>
          
          <view class="station-item" @click="selectArrival">
            <text class="station-label">目的地</text>
            <text class="station-name">{{ searchForm.arrival || '请选择目的地' }}</text>
            <u-icon name="arrow-right" color="#999" size="24" />
          </view>
        </view>
        
        <!-- 出发日期 -->
        <view class="date-section" @click="showDatePicker = true">
          <text class="date-label">出发日期</text>
          <text class="date-value">{{ formatDate(searchForm.date) || '请选择日期' }}</text>
          <u-icon name="calendar" color="#2979ff" size="28" />
        </view>
        
        <!-- 搜索按钮 -->
        <u-button 
          type="primary" 
          text="查询车票" 
          size="large"
          :loading="loading"
          @click="searchTickets"
        />
      </view>
    </view>
    
    <!-- 历史记录 -->
    <view class="history-section" v-if="searchHistory.length > 0">
      <view class="section-header">
        <text class="section-title">搜索历史</text>
        <text class="clear-btn" @click="clearHistory">清空</text>
      </view>
      <view class="history-list">
        <view 
          v-for="(item, index) in searchHistory" 
          :key="index"
          class="history-item"
          @click="selectHistory(item)"
        >
          <view class="history-route">
            <text class="history-station">{{ item.departure }}</text>
            <u-icon name="arrow-right" color="#999" size="20" />
            <text class="history-station">{{ item.arrival }}</text>
          </view>
          <text class="history-date">{{ formatDate(item.date) }}</text>
        </view>
      </view>
    </view>
    
    <!-- 热门城市 -->
    <view class="hot-city-section">
      <view class="section-header">
        <text class="section-title">热门城市</text>
      </view>
      <view class="hot-city-grid">
        <view 
          v-for="city in hotCities" 
          :key="city"
          class="city-item"
          @click="selectHotCity(city)"
        >
          <text class="city-name">{{ city }}</text>
        </view>
      </view>
    </view>
    
    <!-- 站点选择弹窗 -->
    <u-popup :show="showStationPopup" mode="bottom" @close="showStationPopup = false">
      <view class="station-popup">
        <view class="popup-header">
          <text class="popup-title">选择{{ stationSelectType === 'departure' ? '出发地' : '目的地' }}</text>
          <u-icon name="close" color="#999" size="24" @click="showStationPopup = false" />
        </view>
        
        <!-- 搜索框 -->
        <view class="station-search">
          <u-search 
            v-model="stationKeyword" 
            placeholder="搜索站点"
            :show-action="false"
            @change="searchStations"
          />
        </view>
        
        <!-- 站点列表 -->
        <scroll-view scroll-y class="station-list">
          <view 
            v-for="station in filteredStations" 
            :key="station.id"
            class="station-option"
            @click="confirmStation(station)"
          >
            <text class="station-name">{{ station.stationName }}</text>
            <text class="station-pinyin">{{ station.pinyin }}</text>
          </view>
          
          <view v-if="filteredStations.length === 0" class="empty-stations">
            <u-empty mode="search" text="未找到相关站点" />
          </view>
        </scroll-view>
      </view>
    </u-popup>
    
    <!-- 日期选择器 -->
    <u-datetime-picker
      :show="showDatePicker"
      mode="date"
      :min-date="minDate"
      :max-date="maxDate"
      :default-value="searchForm.date ? new Date(searchForm.date).getTime() : Date.now()"
      @confirm="handleDateConfirm"
      @cancel="showDatePicker = false"
    />
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getStations } from '@/api/train'

// 搜索表单
const searchForm = reactive({
  departure: '',
  arrival: '',
  date: ''
})

// 状态控制
const loading = ref(false)
const showStationPopup = ref(false)
const showDatePicker = ref(false)
const stationSelectType = ref('departure')
const stationKeyword = ref('')

// 数据列表
const stations = ref([])
const searchHistory = ref([])

// 热门城市
const hotCities = [
  '北京', '上海', '广州', '深圳', '杭州', '南京', '苏州', '天津',
  '重庆', '成都', '武汉', '西安', '长沙', '郑州', '青岛', '大连'
]

// 日期范围
const minDate = computed(() => Date.now())
const maxDate = computed(() => Date.now() + 90 * 24 * 60 * 60 * 1000)

// 过滤后的站点
const filteredStations = computed(() => {
  if (!stationKeyword.value) return stations.value
  const keyword = stationKeyword.value.toLowerCase()
  return stations.value.filter(station => 
    station.stationName.toLowerCase().includes(keyword) ||
    station.pinyin.toLowerCase().includes(keyword)
  )
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const today = new Date()
  const tomorrow = new Date(today)
  tomorrow.setDate(tomorrow.getDate() + 1)
  
  let dateText = ''
  if (date.toDateString() === today.toDateString()) {
    dateText = '今天'
  } else if (date.toDateString() === tomorrow.toDateString()) {
    dateText = '明天'
  } else {
    const month = date.getMonth() + 1
    const day = date.getDate()
    const weekDays = ['日', '一', '二', '三', '四', '五', '六']
    const weekDay = weekDays[date.getDay()]
    dateText = `${month}月${day}日 周${weekDay}`
  }
  
  return dateText
}

// 加载站点数据
const loadStations = async () => {
  try {
    const response = await getStations()
    stations.value = response || []
  } catch (error) {
    console.error('加载站点失败:', error)
    uni.showToast({
      title: '加载站点失败',
      icon: 'none'
    })
  }
}

// 加载搜索历史
const loadSearchHistory = () => {
  const history = uni.getStorageSync('ticketSearchHistory')
  if (history) {
    searchHistory.value = JSON.parse(history)
  }
}

// 选择出发地
const selectDeparture = () => {
  stationSelectType.value = 'departure'
  showStationPopup.value = true
}

// 选择目的地
const selectArrival = () => {
  stationSelectType.value = 'arrival'
  showStationPopup.value = true
}

// 交换站点
const exchangeStations = () => {
  const temp = searchForm.departure
  searchForm.departure = searchForm.arrival
  searchForm.arrival = temp
}

// 确认站点选择
const confirmStation = (station) => {
  if (stationSelectType.value === 'departure') {
    searchForm.departure = station.stationName
  } else {
    searchForm.arrival = station.stationName
  }
  showStationPopup.value = false
  stationKeyword.value = ''
}

// 处理日期确认
const handleDateConfirm = (e) => {
  const date = new Date(e.value)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  searchForm.date = `${year}-${month}-${day}`
  showDatePicker.value = false
}

// 搜索车票
const searchTickets = () => {
  if (!searchForm.departure || !searchForm.arrival || !searchForm.date) {
    uni.showToast({
      title: '请完善搜索信息',
      icon: 'none'
    })
    return
  }
  
  if (searchForm.departure === searchForm.arrival) {
    uni.showToast({
      title: '出发地和目的地不能相同',
      icon: 'none'
    })
    return
  }
  
  // 保存搜索历史
  saveSearchHistory()
  
  // 跳转到车次列表
  uni.navigateTo({
    url: `/pages/train-list/train-list?departure=${searchForm.departure}&arrival=${searchForm.arrival}&date=${searchForm.date}`
  })
}

// 保存搜索历史
const saveSearchHistory = () => {
  const searchItem = {
    departure: searchForm.departure,
    arrival: searchForm.arrival,
    date: searchForm.date
  }
  
  // 移除重复项
  searchHistory.value = searchHistory.value.filter(item => 
    !(item.departure === searchItem.departure && 
      item.arrival === searchItem.arrival && 
      item.date === searchItem.date)
  )
  
  // 添加到开头
  searchHistory.value.unshift(searchItem)
  
  // 限制历史记录数量
  if (searchHistory.value.length > 5) {
    searchHistory.value = searchHistory.value.slice(0, 5)
  }
  
  // 保存到本地存储
  uni.setStorageSync('ticketSearchHistory', JSON.stringify(searchHistory.value))
}

// 清空搜索历史
const clearHistory = () => {
  uni.showModal({
    title: '提示',
    content: '确定要清空搜索历史吗？',
    success: (res) => {
      if (res.confirm) {
        searchHistory.value = []
        uni.removeStorageSync('ticketSearchHistory')
      }
    }
  })
}

// 选择历史记录
const selectHistory = (item) => {
  searchForm.departure = item.departure
  searchForm.arrival = item.arrival
  searchForm.date = item.date
}

// 选择热门城市
const selectHotCity = (city) => {
  if (!searchForm.departure) {
    searchForm.departure = city
  } else if (!searchForm.arrival) {
    searchForm.arrival = city
  } else {
    // 如果都已选择，替换目的地
    searchForm.arrival = city
  }
}

// 搜索站点
const searchStations = () => {
  // 过滤逻辑在computed属性中处理
}

onMounted(() => {
  loadStations()
  loadSearchHistory()
  
  // 设置默认日期为今天
  if (!searchForm.date) {
    const today = new Date()
    const year = today.getFullYear()
    const month = String(today.getMonth() + 1).padStart(2, '0')
    const day = String(today.getDate()).padStart(2, '0')
    searchForm.date = `${year}-${month}-${day}`
  }
})
</script>

<style lang="scss" scoped>
.ticket-search-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20rpx;
}

.search-form-section {
  margin-bottom: 30rpx;
}

.form-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.station-section {
  position: relative;
  margin-bottom: 40rpx;
  
  .station-item {
    display: flex;
    align-items: center;
    padding: 30rpx 0;
    border-bottom: 2rpx solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .station-label {
      width: 120rpx;
      font-size: 28rpx;
      color: #999;
    }
    
    .station-name {
      flex: 1;
      font-size: 32rpx;
      color: #333;
      font-weight: 500;
    }
  }
  
  .exchange-btn {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    width: 80rpx;
    height: 80rpx;
    background: #f5f5f5;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 10;
    border: 4rpx solid #fff;
  }
}

.date-section {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 2rpx solid #f0f0f0;
  margin-bottom: 40rpx;
  
  .date-label {
    width: 120rpx;
    font-size: 28rpx;
    color: #999;
  }
  
  .date-value {
    flex: 1;
    font-size: 32rpx;
    color: #333;
    font-weight: 500;
  }
}

.history-section,
.hot-city-section {
  background: #fff;
  border-radius: 24rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    
    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
    
    .clear-btn {
      font-size: 26rpx;
      color: #999;
    }
  }
}

.history-list {
  .history-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .history-route {
      display: flex;
      align-items: center;
      gap: 10rpx;
      
      .history-station {
        font-size: 28rpx;
        color: #333;
      }
    }
    
    .history-date {
      font-size: 24rpx;
      color: #999;
    }
  }
}

.hot-city-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
  
  .city-item {
    background: #f8f8f8;
    border-radius: 12rpx;
    padding: 20rpx;
    text-align: center;
    
    .city-name {
      font-size: 28rpx;
      color: #666;
    }
  }
}

.station-popup {
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 80vh;
  
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
  
  .station-search {
    padding: 20rpx 30rpx;
  }
  
  .station-list {
    height: 600rpx;
    padding: 0 30rpx;
    
    .station-option {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 30rpx 0;
      border-bottom: 1rpx solid #f0f0f0;
      
      .station-name {
        font-size: 32rpx;
        color: #333;
      }
      
      .station-pinyin {
        font-size: 24rpx;
        color: #999;
      }
    }
    
    .empty-stations {
      padding: 100rpx 0;
    }
  }
}
</style>