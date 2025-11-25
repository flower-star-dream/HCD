<template>
  <!-- 列车管理弹窗使用示例 -->
  <DialogForm
    v-model:visible="dialogVisible"
    title="列车管理"
    :form-data="trainForm"
    :fields="formFields"
    :rules="formRules"
    :is-edit="isEdit"
    :confirm-text="isEdit ? '更新' : '新增'"
    :loading="submitLoading"
    width="600px"
    @submit="handleFormSubmit"
  />
</template>

<script setup>
import { ref, computed } from 'vue'

// Props定义
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  isEdit: {
    type: Boolean,
    default: false
  },
  trainData: {
    type: Object,
    default: () => ({})
  }
})

// Emits定义
const emit = defineEmits(['update:visible', 'submit'])

// 列车状态枚举
const TRAIN_STATUS = {
  ENABLED: 1,
  DISABLED: 0
}

// 响应式数据
const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const trainForm = ref({
  id: '',
  trainName: '',
  trainModel: '',
  seatNum: 0,
  serviceYears: 0,
  status: TRAIN_STATUS.ENABLED
})

const submitLoading = ref(false)

// 表单字段配置
const formFields = [
  {
    prop: 'trainName',
    label: '列车名',
    type: 'input',
    placeholder: '请输入列车名',
    maxlength: 10,
    showWordLimit: true,
    disabled: (isEditMode) => isEditMode
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

/**
 * 处理表单提交
 * @param {Object} formData - 表单数据
 */
const handleFormSubmit = (formData) => {
  // 数据验证和格式化
  const submitData = {
    ...formData,
    seatNum: Number(formData.seatNum),
    serviceYears: Number(formData.serviceYears)
  }
  
  // 触发提交事件
  emit('submit', submitData)
}

/**
 * 加载列车数据
 * @param {Object} data - 列车数据
 */
const loadTrainData = (data) => {
  if (data && Object.keys(data).length > 0) {
    trainForm.value = {
      id: data.id || '',
      trainName: data.trainName || '',
      trainModel: data.trainModel || '',
      seatNum: data.seatNum || 0,
      serviceYears: data.serviceYears || 0,
      status: data.status !== undefined ? data.status : TRAIN_STATUS.ENABLED
    }
  } else {
    // 重置表单
    trainForm.value = {
      id: '',
      trainName: '',
      trainModel: '',
      seatNum: 0,
      serviceYears: 0,
      status: TRAIN_STATUS.ENABLED
    }
  }
}

// 监听props.trainData变化
watch(
  () => props.trainData,
  (newData) => {
    loadTrainData(newData)
  },
  { immediate: true, deep: true }
)

// 暴露方法给父组件
defineExpose({
  loadTrainData
})
</script>

<style scoped lang="scss">
// 列车管理弹窗样式（可选）
:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-form) {
  padding: 20px 0;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

// 响应式布局
@media screen and (max-width: 768px) {
  :deep(.el-dialog) {
    width: 90% !important;
    margin-top: 15vh !important;
  }
}
</style>