
// 分页相关类型
export interface PageQuery {
  page: number
  pageSize: number
  keyword?: string
  [key: string]: any
}

export interface PageResult<T> {
  total: number
  records: T[]
}

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 启用或禁用表单数据结构
export interface StartOrStopForm {
  id: string
  status: number
}

export * from './employee'
export * from './train'
export * from './user'
export * from './passenger'