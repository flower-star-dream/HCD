import { userRequest } from '@/utils/request'
import type { LoginForm, UserInfo, ApiResponse, PageQuery, PageResult, LoginResponse } from '@/types'

/**
 * 用户登录
 * @param data 登录表单数据
 * @returns 登录响应
 */
export const login = (data: LoginForm): Promise<LoginResponse> => {
  return userRequest.post('/login', data)
}

/**
 * 获取当前用户信息
 * @returns 用户信息
 */
export const getUserInfo = (): Promise<UserInfo> => {
  return userRequest.get('/info')
}

/**
 * 获取用户列表
 * @param params 分页查询参数
 * @returns 用户分页列表
 */
export const getUserList = (params: PageQuery): Promise<PageResult<UserInfo>> => {
  return userRequest.get('/list', { params })
}

/**
 * 创建用户
 * @param data 用户信息
 * @returns 创建响应
 */
export const createUser = (data: Partial<UserInfo>): Promise<ApiResponse> => {
  return userRequest.post('/create', data)
}

/**
 * 更新用户
 * @param id 用户ID
 * @param data 用户信息
 * @returns 更新响应
 */
export const updateUser = (id: number, data: Partial<UserInfo>): Promise<ApiResponse> => {
  return userRequest.put(`/update/${id}`, data)
}

/**
 * 删除用户
 * @param id 用户ID
 * @returns 删除响应
 */
export const deleteUser = (id: number): Promise<ApiResponse> => {
  return userRequest.delete(`/delete/${id}`)
}