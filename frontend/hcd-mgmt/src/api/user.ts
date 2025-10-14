import request from '@/utils/request'
import type { LoginForm, UserInfo, ApiResponse, PageQuery, PageResult } from '@/types'

export const login = (data: LoginForm): Promise<ApiResponse> => {
  return request.post('/user/login', data)
}

export const getUserInfo = (): Promise<UserInfo> => {
  return request.get('/user/info')
}

export const getUserList = (params: PageQuery): Promise<PageResult<UserInfo>> => {
  return request.get('/user/list', { params })
}

export const createUser = (data: Partial<UserInfo>): Promise<ApiResponse> => {
  return request.post('/user/create', data)
}

export const updateUser = (id: number, data: Partial<UserInfo>): Promise<ApiResponse> => {
  return request.put(`/user/update/${id}`, data)
}

export const deleteUser = (id: number): Promise<ApiResponse> => {
  return request.delete(`/user/delete/${id}`)
}