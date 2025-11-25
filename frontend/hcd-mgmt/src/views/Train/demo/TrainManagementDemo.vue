<template>
  <div class="train-management-demo">
    <h2>列车管理组件演示</h2>
    
    <!-- 基础列表演示 -->
    <el-card class="demo-section">
      <template #header>
        <div class="card-header">
          <span>列车列表管理</span>
          <el-button type="primary" size="small" @click="showListDemo = !showListDemo">
            {{ showListDemo ? '隐藏' : '显示' }}
          </el-button>
        </div>
      </template>
      
      <div v-if="showListDemo" class="demo-content">
        <TrainListView v-if="!showDialogDemo" />
        <div v-else class="placeholder">
          <el-empty description="列表已隐藏，请查看弹窗演示" />
        </div>
      </div>
    </el-card>

    <!-- 表单弹窗演示 -->
    <el-card class="demo-section">
      <template #header>
        <div class="card-header">
          <span>列车表单弹窗</span>
          <div>
            <el-button type="success" size="small" @click="openAddDialog">新增列车</el-button>
            <el-button type="warning" size="small" @click="openEditDialog">编辑列车</el-button>
          </div>
        </div>
      </template>
      
      <div class="demo-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="弹窗状态">{{ dialogVisible ? '显示' : '隐藏' }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">{{ isEdit ? '编辑' : '新增' }}</el-descriptions-item>
          <el-descriptions-item label="表单数据">
            <pre>{{ JSON.stringify(trainForm, null, 2) }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="验证规则">
            <el-tag v-for="rule in activeRules" :key="rule.prop" size="small" style="margin: 2px">
              {{ rule.label }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- API调用演示 -->
    <el-card class="demo-section">
      <template #header>
        <div class="card-header">
          <span>API接口测试</span>
          <div>
            <el-button type="info" size="small" @click="testGetList">测试列表</el-button>
            <el-button type="info" size="small" @click="testCreate">测试创建</el-button>
            <el-button type="info" size="small" @click="testUpdate">测试更新</el-button>
          </div>
        </div>
      </template>
      
      <div class="demo-content">
        <el-input
          v-model="apiTestResult"
          type="textarea"
          :rows="6"
          readonly
          placeholder="API测试结果将显示在这里"
        />
      </div>
    </el-card>

    <!-- 组件属性说明 -->
    <el-card class="demo-section">
      <template #header>
        <div class="card-header">
          <span>组件属性说明</span>
        </div>
      </template>
      
      <div class="demo-content">
        <el-collapse v-model="activeNames">
          <el-collapse-item title="ListPage组件属性" name="list">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="title">页面标题</el-descriptions-item>
              <el-descriptions-item label="showTabs">是否显示标签页</el-descriptions-item>
              <el-descriptions-item label="tabs">标签页配置数组</el-descriptions-item>
              <el-descriptions-item label="tableData">表格数据数组</el-descriptions-item>
              <el-descriptions-item label="tableColumns">表格列配置数组</el-descriptions-item>
              <el-descriptions-item label="searchFields">搜索字段配置数组</el-descriptions-item>
            </el-descriptions>
          </el-collapse-item>
          
          <el-collapse-item title="DialogForm组件属性" name="dialog">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="visible">控制弹窗显示</el-descriptions-item>
              <el-descriptions-item label="title">弹窗标题</el-descriptions-item>
              <el-descriptions-item label="formData">表单数据对象</el-descriptions-item>
              <el-descriptions-item label="fields">表单项配置数组</el-descriptions-item>
              <el-descriptions-item label="rules">表单验证规则</el-descriptions-item>
              <el-descriptions-item label="isEdit">是否为编辑模式</el-descriptions-item>
            </el-descriptions>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-card>

    <!-- 实际使用的弹窗组件 -->
    <DialogForm
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form-data="trainForm"
      :fields="formFields"
      :rules="formRules"
      :is-edit="isEdit"
      :confirm-text="isEdit ? '更新' : '新增'"
      :loading="submitLoading"
      width="600px"
      @submit="handleFormSubmit"
    />
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import TrainListView from '../Train-list-view/Train-list-view.vue'

// 演示控制
const showListDemo = ref(true)
const activeNames = ref(['list'])

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)

const trainForm = reactive({
  id: '',
  trainName: '',
  trainModel: '',
  seatNum: 0,
  serviceYears: 0,
  status: 1
})

const dialogTitle = computed(() => isEdit.value ? '编辑列车' : '新增列车')

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
    activeValue: 1,
    inactiveValue: 0,
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

const activeRules = computed(() => {
  return formFields.filter(field => !field.hidden || !field.hidden(isEdit.value))
})

// API测试
const apiTestResult = ref('')

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  Object.assign(trainForm, {
    id: '',
    trainName: '',
    trainModel: '',
    seatNum: 0,
    serviceYears: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 打开编辑弹窗
const openEditDialog = () => {
  isEdit.value = true
  Object.assign(trainForm, {
    id: 1,
    trainName: 'G1234',
    trainModel: 'CRH380A',
    seatNum: 556,
    serviceYears: 5,
    status: 1
  })
  dialogVisible.value = true
}

// 处理表单提交
const handleFormSubmit = (formData) => {
  submitLoading.value = true
  
  setTimeout(() => {
    submitLoading.value = false
    dialogVisible.value = false
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    
    apiTestResult.value = JSON.stringify({
      type: isEdit.value ? 'update' : 'create',
      data: formData,
      timestamp: new Date().toISOString(),
      message: '表单提交成功'
    }, null, 2)
  }, 1500)
}

// API测试函数
const testGetList = () => {
  apiTestResult.value = JSON.stringify({
    type: 'getList',
    params: { page: 1, pageSize: 10 },
    response: {
      total: 25,
      records: [
        { id: 1, trainName: 'G1234', trainModel: 'CRH380A', seatNum: 556, serviceYears: 5, status: 1 },
        { id: 2, trainName: 'D5678', trainModel: 'CRH2A', seatNum: 610, serviceYears: 8, status: 1 }
      ]
    },
    timestamp: new Date().toISOString()
  }, null, 2)
  ElMessage.success('列表API测试完成')
}

const testCreate = () => {
  const testData = {
    trainName: 'G9999',
    trainModel: 'CR400AF',
    seatNum: 576,
    serviceYears: 0,
    status: 1,
    createPerson: '管理员'
  }
  
  apiTestResult.value = JSON.stringify({
    type: 'create',
    data: testData,
    response: { code: 200, message: '创建成功', data: { id: 999 } },
    timestamp: new Date().toISOString()
  }, null, 2)
  ElMessage.success('创建API测试完成')
}

const testUpdate = () => {
  const testData = {
    id: 1,
    trainName: 'G1234',
    trainModel: 'CRH380A-更新',
    seatNum: 600,
    serviceYears: 6,
    status: 1,
    updatePerson: '管理员'
  }
  
  apiTestResult.value = JSON.stringify({
    type: 'update',
    data: testData,
    response: { code: 200, message: '更新成功' },
    timestamp: new Date().toISOString()
  }, null, 2)
  ElMessage.success('更新API测试完成')
}
</script>

<style scoped lang="scss">
.train-management-demo {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.demo-section {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  
  :deep(.el-card__header) {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 16px 20px;
  }
  
  :deep(.el-card__body) {
    padding: 20px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  span {
    font-size: 16px;
    font-weight: 600;
  }
}

.demo-content {
  .placeholder {
    padding: 40px 0;
    text-align: center;
  }
  
  pre {
    background-color: #f8f9fa;
    padding: 12px;
    border-radius: 4px;
    font-size: 12px;
    line-height: 1.5;
    margin: 0;
    max-height: 200px;
    overflow-y: auto;
  }
  
  :deep(.el-descriptions) {
    margin-bottom: 0;
  }
  
  :deep(.el-collapse) {
    border: none;
    
    .el-collapse-item__header {
      background-color: transparent;
      border-bottom: 1px solid #ebeef5;
      font-weight: 500;
    }
    
    .el-collapse-item__wrap {
      background-color: transparent;
      border-bottom: none;
    }
    
    .el-collapse-item__content {
      padding: 20px 0;
    }
  }
}

h2 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
  text-align: center;
}

// 响应式设计
@media screen and (max-width: 768px) {
  .train-management-demo {
    padding: 10px;
  }
  
  .demo-section {
    margin-bottom: 15px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
    
    > div {
      display: flex;
      gap: 8px;
    }
  }
}
</style>