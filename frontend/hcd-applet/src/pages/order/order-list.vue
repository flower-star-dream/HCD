<template>
  <view class="order-list-container">
    <!-- 订单状态标签页 -->
    <view class="order-tabs">
      <u-tabs 
        :list="tabs" 
        :current="currentTab" 
        @change="handleTabChange"
        line-color="#2979ff"
        active-color="#2979ff"
      />
    </view>

    <!-- 订单列表 -->
    <scroll-view scroll-y class="order-scroll">
      <view class="order-list">
        <view 
          v-for="order in orderList" 
          :key="order.id"
          class="order-item"
          @click="goToOrderDetail(order)"
        >
          <view class="order-header">
            <view class="order-info">
              <text class="order-number">订单号：{{ order.orderNumber }}</text>
              <u-tag 
                :text="getStatusText(order.status)" 
                :type="getStatusType(order.status)"
                size="mini"
              />
            </view>
            <text class="order-price">¥{{ order.totalPrice }}</text>
          </view>

          <view class="order-route">
            <view class="station-info">
              <text class="station-name">{{ order.startStation }}</text>
              <text class="departure-time">{{ formatTime(order.startTime) }}</text>
            </view>
            <view class="route-arrow">
              <u-icon name="arrow-right" color="#999" size="32" />
            </view>
            <view class="station-info">
              <text class="station-name">{{ order.endStation }}</text>
              <text class="arrival-time">{{ formatTime(order.endTime) }}</text>
            </view>
          </view>

          <view class="order-footer">
            <text class="order-date">{{ formatDate(order.createTime) }}</text>
            <view class="order-actions">
              <u-button
                v-if="order.status === 0"
                type="primary"
                text="支付"
                size="mini"
                @click.stop="handlePayment(order)"
              />
              <u-button
                v-if="order.status === 0"
                type="info"
                text="取消"
                size="mini"
                @click.stop="handleCancel(order)"
              />
              <u-button
                v-if="order.status === 1"
                type="error"
                text="退票"
                size="mini"
                @click.stop="handleRefund(order)"
              />
            </view>
          </view>
        </view>

        <view v-if="orderList.length === 0" class="empty-state">
          <u-empty
            mode="order"
            :text="getEmptyText()"
            icon="/static/empty-order.png"
          />
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getOrderList, cancelOrder } from '@/api/order'

const currentTab = ref(0)
const orderList = ref([])
const loading = ref(false)

const tabs = [
  { name: '全部' },
  { name: '待支付' },
  { name: '已支付' },
  { name: '已取消' }
]

const statusMap = {
  0: '待支付',
  1: '已支付',
  2: '已出票',
  3: '已完成',
  4: '已取消',
  5: '已退款'
}

const statusTypeMap = {
  0: 'warning',
  1: 'success',
  2: 'success',
  3: 'success',
  4: 'info',
  5: 'info'
}

onMounted(() => {
  loadOrderList()
})

const filteredOrderList = computed(() => {
  if (currentTab.value === 0) {
    return orderList.value
  }
  const statusFilter = currentTab.value === 1 ? 0 : 
                      currentTab.value === 2 ? 1 : 
                      currentTab.value === 3 ? 4 : null
  return statusFilter !== null ? orderList.value.filter(order => order.status === statusFilter) : orderList.value
})

const loadOrderList = async () => {
  loading.value = true
  try {
    const response = await getOrderList()
    orderList.value = response || []
  } catch (error) {
    uni.showToast({
      title: '获取订单列表失败',
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
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatDate = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}月${date.getDate()}日 ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getStatusText = (status) => {
  return statusMap[status] || '未知状态'
}

const getStatusType = (status) => {
  return statusTypeMap[status] || 'info'
}

const getEmptyText = () => {
  const texts = ['暂无订单', '暂无待支付订单', '暂无已支付订单', '暂无已取消订单']
  return texts[currentTab.value]
}

const goToOrderDetail = (order) => {
  uni.navigateTo({
    url: `/pages/order/order-detail?orderId=${order.id}`
  })
}

const handlePayment = (order) => {
  uni.navigateTo({
    url: `/pages/order/payment?orderId=${order.id}`
  })
}

const handleCancel = (order) => {
  uni.showModal({
    title: '取消订单',
    content: '确定要取消这个订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelOrder(order.id)
          uni.showToast({
            title: '订单已取消',
            icon: 'success'
          })
          loadOrderList()
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

const handleRefund = (order) => {
  uni.showModal({
    title: '退票确认',
    content: '确定要退掉这个订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await refundOrder(order.id)
          uni.showToast({
            title: '退票成功',
            icon: 'success'
          })
          loadOrderList()
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
.order-list-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.order-tabs {
  background: #fff;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.order-scroll {
  flex: 1;
  padding: 20rpx;
}

.order-list {
  .order-item {
    background: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20rpx;

      .order-info {
        display: flex;
        align-items: center;
        gap: 20rpx;

        .order-number {
          font-size: 28rpx;
          color: #333;
        }
      }

      .order-price {
        font-size: 32rpx;
        font-weight: bold;
        color: #f56c6c;
      }
    }

    .order-route {
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
      }
    }

    .order-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .order-date {
        font-size: 24rpx;
        color: #999;
      }

      .order-actions {
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