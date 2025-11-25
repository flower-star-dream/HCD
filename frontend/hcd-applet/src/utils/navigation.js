/**
 * 页面跳转工具函数
 */

// 跳转到车次详情页面
export const goToTrainDetail = (scheduleId) => {
  uni.navigateTo({
    url: `/pages/train-detail/train-detail?scheduleId=${scheduleId}`
  })
}

// 跳转到车票详情页面
export const goToTicketDetail = (ticketId) => {
  uni.navigateTo({
    url: `/pages/ticket-detail/ticket-detail?ticketId=${ticketId}`
  })
}

// 跳转到订单详情页面
export const goToOrderDetail = (orderId) => {
  uni.navigateTo({
    url: `/pages/order-detail/order-detail?orderId=${orderId}`
  })
}

// 跳转到我的车票页面
export const goToMyTickets = () => {
  uni.switchTab({
    url: '/pages/my-tickets/my-tickets'
  })
}

// 跳转到订单确认页面
export const goToOrderConfirm = (scheduleId, params = {}) => {
  const queryString = Object.keys(params)
    .map(key => `${key}=${encodeURIComponent(params[key])}`)
    .join('&')
  
  uni.navigateTo({
    url: `/pages/order-confirm/order-confirm?scheduleId=${scheduleId}${queryString ? '&' + queryString : ''}`
  })
}

// 返回上一页
export const goBack = (delta = 1) => {
  uni.navigateBack({
    delta
  })
}

// 重定向到首页
export const goToHome = () => {
  uni.switchTab({
    url: '/pages/index/index'
  })
}

// 跳转到登录页面
export const goToLogin = (redirect) => {
  const url = redirect 
    ? `/pages/user/login?redirect=${encodeURIComponent(redirect)}`
    : '/pages/user/login'
  
  uni.navigateTo({
    url
  })
}

// 检查用户是否登录，如果未登录则跳转到登录页面
export const checkAuthAndNavigate = (redirect) => {
  const token = uni.getStorageSync('token')
  if (!token) {
    goToLogin(redirect)
    return false
  }
  return true
}