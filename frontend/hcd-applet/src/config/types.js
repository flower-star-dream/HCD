// API响应类型
export const ApiResponse = {
  code: 0,
  message: '',
  data: null,
  success: true,
}

// 分页参数类型
export const PageParams = {
  page: 1,
  size: 10,
  total: 0,
}

// 分页响应类型
export const PageResponse = {
  list: [],
  total: 0,
  page: 1,
  size: 10,
}

// 用户信息类型
export const UserInfo = {
  id: '',
  username: '',
  nickname: '',
  avatar: '',
  phone: '',
  email: '',
  token: '',
  createTime: '',
}

// 车票信息类型
export const TicketInfo = {
  id: '',
  trainNumber: '',
  departure: '',
  arrival: '',
  departureTime: '',
  arrivalTime: '',
  duration: '',
  price: 0,
  seatType: '',
  seatNumber: '',
  status: '',
}

// 订单信息类型
export const OrderInfo = {
  id: '',
  orderNumber: '',
  userId: '',
  tickets: [],
  totalPrice: 0,
  status: '',
  createTime: '',
  payTime: '',
  passengers: [],
}

// 乘客信息类型
export const PassengerInfo = {
  id: '',
  name: '',
  idCard: '',
  phone: '',
  type: '',
}

// 车站信息类型
export const StationInfo = {
  id: '',
  name: '',
  code: '',
  city: '',
  province: '',
}

// 车次信息类型
export const TrainInfo = {
  id: '',
  trainNumber: '',
  departureStation: '',
  arrivalStation: '',
  departureTime: '',
  arrivalTime: '',
  duration: '',
  seatTypes: [],
  prices: {},
}