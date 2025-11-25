<template>
  <view class="my-tickets-container">
    <!-- 顶部统计卡片 -->
    <view class="stats-card">
      <view class="stats-header">
        <text class="stats-title">我的车票</text>
        <text class="stats-subtitle">{{ todayDate }}</text>
      </view>
      <view class="stats-content">
        <view class="stat-item">
          <text class="stat-number">{{ stats.total }}</text>
          <text class="stat-label">总票数</text>
        </view>
        <view class="stat-item">
          <text class="stat-number">{{ stats.normal }}</text>
          <text class="stat-label">正常</text>
        </view>
        <view class="stat-item">
          <text class="stat-number">{{ stats.used }}</text>
          <text class="stat-label">已使用</text>
        </view>
        <view class="stat-item">
          <text class="stat-number">{{ stats.refunded }}</text>
          <text class="stat-label">已退票</text>
        </view>
      </view>
    </view>

    <!-- 筛选标签 -->
    <view class="filter-tabs">
      <u-tabs 
        :list="filterTabs" 
        :current="currentFilter" 
        @change="handleFilterChange"
        line-color="#2979ff"
        active-color="#2979ff"
      />
    </view>

    <!-- 车票列表 -->
    <scroll-view scroll-y class="tickets-scroll" refresher-enabled @refresherrefresh="handleRefresh">
      <view class="tickets-list">
        <!-- 今日车票 -->
        <view v-if="todayTickets.length > 0" class="ticket-section">
          <view class="section-header">
            <u-icon name="clock-fill" color="#ff9800" size="24" />
            <text class="section-title">今日车票</text>
            <text class="section-count">{{ todayTickets.length }}张</text>
          </view>
          <view class="ticket-cards">
            <view 
              v-for="ticket in todayTickets" 
              :key="ticket.id"
              class="ticket-card today-ticket"
              @click="goToTicketDetail(ticket)"
            >
              <view class="card-content">
                <view class="ticket-header">
                  <text class="train-number">{{ ticket.trainNumber }}</text>
                  <u-tag 
                    :text="getStatusText(ticket.status)" 
                    :type="getStatusType(ticket.status)"
                    size="mini"
                  />
                </view>
                <view class="ticket-route">
                  <view class="station-info">
                    <text class="station-name">{{ ticket.startStation }}</text>
                    <text class="station-time">{{ formatTime(ticket.startTime) }}</text>
                  </view>
                  <view class="route-arrow">
                    <u-icon name="arrow-right" color="#2979ff" size="32" />
                    <text class="duration">{{ ticket.duration }}</text>
                  </view>
                  <view class="station-info">
                    <text class="station-name">{{ ticket.endStation }}</text>
                    <text class="station-time">{{ formatTime(ticket.endTime) }}</text>
                  </view>
                </view>
                <view class="ticket-footer">
                  <view class="passenger-info">
                    <text class="passenger-name">{{ ticket.realName }}</text>
                    <text class="seat-info">{{ getSeatTypeText(ticket.seatType) }} {{ ticket.seatNumber }}</text>
                  </view>
                  <view class="ticket-actions">
                    <u-button
                      v-if="ticket.status === 1"
                      type="primary"
                      text="验票"
                      size="mini"
                      @click.stop="showTicketQr(ticket)"
                    />
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 其他车票 -->
        <view v-if="otherTickets.length > 0" class="ticket-section">
          <view class="section-header">
            <u-icon name="calendar-fill" color="#2979ff" size="24" />
            <text class="section-title">其他车票</text>
            <text class="section-count">{{ otherTickets.length }}张</text>
          </view>
          <view class="ticket-cards">
            <view 
              v-for="ticket in otherTickets" 
              :key="ticket.id"
              class="ticket-card"
              @click="goToTicketDetail(ticket)"
            >
              <view class="card-content">
                <view class="ticket-header">
                  <text class="train-number">{{ ticket.trainNumber }}</text>
                  <u-tag 
                    :text="getStatusText(ticket.status)" 
                    :type="getStatusType(ticket.status)"
                    size="mini"
                  />
                </view>
                <view class="ticket-route">
                  <view class="station-info">
                    <text class="station-name">{{ ticket.startStation }}</text>
                    <text class="station-time">{{ formatTime(ticket.startTime) }}</text>
                    <text class="station-date">{{ formatDate(ticket.startTime) }}</text>
                  </view>
                  <view class="route-arrow">
                    <u-icon name="arrow-right" color="#999" size="32" />
                    <text class="duration">{{ ticket.duration }}</text>
                  </view>
                  <view class="station-info">
                    <text class="station-name">{{ ticket.endStation }}</text>
                    <text class="station-time">{{ formatTime(ticket.endTime) }}</text>
                    <text class="station-date">{{ formatDate(ticket.endTime) }}</text>
                  </view>
                </view>
                <view class="ticket-footer">
                  <view class="passenger-info">
                    <text class="passenger-name">{{ ticket.realName }}</text>
                    <text class="seat-info">{{ getSeatTypeText(ticket.seatType) }} {{ ticket.seatNumber }}</text>
                  </view>
                  <view class="ticket-price">¥{{ ticket.money }}</view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-if="filteredTickets.length === 0" class="empty-state">
          <u-empty
            mode="data"
            :text="getEmptyText()"
            icon="/static/empty-ticket.png"
          />
        </view>
      </view>
    </scroll-view>

    <!-- 电子票二维码弹窗 -->
    <u-popup v-model="showQrPopup" mode="center" border-radius="20">
      <view class="qr-popup">
        <view class="popup-header">
          <text class="popup-title">电子车票</text>
          <u-icon name="close" size="24" @click="showQrPopup = false" />
        </view>
        <view class="qr-content" v-if="currentTicket">
          <view class="ticket-info">
            <text class="train-number">{{ currentTicket.trainNumber }}</text>
            <text class="route-info">{{ currentTicket.startStation }} → {{ currentTicket.endStation }}</text>
            <text class="seat-info">{{ getSeatTypeText(currentTicket.seatType) }} {{ currentTicket.seatNumber }}</text>
            <text class="passenger-info">{{ currentTicket.realName }}</text>
          </view>
          <view class="qr-code">
            <u-qrcode :text="qrCodeText" size="300" />
          </view>
          <view class="qr-tips">
            <text>请向检票员出示此二维码</text>
            <text>有效时间: {{ formatValidityTime() }}</text>
          </view>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyTickets } from '@/api/ticket'

const currentFilter = ref(0)
const filterTabs = [
  { name: '全部' },
  { name: '正常' },
  { name: '今日' },
  { name: '已使用' },
  { name: '已退票' }
]

const ticketList = ref([])
const loading = ref(false)
const showQrPopup = ref(false)
const currentTicket = ref(null)

const stats = computed(() => {
  const total = ticketList.value.length
  const normal = ticketList.value.filter(t => t.status === 1).length
  const used = ticketList.value.filter(t => t.status === 2).length
  const refunded = ticketList.value.filter(t => t.status === 5).length
  return { total, normal, used, refunded }
})

const todayDate = computed(() => {
  const today = new Date()
  return `${today.getFullYear()}年${today.getMonth() + 1}月${today.getDate()}日`
})

const todayTickets = computed(() => {
  const today = new Date().toDateString()
  return ticketList.value.filter(ticket => {
    const ticketDate = new Date(ticket.startTime).toDateString()
    return ticketDate === today && ticket.status === 1
  })
})

const otherTickets = computed(() => {
  const today = new Date().toDateString()
  return filteredTickets.value.filter(ticket => {
    const ticketDate = new Date(ticket.startTime).toDateString()
    return ticketDate !== today
  })
})

const filteredTickets = computed(() => {
  let filtered = ticketList.value
  
  switch (currentFilter.value) {
    case 1: // 正常
      filtered = ticketList.value.filter(ticket => ticket.status === 1)
      break
    case 2: // 今日
      const today = new Date().toDateString()
      filtered = ticketList.value.filter(ticket => {
        const ticketDate = new Date(ticket.startTime).toDateString()
        return ticketDate === today
      })
      break
    case 3: // 已使用
      filtered = ticketList.value.filter(ticket => ticket.status === 2)
      break
    case 4: // 已退票
      filtered = ticketList.value.filter(ticket => ticket.status === 5)
      break
  }
  
  return filtered.sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
})

onMounted(() => {
  loadTicketList()
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

const handleFilterChange = (index) => {
  currentFilter.value = index
}

const handleRefresh = async () => {
  await loadTicketList()
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

const getStatusText = (status) => {
  const statusMap = {
    1: '正常',
    2: '已使用',
    3: '已取消',
    4: '已改签',
    5: '已退票'
  }
  return statusMap[status] || '未知状态'
}

const getStatusType = (status) => {
  const typeMap = {
    1: 'success',
    2: 'info',
    3: 'danger',
    4: 'warning',
    5: 'info'
  }
  return typeMap[status] || 'info'
}

const getSeatTypeText = (seatType) => {
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

const getEmptyText = () => {
  const texts = ['暂无车票信息', '暂无正常车票', '今日暂无车票', '暂无已使用车票', '暂无已退票']
  return texts[currentFilter.value]
}

const goToTicketDetail = (ticket) => {
  uni.navigateTo({
    url: `/pages/ticket/ticket-detail?ticketId=${ticket.id}`
  })
}

const showTicketQr = (ticket) => {
  currentTicket.value = ticket
  showQrPopup.value = true
}

const qrCodeText = computed(() => {
  if (!currentTicket.value) return ''
  return JSON.stringify({
    ticketId: currentTicket.value.id,
    trainNumber: currentTicket.value.trainNumber,
    seatNumber: currentTicket.value.seatNumber,
    realName: currentTicket.value.realName,
    idCard: currentTicket.value.idCard,
    startStation: currentTicket.value.startStation,
    endStation: currentTicket.value.endStation,
    startTime: currentTicket.value.startTime,
    timestamp: Date.now()
  })
})

const formatValidityTime = () => {
  const now = new Date()
  const validityTime = new Date(now.getTime() + 2 * 60 * 60 * 1000)
  return `${String(validityTime.getHours()).padStart(2, '0')}:${String(validityTime.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped lang="scss">
.my-tickets-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
  color: #fff;
  border-radius: 0 0 30rpx 30rpx;
  margin-bottom: 20rpx;

  .stats-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;

    .stats-title {
      font-size: 40rpx;
      font-weight: bold;
    }

    .stats-subtitle {
      font-size: 24rpx;
      opacity: 0.9;
    }
  }

  .stats-content {
    display: flex;
    justify-content: space-around;

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10rpx;

      .stat-number {
        font-size: 40rpx;
        font-weight: bold;
      }

      .stat-label {
        font-size: 24rpx;
        opacity: 0.9;
      }
    }
  }
}

.filter-tabs {
  background: #fff;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.tickets-scroll {
  flex: 1;
  padding: 20rpx;
}

.ticket-section {
  margin-bottom: 40rpx;

  .section-header {
    display: flex;
    align-items: center;
    gap: 10rpx;
    margin-bottom: 20rpx;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }

    .section-count {
      font-size: 24rpx;
      color: #666;
      margin-left: auto;
    }
  }

  .ticket-cards {
    .ticket-card {
      background: #fff;
      border-radius: 20rpx;
      padding: 30rpx;
      margin-bottom: 20rpx;
      box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

      &.today-ticket {
        border: 2rpx solid #ff9800;
        background: linear-gradient(135deg, #fff8e1 0%, #ffffff 100%);
      }

      .card-content {
        .ticket-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20rpx;

          .train-number {
            font-size: 32rpx;
            font-weight: bold;
            color: #333;
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

            .station-time {
              font-size: 28rpx;
              color: #2979ff;
              font-weight: bold;
              margin-bottom: 5rpx;
            }

            .station-date {
              font-size: 24rpx;
              color: #666;
            }
          }

          .route-arrow {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 0 20rpx;
            min-width: 100rpx;

            .duration {
              font-size: 22rpx;
              color: #999;
              margin-top: 10rpx;
            }
          }
        }

        .ticket-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .passenger-info {
            display: flex;
            flex-direction: column;
            gap: 8rpx;

            .passenger-name {
              font-size: 28rpx;
              color: #333;
              font-weight: bold;
            }

            .seat-info {
              font-size: 24rpx;
              color: #666;
            }
          }

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
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;
}

.qr-popup {
  width: 600rpx;
  padding: 40rpx;

  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;

    .popup-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }

  .qr-content {
    display: flex;
    flex-direction: column;
    align-items: center;

    .ticket-info {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10rpx;
      margin-bottom: 40rpx;

      .train-number {
        font-size: 36rpx;
        font-weight: bold;
        color: #333;
      }

      .route-info {
        font-size: 24rpx;
        color: #666;
      }

      .seat-info {
        font-size: 28rpx;
        color: #2979ff;
        font-weight: bold;
      }

      .passenger-info {
        font-size: 24rpx;
        color: #666;
      }
    }

    .qr-code {
      margin-bottom: 20rpx;
    }

    .qr-tips {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10rpx;

      text {
        font-size: 24rpx;
        color: #666;
      }
    }
  }
}
</style>