import request from '@/utils/request'

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