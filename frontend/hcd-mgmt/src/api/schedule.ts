import { trainSeatRequest } from '@/utils/request'
import type { Schedule, ScheduleList, ScheduleQuery, ApiResponse, PageResult, TrainOption, RouteOption } from '@/types'

/**
 * 获取班次列表（基于hcd_schedule表）
 * @param params 分页查询参数
 * @returns 班次分页列表
 */
export const getScheduleList = (params: ScheduleQuery): Promise<PageResult<ScheduleList>> => {
  return trainSeatRequest.get('/schedule/list', { params })
}

/**
 * 获取班次详情
 * @param id 班次ID
 * @returns 班次详情
 */
export const getScheduleDetail = (id: number): Promise<Schedule> => {
  return trainSeatRequest.get(`/schedule/detail/${id}`)
}

/**
 * 创建班次
 * @param data 班次信息
 * @returns 创建响应
 */
export const createSchedule = (data: Partial<Schedule>): Promise<ApiResponse> => {
  return trainSeatRequest.post('/schedule/create', data)
}

/**
 * 更新班次
 * @param id 班次ID
 * @param data 班次信息
 * @returns 更新响应
 */
export const updateSchedule = (id: number, data: Partial<Schedule>): Promise<ApiResponse> => {
  return trainSeatRequest.put(`/schedule/update/${id}`, data)
}

/**
 * 删除班次
 * @param id 班次ID
 * @returns 删除响应
 */
export const deleteSchedule = (id: number): Promise<ApiResponse> => {
  return trainSeatRequest.delete(`/schedule/delete/${id}`)
}

/**
 * 获取列车选择列表（用于下拉框）
 * @returns 列车选项列表
 */
export const getTrainOptions = (): Promise<TrainOption[]> => {
  return trainSeatRequest.get('/schedule/train-options')
}

/**
 * 获取线路选择列表（用于下拉框）
 * @returns 线路选项列表
 */
export const getRouteOptions = (): Promise<RouteOption[]> => {
  return trainSeatRequest.get('/schedule/route-options')
}