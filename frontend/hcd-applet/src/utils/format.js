/**
 * 数据格式化工具函数
 */

// 格式化时间
export const formatTime = (timeStr, format = 'HH:mm') => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

// 格式化日期
export const formatDate = (timeStr, format = 'MM月DD日') => {
  return formatTime(timeStr, format)
}

// 格式化日期时间
export const formatDateTime = (timeStr, format = 'YYYY-MM-DD HH:mm') => {
  return formatTime(timeStr, format)
}

// 格式化身份证
export const formatIdCard = (idCard) => {
  if (!idCard) return ''
  if (idCard.length === 18) {
    return idCard.substring(0, 4) + '**********' + idCard.substring(14)
  }
  return idCard
}

// 格式化手机号
export const formatPhone = (phone) => {
  if (!phone) return ''
  if (phone.length === 11) {
    return phone.substring(0, 3) + '****' + phone.substring(7)
  }
  return phone
}

// 格式化价格
export const formatPrice = (price, currency = '¥') => {
  if (price === null || price === undefined) return ''
  return `${currency}${Number(price).toFixed(2)}`
}

// 格式化时长
export const formatDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return ''
  const start = new Date(startTime)
  const end = new Date(endTime)
  const duration = end - start
  
  const hours = Math.floor(duration / (1000 * 60 * 60))
  const minutes = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60))
  
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  } else {
    return `${minutes}分钟`
  }
}

// 获取座位类型名称
export const getSeatTypeName = (seatType) => {
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

// 获取订单状态名称
export const getOrderStatusName = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已出票',
    3: '已完成',
    4: '已取消',
    5: '已退款'
  }
  return statusMap[status] || '未知状态'
}

// 获取订单状态类型
export const getOrderStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'info',
    4: 'danger',
    5: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取车票状态名称
export const getTicketStatusName = (status) => {
  const statusMap = {
    1: '正常',
    2: '已使用',
    3: '已取消',
    4: '已改签',
    5: '已退票'
  }
  return statusMap[status] || '未知状态'
}

// 获取车票状态类型
export const getTicketStatusType = (status) => {
  const typeMap = {
    1: 'success',
    2: 'info',
    3: 'danger',
    4: 'warning',
    5: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取列车类型名称
export const getTrainTypeName = (trainType) => {
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

// 获取证件类型名称
export const getCardTypeName = (cardType) => {
  const typeMap = {
    'ID_CARD': '身份证',
    'PASSPORT': '护照',
    'HK_MACAO_PASS': '港澳通行证',
    'TAIWAN_PASS': '台湾通行证'
  }
  return typeMap[cardType] || '未知证件'
}

// 提取车厢号
export const getCarriageNumber = (seatNumber) => {
  if (!seatNumber) return '待定'
  const match = seatNumber.match(/^(\d+)/)
  return match ? `${match[1]}车` : '待定'
}

// 提取座位位置
export const getSeatPosition = (seatNumber) => {
  if (!seatNumber) return '待定'
  const match = seatNumber.match(/([A-Z])$/)
  return match ? `${match[1]}座` : '待定'
}

// 判断是否为今日
export const isToday = (dateStr) => {
  if (!dateStr) return false
  const date = new Date(dateStr)
  const today = new Date()
  return date.toDateString() === today.toDateString()
}

// 判断是否为明日
export const isTomorrow = (dateStr) => {
  if (!dateStr) return false
  const date = new Date(dateStr)
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  return date.toDateString() === tomorrow.toDateString()
}

// 计算剩余时间
export const getRemainingTime = (targetTime) => {
  if (!targetTime) return ''
  const now = new Date()
  const target = new Date(targetTime)
  const diff = target - now
  
  if (diff <= 0) return '已过期'
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  
  if (days > 0) {
    return `${days}天${hours}小时`
  } else if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  } else {
    return `${minutes}分钟`
  }
}