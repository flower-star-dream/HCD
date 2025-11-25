<template>
  <div class="schedule-dialog-example">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>班次对话框组件示例</span>
        </div>
      </template>
      
      <div class="example-content">
        <el-button type="primary" @click="showAddDialog">新增班次（使用独立对话框）</el-button>
        <el-button type="success" @click="showEditDialog">编辑班次（使用独立对话框）</el-button>
        <el-button type="warning" @click="showIntegratedDialog">使用集成对话框</el-button>
      </div>
    </el-card>

    <!-- 独立班次对话框组件 -->
    <ScheduleDialog
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form-data="scheduleForm"
      :train-options="trainOptions"
      :route-options="routeOptions"
      :is-edit="isEdit"
      :loading="submitLoading"
      @submit="handleSubmit"
      @cancel="handleCancel"
    />

    <!-- 集成的对话框（使用通用DialogForm组件） -->
    <DialogForm
      v-model:visible="integratedDialogVisible"
      title="班次管理"
      :form-data="integratedForm"
      :fields="integratedFields"
      :rules="integratedRules"
      :is-edit="isEdit"
      :loading="submitLoading"
      @submit="handleIntegratedSubmit"
    >
      <!-- 自定义时间选择器 -->
      <template #field-startTime="{ field }">
        <el-date-picker
          v-model="integratedForm.startTime"
          type="datetime"
          placeholder="请选择出发时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%"
        />
      </template>
      
      <template #field-endTime="{ field }">
        <el-date-picker
          v-model="integratedForm.endTime"
          type="datetime"
          placeholder="请选择到达时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%"
        />
      </template>
    </DialogForm>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import ScheduleDialog from './Schedule-dialog-view/Schedule-dialog-view.vue'
import DialogForm from '@/components/DialogForm/DialogForm.vue'

// 响应式数据
const dialogVisible = ref(false)
const integratedDialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)

// 表单数据
const scheduleForm = ref({
  id: '',
  trainId: '',
  routeId: '',
  conductor: '',
  availableTickets: 0,
  startTime: '',
  endTime: ''
})

const integratedForm = ref({
  id: '',
  trainId: '',
  routeId: '',
  conductor: '',
  availableTickets: 0,
  startTime: '',
  endTime: ''
})

// 模拟的下拉框选项
const trainOptions = ref([
  { value: 1, label: 'G1234高铁', trainModel: 'CRH380A', seatNum: 556 },
  { value: 2, label: 'K5678快速', trainModel: '25G型', seatNum: 118 },
  { value: 3, label: 'D901动车', trainModel: 'CRH2A', seatNum: 610 }
])

const routeOptions = ref([
  { value: 1, label: '京广线', departureStation: '北京西', arrivalStation: '广州南', distance: 2298 },
  { value: 2, label: '沪昆线', departureStation: '上海虹桥', arrivalStation: '昆明南', distance: 2252 },
  { value: 3, label: '成渝线', departureStation: '成都东', arrivalStation: '重庆北', distance: 307 }
])

// 集成的表单字段配置
const integratedFields = [
  {
    prop: 'trainId',
    label: '列车',
    type: 'select',
    placeholder: '请选择列车',
    options: trainOptions,
    required: true,
    clearable: true
  },
  {
    prop: 'routeId',
    label: '线路',
    type: 'select',
    placeholder: '请选择线路',
    options: routeOptions,
    required: true,
    clearable: true
  },
  {
    prop: 'conductor',
    label: '列车长',
    type: 'input',
    placeholder: '请输入列车长姓名',
    maxlength: 10,
    showWordLimit: true,
    required: true
  },
  {
    prop: 'availableTickets',
    label: '余票数',
    type: 'input',
    placeholder: '请输入余票数',
    inputType: 'number',
    required: true
  },
  {
    prop: 'startTime',
    label: '出发时间',
    type: 'custom',
    required: true
  },
  {
    prop: 'endTime',
    label: '到达时间',
    type: 'custom',
    required: true
  }
]

// 集成的表单验证规则
const integratedRules = computed(() => ({
  trainId: [
    { required: true, message: '请选择列车', trigger: 'change' }
  ],
  routeId: [
    { required: true, message: '请选择线路', trigger: 'change' }
  ],
  conductor: [
    { required: true, message: '请输入列车长姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '列车长姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  availableTickets: [
    { required: true, message: '请输入余票数', trigger: 'blur' },
    { type: 'number', min: 0, max: 2000, message: '余票数必须在 0 到 2000 之间', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择出发时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择到达时间', trigger: 'change' }
  ]
}))

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑班次（独立对话框）' : '新增班次（独立对话框）')

/**
 * 显示新增对话框（独立组件）
 */
const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
  // 重置表单
  scheduleForm.value = {
    id: '',
    trainId: '',
    routeId: '',
    conductor: '',
    availableTickets: 0,
    startTime: '',
    endTime: ''
  }
}

/**
 * 显示编辑对话框（独立组件）
 */
const showEditDialog = () => {
  isEdit.value = true
  dialogVisible.value = true
  // 模拟编辑数据
  scheduleForm.value = {
    id: 123,
    trainId: 1,
    routeId: 1,
    conductor: '张三',
    availableTickets: 156,
    startTime: '2024-01-15 08:30:00',
    endTime: '2024-01-15 14:45:00'
  }
}

/**
 * 显示集成对话框
 */
const showIntegratedDialog = () => {
  isEdit.value = false
  integratedDialogVisible.value = true
  // 重置表单
  integratedForm.value = {
    id: '',
    trainId: '',
    routeId: '',
    conductor: '',
    availableTickets: 0,
    startTime: '',
    endTime: ''
  }
}

/**
 * 处理独立对话框提交
 */
const handleSubmit = async (formData) => {
  try {
    submitLoading.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success(isEdit.value ? '更新班次成功' : '新增班次成功')
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

/**
 * 处理集成对话框提交
 */
const handleIntegratedSubmit = async (formData) => {
  try {
    submitLoading.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success(isEdit.value ? '更新班次成功' : '新增班次成功')
    integratedDialogVisible.value = false
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

/**
 * 处理取消
 */
const handleCancel = () => {
  dialogVisible.value = false
}
</script>

<style scoped lang="scss">
.schedule-dialog-example {
  padding: 20px;
  
  .card-header {
    font-weight: bold;
    font-size: 16px;
  }
  
  .example-content {
    padding: 20px 0;
    
    .el-button {
      margin-right: 12px;
      margin-bottom: 12px;
    }
  }
}
</style>