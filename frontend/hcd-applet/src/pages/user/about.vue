<template>
  <view class="about-container">
    <!-- 应用信息 -->
    <view class="app-info-section">
      <view class="app-icon">
        <image src="/static/logo.png" mode="aspectFit" />
      </view>
      <view class="app-name">火车订票系统</view>
      <view class="app-version">版本 {{ version }}</view>
      <view class="app-slogan">让出行更简单</view>
    </view>

    <!-- 功能介绍 -->
    <view class="features-section">
      <view class="section-title">主要功能</view>
      <view class="features-grid">
        <view class="feature-item">
          <u-icon name="train" size="48" color="#2979ff" />
          <text class="feature-name">车次查询</text>
          <text class="feature-desc">实时查询全国列车车次</text>
        </view>
        <view class="feature-item">
          <u-icon name="order" size="48" color="#2979ff" />
          <text class="feature-name">在线订票</text>
          <text class="feature-desc">便捷的车票预订服务</text>
        </view>
        <view class="feature-item">
          <u-icon name="coupon" size="48" color="#2979ff" />
          <text class="feature-name">电子车票</text>
          <text class="feature-desc">手机二维码检票乘车</text>
        </view>
        <view class="feature-item">
          <u-icon name="user" size="48" color="#2979ff" />
          <text class="feature-name">乘客管理</text>
          <text class="feature-desc">常用乘客信息管理</text>
        </view>
      </view>
    </view>

    <!-- 联系信息 -->
    <view class="contact-section">
      <view class="section-title">联系我们</view>
      <u-cell-group>
        <u-cell title="客服电话" :value="'400-123-4567'" is-link @click="makePhoneCall" />
        <u-cell title="客服邮箱" :value="'service@hcd.com'" />
        <u-cell title="官方网站" :value="'www.hcd.com'" is-link @click="openWebsite" />
        <u-cell title="工作时间" :value="'7:00-23:00'" />
      </u-cell-group>
    </view>

    <!-- 版本信息 -->
    <view class="version-section">
      <view class="section-title">版本信息</view>
      <u-cell-group>
        <u-cell title="当前版本" :value="version" />
        <u-cell title="更新日期" :value="updateDate" />
        <u-cell title="系统要求" :value="systemRequirements" />
      </u-cell-group>
    </view>

    <!-- 版权信息 -->
    <view class="copyright-section">
      <text class="copyright-text">© 2024 火车订票系统 版权所有</text>
      <text class="icp-text">ICP备案号：京ICP备12345678号</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const version = ref('1.0.0')
const updateDate = ref('2024-01-01')
const systemRequirements = ref('Android 5.0+ / iOS 10.0+')

onMounted(() => {
  // 获取应用版本信息
  try {
    // #ifdef APP-PLUS
    version.value = plus.runtime.version
    // #endif
    
    // #ifdef MP-WEIXIN
    const accountInfo = uni.getAccountInfoSync()
    version.value = accountInfo.miniProgram.version || '1.0.0'
    // #endif
  } catch (error) {
    version.value = '1.0.0'
  }
})

const makePhoneCall = () => {
  uni.makePhoneCall({
    phoneNumber: '400-123-4567'
  })
}

const openWebsite = () => {
  // #ifdef APP-PLUS
  plus.runtime.openURL('https://www.hcd.com')
  // #endif
  
  // #ifndef APP-PLUS
  uni.showToast({
    title: '请在浏览器中访问 www.hcd.com',
    icon: 'none'
  })
  // #endif
}
</script>

<style scoped lang="scss">
.about-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx 0;
}

.app-info-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
  margin-bottom: 20rpx;
  text-align: center;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .app-icon {
    margin-bottom: 30rpx;

    image {
      width: 120rpx;
      height: 120rpx;
      border-radius: 24rpx;
    }
  }

  .app-name {
    font-size: 40rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 10rpx;
  }

  .app-version {
    font-size: 28rpx;
    color: #999;
    margin-bottom: 10rpx;
  }

  .app-slogan {
    font-size: 28rpx;
    color: #666;
    font-style: italic;
  }
}

.features-section {
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
    text-align: center;
  }

  .features-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 40rpx;

    .feature-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      padding: 30rpx;
      background: #f8f9fa;
      border-radius: 16rpx;

      .feature-name {
        font-size: 28rpx;
        font-weight: bold;
        color: #333;
        margin-top: 20rpx;
        margin-bottom: 10rpx;
      }

      .feature-desc {
        font-size: 24rpx;
        color: #666;
        line-height: 1.5;
      }
    }
  }
}

.contact-section,
.version-section {
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

.copyright-section {
  text-align: center;
  padding: 40rpx 0;

  .copyright-text {
    display: block;
    font-size: 24rpx;
    color: #999;
    margin-bottom: 10rpx;
  }

  .icp-text {
    font-size: 22rpx;
    color: #ccc;
  }
}
</style>