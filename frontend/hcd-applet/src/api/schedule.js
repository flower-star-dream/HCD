import request from '@/utils/request'

/**
 * 获取班次详情
 * @param {string} scheduleId 班次ID
 * @returns {Promise} 班次详情
 */
export const getScheduleDetail = (scheduleId) => {
  return request.get(`/schedule/detail/${scheduleId}`)
}

/**
 * 获取班次的经停站点信息
 * @param {string} scheduleId 班次ID
 * @returns {Promise} 经停站点列表
 */
export const getScheduleStations = (scheduleId) => {
  return request.get(`/schedule/stations/${scheduleId}`)
}

/**
 * 获取班次的座位类型和价格信息
 * @param {string} scheduleId 班次ID
 * @returns {Promise} 座位类型和价格列表
 */
export const getScheduleSeatPrices = (scheduleId) => {
  return request.get(`/schedule/seat-prices/${scheduleId}`)
}

/**
 * 获取班次的实时余票信息
 * @param {string} scheduleId 班次ID
 * @returns {Promise} 余票信息
 */
export const getScheduleAvailability = (scheduleId) => {
  return request.get(`/schedule/availability/${scheduleId}`)
}

/**
 * 获取列车详细信息
 * @param {string} trainId 列车ID
 * @returns {Promise} 列车详情
 */
export const getTrainInfo = (trainId) => {
  return request.get(`/train/info/${trainId}`)
}

/**
 * 获取线路详细信息
 * @param {string} routeId 线路ID
 * @returns {Promise} 线路详情
 */
export const getRouteDetail = (routeId) => {
  return request.get(`/route/detail/${routeId}`)
}