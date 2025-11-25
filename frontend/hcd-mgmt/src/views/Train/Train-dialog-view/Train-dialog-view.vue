<template>
  <!-- 列车管理弹窗组件 -->
  <DialogForm
    v-model:visible="dialogVisible"
    :title="dialogTitle"
    :form-data="trainForm"
    :fields="formFields"
    :rules="formRules"
    :is-edit="isEdit"
    :confirm-text="isEdit ? '更新' : '新增'"
    :loading="submitLoading"
    :width="dialogWidth"
    @submit="handleSubmit"
    @cancel="handleCancel"
    @close="handleClose"
  >
    <!-- 自定义表单项插槽（如果需要） -->
    <template #field-trainName="{ field }">
      <el-input
        v-model="trainForm.trainName"
        :placeholder="field.placeholder"
        :maxlength="field.maxlength"
        :show-word-limit="field.showWordLimit"
        :disabled="isFieldDisabled(field)"
        clearable
      />
    </template>

    <template #field-trainModel="{ field }">
      <el-input
        v-model="trainForm.trainModel"
        :placeholder="field.placeholder"
        :maxlength="field.maxlength"
        :show-word-limit="field.showWordLimit"
        :disabled="isFieldDisabled(field)"
        clearable
      />
    </template>

    <template #field-seatNum="{ field }">
      <el-input-number
        v-model="trainForm.seatNum"
        :placeholder="field.placeholder"
        :min="1"
        :max="2000"
        :disabled="isFieldDisabled(field)"
        controls-position="right"
        style="width: 100%"
      />
    </template>

    <template #field-serviceYears="{ field }">
      <el-input-number
        v-model="trainForm.serviceYears"
        :placeholder="field.placeholder"
        :min="0"
        :max="50"
        :disabled="isFieldDisabled(field)"
        controls-position="right"
        style="width: 100%"
      />
    </template>

    <template #field-status="{ field }">
      <el-switch
        v-model="trainForm.status"
        :active-value="field.activeValue"
        :inactive-value="field.inactiveValue"
        :active-text="field.activeText"
        :inactive-text="field.inactiveText"
        :disabled="isFieldDisabled(field)"
      />
    </template>
  </DialogForm>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import DialogForm from '@/components/DialogForm/DialogForm.vue'

// Props定义
const props = defineProps({
  // 控制弹窗显示
  visible: {
    type: Boolean,
    default: false
  },
  // 是否为编辑模式
  isEdit: {
    type: Boolean,
    default: false
  },
  // 列车数据
  trainData: {
    type: Object,
    default: () => ({})
  },
  // 弹窗标题
  title: {
    type: String,
    default: ''
  },
  // 是否显示加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 弹窗宽度
  width: {
    type: String,
    default: '600px'
  }
})

// Emits定义
const emit = defineEmits([
  'update:visible',
  'submit',
  'cancel',
  'close'
])

// 列车状态枚举常量
const TRAIN_STATUS = {
  ENABLED: 1,
  DISABLED: 0,
  getText: (status) => {
    const statusMap = {
      [TRAIN_STATUS.ENABLED]: '已启用',
      [TRAIN_STATUS.DISABLED]: '已禁用'
    }
    return statusMap[status] || '未知状态'
  }
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

// 计算属性
const dialogTitle = computed(() => {
  if (props.title) return props.title
  return props.isEdit ? '编辑列车' : '新增列车'
})

const dialogWidth = computed(() => props.width)

const submitLoading = computed(() => props.loading)

// 表单字段配置
const formFields = [
  {
    prop: 'trainName',
    label: '列车名',
    type: 'custom',
    placeholder: '请输入列车名',
    maxlength: 10,
    showWordLimit: true,
    disabled: (isEditMode) => isEditMode,
    required: true
  },
  {
    prop: 'trainModel',
    label: '列车型号',
    type: 'custom',
    placeholder: '请输入列车型号',
    maxlength: 50,
    showWordLimit: true,
    required: true
  },
  {
    prop: 'seatNum',
    label: '座位数',
    type: 'custom',
    placeholder: '请输入座位数',
    required: true
  },
  {
    prop: 'serviceYears',
    label: '服务年数',
    type: 'custom',
    placeholder: '请输入服务年数',
    required: true
  },
  {
    prop: 'status',
    label: '状态',
    type: 'custom',
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
 * 检查字段是否禁用
 * @param {Object} field - 字段配置
 * @returns {boolean} 是否禁用
 */
const isFieldDisabled = (field) => {
  if (typeof field.disabled === 'function') {
    return field.disabled(props.isEdit)
  }
  return field.disabled || false
}

/**
 * 检查字段是否隐藏
 * @param {Object} field - 字段配置
 * @returns {boolean} 是否隐藏
 */
const isFieldHidden = (field) => {
  if (typeof field.hidden === 'function') {
    return field.hidden(props.isEdit)
  }
  return field.hidden || false
}

/**
 * 处理表单提交
 * @param {Object} formData - 表单数据
 */
const handleSubmit = (formData) => {
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
 * 处理取消操作
 */
const handleCancel = () => {
  emit('cancel')
  dialogVisible.value = false
}

/**
 * 处理关闭弹窗
 */
const handleClose = () => {
  emit('close')
  // 重置表单数据
  resetForm()
}

/**
 * 重置表单数据
 */
const resetForm = () => {
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
    resetForm()
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

// 监听弹窗显示状态
watch(
  () => dialogVisible.value,
  (visible) => {
    if (!visible) {
      // 弹窗关闭时重置表单
      resetForm()
    }
  }
)

// 暴露方法给父组件
defineExpose({
  // 重置表单
  resetForm,
  // 加载数据
  loadTrainData,
  // 获取表单数据
  getFormData: () => ({ ...trainForm.value })
})
</script>

<style scoped lang="scss">
// 列车管理弹窗样式
:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-form) {
  padding: 20px 0;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input-number) {
  width: 100%;
}

// 自定义字段样式
.train-name-input,
.train-model-input {
  :deep(.el-input__inner) {
    font-weight: 500;
  }
}

// 响应式布局
@media screen and (max-width: 768px) {
  :deep(.el-dialog) {
    width: 90% !important;
    margin-top: 15vh !important;
  }
  
  :deep(.el-form) {
    padding: 10px 0;
  }
  
  :deep(.el-form-item) {
    margin-bottom: 15px;
  }
}
</style>