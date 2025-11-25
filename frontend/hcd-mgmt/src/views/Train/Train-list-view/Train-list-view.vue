<template>
  <!-- 使用增强的ListPage组件，通过配置实现动态列和搜索条件 -->
  <ListPage
    title="列车列表"
    :show-tabs="true"
    :tabs="tabs"
    :active-tab="statusFilter"
    :total="total"
    :current-page="currentPage"
    :page-size="pageSize"
    :selected-count="selectedRows.length"
    :loading="loading"
    :show-selection="true"
    :table-data="trainList"
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
      <el-button type="primary" @click="handleAdd">新增列车</el-button>
      <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
        批量删除
        <span class="selected-count">({{ selectedRows.length }})</span>
      </el-button>
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

    <!-- 自定义操作列 -->
    <template #column-action="{ row }">
      <el-button type="primary" text size="small" @click="handleEdit(row)">编辑</el-button>
      <el-button type="primary" text size="small" @click="handleStatusChange(row)">{{ row.status === TRAIN_STATUS.ENABLED ? '禁用' : '启用' }}</el-button>
      <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
    </template>
  </ListPage>

  <!-- 使用通用表单弹窗组件 -->
  <DialogForm
    v-model:visible="dialogVisible"
    :title="dialogTitle"
    :form-data="trainForm"
    :fields="formFields"
    :rules="formRules"
    :is-edit="isEdit"
    :confirm-text="isEdit ? '更新' : '新增'"
    :loading="submitLoading"
    @submit="handleFormSubmit"
  />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getTrainList, createTrain, updateTrain, deleteTrain, updateTrainStatus } from '@/api/train'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useEmployeeStore } from '@/stores'
import DialogForm from '@/components/DialogForm/DialogForm.vue'

const employeeStore = useEmployeeStore()
const employeeInfo = computed(() => employeeStore.employeeInfo)

// 列车状态枚举常量
const TRAIN_STATUS = {
  ENABLED: 1,
  DISABLED: 0,
  // 获取状态文本
  getText: (status) => {
    const statusMap = {
      [TRAIN_STATUS.ENABLED]: '已启用',
      [TRAIN_STATUS.DISABLED]: '已禁用'
    }
    return statusMap[status] || '未知状态'
  },
  // 获取状态标签类型
  getTagType: (status) => {
    const typeMap = {
      [TRAIN_STATUS.ENABLED]: 'success',
      [TRAIN_STATUS.DISABLED]: 'danger'
    }
    return typeMap[status] || 'info'
  }
}

// 表单相关响应式数据
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const trainForm = ref({
  id: '',
  trainName: '',
  trainModel: '',
  seatNum: 0,
  serviceYears: 0,
  status: TRAIN_STATUS.ENABLED
})

// 表单字段配置
const formFields = [
  {
    prop: 'trainName',
    label: '列车名',
    type: 'input',
    placeholder: '请输入列车名',
    maxlength: 10,
    showWordLimit: true,
    disabled: (isEditMode) => {
      return isEditMode 
    }
  },
  {
    prop: 'trainModel',
    label: '列车型号',
    type: 'input',
    placeholder: '请输入列车型号',
    maxlength: 50,
    showWordLimit: true
  },
  {
    prop: 'seatNum',
    label: '座位数',
    type: 'input',
    placeholder: '请输入座位数',
    inputType: 'number'
  },
  {
    prop: 'serviceYears',
    label: '服务年数',
    type: 'input',
    placeholder: '请输入服务年数',
    inputType: 'number'
  },
  {
    prop: 'status',
    label: '状态',
    type: 'switch',
    activeValue: TRAIN_STATUS.ENABLED,
    inactiveValue: TRAIN_STATUS.DISABLED,
    activeText: '启用',
    inactiveText: '禁用',
    // 只在编辑时显示状态字段
    hidden: (isEditMode) => !isEditMode
  }
]

// 表单验证规则
const formRules = computed(() => ({
  trainName: [
    { required: true, message: '请输入列车名', trigger: 'blur' },
    { min: 2, max: 10, message: '列车名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  trainModel: [
    { required: true, message: '请输入列车型号', trigger: 'blur' },
    { max: 50, message: '列车型号长度不能超过 50 个字符', trigger: 'blur' }
  ],
  seatNum: [
    { required: true, message: '请输入座位数', trigger: 'blur' },
    { type: 'number', min: 1, max: 2000, message: '座位数必须在 1 到 2000 之间', trigger: 'blur' }
  ],
  serviceYears: [
    { required: true, message: '请输入服务年数', trigger: 'blur' },
    { type: 'number', min: 0, max: 50, message: '服务年数必须在 0 到 50 之间', trigger: 'blur' }
  ]
}))

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑列车' : '新增列车')

// 响应式数据
const trainList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedRows = ref([])
const statusFilter = ref('all')

// 状态数量统计
const statusCounts = ref({
  all: 0,
  enabled: 0,
  disabled: 0
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
    name: 'enabled',
    label: '已启用',
    count: statusCounts.value.enabled,
    lazy: true
  },
  {
    name: 'disabled',
    label: '已禁用',
    count: statusCounts.value.disabled,
    lazy: true
  }
])

// 表格列配置
const tableColumns = [
  {
    prop: 'id',
    label: '列车号',
    width: 120,
    align: 'center'
  },
  {
    prop: 'trainName',
    label: '列车名',
    minWidth: 120,
    align: 'center'
  },
  {
    prop: 'trainModel',
    label: '列车型号',
    minWidth: 150,
    align: 'center',
    showOverflowTooltip: true
  },
  {
    prop: 'seatNum',
    label: '座位数',
    width: 100,
    align: 'center'
  },
  {
    prop: 'serviceYears',
    label: '服务年数',
    width: 100,
    align: 'center'
  },
  {
    prop: 'createTime',
    label: '创建时间',
    minWidth: 180,
    align: 'center'
  },
  {
    prop: 'updateTime',
    label: '更新时间',
    minWidth: 180,
    align: 'center'
  },
  {
    prop: 'createPerson',
    label: '创建人',
    minWidth: 120,
    align: 'center'
  },
  {
    prop: 'updatePerson',
    label: '更新者',
    minWidth: 120,
    align: 'center'
  },
  {
    prop: 'status',
    label: '状态',
    width: 100,
    align: 'center'
  },
  {
    prop: 'action',
    label: '操作',
    width: 200,
    align: 'center',
    fixed: 'right'
  }
]

// 搜索字段配置
const searchFields = [
  {
    prop: 'trainName',
    label: '列车名',
    type: 'input',
    placeholder: '请输入列车名',
    clearable: true
  },
  {
    prop: 'trainModel',
    label: '列车型号',
    type: 'input',
    placeholder: '请输入列车型号',
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
 * 获取列车列表
 * @param {Object} searchParams - 搜索参数
 */
const fetchTrainList = async (searchParams = {}) => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      ...searchParams
    }
    
    // API调用
    const response = await getTrainList(params)
    const data = response.records
    
    // 按状态筛选
    let filteredData = filterDataByStatus(data)
    
    // 模拟分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    const paginatedData = filteredData.slice(start, end)
    
    trainList.value = paginatedData
    total.value = filteredData.length
    
    // 更新状态数量统计
    updateStatusCounts(data)
  } catch (error) {
    trainList.value = []
    total.value = 0
    
    // 重置状态统计
    statusCounts.value = {
      all: 0,
      enabled: 0,
      disabled: 0
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
  if (statusFilter.value === 'enabled') {
    return data.filter(item => item.status === TRAIN_STATUS.ENABLED)
  }
  if (statusFilter.value === 'disabled') {
    return data.filter(item => item.status === TRAIN_STATUS.DISABLED)
  }
  return data
}

/**
 * 更新状态数量统计
 * @param {Array} list - 列车列表数据
 */
const updateStatusCounts = (list) => {
  const enabledCount = list.filter(item => item.status === TRAIN_STATUS.ENABLED).length
  const disabledCount = list.filter(item => item.status === TRAIN_STATUS.DISABLED).length
  
  statusCounts.value = {
    all: list.length,
    enabled: enabledCount,
    disabled: disabledCount
  }
}

/**
 * 获取状态文本
 * @param {number} status - 状态值
 * @returns {string} 状态文本
 */
const getStatusText = (status) => {
  return TRAIN_STATUS.getText(status)
}

/**
 * 获取状态标签类型
 * @param {number} status - 状态值
 * @returns {string} 标签类型
 */
const getStatusTagType = (status) => {
  return TRAIN_STATUS.getTagType(status)
}

/**
 * 处理查询操作
 * @param {Object} formData - 搜索表单数据
 */
const handleSearch = (formData) => {
  currentPage.value = 1 // 重置为第一页
  selectedRows.value = [] // 清空选择
  fetchTrainList(formData)
}

/**
 * 重置查询条件
 */
const handleReset = () => {
  statusFilter.value = 'all' // 重置状态筛选
  currentPage.value = 1 // 重置为第一页
  selectedRows.value = [] // 清空选择
  fetchTrainList({}) // 重置后自动查询
}

/**
 * 处理状态标签页切换
 * @param {string} tabName - 切换到的标签页名称
 */
const handleStatusTabChange = (tabName) => {
  statusFilter.value = tabName // 更新状态筛选
  currentPage.value = 1 // 重置为第一页
  selectedRows.value = [] // 清空选择
  fetchTrainList()
}

/**
 * 处理表格选择变化
 * @param {Array} selection - 选中的行数据
 */
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

/**
 * 新增列车
 */
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  // 重置表单数据
  trainForm.value = {
    id: '',
    trainName: '',
    trainModel: '',
    seatNum: 0,
    serviceYears: 0,
    status: TRAIN_STATUS.ENABLED
  }
}

/**
 * 批量删除列车
 */
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的列车')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个列车吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    for (const id of ids) {
      await deleteTrain(id)
    }
    ElMessage.success(`成功删除 ${selectedRows.value.length} 个列车`)
    
    // 删除成功后刷新列表
    fetchTrainList()
  } catch (error) {
    // 用户取消删除或发生错误
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 分页大小变化
 * @param {number} size - 每页条数
 */
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchTrainList()
}

/**
 * 分页切换
 * @param {number} page - 当前页码
 */
const handlePageChange = (page) => {
  currentPage.value = page
  fetchTrainList()
}

/**
 * 编辑列车
 * @param {Object} row - 列车数据
 */
const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  // 复制数据到表单
  trainForm.value = {
    id: row.id,
    trainName: row.trainName,
    trainModel: row.trainModel,
    seatNum: row.seatNum,
    serviceYears: row.serviceYears,
    status: row.status
  }
}

/**
 * 处理表单提交
 * @param {Object} formData - 表单数据
 */
const handleFormSubmit = async (formData) => {
  try {
    submitLoading.value = true
    
    // 准备提交数据
    const submitData = { ...formData }
    
    // 设置创建人或更新人
    if (isEdit.value) {
      submitData.updatePerson = employeeInfo.value?.nickname || employeeInfo.value?.username
    } else {
      submitData.createPerson = employeeInfo.value?.nickname || employeeInfo.value?.username
    }
    
    if (isEdit.value) {
      // 编辑列车
      await updateTrain(submitData.id, submitData)
      ElMessage.success('更新列车成功')
    } else {
      // 新增列车
      await createTrain(submitData)
      ElMessage.success('新增列车成功')
    }
    
    // 关闭弹窗
    dialogVisible.value = false
    // 刷新列表
    fetchTrainList()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

/**
 * 切换列车状态
 * @param {Object} row - 列车数据
 */
const handleStatusChange = async (row) => {
  const newStatus = row.status === TRAIN_STATUS.ENABLED ? TRAIN_STATUS.DISABLED : TRAIN_STATUS.ENABLED
  const actionText = newStatus === TRAIN_STATUS.ENABLED ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}列车「${row.trainName}」吗？`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const data = { id: row.id, status: newStatus }
    
    await updateTrainStatus(data)
    ElMessage.success(`${actionText}成功`)
    
    // 刷新列表
    fetchTrainList()
  } catch (error) {
    // 用户取消操作或发生错误
    if (error !== 'cancel') {
      ElMessage.error(`${actionText}失败`)
    }
  }
}

/**
 * 删除列车
 * @param {Object} row - 列车数据
 */
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除列车「${row.trainName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用删除接口
    await deleteTrain(row.id)
    ElMessage.success('删除成功')
    // 删除成功后刷新列表
    fetchTrainList()
  } catch (error) {
    // 用户取消删除或发生错误
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 组件挂载后加载数据
onMounted(() => {
  fetchTrainList()
})
</script>

<style scoped lang="scss">
/* 使用ListPage组件的样式，无需额外样式 */
@use './Train-list-view.scss';
</style>