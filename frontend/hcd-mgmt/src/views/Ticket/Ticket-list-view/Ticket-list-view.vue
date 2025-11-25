<template>
  <!-- 使用增强的ListPage组件，通过配置实现动态列和搜索条件 -->
  <ListPage
    title="车票列表"
    :show-tabs="true"
    :tabs="tabs"
    :active-tab="statusFilter"
    :total="total"
    :current-page="currentPage"
    :page-size="pageSize"
    :selected-count="selectedRows.length"
    :loading="loading"
    :show-selection="true"
    :table-data="ticketList"
    :table-columns="tableColumns"
    :search-fields="searchFields"
    :initial-search-form="initialSearchForm"
    @tab-click="handleStatusTabChange"
    @size-change="handleSizeChange"
    @current-change="handlePageChange"
    @search="handleSearch"
    @reset="handleReset"
    @selection-change="handleSelectionChange"
  >
    <!-- 操作按钮区域 -->
    <template #actions>
      <!-- 车票列表暂不提供批量操作 -->
    </template>

    <!-- 自定义状态列 -->
    <template #column-status="{ row }">
      <el-tag :type="getStatusTagType(row.status)" size="small">
        {{ getStatusText(row.status) }}
      </el-tag>
    </template>

    <!-- 自定义创建时间列 -->
    <template #column-createTime="{ row }">
      {{ formatDate(row.createTime) }}
    </template>
    <template #column-updateTime="{ row }">
      {{ formatDate(row.updateTime) }}
    </template>
    <template #column-departureTime="{ row }">
      {{ formatDate(row.departureTime) }}
    </template>
    <template #column-arrivalTime="{ row }">
      {{ formatDate(row.arrivalTime) }}
    </template>

    <!-- 自定义证件类型列 -->
    <template #column-cardType="{ row }">
      {{ getCardTypeText(row.cardType) }}
    </template>

    <!-- 自定义票价列 -->
    <template #column-price="{ row }">
      <span style="color: #f56c6c; font-weight: bold;">¥{{ row.price.toFixed(2) }}</span>
    </template>

    <!-- 自定义操作列 -->
    <template #column-action="{ row }">
      <el-button type="primary" text size="small" @click="handleViewDetail(row)">查看详情</el-button>
      <el-button 
        type="primary" 
        text 
        size="small" 
        @click="handleStatusChange(row)"
        :disabled="row.status === TICKET_STATUS.CANCELLED || row.status === TICKET_STATUS.USED || row.status === TICKET_STATUS.REFUNDED"
      >
        {{ row.status === TICKET_STATUS.CANCELLED ? '已取消' : '取消车票' }}
      </el-button>
    </template>
  </ListPage>

  <!-- 车票详情弹窗 -->
  <el-dialog
    v-model="detailDialogVisible"
    :title="'车票详情 - ' + currentTicket.trainNumber"
    width="900px"
    :close-on-click-modal="false"
  >
    <el-descriptions :column="2" border>
      <el-descriptions-item label="车票ID">{{ currentTicket.id }}</el-descriptions-item>
      <el-descriptions-item label="订单号">{{ currentTicket.orderNumber }}</el-descriptions-item>
      <el-descriptions-item label="车次号">{{ currentTicket.trainNumber }}</el-descriptions-item>
      <el-descriptions-item label="座位号">{{ currentTicket.seatNumber }}</el-descriptions-item>
      <el-descriptions-item label="座位类型">{{ currentTicket.seatType }}</el-descriptions-item>
      <el-descriptions-item label="车票状态">
        <el-tag :type="getStatusTagType(currentTicket.status)" size="small">
          {{ getStatusText(currentTicket.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="票价">
        <span style="color: #f56c6c; font-weight: bold;">¥{{ currentTicket.price?.toFixed(2) }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="乘车人">{{ currentTicket.realName }}</el-descriptions-item>
      <el-descriptions-item label="证件类型">{{ getCardTypeText(currentTicket.cardType) }}</el-descriptions-item>
      <el-descriptions-item label="证件号码" :span="1">{{ currentTicket.idCard }}</el-descriptions-item>
      <el-descriptions-item label="出发站">{{ currentTicket.startStation }}</el-descriptions-item>
      <el-descriptions-item label="到达站">{{ currentTicket.endStation }}</el-descriptions-item>
      <el-descriptions-item label="出发时间">{{ formatDate(currentTicket.startTime) }}</el-descriptions-item>
      <el-descriptions-item label="到达时间">{{ formatDate(currentTicket.endTime) }}</el-descriptions-item>
      <el-descriptions-item label="行程时长">{{ currentTicket.duration }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(currentTicket.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(currentTicket.updateTime) }}</el-descriptions-item>
      <el-descriptions-item label="创建人">{{ currentTicket.createPerson }}</el-descriptions-item>
      <el-descriptions-item label="更新人">{{ currentTicket.updatePerson }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getTicketListService, getTicketDetailService, updateTicketStatusService } from '@/api/ticket'
import { ElMessage, ElMessageBox } from 'element-plus'

// 车票状态枚举常量
const TICKET_STATUS = {
  NORMAL: 1,
  USED: 2,
  CANCELLED: 3,
  CHANGED: 4,
  REFUNDED: 5,
  // 获取状态文本
  getText: (status) => {
    const statusMap = {
      [TICKET_STATUS.NORMAL]: '正常',
      [TICKET_STATUS.USED]: '已使用',
      [TICKET_STATUS.CANCELLED]: '已取消',
      [TICKET_STATUS.CHANGED]: '已改签',
      [TICKET_STATUS.REFUNDED]: '已退票'
    }
    return statusMap[status] || '未知状态'
  },
  // 获取状态标签类型
  getTagType: (status) => {
    const typeMap = {
      [TICKET_STATUS.NORMAL]: 'success',
      [TICKET_STATUS.USED]: 'info',
      [TICKET_STATUS.CANCELLED]: 'danger',
      [TICKET_STATUS.CHANGED]: 'warning',
      [TICKET_STATUS.REFUNDED]: 'info'
    }
    return typeMap[status] || 'info'
  }
}

// 证件类型常量
const CARD_TYPE = {
  ID_CARD: 'ID_CARD',
  PASSPORT: 'PASSPORT',
  // 获取证件类型文本
  getText: (type) => {
    const typeMap = {
      [CARD_TYPE.ID_CARD]: '身份证',
      [CARD_TYPE.PASSPORT]: '护照'
    }
    return typeMap[type] || '未知类型'
  }
}

// 响应式数据
const ticketList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedRows = ref([])
const statusFilter = ref('all')
const detailDialogVisible = ref(false)
const currentTicket = ref({})

// 状态数量统计
const statusCounts = ref({
  all: 0,
  normal: 0,
  used: 0,
  cancelled: 0,
  changed: 0,
  refunded: 0
})

// 标签页配置（计算属性）
const tabs = computed(() => [
  {
    name: 'all',
    label: '全部',
    count: statusCounts.value.all,
    lazy: true
  },
  {
    name: 'normal',
    label: '正常',
    count: statusCounts.value.normal,
    lazy: true
  },
  {
    name: 'used',
    label: '已使用',
    count: statusCounts.value.used,
    lazy: true
  },
  {
    name: 'cancelled',
    label: '已取消',
    count: statusCounts.value.cancelled,
    lazy: true
  },
  {
    name: 'changed',
    label: '已改签',
    count: statusCounts.value.changed,
    lazy: true
  },
  {
    name: 'refunded',
    label: '已退票',
    count: statusCounts.value.refunded,
    lazy: true
  }
])

// 表格列配置
const tableColumns = [
  {
    prop: 'id',
    label: '车票ID',
    width: 80,
    align: 'center'
  },
  {
    prop: 'trainNumber',
    label: '车次号',
    width: 100,
    align: 'center'
  },
  {
    prop: 'startStation',
    label: '出发站',
    width: 100,
    align: 'center'
  },
  {
    prop: 'endStation',
    label: '到达站',
    width: 100,
    align: 'center'
  },
  {
    prop: 'startTime',
    label: '出发时间',
    minWidth: 160,
    align: 'center'
  },
  {
    prop: 'endTime',
    label: '到达时间',
    minWidth: 160,
    align: 'center'
  },
  {
    prop: 'seatNumber',
    label: '座位号',
    width: 80,
    align: 'center'
  },
  {
    prop: 'seatType',
    label: '座位类型',
    width: 100,
    align: 'center'
  },
  {
    prop: 'realName',
    label: '乘车人',
    width: 100,
    align: 'center'
  },
  {
    prop: 'cardType',
    label: '证件类型',
    width: 100,
    align: 'center'
  },
  {
    prop: 'idCard',
    label: '证件号码',
    minWidth: 150,
    align: 'center',
    showOverflowTooltip: true
  },
  {
    prop: 'status',
    label: '状态',
    width: 100,
    align: 'center'
  },
  {
    prop: 'price',
    label: '票价',
    width: 100,
    align: 'center'
  },
  {
    prop: 'createTime',
    label: '创建时间',
    minWidth: 160,
    align: 'center'
  },
  {
    prop: 'action',
    label: '操作',
    width: 150,
    align: 'center',
    fixed: 'right'
  }
]

// 搜索字段配置
const searchFields = [
  {
    prop: 'orderId',
    label: '订单ID',
    type: 'input',
    placeholder: '请输入订单ID',
    clearable: true
  },
  {
    prop: 'passengerName',
    label: '乘车人姓名',
    type: 'input',
    placeholder: '请输入乘车人姓名',
    clearable: true
  },
  {
    prop: 'startStation',
    label: '出发站',
    type: 'input',
    placeholder: '请输入出发站',
    clearable: true
  },
  {
    prop: 'endStation',
    label: '到达站',
    type: 'input',
    placeholder: '请输入到达站',
    clearable: true
  },
  {
    prop: 'status',
    label: '车票状态',
    type: 'select',
    placeholder: '请选择车票状态',
    clearable: true,
    options: [
      { label: '正常', value: TICKET_STATUS.NORMAL },
      { label: '已使用', value: TICKET_STATUS.USED },
      { label: '已取消', value: TICKET_STATUS.CANCELLED },
      { label: '已改签', value: TICKET_STATUS.CHANGED },
      { label: '已退票', value: TICKET_STATUS.REFUNDED }
    ]
  },
  {
    prop: 'rideDateStart',
    label: '乘车开始日期',
    type: 'date',
    placeholder: '选择开始日期',
    clearable: true
  },
  {
    prop: 'rideDateEnd',
    label: '乘车结束日期',
    type: 'date',
    placeholder: '选择结束日期',
    clearable: true
  }
]

// 初始搜索表单数据
const initialSearchForm = searchFields.reduce((acc, field) => {
  acc[field.prop] = ''
  return acc
}, {})

/**
 * 格式化日期时间
 * @param {string|number|Date} date - 日期对象或时间戳
 * @returns {string} 格式化后的日期字符串
 */
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

/**
 * 获取车票列表
 * @param {Object} searchParams - 搜索参数
 */
const fetchTicketList = async (searchParams = {}) => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      ...searchParams
    }
    
    // API调用
    const response = await getTicketListService(params)
    const data = response.records
    
    // 按状态筛选
    let filteredData = filterDataByStatus(data)
    
    // 模拟分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    const paginatedData = filteredData.slice(start, end)
    
    ticketList.value = paginatedData
    total.value = filteredData.length
    
    // 更新状态数量统计
    updateStatusCounts(data)
  } catch (error) {
    ticketList.value = []
    total.value = 0
    
    // 重置状态统计
    statusCounts.value = {
      all: 0,
      valid: 0,
      used: 0,
      cancelled: 0,
      expired: 0,
      refunded: 0
    }
  } finally {
    loading.value = false
  }
}

/**
 * 根据状态筛选数据
 * @param {Array} data - 原始数据
 * @returns {Array} 筛选后的数据
 */
const filterDataByStatus = (data) => {
  if (statusFilter.value === 'all') {
    return data
  }
  // 根据筛选条件返回对应状态的数据
  if (statusFilter.value === 'normal') {
    return data.filter(item => item.status === TICKET_STATUS.NORMAL)
  }
  if (statusFilter.value === 'used') {
    return data.filter(item => item.status === TICKET_STATUS.USED)
  }
  if (statusFilter.value === 'cancelled') {
    return data.filter(item => item.status === TICKET_STATUS.CANCELLED)
  }
  if (statusFilter.value === 'changed') {
    return data.filter(item => item.status === TICKET_STATUS.CHANGED)
  }
  if (statusFilter.value === 'refunded') {
    return data.filter(item => item.status === TICKET_STATUS.REFUNDED)
  }
  return data
}

/**
 * 更新状态数量统计
 * @param {Array} list - 车票列表数据
 */
const updateStatusCounts = (list) => {
  const normalCount = list.filter(item => item.status === TICKET_STATUS.NORMAL).length
  const usedCount = list.filter(item => item.status === TICKET_STATUS.USED).length
  const cancelledCount = list.filter(item => item.status === TICKET_STATUS.CANCELLED).length
  const changedCount = list.filter(item => item.status === TICKET_STATUS.CHANGED).length
  const refundedCount = list.filter(item => item.status === TICKET_STATUS.REFUNDED).length
  
  statusCounts.value = {
    all: list.length,
    normal: normalCount,
    used: usedCount,
    cancelled: cancelledCount,
    changed: changedCount,
    refunded: refundedCount
  }
}

/**
 * 获取状态文本
 * @param {number} status - 状态值
 * @returns {string} 状态文本
 */
const getStatusText = (status) => {
  return TICKET_STATUS.getText(status)
}

/**
 * 获取状态标签类型
 * @param {number} status - 状态值
 * @returns {string} 标签类型
 */
const getStatusTagType = (status) => {
  return TICKET_STATUS.getTagType(status)
}

/**
 * 获取证件类型文本
 * @param {string} type - 证件类型
 * @returns {string} 证件类型文本
 */
const getCardTypeText = (type) => {
  return CARD_TYPE.getText(type)
}

/**
 * 处理查询操作
 * @param {Object} formData - 搜索表单数据
 */
const handleSearch = (formData) => {
  currentPage.value = 1 // 重置为第一页
  selectedRows.value = [] // 清空选择
  fetchTicketList(formData)
}

/**
 * 重置查询条件
 */
const handleReset = () => {
  statusFilter.value = 'all' // 重置状态筛选
  currentPage.value = 1 // 重置为第一页
  selectedRows.value = [] // 清空选择
  fetchTicketList({}) // 重置后自动查询
}

/**
 * 处理状态标签页切换
 * @param {string} tabName - 切换到的标签页名称
 */
const handleStatusTabChange = (tabName) => {
  statusFilter.value = tabName // 更新状态筛选
  currentPage.value = 1 // 重置为第一页
  selectedRows.value = [] // 清空选择
  fetchTicketList()
}

/**
 * 处理表格选择变化
 * @param {Array} selection - 选中的行数据
 */
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

/**
 * 分页大小变化
 * @param {number} size - 每页条数
 */
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchTicketList()
}

/**
 * 分页切换
 * @param {number} page - 当前页码
 */
const handlePageChange = (page) => {
  currentPage.value = page
  fetchTicketList()
}

/**
 * 查看车票详情
 * @param {Object} row - 车票数据
 */
const handleViewDetail = async (row) => {
  if (!row) {
    ElMessage.warning('请选择车票查看详情')
    return
  }
  
  try {
    loading.value = true
    const detail = await getTicketDetailService(row.id)
    currentTicket.value = detail
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取车票详情失败')
  } finally {
    loading.value = false
  }
}

/**
 * 取消车票
 * @param {Object} row - 车票数据
 */
const handleStatusChange = async (row) => {
  if (row.status === TICKET_STATUS.CANCELLED || row.status === TICKET_STATUS.USED || row.status === TICKET_STATUS.REFUNDED || row.status === TICKET_STATUS.CHANGED) {
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要取消车票「${row.trainNumber} - ${row.seatNumber}」吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const data = { 
      id: row.id, 
      status: TICKET_STATUS.CANCELLED,
      remarks: '后台管理员取消车票'
    }
    
    await updateTicketStatusService(data)
    ElMessage.success('车票取消成功')
    
    // 刷新列表
    fetchTicketList()
  } catch (error) {
    // 用户取消操作或发生错误
    if (error !== 'cancel') {
      ElMessage.error('车票取消失败')
    }
  }
}

// 组件挂载后加载数据
onMounted(() => {
  fetchTicketList()
})
</script>

<style scoped lang="scss">
/* 使用ListPage组件的样式，无需额外样式 */
@use './Ticket-list-view.scss';

:deep(.el-dialog__body) {
  padding-top: 20px;
}

h4 {
  margin: 20px 0 10px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 500;
}
</style>