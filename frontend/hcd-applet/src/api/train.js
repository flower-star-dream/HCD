import request from '@/utils/request'

// 列车搜索相关
export const searchTrains = (params) => {
  return request.get('/train/search', params)
}

export const getTrainDetail = (id) => {
  return request.get(`/train/detail/${id}`)
}

export const getTrainSeats = (trainId) => {
  return request.get(`/train/seats/${trainId}`)
}

export const getStations = () => {
  return request.get('/station/list')
}

// 新增：班次查询
export const searchSchedules = (params) => {
  return request.get('/schedule/search', params)
}

export const getScheduleDetail = (id) => {
  return request.get(`/schedule/detail/${id}`)
}

export const getScheduleSeats = (scheduleId) => {
  return request.get(`/schedule/seats/${scheduleId}`)
}

// 新增：站点相关
export const searchStations = (keyword) => {
  return request.get('/station/search', { keyword })
}

export const getStationById = (id) => {
  return request.get(`/station/${id}`)
}

// 新增：线路相关
export const getRouteInfo = (departureId, arrivalId) => {
  return request.get('/route/info', { departureId, arrivalId })
}

export const getRouteStations = (routeId) => {
  return request.get(`/route/stations/${routeId}`)
}