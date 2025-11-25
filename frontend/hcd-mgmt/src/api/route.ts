import { trainSeatRequest } from '@/utils/request'
import type { ApiResponse, PageQuery, PageResult } from '@/types'
import type { Route, RouteQuery, RouteForm } from '@/types/route'

/**
 * 获取线路列表
 * @param params 分页查询参数
 * @returns 线路分页列表
 */
export const getRouteList = (params: RouteQuery): Promise<PageResult<Route>> => {
  return trainSeatRequest.get('/route/list', { params })
}

/**
 * 获取线路详情
 * @param id 线路ID
 * @returns 线路详情
 */
export const getRouteDetail = (id: number): Promise<Route> => {
  return trainSeatRequest.get(`/route/detail/${id}`)
}

/**
 * 创建线路
 * @param data 线路信息
 * @returns 创建响应
 */
export const createRoute = (data: RouteForm): Promise<ApiResponse> => {
  return trainSeatRequest.post('/route/create', data)
}

/**
 * 更新线路
 * @param id 线路ID
 * @param data 线路信息
 * @returns 更新响应
 */
export const updateRoute = (id: number, data: RouteForm): Promise<ApiResponse> => {
  return trainSeatRequest.put(`/route/update/${id}`, data)
}

/**
 * 删除线路
 * @param id 线路ID
 * @returns 删除响应
 */
export const deleteRoute = (id: number): Promise<ApiResponse> => {
  return trainSeatRequest.delete(`/route/delete/${id}`)
}