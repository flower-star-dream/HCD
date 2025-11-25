import { trainSeatRequest } from '@/utils/request'
import type { ApiResponse, PageQuery, PageResult } from '@/types'
import type { RouteStation, RouteStationList, RouteStationQuery, RouteStationForm, RouteStationSortUpdate } from '@/types/route-station'

/**
 * 获取线路站点关联列表
 * @param params 查询参数
 * @returns 线路站点关联分页列表
 */
export const getRouteStationList = (params: RouteStationQuery): Promise<PageResult<RouteStationList>> => {
  return trainSeatRequest.get('/route-station/list', { params })
}

/**
 * 获取线路所有站点（按顺序）
 * @param routeId 线路ID
 * @returns 线路站点列表（按stationSorting排序）
 */
export const getRouteStations = (routeId: number): Promise<RouteStationList[]> => {
  return trainSeatRequest.get(`/route-station/route/${routeId}`)
}

/**
 * 添加线路站点关联
 * @param data 线路站点关联信息
 * @returns 创建响应
 */
export const addRouteStation = (data: RouteStationForm): Promise<ApiResponse> => {
  return trainSeatRequest.post('/route-station/add', data)
}

/**
 * 更新线路站点关联
 * @param id 关联ID
 * @param data 线路站点关联信息
 * @returns 更新响应
 */
export const updateRouteStation = (id: number, data: RouteStationForm): Promise<ApiResponse> => {
  return trainSeatRequest.put(`/route-station/update/${id}`, data)
}

/**
 * 删除线路站点关联
 * @param id 关联ID
 * @returns 删除响应
 */
export const deleteRouteStation = (id: number): Promise<ApiResponse> => {
  return trainSeatRequest.delete(`/route-station/delete/${id}`)
}

/**
 * 批量更新线路站点排序
 * @param data 排序更新数据
 * @returns 更新响应
 */
export const updateRouteStationSort = (data: RouteStationSortUpdate): Promise<ApiResponse> => {
  return trainSeatRequest.put('/route-station/sort', data)
}

/**
 * 获取线路完整信息（包含起点终点和站点列表）
 * @param routeId 线路ID
 * @returns 线路完整信息
 */
export const getRouteFullInfo = (routeId: number): Promise<any> => {
  return trainSeatRequest.get(`/route-station/route-full/${routeId}`)
}