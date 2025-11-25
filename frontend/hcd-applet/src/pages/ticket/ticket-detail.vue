<template>
  <view class="ticket-detail-container">
    <!-- 车票信息卡片 -->
    <view class="ticket-card">
      <view class="ticket-header">
        <view class="train-info">
          <text class="train-number">{{ ticketInfo.trainNumber }}</text>
          <text class="seat-info">{{ ticketInfo.seatType }} {{ ticketInfo.seatNumber }}</text>
        </view>
        <view class="ticket-status">
          <u-tag 
            :text="getStatusText(ticketInfo.status)" 
            :type="getStatusType(ticketInfo.status)"
            size="medium"
          />
        </view>
      </view>

      <view class="route-info">
        <view class="station-info">
          <text class="station-name">{{ ticketInfo.startStation }}</text>
          <text class="departure-time">{{ formatTime(ticketInfo.startTime) }}</text>
          <text class="departure-date">{{ formatDate(ticketInfo.startTime) }}</text>
        </view>
        <view class="route-arrow">
          <u-icon name="arrow-right" color="#2979ff" size="48" />
          <text class="duration">{{ ticketInfo.duration }}</text>
        </view>
        <view class="station-info">
          <text class="station-name">{{ ticketInfo.endStation }}</text>
          <text class="arrival-time">{{ formatTime(ticketInfo.endTime) }}</text>
          <text class="arrival-date">{{ formatDate(ticketInfo.endTime) }}</text>
        </view>
      </view>

      <view class="divider"></view>

      <view class="passenger-info">
        <text class="passenger-name">{{ ticketInfo.realName }}</text>
        <text class="id-card">{{ ticketInfo.cardType }}：{{ formatIdCard(ticketInfo.idCard) }}</text>
      </view>
    </view>

    <!-- 电子票二维码 -->
    <view class="qr-section" v-if="ticketInfo.status === 1">
      <view class="qr-header">
        <text class="qr-title">电子票二维码</text>
        <text class="qr-desc">请向检票员出示此码</text>
      </view>
      <view class="qr-code">
        <u-qrcode :text="qrCodeText" size="300" />
      </view>
      <view class="qr-tips">
        <text>二维码有效时间：{{ formatValidityTime() }}</text>
      </view>
    </view>

    <!-- 订单信息 -->
    <view class="order-info-section">
      <view class="section-title">订单信息</view>
      <u-cell-group>
        <u-cell title="订单号" :value="ticketInfo.orderNumber" />
        <u-cell title="订单金额" :value="`¥${ticketInfo.money}`" />
        <u-cell title="购票时间" :value="formatDateTime(ticketInfo.createTime)" />
        <u-cell title="票号" :value="ticketInfo.id" />
      </u-cell-group>
    </view>

    <!-- 操作按钮 -->
    <view class="action-buttons" v-if="ticketInfo.status === 1">
      <u-button
        type="primary"
        text="退票"
        size="large"
        @click="handleRefund"
      />
      <u-button
        type="info"
        text="改签"
        size="large"
        @click="handleChange"
      />
    </view>

    <!-- 底部提示 -->
    <view class="bottom-tips">
      <text class="tips-title">乘车须知：</text>
      <view class="tips-content">
        <text>• 请提前30分钟到达车站，预留充足时间安检和候车</text>
        <text>• 请携带有效身份证件，配合车站工作人员查验</text>
        <text>• 发车前5分钟停止检票，请合理安排时间</text>
        <text>• 如需帮助，请联系车站工作人员或拨打客服电话</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTicketDetail } from '@/api/ticket'

const ticketId = ref('')
const ticketInfo = ref({
  trainNumber: '',
  seatType: '',
  seatNumber: '',
  startStation: '',
  endStation: '',
  startTime: '',
  endTime: '',
  duration: '',
  realName: '',
  idCard: '',
  cardType: '',
  status: 1,
  money: '0.00',
  orderNumber: '',
  createTime: ''
})

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
  // 获取车票ID
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  ticketId.value = currentPage.options.ticketId
  
  if (ticketId.value) {
    loadTicketDetail()
  }
})

const loadTicketDetail = async () => {
  try {
    const response = await getTicketDetail(ticketId.value)
    ticketInfo.value = response
  } catch (error) {
    uni.showToast({
      title: '获取车票详情失败',
      icon: 'none'
    })
  }
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

const getCardTypeText = (cardType) => {
  const typeMap = {
    'ID_CARD': '身份证',
    'PASSPORT': '护照',
    'HK_MACAO_PASS': '港澳通行证',
    'TAIWAN_PASS': '台湾通行证'
  }
  return typeMap[cardType] || '未知证件'
}

const formatValidityTime = () => {
  // 二维码有效期为当前时间后2小时
  const now = new Date()
  const validityTime = new Date(now.getTime() + 2 * 60 * 60 * 1000)
  return `${String(validityTime.getHours()).padStart(2, '0')}:${String(validityTime.getMinutes()).padStart(2, '0')}`
}

const qrCodeText = computed(() => {
  // 生成二维码内容
  return JSON.stringify({
    ticketId: ticketInfo.value.id,
    trainNumber: ticketInfo.value.trainNumber,
    seatNumber: ticketInfo.value.seatNumber,
    realName: ticketInfo.value.realName,
    idCard: ticketInfo.value.idCard,
    timestamp: Date.now()
  })
})

const handleRefund = () => {
  uni.showModal({
    title: '退票确认',
    content: `确定要退掉${ticketInfo.value.realName}的${ticketInfo.value.trainNumber}次列车车票吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await refundTicket(ticketInfo.value.id)
          uni.showToast({
            title: '退票成功',
            icon: 'success'
          })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
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

const handleChange = () => {
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
.ticket-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.ticket-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20rpx;
  padding: 40rpx;
  margin-bottom: 20rpx;
  color: #fff;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);

  .ticket-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;

    .train-info {
      display: flex;
      flex-direction: column;
      gap: 10rpx;

      .train-number {
        font-size: 48rpx;
        font-weight: bold;
      }

      .seat-info {
        font-size: 28rpx;
        opacity: 0.9;
      }
    }
  }

  .route-info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 30rpx;

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

    .route-arrow {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0 20rpx;

      .duration {
        font-size: 24rpx;
        opacity: 0.8;
        margin-top: 10rpx;
      }
    }
  }

  .divider {
    height: 1rpx;
    background: rgba(255, 255, 255, 0.3);
    margin: 30rpx 0;
  }

  .passenger-info {
    display: flex;
    flex-direction: column;
    gap: 10rpx;

    .passenger-name {
      font-size: 32rpx;
      font-weight: bold;
    }

    .id-card {
      font-size: 24rpx;
      opacity: 0.9;
    }
  }
}

.qr-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  margin-bottom: 20rpx;
  text-align: center;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .qr-header {
    margin-bottom: 40rpx;

    .qr-title {
      display: block;
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 10rpx;
    }

    .qr-desc {
      font-size: 24rpx;
      color: #666;
    }
  }

  .qr-code {
    display: flex;
    justify-content: center;
    margin-bottom: 20rpx;
  }

  .qr-tips {
    font-size: 24rpx;
    color: #999;
  }
}

.order-info-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }
}

.action-buttons {
  display: flex;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.bottom-tips {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .tips-title {
    display: block;
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }

  .tips-content {
    display: flex;
    flex-direction: column;
    gap: 10rpx;

    text {
      font-size: 24rpx;
      color: #666;
      line-height: 1.6;
    }
  }
}
</style>