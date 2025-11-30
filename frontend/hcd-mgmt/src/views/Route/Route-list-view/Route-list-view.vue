<template>
  <ListPage
    title="线路管理"
    :show-tabs="true"
    :tabs="tabs"
    :active-tab="statusFilter"
    :total="total"
    :current-page="currentPage"
    :page-size="pageSize"
    :selected-count="selectedRows.length"
    :loading="loading"
    :show-selection="true"
    :table-data="routeList"
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
    <template #actions>
      <el-button type="primary" @click="handleAdd">新增线路</el-button>
      <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
        批量删除
        <span class="selected-count">({{ selectedRows.length }})</span>
      </el-button>
    </template>

    <template #column-createTime="{ row }">
      {{ formatDate(row.createTime) }}
    </template>
    <template #column-updateTime="{ row }">
      {{ formatDate(row.updateTime) }}
    </template>

    <template #column-action="{ row }">
      <el-button type="primary" text size="small" @click="handleEdit(row)">编辑</el-button>
      <el-button type="primary" text size="small" @click="handleManageStations(row)">站点管理</el-button>
      <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
    </template>
  </ListPage>

  <DialogForm
    v-model:visible="dialogVisible"
    :title="dialogTitle"
    :form-data="routeForm"
    :fields="formFields"
    :rules="formRules"
    :is-edit="isEdit"
    :confirm-text="isEdit ? '更新' : '新增'"
    :loading="submitLoading"
    @submit="handleFormSubmit"
  />

  <!-- 线路站点管理弹窗 -->
  <RouteStationDialog
    v-model:visible="routeStationDialogVisible"
    :route-id="currentRouteId"
    :route-name="currentRouteName"
    @close="handleRouteStationDialogClose"
  />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useEmployeeStore } from '@/stores'
import DialogForm from '@/components/DialogForm/DialogForm.vue'
import RouteStationDialog from '@/components/RouteStationDialog/RouteStationDialog.vue'
import ListPage from '@/components/ListPage/ListPage.vue'
import { getRouteList, addRoute, updateRoute, deleteRoute } from '@/api/route'

const employeeStore = useEmployeeStore()
const employeeInfo = computed(() => employeeStore.employeeInfo)

// 表单相关响应式数据
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const routeStationDialogVisible = ref(false)
const currentRouteId = ref(0)
const currentRouteName = ref('')

const routeForm = ref({
  id: '',
  routeName: '',
  startStationId: '',
  endStationId: ''
})

// 站点下拉选项
const stationOptions = ref([])

// 表单字段配置
const formFields = computed(() => [
  {
    prop: 'routeName',
    label: '线路名',
    type: 'input',
    placeholder: '请输入线路名',
    maxlength: 20,
    showWordLimit: true,
    disabled: (isEditMode) => isEditMode
  },
  {
    prop: 'startStationId',
    label: '起点站',
    type: 'select',
    placeholder: '请选择起点站',
    options: stationOptions.value,
    clearable: true,
    filterable: true
  },
  {
    prop: 'endStationId',
    label: '终点站',
    type: 'select',
    placeholder: '请选择终点站',
    options: stationOptions.value,
    clearable: true,
    filterable: true
  }
])

// 表单验证规则
const formRules = computed(() => ({
  routeName: [
    { required: true, message: '请输入线路名', trigger: 'blur' },
    { min: 2, max: 20, message: '线路名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  startStationId: [
    { required: true, message: '请选择起点站', trigger: 'change' }
  ],
  endStationId: [
    { required: true, message: '请选择终点站', trigger: 'change' }
  ]
}))

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑线路' : '新增线路')

// 响应式数据
const routeList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedRows = ref([])
const statusFilter = ref('all')

// 表格列配置
const tableColumns = [
  {
    prop: 'id',
    label: '线路号',
    width: 120,
    align: 'center'
  },
  {
    prop: 'routeName',
    label: '线路名',
    minWidth: 150,
    align: 'center'
  },
  {
    prop: 'startStation',
    label: '起点站',
    minWidth: 150,
    align: 'center'
  },
  {
    prop: 'endStation',
    label: '终点站',
    minWidth: 150,
    align: 'center'
  },
  {
    prop: 'stationCount',
    label: '站点数',
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
    prop: 'action',
    label: '操作',
    width: 250,
    align: 'center',
    fixed: 'right'
  }
]

// 搜索字段配置
const searchFields = [
  {
    prop: 'routeName',
    label: '线路名',
    type: 'input',
    placeholder: '请输入线路名',
    clearable: true
  },
  {
    prop: 'startStation',
    label: '起点站',
    type: 'input',
    placeholder: '请输入起点站',
    clearable: true
  },
  {
    prop: 'endStation',
    label: '终点站',
    type: 'input',
    placeholder: '请输入终点站',
    clearable: true
  }
]

// 初始搜索表单数据
const initialSearchForm = searchFields.reduce((acc, field) => {
  acc[field.prop] = ''
  return acc
}, {})

// 标签页配置
const tabs = [
  {
    name: 'all',
    label: '全部',
    count: 0,
    lazy: true
  }
]

/**
 * 格式化日期时间
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
 * 获取站点列表（用于下拉选择）
 */
const fetchStations = async () => {
  try {
    const response = await getAllStations()
    stationOptions.value = response.map(station => ({
      value: station.id,
      label: station.stationName,
      address: station.address
    }))
  } catch (error) {
    ElMessage.error('获取站点列表失败')
  }
}

/**
 * 获取线路列表
 */
const fetchRouteList = async (searchParams = {}) => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      ...searchParams
    }
    
    const response = await getRouteList(params)
    routeList.value = response.records
    total.value = response.total
  } catch (error) {
    routeList.value = []
    total.value = 0
    ElMessage.error('获取线路列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理查询操作
 */
const handleSearch = (formData) => {
  currentPage.value = 1
  selectedRows.value = []
  fetchRouteList(formData)
}

/**
 * 重置查询条件
 */
const handleReset = () => {
  currentPage.value = 1
  selectedRows.value = []
  fetchRouteList({})
}

/**
 * 处理状态标签页切换
 */
const handleStatusTabChange = (tabName) => {
  statusFilter.value = tabName
  currentPage.value = 1
  selectedRows.value = []
  fetchRouteList()
}

/**
 * 处理分页大小变化
 */
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchRouteList()
}

/**
 * 处理分页切换
 */
const handlePageChange = (page) => {
  currentPage.value = page
  fetchRouteList()
}

/**
 * 处理表格选择变化
 */
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

/**
 * 新增线路
 */
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  routeForm.value = {
    id: '',
    routeName: '',
    startStationId: '',
    endStationId: ''
  }
}

/**
 * 批量删除线路
 */
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的线路')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个线路吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const ids = selectedRows.value.map(row => row.id)
    await deleteRoute(ids)
    ElMessage.success(`成功删除 ${selectedRows.value.length} 个线路`)

    fetchRouteList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 编辑线路
 */
const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  routeForm.value = {
    id: row.id,
    routeName: row.routeName,
    startStationId: row.startStationId,
    endStationId: row.endStationId
  }
}

/**
 * 管理线路站点
 */
const handleManageStations = (row) => {
  currentRouteId.value = row.id
  currentRouteName.value = row.routeName
  routeStationDialogVisible.value = true
}

/**
 * 处理表单提交
 */
const handleFormSubmit = async (formData) => {
  try {
    submitLoading.value = true
    
    const submitData = { ...formData }
    
    if (isEdit.value) {
      submitData.updatePerson = employeeInfo.value?.nickname || employeeInfo.value?.username
      await updateRoute(submitData)
      ElMessage.success('更新线路成功')
    } else {
      submitData.createPerson = employeeInfo.value?.nickname || employeeInfo.value?.username
      await addRoute(submitData)
      ElMessage.success('新增线路成功')
    }
    
    dialogVisible.value = false
    fetchRouteList()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

/**
 * 删除线路
 */
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除线路「${row.routeName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteRoute([row.id])
    ElMessage.success('删除成功')
    fetchRouteList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 处理线路站点管理弹窗关闭
 */
const handleRouteStationDialogClose = () => {
  routeStationDialogVisible.value = false
  currentRouteId.value = 0
  currentRouteName.value = ''
  fetchRouteList()
}

// 组件挂载后加载数据
onMounted(() => {
  fetchStations()
  fetchRouteList()
})
</script>

<style scoped lang="scss">
@use './Route-list-view.scss';
</style>