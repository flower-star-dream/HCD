import request from '@/utils/request'
import type { Train, ApiResponse, PageQuery, PageResult } from '@/types'

export const getTrainList = (params: PageQuery): Promise<PageResult<Train>> => {
  return request.get('/train/list', { params })
}

export const getTrainDetail = (id: number): Promise<Train> => {
  return request.get(`/train/detail/${id}`)
}

export const createTrain = (data: Partial<Train>): Promise<ApiResponse> => {
  return request.post('/train/create', data)
}

export const updateTrain = (id: number, data: Partial<Train>): Promise<ApiResponse> => {
  return request.put(`/train/update/${id}`, data)
}

export const deleteTrain = (id: number): Promise<ApiResponse> => {
  return request.delete(`/train/delete/${id}`)
}