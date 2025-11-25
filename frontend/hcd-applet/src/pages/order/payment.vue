<template>
  <view class="payment-container">
    <!-- 订单信息 -->
    <view class="order-info-section">
      <view class="order-header">
        <text class="order-title">订单支付</text>
        <text class="order-no">订单号：{{ orderInfo.orderNumber }}</text>
      </view>
      <view class="amount-info">
        <text class="amount-label">应付金额</text>
        <text class="amount-value">¥{{ orderInfo.totalPrice }}</text>
      </view>
    </view>

    <!-- 支付方式选择 -->
    <view class="payment-method-section">
      <view class="section-title">选择支付方式</view>
      <u-radio-group v-model="paymentMethod">
        <view class="payment-method-item" @click="paymentMethod = 'wechat'">
          <view class="method-info">
            <u-icon name="weixin-fill" color="#07c160" size="48" />
            <view class="method-text">
              <text class="method-name">微信支付</text>
              <text class="method-desc">推荐使用，安全快捷</text>
            </view>
          </view>
          <u-radio name="wechat" />
        </view>
        
        <view class="payment-method-item" @click="paymentMethod = 'alipay'">
          <view class="method-info">
            <u-icon name="zhifubao" color="#1677ff" size="48" />
            <view class="method-text">
              <text class="method-name">支付宝</text>
              <text class="method-desc">支付宝安全支付</text>
            </view>
          </view>
          <u-radio name="alipay" />
        </view>
      </u-radio-group>
    </view>

    <!-- 支付按钮 -->
    <view class="payment-actions">
      <u-button
        type="primary"
        :text="`确认支付 ¥${orderInfo.totalPrice}`"
        size="large"
        :loading="paying"
        @click="handlePayment"
      />
      <u-button
        text="取消支付"
        size="large"
        @click="handleCancel"
      />
    </view>

    <!-- 支付说明 -->
    <view class="payment-notice">
      <text class="notice-title">支付说明：</text>
      <view class="notice-content">
        <text>• 请在15分钟内完成支付，超时订单将自动取消</text>
        <text>• 支付成功后，车票信息将发送至您的手机</text>
        <text>• 如支付遇到问题，请联系客服：400-123-4567</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderDetail, getPaymentParams, payOrder, confirmPayment } from '@/api/order'

const orderId = ref('')
const orderInfo = ref({
  orderNumber: '',
  totalPrice: '0.00',
  status: 0
})
const paymentMethod = ref('wechat')
const paying = ref(false)

onMounted(() => {
  // 获取订单ID
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  orderId.value = currentPage.options.orderId
  
  if (orderId.value) {
    loadOrderInfo()
  }
})

const loadOrderInfo = async () => {
  try {
    const response = await getOrderDetail(orderId.value)
    orderInfo.value = response
    
    // 检查订单状态
    if (response.status !== 0) {
      uni.showModal({
        title: '提示',
        content: '该订单状态异常，无法支付',
        showCancel: false,
        success: () => {
          uni.navigateBack()
        }
      })
    }
  } catch (error) {
    uni.showToast({
      title: '获取订单信息失败',
      icon: 'none'
    })
  }
}

const handlePayment = async () => {
  if (paying.value) return
  
  paying.value = true
  
  try {
    // #ifdef MP-WEIXIN
    if (paymentMethod.value === 'wechat') {
      await wechatPayment()
    } else {
      uni.showToast({
        title: '小程序暂不支持支付宝支付',
        icon: 'none'
      })
    }
    // #endif
    
    // #ifndef MP-WEIXIN
    uni.showToast({
      title: '请在微信小程序中完成支付',
      icon: 'none'
    })
    // #endif
  } catch (error) {
    uni.showToast({
      title: '支付失败，请重试',
      icon: 'none'
    })
  } finally {
    paying.value = false
  }
}

const wechatPayment = async () => {
  try {
    // 获取支付参数
    const paymentParams = await getPaymentParams(orderId.value)
    
    // 调起微信支付
    const paymentResult = await new Promise((resolve, reject) => {
      uni.requestPayment({
        provider: 'wxpay',
        orderInfo: paymentParams,
        success: resolve,
        fail: reject
      })
    })
    
    // 确认支付结果
    await confirmPayment(orderId.value, paymentResult)
    
    // 支付成功
    uni.showToast({
      title: '支付成功',
      icon: 'success'
    })
    
    // 跳转到订单详情页
    setTimeout(() => {
      uni.redirectTo({
        url: `/pages/order/order-detail?orderId=${orderId.value}`
      })
    }, 1500)
    
  } catch (error) {
    if (error.errMsg && error.errMsg.includes('cancel')) {
      uni.showToast({
        title: '支付已取消',
        icon: 'none'
      })
    } else {
      throw error
    }
  }
}

const handleCancel = () => {
  uni.showModal({
    title: '提示',
    content: '确定要取消支付吗？',
    success: (res) => {
      if (res.confirm) {
        uni.navigateBack()
      }
    }
  })
}
</script>

<style scoped lang="scss">
.payment-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.order-info-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;

    .order-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
    }

    .order-no {
      font-size: 24rpx;
      color: #999;
    }
  }

  .amount-info {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .amount-label {
      font-size: 28rpx;
      color: #666;
    }

    .amount-value {
      font-size: 48rpx;
      font-weight: bold;
      color: #f56c6c;
    }
  }
}

.payment-method-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 30rpx;
  }

  .payment-method-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 30rpx 0;
    border-bottom: 1rpx solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .method-info {
      display: flex;
      align-items: center;
      gap: 20rpx;

      .method-text {
        display: flex;
        flex-direction: column;
        gap: 8rpx;

        .method-name {
          font-size: 32rpx;
          color: #333;
        }

        .method-desc {
          font-size: 24rpx;
          color: #999;
        }
      }
    }
  }
}

.payment-actions {
  padding: 40rpx 0;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.payment-notice {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .notice-title {
    display: block;
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }

  .notice-content {
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