import { trainSeatRequest } from '@/utils/request'
import type { Train, ApiResponse, PageQuery, PageResult } from '@/types'

/**
 * 获取车次列表
 * @param params 分页查询参数
 * @returns 车次分页列表
 */
export const getTrainList = (params: PageQuery): Promise<PageResult<Train>> => {
  return trainSeatRequest.get('/train/list', { params })
}

/**
 * 获取车次详情
 * @param id 车次ID
 * @returns 车次详情
 */
export const getTrainDetail = (id: number): Promise<Train> => {
  return trainSeatRequest.get(`/train/detail/${id}`)
}

/**
 * 创建车次
 * @param data 车次信息
 * @returns 创建响应
 */
export const createTrain = (data: Partial<Train>): Promise<ApiResponse> => {
  return trainSeatRequest.post('/train/create', data)
}

/**
 * 更新车次
 * @param id 车次ID
 * @param data 车次信息
 * @returns 更新响应
 */
export const updateTrain = (id: number, data: Partial<Train>): Promise<ApiResponse> => {
  return trainSeatRequest.put(`/train/update/${id}`, data)
}

/**
 * 删除车次
 * @param id 车次ID
 * @returns 删除响应
 */
export const deleteTrain = (id: number): Promise<ApiResponse> => {
  return trainSeatRequest.delete(`/train/delete/${id}`)
}