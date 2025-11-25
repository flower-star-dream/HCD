import request from '@/utils/request'

/**
 * 获取我的车票列表
 * @returns {Promise} 车票列表
 */
export const getMyTickets = () => {
  return request.get('/ticket/my-tickets')
}

/**
 * 根据订单ID获取车票
 * @param {string} orderId 订单ID
 * @returns {Promise} 车票列表
 */
export const getTicketsByOrder = (orderId) => {
  return request.get(`/ticket/by-order/${orderId}`)
}

/**
 * 取消车票
 * @param {string} ticketId 车票ID
 * @returns {Promise} 取消结果
 */
export const cancelTicket = (ticketId) => {
  return request.post(`/ticket/cancel/${ticketId}`)
}

/**
 * 获取车票详情
 * @param {string} ticketId 车票ID
 * @returns {Promise} 车票详情
 */
export const getTicketDetail = (ticketId) => {
  return request.get(`/ticket/detail/${ticketId}`)
}