
// 分页相关类型
export interface PageQuery {
  page: number
  size: number
  keyword?: string
  [key: string]: any
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  size: number
}

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export * from './user'