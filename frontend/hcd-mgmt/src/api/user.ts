import { userRequest } from '@/utils/request'
import type * as T from '@/types'

/**
 * 用户登录
 * @param data 登录表单数据
 * @returns 登录响应
 */
export const login = (data: T.LoginForm): Promise<T.LoginResponse> => {
  return userRequest.post('/login', data)
}

/**
 * 获取当前用户信息
 * @returns 用户信息
 */
export const getUserInfoService = (): Promise<T.UserInfo> => {
  return userRequest.get(`/info`)
}

/**
 * 更新用户信息
 * @param data 用户信息
 * @returns 更新响应
 */
export const updateUserInfoService = (data: Partial<T.UserInfo>): Promise<T.ApiResponse> => {
  return userRequest.put('/info', data)
}

/**
 * 获取用户列表
 * @param params 分页查询参数
 * @returns 用户分页列表
 */
export const getUserListService = (params: T.PageQuery): Promise<T.PageResult<T.UserInfo>> => {
  return userRequest.get('/list', { params })
}

/**
 * 创建用户
 * @param data 用户信息
 * @returns 创建响应
 */
export const createUserService = (data: Partial<T.UserInfo>): Promise<T.ApiResponse> => {
  return userRequest.post('/', data)
}

/**
 * 更新用户
 * @param id 用户ID
 * @param data 用户信息
 * @returns 更新响应
 */
export const updateUserService = (id: number, data: Partial<T.UserInfo>): Promise<T.ApiResponse> => {
  return userRequest.put(`/${id}`, data)
}

/**
 * 删除用户
 * @param id 用户ID
 * @returns 删除响应
 */
export const deleteUserService = (id: number): Promise<T.ApiResponse> => {
  return userRequest.delete(`/${id}`)
}

/** 
 * 重置用户密码
 * @param data 重置密码表单数据
 * @returns 重置响应
 */
export const resetPasswordService = (data: T.ResetPasswordForm): Promise<T.ApiResponse> => {
  return userRequest.post('/resetPwd', data)
}

/** 
 * 修改账号状态
 * @param id 用户ID
 * @param data 状态表单数据
 * @returns 状态响应
 */
// 启用或禁用员工账号
export const startOrStopService = (data: T.StartOrStopForm): Promise<T.ApiResponse> => {
  return userRequest.post(`/status`, data);
};
