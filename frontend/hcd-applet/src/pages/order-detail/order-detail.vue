<template>
  <view class="order-detail-container">
    <!-- 订单状态卡片 -->
    <view class="order-status-card" :class="getStatusClass(orderInfo.status)">
      <view class="status-main">
        <u-icon :name="getStatusIcon(orderInfo.status)" size="64" color="#fff" />
        <view class="status-text-group">
          <text class="status-title">{{ getStatusText(orderInfo.status) }}</text>
          <text class="status-desc">{{ getStatusDesc(orderInfo.status) }}</text>
        </view>
      </view>
      <view class="status-timeline" v-if="orderInfo.status === 1">
        <view class="timeline-item active">
          <text class="timeline-text">已支付</text>
        </view>
        <view class="timeline-line"></view>
        <view class="timeline-item">
          <text class="timeline-text">已出票</text>
        </view>
        <view class="timeline-line"></view>
        <view class="timeline-item">
          <text class="timeline-text">已完成</text>
        </view>
      </view>
    </view>

    <!-- 车次信息卡片 -->
    <view class="train-info-card">
      <view class="card-header">
        <u-icon name="train-fill" color="#2979ff" size="28" />
        <text class="card-title">车次信息</text>
      </view>
      <view class="train-route">
        <view class="station-group">
          <text class="station-name">{{ orderInfo.startStation }}</text>
          <text class="station-time">{{ formatTime(orderInfo.startTime) }}</text>
          <text class="station-date">{{ formatDate(orderInfo.startTime) }}</text>
        </view>
        <view class="route-arrow">
          <u-icon name="arrow-right" color="#2979ff" size="40" />
          <text class="train-number">{{ orderInfo.trainNumber }}</text>
          <text class="duration">{{ orderInfo.duration }}</text>
        </view>
        <view class="station-group">
          <text class="station-name">{{ orderInfo.endStation }}</text>
          <text class="station-time">{{ formatTime(orderInfo.endTime) }}</text>
          <text class="station-date">{{ formatDate(orderInfo.endTime) }}</text>
        </view>
      </view>
    </view>

    <!-- 乘客信息 -->
    <view class="passengers-card">
      <view class="card-header">
        <u-icon name="account-fill" color="#2979ff" size="28" />
        <text class="card-title">乘客信息 ({{ orderInfo.passengers.length }}人)</text>
      </view>
      <view class="passenger-list">
        <view 
          v-for="(passenger, index) in orderInfo.passengers" 
          :key="index"
          class="passenger-item"
        >
          <view class="passenger-main">
            <view class="passenger-header">
              <text class="passenger-name">{{ passenger.realName }}</text>
              <u-tag 
                :text="getCardTypeText(passenger.cardType)" 
                type="info"
                size="mini"
              />
            </view>
            <text class="id-number">{{ formatIdCard(passenger.idCard) }}</text>
          </view>
          <view class="ticket-info" v-if="passenger.tickets && passenger.tickets[0]">
            <view class="seat-info">
              <text class="seat-type">{{ getSeatTypeText(passenger.tickets[0].seatType) }}</text>
              <text class="seat-number">{{ passenger.tickets[0].seatNumber }}</text>
            </view>
            <text class="ticket-price">¥{{ passenger.tickets[0].money }}</text>
          </view>
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
        <u-cell title="订单编号" :value="orderInfo.orderNumber" />
        <u-cell title="订单金额">
          <template #value>
            <text class="price-text">¥{{ orderInfo.totalPrice }}</text>
          </template>
        </u-cell>
        <u-cell title="订单状态" :value="getStatusText(orderInfo.status)" />
        <u-cell title="创建时间" :value="formatDateTime(orderInfo.createTime)" />
        <u-cell 
          title="支付时间" 
          :value="orderInfo.payTime ? formatDateTime(orderInfo.payTime) : '未支付'" 
        />
        <u-cell title="联系人" :value="orderInfo.contactName" />
        <u-cell title="联系电话" :value="orderInfo.contactPhone" />
      </u-cell-group>
    </view>

    <!-- 车票信息 -->
    <view class="tickets-card" v-if="orderInfo.tickets && orderInfo.tickets.length > 0">
      <view class="card-header">
        <u-icon name="ticket-fill" color="#2979ff" size="28" />
        <text class="card-title">车票信息</text>
      </view>
      <view class="ticket-list">
        <view 
          v-for="ticket in orderInfo.tickets" 
          :key="ticket.id"
          class="ticket-item"
          @click="goToTicketDetail(ticket)"
        >
          <view class="ticket-main">
            <view class="ticket-header">
              <text class="ticket-number">票号: {{ ticket.id }}</text>
              <u-tag 
                :text="getTicketStatusText(ticket.status)" 
                :type="getTicketStatusType(ticket.status)"
                size="mini"
              />
            </view>
            <view class="ticket-content">
              <text class="ticket-seat">{{ getSeatTypeText(ticket.seatType) }} {{ ticket.seatNumber }}</text>
              <text class="ticket-passenger">{{ ticket.realName }}</text>
            </view>
          </view>
          <u-icon name="arrow-right" color="#999" size="24" />
        </view>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-buttons">
      <view class="button-group">
        <u-button
          v-if="orderInfo.status === 0"
          type="primary"
          text="立即支付"
          size="large"
          @click="handlePayment"
        />
        <u-button
          v-if="orderInfo.status === 0"
          type="info"
          text="取消订单"
          size="large"
          plain
          @click="handleCancel"
        />
        <u-button
          v-if="orderInfo.status === 1"
          type="error"
          text="申请退票"
          size="large"
          @click="handleRefund"
        />
        <u-button
          v-if="orderInfo.status === 1"
          type="primary"
          text="查看车票"
          size="large"
          plain
          @click="goToTickets"
        />
        <u-button
          v-if="orderInfo.status === 3"
          type="info"
          text="删除订单"
          size="large"
          plain
          @click="handleDelete"
        />
      </view>
    </view>

    <!-- 底部提示 -->
    <view class="bottom-tips">
      <text class="tips-title">温馨提示：</text>
      <view class="tips-content">
        <text>• 请在发车前30分钟到达车站，预留充足时间安检和候车</text>
        <text>• 请携带有效身份证件，配合车站工作人员查验</text>
        <text>• 发车前5分钟停止检票，请合理安排时间</text>
        <text>• 如需帮助，请联系车站工作人员或拨打客服电话</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderDetail, cancelOrder } from '@/api/order'

const orderId = ref('')
const orderInfo = ref({
  orderNumber: '',
  totalPrice: '0.00',
  status: 0,
  trainNumber: '',
  trainType: '',
  startStation: '',
  endStation: '',
  startTime: '',
  endTime: '',
  duration: '',
  passengers: [],
  tickets: [],
  contactName: '',
  contactPhone: '',
  createTime: '',
  payTime: ''
})

const statusMap = {
  0: '待支付',
  1: '已支付',
  2: '已出票',
  3: '已完成',
  4: '已取消',
  5: '已退款'
}

const statusClassMap = {
  0: 'status-pending',
  1: 'status-paid',
  2: 'status-ticketed',
  3: 'status-completed',
  4: 'status-cancelled',
  5: 'status-refunded'
}

const statusIconMap = {
  0: 'clock',
  1: 'checkmark-circle',
  2: 'ticket',
  3: 'checkmark-circle-fill',
  4: 'close-circle',
  5: 'refund'
}

const ticketStatusMap = {
  1: '正常',
  2: '已使用',
  3: '已取消',
  4: '已改签',
  5: '已退票'
}

onMounted(() => {
  // 获取订单ID
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  orderId.value = currentPage.options.orderId
  
  if (orderId.value) {
    loadOrderDetail()
  }
})

const loadOrderDetail = async () => {
  try {
    uni.showLoading({ title: '加载中...' })
    const response = await getOrderDetail(orderId.value)
    orderInfo.value = response
  } catch (error) {
    uni.showToast({
      title: '获取订单详情失败',
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

const getStatusClass = (status) => {
  return statusClassMap[status] || 'status-default'
}

const getStatusIcon = (status) => {
  return statusIconMap[status] || 'info-circle'
}

const getStatusDesc = (status) => {
  const descMap = {
    0: '请在15分钟内完成支付，超时订单将自动取消',
    1: '订单已支付成功，请等待出票',
    2: '车票已出票，请按时乘车',
    3: '订单已完成，感谢您的使用',
    4: '订单已取消',
    5: '订单已退款'
  }
  return descMap[status] || ''
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

const getTicketStatusText = (status) => {
  return ticketStatusMap[status] || '未知状态'
}

const getTicketStatusType = (status) => {
  const typeMap = {
    1: 'success',
    2: 'info',
    3: 'danger',
    4: 'warning',
    5: 'info'
  }
  return typeMap[status] || 'info'
}

const goToTicketDetail = (ticket) => {
  uni.navigateTo({
    url: `/pages/ticket/ticket-detail?ticketId=${ticket.id}`
  })
}

const goToTickets = () => {
  uni.switchTab({
    url: '/pages/ticket/ticket-list'
  })
}

const handlePayment = () => {
  uni.navigateTo({
    url: `/pages/order/payment?orderId=${orderId.value}`
  })
}

const handleCancel = () => {
  uni.showModal({
    title: '取消订单',
    content: '确定要取消这个订单吗？取消后订单将无法恢复。',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelOrder(orderId.value)
          uni.showToast({
            title: '订单已取消',
            icon: 'success'
          })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        } catch (error) {
          uni.showToast({
            title: '取消失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

const handleRefund = () => {
  uni.showModal({
    title: '退票确认',
    content: '确定要退掉这个订单的所有车票吗？退票将收取一定手续费。',
    success: async (res) => {
      if (res.confirm) {
        try {
          await refundOrder(orderId.value)
          uni.showToast({
            title: '退票成功',
            icon: 'success'
          })
          loadOrderDetail()
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

const handleDelete = () => {
  uni.showModal({
    title: '删除订单',
    content: '确定要删除这个订单吗？删除后订单将无法恢复。',
    success: async (res) => {
      if (res.confirm) {
        try {
          // await deleteOrder(orderId.value)
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        } catch (error) {
          uni.showToast({
            title: '删除失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

const refundOrder = async (orderId) => {
  // 调用退票API
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve()
    }, 1000)
  })
}
</script>

<style scoped lang="scss">
.order-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 140rpx;
}

.order-status-card {
  padding: 40rpx;
  color: #fff;
  border-radius: 0 0 30rpx 30rpx;
  margin-bottom: 20rpx;

  &.status-pending {
    background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  }

  &.status-paid {
    background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
  }

  &.status-ticketed {
    background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  }

  &.status-completed {
    background: linear-gradient(135deg, #607d8b 0%, #455a64 100%);
  }

  &.status-cancelled {
    background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
  }

  &.status-refunded {
    background: linear-gradient(135deg, #9e9e9e 0%, #757575 100%);
  }

  .status-main {
    display: flex;
    align-items: center;
    gap: 30rpx;
    margin-bottom: 30rpx;

    .status-text-group {
      flex: 1;

      .status-title {
        display: block;
        font-size: 40rpx;
        font-weight: bold;
        margin-bottom: 10rpx;
      }

      .status-desc {
        font-size: 24rpx;
        opacity: 0.9;
      }
    }
  }

  .status-timeline {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 20rpx;
    padding: 20rpx;

    .timeline-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10rpx;

      &.active {
        .timeline-text {
          color: #fff;
          font-weight: bold;
        }
      }

      .timeline-text {
        font-size: 24rpx;
        color: rgba(255, 255, 255, 0.8);
      }
    }

    .timeline-line {
      flex: 1;
      height: 2rpx;
      background: rgba(255, 255, 255, 0.4);
      margin: 0 20rpx;
    }
  }
}

.train-info-card,
.passengers-card,
.order-info-card,
.tickets-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .card-header {
    display: flex;
    align-items: center;
    gap: 10rpx;
    margin-bottom: 30rpx;

    .card-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
  }
}

.train-info-card {
  .train-route {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .station-group {
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

    .route-arrow {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0 20rpx;
      min-width: 120rpx;

      .train-number {
        font-size: 28rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 10rpx;
      }

      .duration {
        font-size: 24rpx;
        color: #666;
      }
    }
  }
}

.passengers-card {
  .passenger-list {
    .passenger-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .passenger-main {
        flex: 1;

        .passenger-header {
          display: flex;
          align-items: center;
          gap: 20rpx;
          margin-bottom: 10rpx;

          .passenger-name {
            font-size: 32rpx;
            font-weight: bold;
            color: #333;
          }
        }

        .id-number {
          font-size: 24rpx;
          color: #666;
        }
      }

      .ticket-info {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        gap: 10rpx;

        .seat-info {
          display: flex;
          align-items: center;
          gap: 10rpx;

          .seat-type {
            font-size: 24rpx;
            color: #666;
          }

          .seat-number {
            font-size: 28rpx;
            font-weight: bold;
            color: #2979ff;
          }
        }

        .ticket-price {
          font-size: 28rpx;
          font-weight: bold;
          color: #f56c6c;
        }
      }
    }
  }
}

.order-info-card {
  .price-text {
    color: #f56c6c;
    font-size: 32rpx;
    font-weight: bold;
  }
}

.tickets-card {
  .ticket-list {
    .ticket-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f0f0f0;
      cursor: pointer;

      &:last-child {
        border-bottom: none;
      }

      .ticket-main {
        flex: 1;

        .ticket-header {
          display: flex;
          align-items: center;
          gap: 20rpx;
          margin-bottom: 10rpx;

          .ticket-number {
            font-size: 24rpx;
            color: #666;
          }
        }

        .ticket-content {
          display: flex;
          align-items: center;
          gap: 20rpx;

          .ticket-seat {
            font-size: 28rpx;
            font-weight: bold;
            color: #333;
          }

          .ticket-passenger {
            font-size: 24rpx;
            color: #666;
          }
        }
      }
    }
  }
}

.action-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 30rpx;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);

  .button-group {
    display: flex;
    gap: 20rpx;
  }
}

.bottom-tips {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin: 20rpx;
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