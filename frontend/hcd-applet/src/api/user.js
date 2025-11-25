import request from '@/utils/request'

export const login = (data) => {
  return request.post('/user/login', data)
}

export const wechatLogin = (data) => {
  return request.post('/user/wechat-login', data)
}

export const getUserInfo = () => {
  return request.get('/user/info')
}

export const register = (data) => {
  return request.post('/user/register', data)
}

export const updateUserInfo = (data) => {
  return request.put('/user/update', data)
}

export const changePassword = (data) => {
  return request.put('/user/change-password', data)
}

export const getPassengerList = () => {
  return request.get('/user/passenger/list')
}

export const addPassenger = (data) => {
  return request.post('/user/passenger/add', data)
}

export const setDefaultPassenger = (id) => {
  return request.put(`/user/passenger/default/${id}`)
}