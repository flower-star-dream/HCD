<template>
  <div class="demo-container">
    <h2>座位预订对话框组件演示</h2>
    
    <div class="demo-section">
      <h3>基本用法</h3>
      <el-button type="primary" @click="showAddDialog">新增座位预订</el-button>
      <el-button type="warning" @click="showEditDialog">编辑座位预订</el-button>
    </div>

    <div class="demo-section">
      <h3>演示数据</h3>
      <el-table :data="demoData" border style="width: 100%">
        <el-table-column prop="id" label="预订号" width="100" />
        <el-table-column prop="scheduleInfo" label="班次信息" min-width="200" />
        <el-table-column prop="seatNumber" label="座位号" width="100" />
        <el-table-column prop="bookingStatus" label="预订状态" width="120">
          <template #default="{ row }">
            <el-tag 
              :type="BOOKING_STATUS_TYPES[row.bookingStatus]" 
              size="small"
              effect="dark"
            >
              {{ BOOKING_STATUS_LABELS[row.bookingStatus] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
      </el-table>
    </div>

    <div class="demo-section">
      <h3>组件说明</h3>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="组件名称">SeatReservationDialogView</el-descriptions-item>
        <el-descriptions-item label="组件路径">@/views/SeatReservation/SeatReservation-dialog-view/SeatReservation-dialog-view.vue</el-descriptions-item>
        <el-descriptions-item label="功能描述">座位预订管理的对话框组件，支持新增和编辑座位预订信息</el-descriptions-item>
        <el-descriptions-item label="主要特性">
          <ul>
            <li>班次选择下拉框，显示班次详细信息</li>
            <li>座位号输入和可用性检查</li>
            <li>预订状态管理（可预订、已预订、已锁定）</li>
            <li>座位状态统计信息展示</li>
            <li>表单验证和错误处理</li>
          </ul>
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </div>

  <!-- 座位预订对话框 -->
  <SeatReservationDialogView
    v-model:visible="dialogVisible"
    :is-edit="isEdit"
    :seat-reservation-data="currentData"
    @submit="handleSubmit"
    @cancel="handleCancel"
  />
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import SeatReservationDialogView from './SeatReservation-dialog-view/SeatReservation-dialog-view.vue'
import { BOOKING_STATUS_LABELS, BOOKING_STATUS_TYPES } from '@/types/seat-reservation'

// 响应式数据
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentData = ref({})

// 演示数据
const demoData = ref([
  {
    id: 1001,
    scheduleId: 1,
    scheduleInfo: 'G123 - 北京南-上海虹桥',
    seatNumber: 15,
    bookingStatus: 0,
    createTime: '2024-01-15 10:30:00',
    updateTime: '2024-01-15 10:30:00',
    createPerson: '管理员'
  },
  {
    id: 1002,
    scheduleId: 2,
    scheduleInfo: 'D456 - 上海虹桥-杭州东',
    seatNumber: 32,
    bookingStatus: 1,
    createTime: '2024-01-15 11:15:00',
    updateTime: '2024-01-15 11:15:00',
    createPerson: '管理员'
  }
])

/**
 * 显示新增对话框
 */
const showAddDialog = () => {
  isEdit.value = false
  currentData.value = {}
  dialogVisible.value = true
}

/**
 * 显示编辑对话框
 */
const showEditDialog = () => {
  isEdit.value = true
  currentData.value = demoData.value[0] // 使用第一条演示数据
  dialogVisible.value = true
}

/**
 * 处理表单提交
 */
const handleSubmit = (formData) => {
  ElMessage.success(`座位预订${isEdit.value ? '更新' : '新增'}成功！`)
  console.log('提交的表单数据:', formData)
}

/**
 * 处理取消操作
 */
const handleCancel = () => {
  ElMessage.info('操作已取消')
}
</script>

<style scoped lang="scss">
.demo-container {
  padding: 20px;
  
  h2 {
    margin-bottom: 20px;
    color: #303133;
  }
  
  .demo-section {
    margin-bottom: 30px;
    
    h3 {
      margin-bottom: 15px;
      color: #606266;
      font-size: 16px;
    }
    
    .el-button {
      margin-right: 10px;
    }
  }
}
</style>