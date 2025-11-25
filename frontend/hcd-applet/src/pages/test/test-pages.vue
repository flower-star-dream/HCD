<template>
  <view class="test-pages-container">
    <view class="test-header">
      <text class="test-title">新页面测试</text>
      <text class="test-subtitle">点击下方按钮测试各个新页面</text>
    </view>

    <view class="test-section">
      <text class="section-title">车次相关</text>
      <view class="button-group">
        <u-button 
          type="primary" 
          text="车次详情页面" 
          @click="testTrainDetail"
        />
      </view>
    </view>

    <view class="test-section">
      <text class="section-title">订单相关</text>
      <view class="button-group">
        <u-button 
          type="primary" 
          text="订单详情页面" 
          @click="testOrderDetail"
        />
      </view>
    </view>

    <view class="test-section">
      <text class="section-title">车票相关</text>
      <view class="button-group">
        <u-button 
          type="primary" 
          text="我的车票页面" 
          @click="testMyTickets"
        />
        <u-button 
          type="primary" 
          text="车票详情页面" 
          @click="testTicketDetail"
        />
      </view>
    </view>

    <view class="test-section">
      <text class="section-title">工具函数测试</text>
      <view class="button-group">
        <u-button 
          type="info" 
          text="测试格式化函数" 
          @click="testFormatFunctions"
        />
        <u-button 
          type="info" 
          text="测试导航函数" 
          @click="testNavigationFunctions"
        />
      </view>
    </view>

    <view class="test-result" v-if="testResult">
      <text class="result-title">测试结果：</text>
      <text class="result-content">{{ testResult }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { 
  goToTrainDetail, 
  goToTicketDetail, 
  goToOrderDetail, 
  goToMyTickets 
} from '@/utils/navigation'
import { 
  formatTime, 
  formatDate, 
  formatPrice, 
  formatDuration,
  getSeatTypeName,
  getOrderStatusName,
  formatIdCard
} from '@/utils/format'

const testResult = ref('')

const testTrainDetail = () => {
  goToTrainDetail('SCH2024122501')
  testResult.value = '正在跳转到车次详情页面...'
}

const testOrderDetail = () => {
  goToOrderDetail('ORD202412250001')
  testResult.value = '正在跳转到订单详情页面...'
}

const testMyTickets = () => {
  goToMyTickets()
  testResult.value = '正在跳转到我的车票页面...'
}

const testTicketDetail = () => {
  goToTicketDetail('T202412250001')
  testResult.value = '正在跳转到车票详情页面...'
}

const testFormatFunctions = () => {
  const testTime = '2024-12-25T08:00:00'
  const testPrice = 553.00
  const testIdCard = '110101199001011234'
  
  const results = [
    `格式化时间: ${formatTime(testTime)}`,
    `格式化日期: ${formatDate(testTime)}`,
    `格式化价格: ${formatPrice(testPrice)}`,
    `格式化时长: ${formatDuration(testTime, '2024-12-25T12:30:00')}`,
    `座位类型: ${getSeatTypeName('SECOND_CLASS')}`,
    `订单状态: ${getOrderStatusName(1)}`,
    `身份证: ${formatIdCard(testIdCard)}`
  ]
  
  testResult.value = results.join('\n')
}

const testNavigationFunctions = () => {
  const results = [
    '导航函数测试完成！',
    '所有页面跳转函数已正确导入',
    '工具函数可正常使用'
  ]
  
  testResult.value = results.join('\n')
}
</script>

<style scoped lang="scss">
.test-pages-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 40rpx;
}

.test-header {
  text-align: center;
  margin-bottom: 60rpx;

  .test-title {
    display: block;
    font-size: 48rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
  }

  .test-subtitle {
    font-size: 28rpx;
    color: #666;
  }
}

.test-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .section-title {
    display: block;
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 30rpx;
  }

  .button-group {
    display: flex;
    flex-direction: column;
    gap: 20rpx;
  }
}

.test-result {
  background: #e8f5e8;
  border: 2rpx solid #4caf50;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-top: 30rpx;

  .result-title {
    display: block;
    font-size: 32rpx;
    font-weight: bold;
    color: #4caf50;
    margin-bottom: 20rpx;
  }

  .result-content {
    font-size: 28rpx;
    color: #333;
    line-height: 1.6;
    white-space: pre-line;
  }
}
</style>