<template>
  <!-- 使用增强的ListPage组件，通过配置实现动态列和搜索条件 -->
  <ListPage
    title="用户列表"
    :show-tabs="true"
    :tabs="tabs"
    :active-tab="statusFilter"
    :total="total"
    :current-page="currentPage"
    :page-size="pageSize"
    :selected-count="selectedRows.length"
    :loading="loading"
    :show-selection="true"
    :table-data="userList"
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
      <el-button type="primary" @click="handleAdd">新增用户</el-button>
      <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
        批量删除
        <span class="selected-count">({{ selectedRows.length }})</span>
      </el-button>
    </template>

    <!-- 自定义状态列 -->
    <template #column-status="{ row }">
      <el-tag :type="row.status === 'enabled' ? 'success' : 'danger'" size="small">
        {{ row.status === 'enabled' ? '已启用' : '已禁用' }}
      </el-tag>
    </template>

    <!-- 自定义创建时间列 -->
    <template #column-createdAt="{ row }">
      {{ formatDate(row.createdAt) }}
    </template>

    <!-- 自定义操作列 -->
    <template #column-action="{ row }">
      <el-button type="primary" text size="small" @click="handleEdit(row)">编辑</el-button>
      <el-button type="primary" text size="small" @click="handleStatusChange(row)">{{ row.status === 'disabled' ? '启用' : '禁用' }}</el-button>
      <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
    </template>
  </ListPage>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getUserListService } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const userList = ref([])
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
    prop: 'status',
    label: '状态',
    width: 100,
    align: 'center'
  },
  {
    prop: 'id',
    label: 'ID',
    width: 80,
    align: 'center'
  },
  {
    prop: 'username',
    label: '用户名',
    minWidth: 120,
    align: 'center'
  },
  {
    prop: 'email',
    label: '邮箱',
    minWidth: 180,
    showOverflowTooltip: true
  },
  {
    prop: 'createdAt',
    label: '创建时间',
    minWidth: 150,
    align: 'center'
  },
  {
    prop: 'updatedAt',
    label: '更新时间',
    minWidth: 150,
    align: 'center'
  },
  {
    prop: 'createdBy',
    label: '创建人',
    minWidth: 150,
    align: 'center'
  },
  {
    prop: 'updatedBy',
    label: '更新人',
    minWidth: 150,
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
    prop: 'username',
    label: '用户名',
    type: 'input',
    placeholder: '请输入用户名',
    clearable: true
  },
  {
    prop: 'email',
    label: '邮箱',
    type: 'input',
    placeholder: '请输入邮箱',
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
 * 获取用户列表
 * @param {Object} searchParams - 搜索参数
 */
const fetchUserList = async (searchParams = {}) => {
  loading.value = true
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据，实际项目中应从接口获取
    const mockData = generateMockData()
    // 先按状态筛选
    let filteredData = filterDataByStatus(mockData)
    // 再按搜索参数筛选
    if (searchParams) {
      filteredData = filteredData.filter(item => {
        // 用户名搜索
        if (searchParams.username && !item.username.includes(searchParams.username)) {
          return false
        }
        // 邮箱搜索
        if (searchParams.email && !item.email.includes(searchParams.email)) {
          return false
        }
        return true
      })
    }
    
    // 模拟分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    const paginatedData = filteredData.slice(start, end)
    
    userList.value = paginatedData
    total.value = filteredData.length
    
    // 更新状态数量统计
    updateStatusCounts(mockData)
  } catch (error) {
    ElMessage.error('获取用户列表失败：' + (error.message || '未知错误'))
    userList.value = []
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
 * 生成模拟用户数据
 * @returns {Array} 模拟用户列表
 */
const generateMockData = () => {
  const mockUsers = []
  const totalCount = 50
  
  for (let i = 1; i <= totalCount; i++) {
    mockUsers.push({
      id: i,
      username: `user${i}`,
      email: `user${i}@example.com`,
      createdAt: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
      status: i % 5 === 0 ? 'disabled' : 'enabled' // 模拟有部分禁用的用户
    })
  }
  
  return mockUsers
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
  return data.filter(item => item.status === statusFilter.value)
}

/**
 * 更新状态数量统计
 * @param {Array} list - 用户列表数据
 */
const updateStatusCounts = (list) => {
  const enabledCount = list.filter(item => item.status === 'enabled').length
  const disabledCount = list.filter(item => item.status === 'disabled').length
  
  statusCounts.value = {
    all: list.length,
    enabled: enabledCount,
    disabled: disabledCount
  }
}

/**
 * 处理查询操作
 * @param {Object} formData - 搜索表单数据
 */
const handleSearch = (formData) => {
  currentPage.value = 1 // 重置为第一页
  selectedRows.value = [] // 清空选择
  fetchUserList(formData)
}

/**
 * 重置查询条件
 */
const handleReset = () => {
  statusFilter.value = 'all' // 重置状态筛选
  currentPage.value = 1 // 重置为第一页
  selectedRows.value = [] // 清空选择
  fetchUserList({}) // 重置后自动查询
}

/**
 * 处理状态标签页切换
 * @param {string} tabName - 切换到的标签页名称
 */
const handleStatusTabChange = (tabName) => {
  statusFilter.value = tabName // 更新状态筛选
  currentPage.value = 1 // 重置为第一页
  selectedRows.value = [] // 清空选择
  fetchUserList()
}

/**
 * 处理表格选择变化
 * @param {Array} selection - 选中的行数据
 */
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

/**
 * 新增用户
 */
const handleAdd = () => {
  // TODO: 跳转到新增页面或打开新增弹窗
  ElMessage.info('打开新增用户页面')
}

/**
 * 批量删除用户
 */
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个用户吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    // TODO: 调用批量删除接口
    ElMessage.success(`成功删除 ${selectedRows.value.length} 个用户`)
    
    // 删除成功后刷新列表
    fetchUserList()
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
  fetchUserList()
}

/**
 * 分页切换
 * @param {number} page - 当前页码
 */
const handlePageChange = (page) => {
  currentPage.value = page
  fetchUserList()
}

/**
 * 编辑用户
 * @param {Object} row - 用户数据
 */
const handleEdit = (row) => {
  // TODO: 跳转到编辑页面或打开编辑弹窗
  ElMessage.info(`编辑用户：${row.username}`)
}

/**
 * 切换用户状态
 * @param {Object} row - 用户数据
 */
const handleStatusChange = async (row) => {
  // TODO: 调用切换状态接口
}

/**
 * 删除用户
 * @param {Object} row - 用户数据
 */
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户「${row.username}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用删除接口
    ElMessage.success('删除成功')
    // 删除成功后刷新列表
    fetchUserList()
  } catch (error) {
    // 用户取消删除或发生错误
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 组件挂载后加载数据
onMounted(() => {
  fetchUserList()
})
</script>

<style scoped lang="scss">
/* 使用ListPage组件的样式，无需额外样式 */
</style>
