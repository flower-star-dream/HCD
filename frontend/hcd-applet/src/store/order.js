import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getOrderList, createOrder } from '@/api/order'

export const useOrderStore = defineStore('order', () => {
  const orderList = ref([])
  const orderDetail = ref(null)

  const getOrderListAction = async (params) => {
    try {
      const response = await getOrderList(params)
      orderList.value = response.list
      return response
    } catch (error) {
      throw error
    }
  }

  const createOrderAction = async (orderData) => {
    try {
      const response = await createOrder(orderData)
      return response
    } catch (error) {
      throw error
    }
  }

  return {
    orderList,
    orderDetail,
    getOrderListAction,
    createOrderAction
  }
})