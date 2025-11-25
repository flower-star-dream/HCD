<template>
  <view class="passenger-list-container">
    <!-- 乘客列表 -->
    <scroll-view scroll-y class="passenger-scroll">
      <view class="passenger-list">
        <view 
          v-for="item in passengerList" 
          :key="item.id"
          class="passenger-item"
          :class="{ 'is-default': item.isDefault }"
          @click="selectPassenger(item)"
        >
          <view class="passenger-info">
            <view class="passenger-header">
              <text class="name">{{ item.realName }}</text>
              <text class="id-card">{{ formatIdCard(item.idCard) }}</text>
              <view v-if="item.isDefault" class="default-tag">默认</view>
            </view>
            <view class="passenger-type">
              <text>{{ getCardTypeText(item.cardType) }}</text>
            </view>
          </view>
          <view class="passenger-actions">
            <u-icon 
              name="edit-pen" 
              size="24" 
              color="#2979ff"
              @click.stop="editPassenger(item)"
            />
            <u-icon 
              name="trash" 
              size="24" 
              color="#f56c6c"
              @click.stop="deletePassenger(item)"
            />
          </view>
        </view>
        
        <view v-if="passengerList.length === 0" class="empty-state">
          <u-empty
            mode="data"
            text="暂无乘客信息"
            icon="/static/empty-passenger.png"
          />
          <u-button
            type="primary"
            text="添加乘客"
            size="medium"
            @click="addPassenger"
            style="margin-top: 40rpx;"
          />
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-actions">
      <u-button
        type="primary"
        text="添加乘客"
        size="large"
        @click="addPassenger"
      />
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPassengerList, setDefaultPassenger } from '@/api/user'

const passengerList = ref([])
const loading = ref(false)

onMounted(() => {
  loadPassengerList()
})

const loadPassengerList = async () => {
  loading.value = true
  try {
    const response = await getPassengerList()
    passengerList.value = response || []
  } catch (error) {
    uni.showToast({
      title: '获取乘客列表失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

const formatIdCard = (idCard) => {
  if (!idCard) return ''
  if (idCard.length === 18) {
    return idCard.substring(0, 4) + '**********' + idCard.substring(14)
  }
  return idCard
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

const selectPassenger = async (passenger) => {
  // 如果是从订单页面进入的，可以选择乘客
  const pages = getCurrentPages()
  const prevPage = pages[pages.length - 2]
  if (prevPage && prevPage.route.includes('order')) {
    // 返回上一页并传递乘客信息
    uni.$emit('selectPassenger', passenger)
    uni.navigateBack()
  }
}

const editPassenger = (passenger) => {
  uni.navigateTo({
    url: `/pages/passenger/passenger-edit?id=${passenger.id}`
  })
}

const deletePassenger = (passenger) => {
  uni.showModal({
    title: '提示',
    content: `确定要删除乘客"${passenger.realName}"吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          // 调用删除接口
          await deletePassengerApi(passenger.id)
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
          loadPassengerList()
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

const deletePassengerApi = (id) => {
  // 这里调用实际的删除接口
  return new Promise((resolve, reject) => {
    // 模拟删除操作
    setTimeout(() => {
      resolve()
    }, 500)
  })
}

const addPassenger = () => {
  uni.navigateTo({
    url: '/pages/passenger/passenger-add'
  })
}

const setDefault = async (passenger) => {
  if (passenger.isDefault) return
  
  try {
    await setDefaultPassenger(passenger.id)
    uni.showToast({
      title: '已设为默认乘客',
      icon: 'success'
    })
    loadPassengerList()
  } catch (error) {
    uni.showToast({
      title: '设置失败',
      icon: 'none'
    })
  }
}
</script>

<style scoped lang="scss">
.passenger-list-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.passenger-scroll {
  flex: 1;
  padding: 20rpx;
}

.passenger-list {
  .passenger-item {
    background: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
    
    &.is-default {
      border: 2rpx solid #2979ff;
    }

    .passenger-info {
      flex: 1;

      .passenger-header {
        display: flex;
        align-items: center;
        gap: 20rpx;
        margin-bottom: 10rpx;

        .name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
        }

        .id-card {
          font-size: 26rpx;
          color: #666;
        }

        .default-tag {
          background: #2979ff;
          color: #fff;
          font-size: 22rpx;
          padding: 4rpx 12rpx;
          border-radius: 12rpx;
        }
      }

      .passenger-type {
        font-size: 26rpx;
        color: #999;
      }
    }

    .passenger-actions {
      display: flex;
      gap: 30rpx;
      align-items: center;
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.bottom-actions {
  padding: 30rpx;
  background: #fff;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
}
</style>