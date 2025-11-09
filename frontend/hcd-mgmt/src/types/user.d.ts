import { StringDecoder } from "node:string_decoder"

// 用户登录表单数据结构
export interface LoginForm {
  username: string
  phone: string
  password: string
}

// 登录响应数据结构
export interface LoginResponse {
  token: string
  id: string
  username: string
}

// 当前用户信息数据结构
export interface UserInfo {
  id: string
  username: string
  nickname: string
  phone: string
  avatar: string
  affiliatedSite: string
  permissionLevel: string
}

// 用户信息数据结构
export interface User {
  id: string
  username: string
  nickname: string
  password: string
  phone: string
  affiliatedSite: string
  permissionLevel: StringDecoder
}

// 用户列表数据结构
export interface UserList {
  id: string
  username: string
  nickname: string
  phone: string
  affiliatedSite: string
  permissionLevel: string
  createdTime: string
  updatedTime: string
  createdPerson: string
  updatedPerson: string
  status: number
}

// 重置密码表单数据结构
export interface ResetPasswordForm {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

