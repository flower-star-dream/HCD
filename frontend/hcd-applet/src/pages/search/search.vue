<template>
  <view class="search-container">
    <!-- 搜索栏 -->
    <view class="search-header">
      <view class="search-box">
        <u-search 
          v-model="searchKeyword" 
          placeholder="搜索车次、车站"
          :show-action="false"
          @search="handleSearch"
        />
      </view>
    </view>

    <!-- 热门搜索 -->
    <view class="hot-search-section" v-if="!searchKeyword">
      <view class="section-title">热门搜索</view>
      <view class="hot-tags">
        <u-tag 
          v-for="tag in hotTags" 
          :key="tag"
          :text="tag"
          type="info"
          @click="selectHotTag(tag)"
        />
      </view>
    </view>

    <!-- 搜索结果 -->
    <view class="search-results" v-if="searchKeyword">
      <view class="result-item" v-for="result in searchResults" :key="result.id">
        <view class="result-info">
          <text class="result-title">{{ result.title }}</text>
          <text class="result-desc">{{ result.description }}</text>
        </view>
      </view>
      
      <view v-if="searchResults.length === 0" class="empty-result">
        <u-empty mode="search" text="暂无搜索结果" />
      </view>
    </view>

    <!-- 搜索历史 -->
    <view class="search-history" v-if="!searchKeyword && searchHistory.length > 0">
      <view class="section-header">
        <text class="section-title">搜索历史</text>
        <text class="clear-history" @click="clearHistory">清除</text>
      </view>
      <view class="history-list">
        <view 
          v-for="(item, index) in searchHistory" 
          :key="index"
          class="history-item"
          @click="selectHistory(item)"
        >
          <u-icon name="clock" size="24" color="#999" />
          <text class="history-text">{{ item }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const searchKeyword = ref('')
const searchResults = ref([])
const searchHistory = ref([])
const hotTags = ref(['G1234', '北京', '上海', '广州', '深圳'])

onMounted(() => {
  loadSearchHistory()
})

const loadSearchHistory = () => {
  const history = uni.getStorageSync('searchHistory') || []
  searchHistory.value = history
}

const handleSearch = () => {
  if (!searchKeyword.value.trim()) return
  
  // 保存搜索历史
  saveSearchHistory(searchKeyword.value)
  
  // 模拟搜索结果
  searchResults.value = [
    {
      id: 1,
      title: searchKeyword.value + '次列车',
      description: '北京 → 上海 07:00-12:30'
    },
    {
      id: 2,
      title: searchKeyword.value + '车站',
      description: '北京市朝阳区'
    }
  ]
}

const selectHotTag = (tag) => {
  searchKeyword.value = tag
  handleSearch()
}

const selectHistory = (item) => {
  searchKeyword.value = item
  handleSearch()
}

const saveSearchHistory = (keyword) => {
  let history = uni.getStorageSync('searchHistory') || []
  // 移除重复项
  history = history.filter(item => item !== keyword)
  // 添加到开头
  history.unshift(keyword)
  // 最多保存10条
  if (history.length > 10) {
    history = history.slice(0, 10)
  }
  uni.setStorageSync('searchHistory', history)
  searchHistory.value = history
}

const clearHistory = () => {
  uni.showModal({
    title: '提示',
    content: '确定要清除搜索历史吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('searchHistory')
        searchHistory.value = []
      }
    }
  })
}
</script>

<style scoped lang="scss">
.search-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.search-header {
  background: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .search-box {
    width: 100%;
  }
}

.hot-search-section {
  background: #fff;
  margin: 20rpx 0;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }

  .hot-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 20rpx;
  }
}

.search-results {
  background: #fff;
  margin: 20rpx 0;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .result-item {
    padding: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .result-info {
      display: flex;
      flex-direction: column;
      gap: 10rpx;

      .result-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }

      .result-desc {
        font-size: 26rpx;
        color: #666;
      }
    }
  }

  .empty-result {
    padding: 100rpx 0;
  }
}

.search-history {
  background: #fff;
  margin: 20rpx 0;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }

    .clear-history {
      font-size: 26rpx;
      color: #2979ff;
    }
  }

  .history-list {
    .history-item {
      display: flex;
      align-items: center;
      gap: 20rpx;
      padding: 30rpx;
      border-bottom: 1rpx solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .history-text {
        font-size: 28rpx;
        color: #333;
        flex: 1;
      }
    }
  }
}
</style>