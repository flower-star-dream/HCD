import { trainSeatRequest } from '@/utils/request'
import type { Schedule, ScheduleList, ScheduleQuery, ApiResponse, PageResult } from '@/types'

/**
 * 获取班次列表
 * @param params 分页查询参数
 * @returns 班次分页列表
 */
export const getScheduleList = (params: ScheduleQuery): Promise<PageResult<ScheduleList>> => {
  return trainSeatRequest.get('/schedule/getSchedules', { params })
}

/**
 * 添加班次
 * @param data 班次信息
 * @returns 添加响应
 */
export const addSchedule = (data: Partial<Schedule>): Promise<void> => {
  return trainSeatRequest.post('/schedule/addSchedule', data)
}

/**
 * 更新班次
 * @param data 班次信息
 * @returns 更新响应
 */
export const updateSchedule = (data: Partial<Schedule>): Promise<void> => {
  return trainSeatRequest.put('/schedule/updateSchedule', data)
}

/**
 * 删除班次
 * @param ids 班次ID列表
 * @returns 删除响应
 */
export const deleteSchedule = (ids: number[]): Promise<void> => {
  return trainSeatRequest.delete('/schedule/deleteSchedule', { data: ids })
}
