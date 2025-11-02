// 用户相关类型
export interface LoginForm {
  username: string
  password: string
}

// 登录响应数据结构
export interface LoginResponse {
  token: string
  id: number
  username: string
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  roles: string[]
  permissions: string[]
}

// 分页相关类型
export interface PageQuery {
  page: number
  size: number
  keyword?: string
  [key: string]: any
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  size: number
}

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 车次相关类型
export interface Train {
  id: number
  trainNumber: string
  startStation: string
  endStation: string
  departureTime: string
  arrivalTime: string
  duration: string
  price: number
  seats: {
    firstClass: number
    secondClass: number
    business: number
    hardSeat: number
    softSeat: number
  }
  status: 0 | 1
  createTime: string
  updateTime: string
}

// 订单相关类型
export interface Order {
  id: number
  orderNumber: string
  userId: number
  trainId: number
  trainNumber: string
  departureStation: string
  arrivalStation: string
  departureTime: string
  arrivalTime: string
  seatType: string
  seatNumber: string
  passengerName: string
  passengerId: string
  phone: string
  price: number
  status: 0 | 1 | 2 | 3
  createTime: string
  updateTime: string
}

// 站点相关类型
export interface Station {
  id: number
  name: string
  code: string
  city: string
  province: string
  status: 0 | 1
  createTime: string
  updateTime: string
}