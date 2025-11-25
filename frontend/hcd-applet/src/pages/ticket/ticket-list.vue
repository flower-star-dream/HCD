<template>
  <view class="ticket-list-container">
    <!-- 车票状态标签页 -->
    <view class="ticket-tabs">
      <u-tabs 
        :list="tabs" 
        :current="currentTab" 
        @change="handleTabChange"
        line-color="#2979ff"
        active-color="#2979ff"
      />
    </view>

    <!-- 车票列表 -->
    <scroll-view scroll-y class="ticket-scroll">
      <view class="ticket-list">
        <view 
          v-for="ticket in ticketList" 
          :key="ticket.id"
          class="ticket-item"
          @click="goToTicketDetail(ticket)"
        >
          <view class="ticket-header">
            <view class="train-info">
              <text class="train-number">{{ ticket.trainNumber }}</text>
              <text class="seat-info">{{ ticket.seatType }} {{ ticket.seatNumber }}</text>
            </view>
            <view class="ticket-status">
              <u-tag 
                :text="getStatusText(ticket.status)" 
                :type="getStatusType(ticket.status)"
                size="mini"
              />
            </view>
          </view>

          <view class="ticket-route">
            <view class="station-info">
              <text class="station-name">{{ ticket.startStation }}</text>
              <text class="departure-time">{{ formatTime(ticket.startTime) }}</text>
            </view>
            <view class="route-arrow">
              <u-icon name="arrow-right" color="#999" size="32" />
              <text class="duration">{{ ticket.duration }}</text>
            </view>
            <view class="station-info">
              <text class="station-name">{{ ticket.endStation }}</text>
              <text class="arrival-time">{{ formatTime(ticket.endTime) }}</text>
            </view>
          </view>

          <view class="ticket-passenger">
            <text class="passenger-name">{{ ticket.realName }}</text>
            <text class="id-card">{{ formatIdCard(ticket.idCard) }}</text>
          </view>

          <view class="ticket-footer">
            <text class="ticket-price">¥{{ ticket.money }}</text>
            <view class="ticket-actions">
              <u-button
                v-if="ticket.status === 1"
                type="primary"
                text="退票"
                size="mini"
                @click.stop="handleRefund(ticket)"
              />
              <u-button
                v-if="ticket.status === 1"
                type="info"
                text="改签"
                size="mini"
                @click.stop="handleChange(ticket)"
              />
            </view>
          </view>
        </view>

        <view v-if="ticketList.length === 0" class="empty-state">
          <u-empty
            mode="data"
            :text="getEmptyText()"
            icon="/static/empty-ticket.png"
          />
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyTickets } from '@/api/ticket'

const currentTab = ref(0)
const ticketList = ref([])
const loading = ref(false)

const tabs = [
  { name: '全部' },
  { name: '正常' },
  { name: '已使用' },
  { name: '已退票' }
]

const statusMap = {
  1: '正常',
  2: '已使用', 
  3: '已取消',
  4: '已改签',
  5: '已退票'
}

const statusTypeMap = {
  1: 'success',
  2: 'info',
  3: 'danger',
  4: 'warning',
  5: 'info'
}

onMounted(() => {
  loadTicketList()
})

const filteredTicketList = computed(() => {
  if (currentTab.value === 0) {
    return ticketList.value
  }
  const statusFilter = currentTab.value
  return ticketList.value.filter(ticket => ticket.status === statusFilter)
})

const loadTicketList = async () => {
  loading.value = true
  try {
    const response = await getMyTickets()
    ticketList.value = response || []
  } catch (error) {
    uni.showToast({
      title: '获取车票列表失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

const handleTabChange = (index) => {
  currentTab.value = index
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}月${date.getDate()}日 ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatIdCard = (idCard) => {
  if (!idCard) return ''
  if (idCard.length === 18) {
    return idCard.substring(0, 4) + '**********' + idCard.substring(14)
  }
  return idCard
}

const getStatusText = (status) => {
  return statusMap[status] || '未知状态'
}

const getStatusType = (status) => {
  return statusTypeMap[status] || 'info'
}

const getEmptyText = () => {
  const texts = ['暂无车票信息', '暂无正常车票', '暂无已使用车票', '暂无已退票']
  return texts[currentTab.value]
}

const goToTicketDetail = (ticket) => {
  uni.navigateTo({
    url: `/pages/ticket/ticket-detail?ticketId=${ticket.id}`
  })
}

const handleRefund = (ticket) => {
  uni.showModal({
    title: '退票确认',
    content: `确定要退掉${ticket.realName}的${ticket.trainNumber}次列车车票吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await refundTicket(ticket.id)
          uni.showToast({
            title: '退票成功',
            icon: 'success'
          })
          loadTicketList()
        } catch (error) {
          uni.showToast({
            title: '退票失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

const handleChange = (ticket) => {
  uni.showToast({
    title: '改签功能开发中...',
    icon: 'none'
  })
}

const refundTicket = async (ticketId) => {
  // 调用退票API
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve()
    }, 1000)
  })
}
</script>

<style scoped lang="scss">
.ticket-list-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.ticket-tabs {
  background: #fff;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.ticket-scroll {
  flex: 1;
  padding: 20rpx;
}

.ticket-list {
  .ticket-item {
    background: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

    .ticket-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20rpx;

      .train-info {
        display: flex;
        align-items: center;
        gap: 20rpx;

        .train-number {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
        }

        .seat-info {
          font-size: 26rpx;
          color: #666;
        }
      }
    }

    .ticket-route {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20rpx;
      padding: 20rpx 0;
      border-top: 1rpx solid #f0f0f0;
      border-bottom: 1rpx solid #f0f0f0;

      .station-info {
        display: flex;
        flex-direction: column;
        align-items: center;
        flex: 1;

        .station-name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 8rpx;
        }

        .departure-time, .arrival-time {
          font-size: 24rpx;
          color: #999;
        }
      }

      .route-arrow {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 0 20rpx;

        .duration {
          font-size: 22rpx;
          color: #999;
          margin-top: 10rpx;
        }
      }
    }

    .ticket-passenger {
      display: flex;
      align-items: center;
      gap: 20rpx;
      margin-bottom: 20rpx;

      .passenger-name {
        font-size: 28rpx;
        color: #333;
        font-weight: bold;
      }

      .id-card {
        font-size: 24rpx;
        color: #666;
      }
    }

    .ticket-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .ticket-price {
        font-size: 32rpx;
        font-weight: bold;
        color: #f56c6c;
      }

      .ticket-actions {
        display: flex;
        gap: 20rpx;
      }
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
</style>