// 系统常量配置

// 缓存键名
export const CACHE_KEY = {
  TOKEN: 'hcd-token',
  USER_INFO: 'hcd-user-info',
  SEARCH_HISTORY: 'hcd-search-history',
  PASSENGER_LIST: 'hcd-passenger-list',
  ORDER_LIST: 'hcd-order-list',
  SETTINGS: 'hcd-settings',
  LOCATION: 'hcd-location',
}

// 分页常量
export const PAGE_CONSTANTS = {
  DEFAULT_PAGE: 1,
  DEFAULT_SIZE: 10,
  MAX_SIZE: 50,
}

// 日期格式
export const DATE_FORMAT = {
  DATE: 'YYYY-MM-DD',
  DATETIME: 'YYYY-MM-DD HH:mm:ss',
  TIME: 'HH:mm',
  MONTH: 'YYYY-MM',
}

// 正则表达式
export const REGEXP = {
  PHONE: /^1[3-9]\d{9}$/,
  ID_CARD: /(^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}$)/,
  EMAIL: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/,
  TRAIN_NUMBER: /^[GCDZTSPKXLY1-9]\d{1,4}$/,
  CHINESE_NAME: /^[\u4e00-\u9fa5]{2,6}$/,
}

// HTTP状态码
export const HTTP_STATUS = {
  SUCCESS: 200,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  ERROR: 500,
}

// 订单状态
export const ORDER_STATUS = {
  PENDING: { value: 0, label: '待支付', color: '#ff9800' },
  PAID: { value: 1, label: '已支付', color: '#4caf50' },
  REFUNDING: { value: 2, label: '退款中', color: '#ff5722' },
  REFUNDED: { value: 3, label: '已退款', color: '#9e9e9e' },
  CANCELLED: { value: 4, label: '已取消', color: '#607d8b' },
  EXPIRED: { value: 5, label: '已过期', color: '#795548' },
}

// 座位类型
export const SEAT_TYPE = {
  BUSINESS: { value: 'business', label: '商务座', price: 200 },
  FIRST_CLASS: { value: 'first_class', label: '一等座', price: 150 },
  SECOND_CLASS: { value: 'second_class', label: '二等座', price: 100 },
  HARD_SEAT: { value: 'hard_seat', label: '硬座', price: 80 },
  SOFT_SEAT: { value: 'soft_seat', label: '软座', price: 120 },
  HARD_SLEEPER: { value: 'hard_sleeper', label: '硬卧', price: 180 },
  SOFT_SLEEPER: { value: 'soft_sleeper', label: '软卧', price: 280 },
}

// 乘客类型
export const PASSENGER_TYPE = {
  ADULT: { value: 'adult', label: '成人', ageLimit: [12, 150] },
  CHILD: { value: 'child', label: '儿童', ageLimit: [2, 12] },
  INFANT: { value: 'infant', label: '婴儿', ageLimit: [0, 2] },
  STUDENT: { value: 'student', label: '学生', discount: 0.75 },
}

// 车票状态
export const TICKET_STATUS = {
  AVAILABLE: { value: 0, label: '可预订', color: '#4caf50' },
  SOLD_OUT: { value: 1, label: '已售完', color: '#f44336' },
  LOCKED: { value: 2, label: '锁定中', color: '#ff9800' },
  BOOKED: { value: 3, label: '已预订', color: '#2196f3' },
}

// 支付方式
export const PAYMENT_METHOD = {
  WECHAT: { value: 'wechat', label: '微信支付', icon: 'wechat-fill' },
  ALIPAY: { value: 'alipay', label: '支付宝', icon: 'alipay' },
  UNIONPAY: { value: 'unionpay', label: '银联支付', icon: 'bank-card-fill' },
}

// 通知类型
export const NOTIFICATION_TYPE = {
  ORDER: { value: 'order', label: '订单通知', icon: 'order' },
  SYSTEM: { value: 'system', label: '系统通知', icon: 'system' },
  PROMOTION: { value: 'promotion', label: '优惠通知', icon: 'promotion' },
}

// 常用城市列表
export const HOT_CITIES = [
  '北京', '上海', '广州', '深圳', '成都', '重庆', '杭州', '南京',
  '武汉', '西安', '长沙', '郑州', '天津', '苏州', '青岛', '济南',
  '沈阳', '大连', '厦门', '福州', '昆明', '贵阳', '南宁', '南昌',
]

// 默认头像
export const DEFAULT_AVATAR = '/static/images/default-avatar.png'

// 默认图片
export const DEFAULT_IMAGE = '/static/images/default-image.png'

// 加载中图片
export const LOADING_IMAGE = '/static/images/loading.gif'

// 空数据提示
export const EMPTY_TEXT = '暂无数据'}