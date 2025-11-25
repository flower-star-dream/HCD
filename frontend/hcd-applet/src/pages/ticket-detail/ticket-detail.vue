<template>
  <view class="ticket-detail-container">
    <!-- 电子车票卡片 -->
    <view class="ticket-card" :class="getTicketStatusClass(ticketInfo.status)">
      <!-- 车票头部 -->
      <view class="ticket-header">
        <view class="train-info">
          <text class="train-number">{{ ticketInfo.trainNumber }}</text>
          <text class="train-type">{{ getTrainTypeText(ticketInfo.trainType) }}</text>
        </view>
        <view class="ticket-status">
          <u-tag 
            :text="getStatusText(ticketInfo.status)" 
            :type="getStatusType(ticketInfo.status)"
            size="medium"
          />
        </view>
      </view>

      <!-- 车票主体 -->
      <view class="ticket-body">
        <!-- 行程信息 -->
        <view class="journey-info">
          <view class="station-section">
            <view class="station-info">
              <text class="station-name">{{ ticketInfo.startStation }}</text>
              <text class="station-time">{{ formatTime(ticketInfo.startTime) }}</text>
              <text class="station-date">{{ formatDate(ticketInfo.startTime) }}</text>
            </view>
            <view class="route-info">
              <view class="duration">{{ ticketInfo.duration }}</view>
              <view class="route-line">
                <view class="start-point"></view>
                <view class="line"></view>
                <view class="end-point"></view>
              </view>
            </view>
            <view class="station-info">
              <text class="station-name">{{ ticketInfo.endStation }}</text>
              <text class="station-time">{{ formatTime(ticketInfo.endTime) }}</text>
              <text class="station-date">{{ formatDate(ticketInfo.endTime) }}</text>
            </view>
          </view>
        </view>

        <!-- 乘客信息 -->
        <view class="passenger-section">
          <view class="passenger-info">
            <text class="passenger-name">{{ ticketInfo.realName }}</text>
            <text class="id-info">{{ getCardTypeText(ticketInfo.cardType) }}: {{ formatIdCard(ticketInfo.idCard) }}</text>
          </view>
          <view class="seat-info">
            <text class="seat-type">{{ getSeatTypeText(ticketInfo.seatType) }}</text>
            <text class="seat-number">{{ ticketInfo.seatNumber }}</text>
          </view>
        </view>

        <!-- 车票信息 -->
        <view class="ticket-info-section">
          <view class="info-row">
            <text class="info-label">票号:</text>
            <text class="info-value">{{ ticketInfo.id }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">车厢:</text>
            <text class="info-value">{{ getCarriageNumber(ticketInfo.seatNumber) }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">座位:</text>
            <text class="info-value">{{ getSeatPosition(ticketInfo.seatNumber) }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">票价:</text>
            <text class="info-value price">¥{{ ticketInfo.money }}</text>
          </view>
        </view>
      </view>

      <!-- 检票口信息 -->
      <view class="gate-info" v-if="ticketInfo.status === 1">
        <view class="gate-header">
          <u-icon name="map-fill" color="#4caf50" size="24" />
          <text class="gate-title">检票信息</text>
        </view>
        <view class="gate-content">
          <text class="gate-number">检票口: {{ ticketInfo.gateNumber || '待定' }}</text>
          <text class="gate-time">检票时间: 开车前30分钟</text>
          <text class="gate-tips">请提前到达车站，预留充足时间</text>
        </view>
      </view>

      <!-- 二维码区域 -->
      <view class="qr-section" v-if="ticketInfo.status === 1">
        <view class="qr-header">
          <u-icon name="scan-fill" color="#2979ff" size="24" />
          <text class="qr-title">电子票二维码</text>
          <text class="qr-desc">请向检票员出示此码</text>
        </view>
        <view class="qr-code-container">
          <u-qrcode :text="qrCodeText" size="280" />
        </view>
        <view class="qr-footer">
          <text class="validity-time">有效时间: {{ formatValidityTime() }}</text>
          <text class="refresh-tips">二维码每5分钟自动更新</text>
        </view>
      </view>
    </view>

    <!-- 订单信息 -->
    <view class="order-info-card">
      <view class="card-header">
        <u-icon name="order-fill" color="#2979ff" size="28" />
        <text class="card-title">订单信息</text>
      </view>
      <u-cell-group :border="false">
        <u-cell title="订单编号" :value="ticketInfo.orderNumber" />
        <u-cell title="购票时间" :value="formatDateTime(ticketInfo.createTime)" />
        <u-cell title="购票渠道" value="手机APP" />
      </u-cell-group>
    </view>

    <!-- 乘车须知 -->
    <view class="travel-tips-card">
      <view class="card-header">
        <u-icon name="info-circle-fill" color="#ff9800" size="28" />
        <text class="card-title">乘车须知</text>
      </view>
      <view class="tips-content">
        <view class="tip-item">
          <text class="tip-number">1</text>
          <text class="tip-text">请提前30分钟到达车站，预留充足时间安检和候车</text>
        </view>
        <view class="tip-item">
          <text class="tip-number">2</text>
          <text class="tip-text">请携带有效身份证件，配合车站工作人员查验</text>
        </view>
        <view class="tip-item">
          <text class="tip-number">3</text>
          <text class="tip-text">发车前5分钟停止检票，请合理安排时间</text>
        </view>
        <view class="tip-item">
          <text class="tip-number">4</text>
          <text class="tip-text">如需帮助，请联系车站工作人员或拨打客服电话</text>
        </view>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-buttons" v-if="ticketInfo.status === 1">
      <u-button
        type="error"
        text="申请退票"
        size="large"
        @click="handleRefund"
      />
      <u-button
        type="info"
        text="改签"
        size="large"
        plain
        @click="handleChange"
      />
    </view>

    <!-- 底部客服 -->
    <view class="customer-service">
      <u-button
        type="primary"
        text="联系客服"
        size="medium"
        plain
        @click="contactService"
      />
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getTicketDetail } from '@/api/ticket'

const ticketId = ref('')
const ticketInfo = ref({
  trainNumber: '',
  trainType: '',
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
  createTime: '',
  gateNumber: ''
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
    uni.showLoading({ title: '加载中...' })
    const response = await getTicketDetail(ticketId.value)
    ticketInfo.value = response
  } catch (error) {
    uni.showToast({
      title: '获取车票详情失败',
      icon: 'none'
    })
  } finally {
    uni.hideLoading()
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

const getTicketStatusClass = (status) => {
  const classMap = {
    1: 'status-normal',
    2: 'status-used',
    3: 'status-cancelled',
    4: 'status-changed',
    5: 'status-refunded'
  }
  return classMap[status] || 'status-normal'
}

const getTrainTypeText = (trainType) => {
  const typeMap = {
    'G': '高铁',
    'D': '动车',
    'C': '城际',
    'Z': '直达',
    'T': '特快',
    'K': '快速'
  }
  return typeMap[trainType] || '列车'
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

const getCardTypeText = (cardType) => {
  const typeMap = {
    'ID_CARD': '身份证',
    'PASSPORT': '护照',
    'HK_MACAO_PASS': '港澳通行证',
    'TAIWAN_PASS': '台湾通行证'
  }
  return typeMap[cardType] || '未知证件'
}

const getCarriageNumber = (seatNumber) => {
  // 从座位号提取车厢号，如 "05A" -> "05车"
  if (!seatNumber) return '待定'
  const match = seatNumber.match(/^(\d+)/)
  return match ? `${match[1]}车` : '待定'
}

const getSeatPosition = (seatNumber) => {
  // 从座位号提取座位位置，如 "05A" -> "A座"
  if (!seatNumber) return '待定'
  const match = seatNumber.match(/([A-Z])$/)
  return match ? `${match[1]}座` : '待定'
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
    startStation: ticketInfo.value.startStation,
    endStation: ticketInfo.value.endStation,
    startTime: ticketInfo.value.startTime,
    timestamp: Date.now()
  })
})

const handleRefund = () => {
  uni.showModal({
    title: '退票确认',
    content: `确定要退掉${ticketInfo.value.realName}的${ticketInfo.value.trainNumber}次列车车票吗？退票将收取一定手续费。`,
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

const contactService = () => {
  uni.showModal({
    title: '联系客服',
    content: '客服电话：400-123-4567\n服务时间：7:00-23:00',
    showCancel: false
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
  padding-bottom: 140rpx;
}

.ticket-card {
  background: #fff;
  border-radius: 20rpx;
  margin: 20rpx;
  overflow: hidden;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);

  &.status-normal {
    border-left: 8rpx solid #4caf50;
  }

  &.status-used {
    border-left: 8rpx solid #9e9e9e;
  }

  &.status-cancelled {
    border-left: 8rpx solid #f44336;
  }

  &.status-changed {
    border-left: 8rpx solid #ff9800;
  }

  &.status-refunded {
    border-left: 8rpx solid #607d8b;
  }

  .ticket-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;

    .train-info {
      display: flex;
      flex-direction: column;
      gap: 10rpx;

      .train-number {
        font-size: 48rpx;
        font-weight: bold;
      }

      .train-type {
        font-size: 28rpx;
        opacity: 0.9;
      }
    }
  }

  .ticket-body {
    padding: 30rpx;

    .journey-info {
      margin-bottom: 30rpx;

      .station-section {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .station-info {
          display: flex;
          flex-direction: column;
          align-items: center;
          flex: 1;

          .station-name {
            font-size: 32rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 10rpx;
          }

          .station-time {
            font-size: 36rpx;
            font-weight: bold;
            color: #2979ff;
            margin-bottom: 5rpx;
          }

          .station-date {
            font-size: 24rpx;
            color: #666;
          }
        }

        .route-info {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 0 20rpx;
          min-width: 120rpx;

          .duration {
            font-size: 24rpx;
            color: #666;
            margin-bottom: 20rpx;
          }

          .route-line {
            display: flex;
            align-items: center;
            width: 100%;

            .start-point, .end-point {
              width: 16rpx;
              height: 16rpx;
              border-radius: 50%;
              background: #2979ff;
            }

            .line {
              flex: 1;
              height: 2rpx;
              background: #2979ff;
              margin: 0 10rpx;
            }
          }
        }
      }
    }

    .passenger-section {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;
      border-top: 1rpx solid #f0f0f0;
      border-bottom: 1rpx solid #f0f0f0;
      margin-bottom: 30rpx;

      .passenger-info {
        display: flex;
        flex-direction: column;
        gap: 10rpx;

        .passenger-name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
        }

        .id-info {
          font-size: 24rpx;
          color: #666;
        }
      }

      .seat-info {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        gap: 10rpx;

        .seat-type {
          font-size: 28rpx;
          color: #666;
        }

        .seat-number {
          font-size: 32rpx;
          font-weight: bold;
          color: #2979ff;
        }
      }
    }

    .ticket-info-section {
      .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15rpx 0;

        .info-label {
          font-size: 28rpx;
          color: #666;
        }

        .info-value {
          font-size: 28rpx;
          color: #333;

          &.price {
            color: #f56c6c;
            font-weight: bold;
          }
        }
      }
    }
  }

  .gate-info {
    background: #f8f9fa;
    padding: 30rpx;
    border-top: 1rpx solid #f0f0f0;

    .gate-header {
      display: flex;
      align-items: center;
      gap: 10rpx;
      margin-bottom: 20rpx;

      .gate-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }
    }

    .gate-content {
      display: flex;
      flex-direction: column;
      gap: 10rpx;

      .gate-number {
        font-size: 36rpx;
        font-weight: bold;
        color: #4caf50;
      }

      .gate-time {
        font-size: 28rpx;
        color: #666;
      }

      .gate-tips {
        font-size: 24rpx;
        color: #999;
      }
    }
  }

  .qr-section {
    background: #f8f9fa;
    padding: 30rpx;
    border-top: 1rpx solid #f0f0f0;
    text-align: center;

    .qr-header {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10rpx;
      margin-bottom: 30rpx;

      .qr-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }

      .qr-desc {
        font-size: 24rpx;
        color: #666;
      }
    }

    .qr-code-container {
      display: flex;
      justify-content: center;
      margin-bottom: 20rpx;
    }

    .qr-footer {
      display: flex;
      flex-direction: column;
      gap: 10rpx;

      .validity-time {
        font-size: 24rpx;
        color: #666;
      }

      .refresh-tips {
        font-size: 22rpx;
        color: #999;
      }
    }
  }
}

.order-info-card,
.travel-tips-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .card-header {
    display: flex;
    align-items: center;
    gap: 10rpx;
    margin-bottom: 20rpx;

    .card-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }
}

.travel-tips-card {
  .tips-content {
    display: flex;
    flex-direction: column;
    gap: 20rpx;

    .tip-item {
      display: flex;
      align-items: flex-start;
      gap: 15rpx;

      .tip-number {
        width: 40rpx;
        height: 40rpx;
        background: #ff9800;
        color: #fff;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24rpx;
        font-weight: bold;
        flex-shrink: 0;
      }

      .tip-text {
        flex: 1;
        font-size: 26rpx;
        color: #666;
        line-height: 1.6;
      }
    }
  }
}

.action-buttons {
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.customer-service {
  display: flex;
  justify-content: center;
  padding: 0 20rpx 40rpx;
}
</style>