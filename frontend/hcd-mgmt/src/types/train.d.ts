// 列车相关类型定义

/**
 * 列车基础类型
 */
export interface Train {
  id: string
  trainCode: string
  trainName: string
  departureStation: string
  arrivalStation: string
  departureTime: string
  arrivalTime: string
  duration: string
  status: number
  seatTypes: Array<{
    type: string
    price: number
    totalSeats: number
    availableSeats: number
  }>
  createdTime?: string
  updatedTime?: string
}

/**
 * 列车列表类型
 */
export interface TrainList extends Train {
  createdPerson?: string
  updatedPerson?: string
}