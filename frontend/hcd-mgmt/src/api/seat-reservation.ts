import { trainSeatRequest } from '@/utils/request'
import type { 
  SeatReservation, 
  SeatReservationList, 
  SeatReservationQuery, 
  SeatReservationForm, 
  ScheduleOption,
  SeatStatusStats,
  ApiResponse, 
  PageResult 
} from '@/types/seat-reservation'

/**
 * 获取座位预订列表（基于hcd_seat_reservation表）
 * @param params 分页查询参数
 * @returns 座位预订分页列表
 */
export const getSeatReservationList = (params: SeatReservationQuery): Promise<PageResult<SeatReservationList>> => {
  return trainSeatRequest.get('/seat-reservation/list', { params })
}

/**
 * 获取座位预订详情
 * @param id 座位预订ID
 * @returns 座位预订详情
 */
export const getSeatReservationDetail = (id: number): Promise<SeatReservation> => {
  return trainSeatRequest.get(`/seat-reservation/detail/${id}`)
}

/**
 * 创建座位预订
 * @param data 座位预订信息
 * @returns 创建响应
 */
export const createSeatReservation = (data: Partial<SeatReservationForm>): Promise<ApiResponse> => {
  return trainSeatRequest.post('/seat-reservation/create', data)
}

/**
 * 更新座位预订
 * @param id 座位预订ID
 * @param data 座位预订信息
 * @returns 更新响应
 */
export const updateSeatReservation = (id: number, data: Partial<SeatReservationForm>): Promise<ApiResponse> => {
  return trainSeatRequest.put(`/seat-reservation/update/${id}`, data)
}

/**
 * 删除座位预订
 * @param id 座位预订ID
 * @returns 删除响应
 */
export const deleteSeatReservation = (id: number): Promise<ApiResponse> => {
  return trainSeatRequest.delete(`/seat-reservation/delete/${id}`)
}

/**
 * 批量删除座位预订
 * @param ids 座位预订ID数组
 * @returns 删除响应
 */
export const batchDeleteSeatReservation = (ids: number[]): Promise<ApiResponse> => {
  return trainSeatRequest.delete('/seat-reservation/batch-delete', { data: { ids } })
}

/**
 * 获取班次选择列表（用于下拉框）
 * @returns 班次选项列表
 */
export const getScheduleOptions = (): Promise<ScheduleOption[]> => {
  return trainSeatRequest.get('/seat-reservation/schedule-options')
}

/**
 * 更新座位状态
 * @param id 座位预订ID
 * @param status 新的状态
 * @returns 更新响应
 */
export const updateSeatStatus = (id: number, status: number): Promise<ApiResponse> => {
  return trainSeatRequest.put(`/seat-reservation/update-status/${id}`, { bookingStatus: status })
}

/**
 * 获取座位状态统计
 * @param scheduleId 班次ID（可选）
 * @returns 座位状态统计信息
 */
export const getSeatStatusStats = (scheduleId?: number): Promise<SeatStatusStats> => {
  return trainSeatRequest.get('/seat-reservation/status-stats', { params: { scheduleId } })
}

/**
 * 检查座位是否可用
 * @param scheduleId 班次ID
 * @param seatNumber 座位号
 * @param excludeId 排除的座位预订ID（编辑时用于排除自身）
 * @returns 检查结果
 */
export const checkSeatAvailability = (scheduleId: number, seatNumber: number, excludeId?: number): Promise<boolean> => {
  return trainSeatRequest.get('/seat-reservation/check-availability', { 
    params: { scheduleId, seatNumber, excludeId } 
  })
}

/**
 * 获取指定班次的座位预订列表
 * @param scheduleId 班次ID
 * @returns 座位预订列表
 */
export const getSeatReservationsBySchedule = (scheduleId: number): Promise<SeatReservationList[]> => {
  return trainSeatRequest.get(`/seat-reservation/schedule/${scheduleId}`)
}

/**
 * 批量更新座位状态
 * @param ids 座位预订ID数组
 * @param status 新的状态
 * @returns 更新响应
 */
export const batchUpdateSeatStatus = (ids: number[], status: number): Promise<ApiResponse> => {
  return trainSeatRequest.put('/seat-reservation/batch-update-status', { ids, bookingStatus: status })
}