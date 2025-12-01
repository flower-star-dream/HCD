<template>
  <!-- 使用增强的ListPage组件，通过配置实现动态列和搜索条件 -->
  <ListPage
    title="座位预订管理"
    :show-tabs="true"
    :tabs="tabs"
    :active-tab="statusFilter"
    :total="total"
    :current-page="currentPage"
    :page-size="pageSize"
    :selected-count="selectedRows.length"
    :loading="loading"
    :show-selection="true"
    :table-data="seatReservationList"
    :table-columns="tableColumns"
    :search-fields="searchFields"
    :initial-search-form="initialSearchForm"
    :pagination="true"
    :show-pagination="true"
    @tab-click="handleStatusTabChange"
    @size-change="handleSizeChange"
    @current-change="handlePageChange"
    @search="handleSearch"
    @reset="handleReset"
    @selection-change="handleSelectionChange"
  >
    <!-- 操作按钮区域 -->
    <template #actions>
      <el-button type="primary" @click="handleAdd">新增座位预订</el-button>
      <el-button
        type="warning"
        :disabled="selectedRows.length === 0"
        @click="handleBatchStatusUpdate(1)"
      >
        批量设为已预订
        <span class="selected-count">({{ selectedRows.length }})</span>
      </el-button>
      <el-button
        type="danger"
        :disabled="selectedRows.length === 0"
        @click="handleBatchDelete"
      >
        批量删除
        <span class="selected-count">({{ selectedRows.length }})</span>
      </el-button>
    </template>

    <!-- 自定义班次信息列 -->
    <template #column-scheduleInfo="{ row }">
      <el-tag type="info" size="small">
        {{
          row.scheduleInfo ||
          `${row.trainName || "未知列车"} - ${row.routeName || "未知线路"}`
        }}
      </el-tag>
    </template>

    <!-- 自定义座位号列 -->
    <template #column-seatNum="{ row }">
      <span class="seat-number">{{ row.seatNum }}</span>
    </template>

    <!-- 自定义预订状态列 -->
    <template #column-bookingStatus="{ row }">
      <el-tag
        :type="BOOKING_STATUS_TYPES[row.bookingStatus] || 'info'"
        size="small"
        effect="dark"
      >
        {{ BOOKING_STATUS_LABELS[row.bookingStatus] || "未知状态" }}
      </el-tag>
    </template>

    <!-- 自定义创建时间列 -->
    <template #column-createTime="{ row }">
      {{ formatDateTime(row.createTime) }}
    </template>

    <template #column-updateTime="{ row }">
      {{ formatDateTime(row.updateTime) }}
    </template>

    <!-- 自定义创建人列 -->
    <template #column-createPerson="{ row }">
      <span class="person-name">{{ row.createPerson || "-" }}</span>
    </template>

    <!-- 自定义操作列 -->
    <template #column-action="{ row }">
      <el-button type="primary" text size="small" @click="handleEdit(row)"
        >编辑</el-button
      >
      <el-button
        v-if="row.bookingStatus === 0"
        type="warning"
        text
        size="small"
        @click="handleStatusUpdate(row, 1)"
      >
        设为已预订
      </el-button>
      <el-button
        v-if="row.bookingStatus === 1"
        type="success"
        text
        size="small"
        @click="handleStatusUpdate(row, 0)"
      >
        设为可预订
      </el-button>

      <el-button type="danger" text size="small" @click="handleDelete(row)"
        >删除</el-button
      >
    </template>
  </ListPage>

  <!-- 使用通用表单弹窗组件 -->
  <DialogForm
    v-model:visible="dialogVisible"
    :title="dialogTitle"
    :form-data="seatReservationForm"
    :fields="formFields"
    :rules="formRules"
    :is-edit="isEdit"
    :confirm-text="isEdit ? '更新' : '新增'"
    :loading="submitLoading"
    width="700px"
    @submit="handleFormSubmit"
  >
    <!-- 自定义班次选择字段 -->
    <template #field-scheduleId>
      <el-select
        v-model="seatReservationForm.scheduleId"
        placeholder="请选择班次"
        style="width: 100%"
        @change="handleScheduleChange"
        filterable
        remote
        :remote-method="handleScheduleRemoteSearch"
        :loading="scheduleLoading"
        popper-class="schedule-select"
        clearable
      >
        <el-option
          v-for="option in scheduleOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        >
          <div class="schedule-option">
            <span>{{ option.label }}</span>
            <span class="schedule-info"
              >{{ option.routeInfo }} | {{ option.departureTime }}</span
            >
          </div>
        </el-option>
        <!-- 加载更多提示 -->
        <template #empty>
          <div v-if="isLoadingMore">加载中...</div>
          <div v-else-if="scheduleOptions.length > 0 && !scheduleAllLoaded">
            滚动到底部加载更多
          </div>
          <div v-else-if="scheduleAllLoaded">已加载全部班次</div>
          <div v-else>请输入关键词搜索班次</div>
        </template>
      </el-select>
    </template>

    <!-- 自定义座位号字段 -->
    <template #field-seatNum>
      <div class="seat-number-input">
        <el-input-number
          v-model="seatReservationForm.seatNum"
          :min="1"
          :max="999"
          controls-position="right"
          placeholder="请输入座位号"
          style="width: 200px"
        />
        <el-button
          type="info"
          size="small"
          @click="checkSeatAvailability"
          :disabled="!seatReservationForm.scheduleId"
        >
          检查可用性
        </el-button>
      </div>
    </template>

    <!-- 预订状态字段不再需要自定义模板 -->
  </DialogForm>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import {
  getSeatReservationList,
  getStatus,
  addSeatReservation,
  updateSeatReservation,
  deleteSeatReservation,
  batchUpdateSeatStatus,
  checkSeatAvailability as apiCheckSeatAvailability,
} from "@/api/seat-reservation";
import { getScheduleList } from "@/api/schedule";
import { ElMessage, ElMessageBox } from "element-plus";
import { useEmployeeStore } from "@/stores";
import DialogForm from "@/components/DialogForm/DialogForm.vue";
import {
  BOOKING_STATUS_LABELS,
  BOOKING_STATUS_TYPES,
  BookingStatus,
} from "@/types/seat-reservation";

const employeeStore = useEmployeeStore();
const employeeInfo = computed(() => employeeStore.employeeInfo);

// 表单相关响应式数据
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const seatReservationForm = ref({
  id: "",
  scheduleId: "",
  seatNum: 1,
  bookingStatus: 0,
});

// 下拉框选项相关响应式数据
const scheduleOptions = ref([]);
const scheduleCurrentPage = ref(1);
const scheduleTotal = ref(0);
const scheduleAllLoaded = ref(false);
const scheduleKeyword = ref('');
const scheduleLoading = ref(false);
const isLoadingMore = ref(false);

// 预订状态选项
const bookingStatusOptions = ref([
  { value: 0, label: "可预订" },
  { value: 1, label: "已预订" },
]);

// 先定义函数，避免引用顺序问题
const handleScheduleRemoteSearch = async (query) => {
  scheduleCurrentPage.value = 1;
  scheduleAllLoaded.value = false;
  scheduleKeyword.value = query;
  if (!query) {
    scheduleOptions.value = [];
    return;
  }
  scheduleLoading.value = true;
  try {
    const response = await getScheduleList({
      page: 1,
      pageSize: 10,
      keyword: query
    });
    scheduleTotal.value = response.total || 0;
    scheduleOptions.value = (response.records || []).map(item => ({
      value: item.id,
      label: item.trainName || '未知班次',
      trainName: item.trainName,
      routeInfo: `${item.startStationName || ''} - ${item.endStationName || ''}`,
      departureTime: item.startTime,
      arrivalTime: item.endTime,
      availableTickets: item.availableTickets
    }));
  } catch (error) {
    console.error("获取班次选项失败:", error);
    ElMessage.error("获取班次选项失败");
    scheduleOptions.value = [];
  } finally {
    scheduleLoading.value = false;
    isLoadingMore.value = false;
    addScrollListener('schedule');
  }
};

const loadMoreSchedules = async () => {
  if (isLoadingMore.value || scheduleAllLoaded.value || !scheduleKeyword.value) return;
  isLoadingMore.value = true;
  try {
    const response = await getScheduleList({
      page: scheduleCurrentPage.value + 1,
      pageSize: 10,
      keyword: scheduleKeyword.value
    });
    scheduleCurrentPage.value++;
    const hasMore = scheduleOptions.value.length + ((response.records || []).length) < scheduleTotal.value;
    scheduleAllLoaded.value = !hasMore;
    const newOptions = (response.records || []).map(item => ({
      value: item.id,
      label: item.trainName || '未知班次',
      trainName: item.trainName,
      routeInfo: `${item.startStationName || ''} - ${item.endStationName || ''}`,
      departureTime: item.startTime,
      arrivalTime: item.endTime,
      availableTickets: item.availableTickets
    }));
    const existingValues = new Set(scheduleOptions.value.map(item => item.value));
    const filteredNewOptions = newOptions.filter(item => !existingValues.has(item.value));
    scheduleOptions.value = [...scheduleOptions.value, ...filteredNewOptions];
  } catch (error) {
    ElMessage.error("加载更多班次失败");
  } finally {
    isLoadingMore.value = false;
  }
};

const addScrollListener = (type) => {
  setTimeout(() => {
    const dropdowns = document.querySelectorAll('.schedule-select .el-select-dropdown__wrap');
    dropdowns.forEach(dropdown => {
      dropdown.removeEventListener('scroll', scrollHandler);
      dropdown.addEventListener('scroll', scrollHandler);
    });
    function scrollHandler(e) {
      const { scrollTop, scrollHeight, clientHeight } = e.target;
      if (scrollTop + clientHeight >= scrollHeight - 10) {
        if (type === 'schedule') loadMoreSchedules();
      }
    }
  }, 100);
};

// 表单字段配置
const formFields = [
  {
    prop: "scheduleId",
    label: "班次",
    type: "custom",
    placeholder: "请选择班次",
    required: true,
    clearable: true,
    filterable: true,
    remote: true,
    remoteMethod: handleScheduleRemoteSearch,
    loading: scheduleLoading,
    popperClass: 'schedule-select'
  },
  {
    prop: "seatNum",
    label: "座位号",
    type: "custom",
    placeholder: "请输入座位号",
    required: true,
  },
  {
    prop: "bookingStatus",
    label: "预订状态",
    type: "select",
    placeholder: "请选择预订状态",
    options: bookingStatusOptions,
    required: true,
  },
];

// 表单验证规则
const formRules = computed(() => ({
  scheduleId: [{ required: true, message: "请选择班次", trigger: "change" }],
  seatNum: [
    { required: true, message: "请输入座位号", trigger: "blur" },
    {
      type: "number",
      min: 1,
      max: 999,
      message: "座位号必须在 1 到 999 之间",
      trigger: "blur",
    },
  ],
  bookingStatus: [
    { required: true, message: "请选择预订状态", trigger: "change" },
  ],
}));

// 计算属性
const dialogTitle = computed(() =>
  isEdit.value ? "编辑座位预订" : "新增座位预订"
);

// 响应式数据
const seatReservationList = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const selectedRows = ref([]);
const statusFilter = ref('all');

// 状态数量统计
const statusCounts = ref({
  all: 0,
  available: 0,
  booked: 0
});

// 表格列配置
const tableColumns = [
  {
    prop: "id",
    label: "预订号",
    width: 100,
    align: "center",
  },
  {
    prop: "scheduleInfo",
    label: "班次信息",
    minWidth: 200,
    align: "center",
  },
  {
    prop: "seatNum",
    label: "座位号",
    width: 100,
    align: "center",
  },
  {
    prop: "bookingStatus",
    label: "预订状态",
    width: 120,
    align: "center",
  },
  {
    prop: "createTime",
    label: "创建时间",
    minWidth: 160,
    align: "center",
  },
  {
    prop: "updateTime",
    label: "更新时间",
    minWidth: 160,
    align: "center",
  },
  {
    prop: "createPerson",
    label: "创建人",
    minWidth: 120,
    align: "center",
  },
  {
    prop: "action",
    label: "操作",
    width: 250,
    align: "center",
    fixed: "right",
  },
];

// 搜索字段配置
const searchFields = [
  {
    prop: "scheduleId",
    label: "班次",
    type: "select",
    placeholder: "请选择班次",
    options: scheduleOptions,
    clearable: true,
    filterable: true,
    remote: true,
    remoteMethod: handleScheduleRemoteSearch,
    loading: scheduleLoading,
    popperClass: 'schedule-select'
  },
  {
    prop: "seatNum",
    label: "座位号",
    type: "input",
    placeholder: "请输入座位号",
    inputType: "number",
    clearable: true,
  },
];

// 标签页配置 - 计算属性，包含状态数量统计
const tabs = computed(() => [
  {
    name: 'all',
    label: '全部',
    count: statusCounts.value.all,
    lazy: true
  },
  {
    name: 'available',
    label: '可预订',
    count: statusCounts.value.available,
    lazy: true
  },
  {
    name: 'booked',
    label: '已预订',
    count: statusCounts.value.booked,
    lazy: true
  }
]);

// 搜索处理函数
const handleSearch = (formData) => {
  currentPage.value = 1; // 重置为第一页
  selectedRows.value = []; // 清空选择
  
  // 根据当前选中的标签页应用状态筛选
  fetchSeatReservationList(formData);
};

/**
 * 处理状态标签页切换
 * @param {string} tabName - 切换到的标签页名称
 */
const handleStatusTabChange = (tabName) => {
  statusFilter.value = tabName; // 更新状态筛选
  currentPage.value = 1; // 重置为第一页
  selectedRows.value = []; // 清空选择
  fetchSeatReservationList();
};


// 初始搜索表单数据
const initialSearchForm = searchFields.reduce((acc, field) => {
  acc[field.prop] = "";
  return acc;
}, {});

/**
 * 格式化日期时间
 * @param {string|number|Date} dateTime - 日期时间对象或时间戳
 * @returns {string} 格式化后的日期时间字符串
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return "-";
  const d = new Date(dateTime);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  const hours = String(d.getHours()).padStart(2, "0");
  const minutes = String(d.getMinutes()).padStart(2, "0");
  const seconds = String(d.getSeconds()).padStart(2, "0");
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};



/**
 * 根据状态筛选数据
 * @param {Array} data - 原始数据
 * @returns {Array} 筛选后的数据
 */
const filterDataByStatus = (data) => {
  if (statusFilter.value === 'all') {
    return data;
  }
  // 根据筛选条件返回对应状态的数据
  if (statusFilter.value === 'available') {
    return data.filter(item => item.bookingStatus === 0);
  }
  if (statusFilter.value === 'booked') {
    return data.filter(item => item.bookingStatus === 1);
  }
  return data;
};

/**
 * 更新状态数量统计
 * @param {Array} list - 座位预订列表数据
 */
const updateStatusCounts = (list) => {
  const availableCount = list.filter(item => item.bookingStatus === 0).length;
  const bookedCount = list.filter(item => item.bookingStatus === 1).length;
  
  statusCounts.value = {
    all: list.length,
    available: availableCount,
    booked: bookedCount
  };
};

/**
 * 获取座位预订列表
 * @param {Object} searchParams - 搜索参数
 */
const fetchSeatReservationList = async (searchParams = {}) => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      ...searchParams,
    };

    // API调用
    const response = await getSeatReservationList(params);
    const data = response.records || [];

    // 按状态筛选
    let filteredData = filterDataByStatus(data);
    
    // 直接使用筛选后的数据，不再进行客户端分页
    // 这样每次切换分页时都会从服务器获取对应页码的数据
    seatReservationList.value = filteredData;
    total.value = Number(response.total);
    
    // 更新状态数量统计
    updateStatusCounts(data);
  } catch (error) {
    seatReservationList.value = [];
    total.value = 0;
    
    // 重置状态统计
    statusCounts.value = {
      all: 0,
      available: 0,
      booked: 0
    };
    ElMessage.error("获取座位预订列表失败");
  } finally {
    loading.value = false;
  }
};



/**
 * 重置查询条件
 */
const handleReset = () => {
  statusFilter.value = 'all'; // 重置状态筛选
  currentPage.value = 1; // 重置为第一页
  selectedRows.value = []; // 清空选择
  fetchSeatReservationList({}); // 重置后自动查询
};

/**
 * 处理表格选择变化
 * @param {Array} selection - 选中的行数据
 */
const handleSelectionChange = (selection) => {
  selectedRows.value = selection;
};

/**
 * 分页大小变化
 * @param {number} size - 每页条数
 */
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchSeatReservationList();
};

/**
 * 分页切换
 * @param {number} page - 当前页码
 */
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchSeatReservationList();
};

/**
 * 新增座位预订
 */
const handleAdd = () => {
  isEdit.value = false;
  dialogVisible.value = true;
  // 重置表单数据
  seatReservationForm.value = {
    id: "",
    scheduleId: "",
    seatNum: 1,
    bookingStatus: 0,
  };
};

/**
 * 批量更新座位状态
 * @param {number} status - 目标状态
 */
const handleBatchStatusUpdate = async (status) => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning("请选择要更新的座位预订");
    return;
  }

  const statusLabel = BOOKING_STATUS_LABELS[status];

  try {
    await ElMessageBox.confirm(
      `确定要将选中的 ${selectedRows.value.length} 个座位预订设为${statusLabel}吗？`,
      "状态更新确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    // 只处理0(可预订)和1(已预订)两种状态
    if (status !== 0 && status !== 1) {
      ElMessage.warning("不支持的批量状态更新");
      return;
    }

    const ids = selectedRows.value.map((row) => row.id);
    await batchUpdateSeatStatus(ids, status);
    ElMessage.success(
      `成功更新 ${selectedRows.value.length} 个座位预订状态为${statusLabel}`
    );

    // 更新成功后刷新列表
    fetchSeatReservationList();
  } catch (error) {
    // 用户取消更新或发生错误
    if (error !== "cancel") {
      ElMessage.error("状态更新失败");
    }
  }
};

/**
 * 批量删除座位预订
 */
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning("请选择要删除的座位预订");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个座位预订吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    const ids = selectedRows.value.map((row) => row.id);
    await deleteSeatReservation(ids);
    ElMessage.success(`成功删除 ${selectedRows.value.length} 个座位预订`);

    // 删除成功后刷新列表
    fetchSeatReservationList();
  } catch (error) {
    // 用户取消删除或发生错误
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

/**
 * 编辑座位预订
 * @param {Object} row - 座位预订数据
 */
const handleEdit = (row) => {
  isEdit.value = true;
  dialogVisible.value = true;
  // 复制数据到表单
  seatReservationForm.value = {
    id: row.id,
    scheduleId: row.scheduleId,
    seatNum: row.seatNum,
    bookingStatus: row.bookingStatus,
  };
};

/**
 * 处理班次变化
 */
const handleScheduleChange = () => {
  // 清空座位号，重新选择
  seatReservationForm.value.seatNum = 1;
};

/**
 * 检查座位可用性
 */
const checkSeatAvailability = async () => {
  if (
    !seatReservationForm.value.scheduleId ||
    !seatReservationForm.value.seatNum
  ) {
    ElMessage.warning("请先选择班次和座位号");
    return;
  }

  try {
    const isAvailable = await apiCheckSeatAvailability(
      seatReservationForm.value.scheduleId,
      seatReservationForm.value.seatNum,
      isEdit.value ? seatReservationForm.value.id : undefined
    );

    if (isAvailable) {
      ElMessage.success("该座位可用");
    } else {
      ElMessage.warning("该座位已被预订或锁定");
    }
  } catch (error) {
    ElMessage.error("检查座位可用性失败");
  }
};

/**
 * 处理表单提交
 * @param {Object} formData - 表单数据
 */
const handleFormSubmit = async (formData) => {
  try {
    submitLoading.value = true;

    // 准备提交数据
    const submitData = { ...formData };

    // 设置创建人或更新人
    if (isEdit.value) {
      submitData.updatePerson =
        employeeInfo.value?.nickname || employeeInfo.value?.username;
    } else {
      submitData.createPerson =
        employeeInfo.value?.nickname || employeeInfo.value?.username;
    }

    if (isEdit.value) {
      // 编辑座位预订
      await updateSeatReservation(submitData);
      ElMessage.success("更新座位预订成功");
    } else {
      // 新增座位预订
      await addSeatReservation(submitData);
      ElMessage.success("新增座位预订成功");
    }

    // 关闭弹窗
    dialogVisible.value = false;
    // 刷新列表
    fetchSeatReservationList();
  } catch (error) {
    ElMessage.error(isEdit.value ? "更新失败" : "新增失败");
  } finally {
    submitLoading.value = false;
  }
};

/**
 * 更新座位状态
 * @param {Object} row - 座位预订数据
 * @param {number} status - 新的状态
 */
const handleStatusUpdate = async (row, status) => {
  try {
    await ElMessageBox.confirm(
      `确定要将座位预订「${row.id}」设为${BOOKING_STATUS_LABELS[status]}吗？`,
      "状态更新确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    // 只处理0(可预订)和1(已预订)两种状态
    if (status !== 0 && status !== 1) {
      ElMessage.warning("不支持的状态更新");
      return;
    }

    // 调用状态更新接口
    await batchUpdateSeatStatus([row.id], status);
    ElMessage.success("状态更新成功");
    // 更新成功后刷新列表
    fetchSeatReservationList();
  } catch (error) {
    // 用户取消更新或发生错误
    if (error !== "cancel") {
      ElMessage.error("状态更新失败");
    }
  }
};

/**
 * 删除座位预订
 * @param {Object} row - 座位预订数据
 */
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除座位预订「${row.id}」吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    // 调用删除接口
    await deleteSeatReservation([row.id]);
    ElMessage.success("删除成功");
    // 删除成功后刷新列表
    fetchSeatReservationList();
  } catch (error) {
    // 用户取消删除或发生错误
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 组件挂载后加载数据
onMounted(() => {
  fetchSeatReservationList();
});
</script>

<style scoped lang="scss">
/* 使用ListPage组件的样式，无需额外样式 */
.selected-count {
  margin-left: 4px;
  font-weight: bold;
}

.seat-number {
  font-weight: bold;
  color: #409eff;
}

.person-name {
  color: #606266;
  font-size: 12px;
}

.schedule-option {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .schedule-info {
    font-size: 12px;
    color: #909399;
    margin-left: 10px;
  }
}

.seat-number-input {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
