import { trainSeatRequest } from '@/utils/request'
import type { ApiResponse, PageQuery, PageResult } from '@/types'
import type { Station, StationQuery, StationForm } from '@/types/station'

/**
 * 获取站点列表
 * @param params 分页查询参数
 * @returns 站点分页列表
 */
export const getStationList = (params: StationQuery): Promise<PageResult<Station>> => {
  return trainSeatRequest.get('/station/list', { params })
}

/**
 * 获取站点详情
 * @param id 站点ID
 * @returns 站点详情
 */
export const getStationDetail = (id: number): Promise<Station> => {
  return trainSeatRequest.get(`/station/detail/${id}`)
}

/**
 * 创建站点
 * @param data 站点信息
 * @returns 创建响应
 */
export const createStation = (data: StationForm): Promise<ApiResponse> => {
  return trainSeatRequest.post('/station/create', data)
}

/**
 * 更新站点
 * @param id 站点ID
 * @param data 站点信息
 * @returns 更新响应
 */
export const updateStation = (id: number, data: StationForm): Promise<ApiResponse> => {
  return trainSeatRequest.put(`/station/update/${id}`, data)
}

/**
 * 删除站点
 * @param id 站点ID
 * @returns 删除响应
 */
export const deleteStation = (id: number): Promise<ApiResponse> => {
  return trainSeatRequest.delete(`/station/delete/${id}`)
}

/**
 * 获取所有站点（用于下拉选择）
 * @returns 所有站点列表
 */
export const getAllStations = (): Promise<Station[]> => {
  return trainSeatRequest.get('/station/all')
}