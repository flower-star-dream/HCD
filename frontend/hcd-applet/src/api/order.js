import request from '@/utils/request'

export const createOrder = (data) => {
  return request.post('/order/create', data)
}

export const getOrderList = (params) => {
  return request.get('/order/list', params)
}

export const getOrderDetail = (id) => {
  return request.get(`/order/detail/${id}`)
}

export const cancelOrder = (id) => {
  return request.put(`/order/cancel/${id}`)
}

export const payOrder = (id) => {
  return request.put(`/order/pay/${id}`)
}