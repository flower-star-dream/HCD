# 车票查询和预订系统使用指南

## 概述

本系统提供了完整的车票查询和预订功能，包括车票搜索、车次列表展示、座位选择和订单确认等功能模块。系统采用uni-app + uView Plus技术栈，适配微信小程序和H5平台。

## 页面结构

### 1. 车票查询页面 (`pages/ticket-search/ticket-search.vue`)

**功能特点：**
- 出发地和目的地选择，支持站点搜索
- 出发日期选择，支持快速日期选择
- 站点交换功能
- 搜索历史记录
- 热门城市快速选择

**主要功能：**
- 站点搜索：支持中文和拼音搜索
- 日期选择：支持今天、明天快速选择，以及日期范围限制
- 历史记录：自动保存最近5次搜索记录
- 热门城市：16个热门城市快速选择

### 2. 车次列表页面 (`pages/train-list-enhanced/train-list-enhanced.vue`)

**功能特点：**
- 车次信息全面展示
- 时间筛选功能
- 多种排序方式
- 下拉刷新和上拉加载
- 座位价格信息展示

**主要功能：**
- 车次展示：车次号、列车类型、出发/到达时间、余票信息
- 时间筛选：上午、下午、晚上时间段筛选
- 排序功能：出发时间、到达时间、用时、价格等多种排序
- 价格展示：不同座位类型的价格和余票状态

### 3. 车票预订确认页面 (`pages/ticket-confirm/ticket-confirm.vue`)

**功能特点：**
- 车次信息展示
- 乘客选择和管理
- 座位类型选择
- 座位图选择（可选）
- 保险服务选择
- 联系人信息填写
- 费用明细展示

**主要功能：**
- 乘客管理：选择常用乘客，支持添加新乘客
- 座位选择：支持座位类型选择和具体座位选择
- 保险服务：铁路乘意险选择
- 联系信息：姓名、手机、邮箱等信息填写
- 费用计算：自动计算车票费用和保险费用

### 4. 座位选择组件 (`components/SeatSelector/SeatSelector.vue`)

**功能特点：**
- 座位类型选择
- 座位图展示
- 车厢切换
- 座位状态显示
- 已选座位管理

**主要功能：**
- 座位类型：支持多种座位类型选择
- 座位图：可视化座位布局
- 车厢导航：支持多车厢切换
- 座位状态：可用、已选、已占、不可选状态
- 座位选择：支持多座位选择

## 数据模型

### 数据库表结构

#### hcd_schedule (班次信息表)
```sql
CREATE TABLE `hcd_schedule` (
  `id` bigint NOT NULL COMMENT '班次号',
  `train_id` bigint NOT NULL COMMENT '列车号',
  `route_id` bigint NOT NULL COMMENT '线路号',
  `conductor` varchar(10) DEFAULT NULL COMMENT '列车长',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  `available_tickets` int NOT NULL COMMENT '余票',
  `start_time` datetime NOT NULL COMMENT '出发时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`)
) COMMENT='hcd_班次';
```

#### hcd_train (列车信息表)
```sql
CREATE TABLE `hcd_train` (
  `id` bigint NOT NULL COMMENT '列车号',
  `train_name` varchar(10) NOT NULL COMMENT '列车名',
  `train_model` varchar(50) NOT NULL COMMENT '列车型号',
  `seat_num` int NOT NULL COMMENT '座位数',
  `service_years` int NOT NULL COMMENT '服务年数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) COMMENT='hcd_列车';
```

#### hcd_route (线路信息表)
```sql
CREATE TABLE `hcd_route` (
  `id` bigint NOT NULL COMMENT '线路号',
  `route_name` varchar(20) NOT NULL COMMENT '线路名',
  `start_station` varchar(50) NOT NULL COMMENT '起点站',
  `end_station` varchar(50) NOT NULL COMMENT '终点站',
  `station_count` int NOT NULL COMMENT '站点数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  `start_station_id` bigint NOT NULL COMMENT '起点站id',
  `end_station_id` bigint NOT NULL COMMENT '终点站id',
  PRIMARY KEY (`id`)
) COMMENT='hcd_线路';
```

#### hcd_station (站点信息表)
```sql
CREATE TABLE `hcd_station` (
  `id` bigint NOT NULL COMMENT '站点号',
  `station_name` varchar(50) NOT NULL COMMENT '站点名',
  `address` varchar(100) NOT NULL COMMENT '地址',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) COMMENT='hcd_站点';
```

#### hcd_seat_reservation (座位预订信息表)
```sql
CREATE TABLE `hcd_seat_reservation` (
  `id` bigint NOT NULL COMMENT '座位预订号',
  `schedule_id` bigint NOT NULL COMMENT '班次号',
  `seat_number` int NOT NULL COMMENT '座位号',
  `booking_status` int NOT NULL COMMENT '预订状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) COMMENT='hcd_座位预订';
```

## API接口

### 列车相关接口

#### 搜索班次
```javascript
export const searchSchedules = (params) => {
  return request.get('/schedule/search', params)
}
```

#### 获取班次详情
```javascript
export const getScheduleDetail = (id) => {
  return request.get(`/schedule/detail/${id}`)
}
```

#### 获取班次座位
```javascript
export const getScheduleSeats = (scheduleId) => {
  return request.get(`/schedule/seats/${scheduleId}`)
}
```

### 站点相关接口

#### 获取站点列表
```javascript
export const getStations = () => {
  return request.get('/station/list')
}
```

#### 搜索站点
```javascript
export const searchStations = (keyword) => {
  return request.get('/station/search', { keyword })
}
```

#### 获取站点详情
```javascript
export const getStationById = (id) => {
  return request.get(`/station/${id}`)
}
```

### 线路相关接口

#### 获取线路信息
```javascript
export const getRouteInfo = (departureId, arrivalId) => {
  return request.get('/route/info', { departureId, arrivalId })
}
```

#### 获取线路站点
```javascript
export const getRouteStations = (routeId) => {
  return request.get(`/route/stations/${routeId}`)
}
```

## 使用流程

### 1. 车票查询流程

1. 用户进入车票查询页面
2. 选择出发地和目的地（支持搜索）
3. 选择出发日期
4. 点击查询按钮
5. 系统保存搜索历史
6. 跳转到车次列表页面

### 2. 车次选择流程

1. 展示符合条件的车次列表
2. 用户可以根据时间、价格等条件筛选
3. 选择具体的车次
4. 跳转到车票预订确认页面

### 3. 车票预订流程

1. 展示车次详细信息
2. 选择乘客（支持多选）
3. 选择座位类型
4. 选择具体座位（可选）
5. 选择保险服务（可选）
6. 填写联系人信息
7. 确认费用明细
8. 提交订单

## 设计特点

### 1. 用户体验优化
- 简洁直观的界面设计
- 快速的页面响应
- 清晰的操作流程
- 友好的错误提示

### 2. 功能完整性
- 覆盖车票查询到预订的全流程
- 支持多种筛选和排序方式
- 提供详细的座位信息
- 包含保险等增值服务

### 3. 技术实现
- 采用uni-app跨平台开发
- 使用uView Plus组件库
- 模块化的组件设计
- 响应式布局适配

### 4. 数据管理
- 本地存储搜索历史
- 状态管理清晰
- API接口规范
- 数据格式统一

## 注意事项

1. **日期范围限制**：支持查询90天内的车票
2. **站点选择**：出发地和目的地不能相同
3. **乘客数量**：一次最多支持5位乘客
4. **座位选择**：根据座位类型显示不同的布局
5. **费用计算**：自动计算车票费用和保险费用

## 扩展功能

未来可以考虑添加的功能：

1. **价格趋势图**：展示车票价格变化趋势
2. **候补购票**：支持候补购票功能
3. **团购优惠**：多人购票优惠
4. **积分兑换**：会员积分兑换车票
5. **智能推荐**：基于历史记录的智能推荐
6. **实时通知**：车票状态变更通知
7. **电子发票**：支持电子发票开具
8. **行程管理**：完整的行程管理功能

## 总结

本车票查询和预订系统提供了完整的购票流程，界面友好，功能丰富，代码结构清晰，易于维护和扩展。系统采用现代化的技术栈，能够很好地满足用户的购票需求。