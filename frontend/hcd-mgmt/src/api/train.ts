import { trainSeatRequest } from '@/utils/request'
import type { Train, ApiResponse, PageQuery, PageResult } from '@/types'

/**
 * 获取列车列表（基于hcd_train表）
 * @param params 分页查询参数
 * @returns 列车分页列表
 */
export const getTrainList = (params: PageQuery): Promise<PageResult<Train>> => {
  return trainSeatRequest.get('/train/list', { params })
}

/**
 * 获取列车详情
 * @param id 列车ID
 * @returns 列车详情
 */
export const getTrainDetail = (id: number): Promise<Train> => {
  return trainSeatRequest.get(`/train/detail/${id}`)
}

/**
 * 创建列车
 * @param data 列车信息
 * @returns 创建响应
 */
export const createTrain = (data: Partial<Train>): Promise<ApiResponse> => {
  return trainSeatRequest.post('/train/create', data)
}

/**
 * 更新列车
 * @param id 列车ID
 * @param data 列车信息
 * @returns 更新响应
 */
export const updateTrain = (id: number, data: Partial<Train>): Promise<ApiResponse> => {
  return trainSeatRequest.put(`/train/update/${id}`, data)
}

/**
 * 删除列车
 * @param id 列车ID
 * @returns 删除响应
 */
export const deleteTrain = (id: number): Promise<ApiResponse> => {
  return trainSeatRequest.delete(`/train/delete/${id}`)
}

/**
 * 更新列车状态（启用/禁用）
 * @param data 状态更新数据 {id, status}
 * @returns 更新响应
 */
export const updateTrainStatus = (data: { id: number; status: number }): Promise<ApiResponse> => {
  return trainSeatRequest.put('/train/status', data)
}