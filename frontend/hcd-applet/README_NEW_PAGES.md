# HCD小程序新页面使用说明

## 新增页面概览

本次为hcd-applet小程序创建了5个新的核心页面，完善了车票查询系统的用户体验：

### 1. 车次详情页面 (`pages/train-detail/train-detail.vue`)
**功能特点：**
- 展示班次的详细信息（列车信息、班次信息、线路信息）
- 经停站点时间轴展示，包含到达时间、发车时间、停车时长
- 座位类型和价格明细，支持折扣显示
- 底部预订按钮，显示价格区间和余票数量
- 响应式设计，支持选项卡切换

**跳转方式：**
```javascript
// 使用工具函数
import { goToTrainDetail } from '@/utils/navigation'
goToTrainDetail('scheduleId')

// 或者直接跳转
uni.navigateTo({
  url: '/pages/train-detail/train-detail?scheduleId=12345'
})
```

### 2. 订单详情页面 (`pages/order-detail/order-detail.vue`)
**功能特点：**
- 订单状态卡片，不同状态显示不同颜色和图标
- 状态时间轴展示（已支付→已出票→已完成）
- 完整的车次信息、乘客信息、订单信息展示
- 车票列表，支持点击查看车票详情
- 根据订单状态显示相应操作按钮（支付、取消、退票、查看车票）
- 乘车须知提示

**跳转方式：**
```javascript
// 使用工具函数
import { goToOrderDetail } from '@/utils/navigation'
goToOrderDetail('orderId')

// 或者直接跳转
uni.navigateTo({
  url: '/pages/order-detail/order-detail?orderId=12345'
})
```

### 3. 我的车票页面 (`pages/my-tickets/my-tickets.vue`)
**功能特点：**
- 顶部统计卡片，显示总票数、正常、已使用、已退票数量
- 多种筛选标签（全部、正常、今日、已使用、已退票）
- 今日车票特殊高亮显示
- 车票卡片展示，包含车次、时间、座位、价格信息
- 支持下拉刷新
- 电子票二维码弹窗展示
- 支持退票和改签操作

**跳转方式：**
```javascript
// 使用工具函数
import { goToMyTickets } from '@/utils/navigation'
goToMyTickets()

// 或者直接跳转（tab页面）
uni.switchTab({
  url: '/pages/my-tickets/my-tickets'
})
```

### 4. 车票详情页面 (`pages/ticket-detail/ticket-detail.vue`)
**功能特点：**
- 电子车票卡片式展示，不同状态不同边框颜色
- 完整的行程信息、乘客信息、座位信息
- 检票口信息显示
- 电子票二维码展示，支持自动更新
- 乘车须知卡片
- 退票和改签操作按钮
- 联系客服功能

**跳转方式：**
```javascript
// 使用工具函数
import { goToTicketDetail } from '@/utils/navigation'
goToTicketDetail('ticketId')

// 或者直接跳转
uni.navigateTo({
  url: '/pages/ticket-detail/ticket-detail?ticketId=12345'
})
```

## 技术实现特点

### 1. 组件化设计
- 使用uView Plus组件库，保持一致的设计风格
- 自定义卡片组件，提升视觉体验
- 响应式布局，适配不同屏幕尺寸

### 2. 数据处理
- 创建了`format.js`工具函数，统一数据格式化
- 支持时间、日期、价格、身份证等多种格式化处理
- 状态映射函数，统一状态显示

### 3. 用户体验
- 加载状态显示
- 错误提示处理
- 操作确认弹窗
- 下拉刷新支持
- 二维码自动更新

### 4. 导航管理
- 创建了`navigation.js`工具函数，统一管理页面跳转
- 支持参数传递和权限检查
- 提供返回和首页跳转功能

## 数据库关联

### 主要数据表关系：
```
hcd_schedule (班次) 
  ├── hcd_train (列车信息)
  ├── hcd_route (线路信息)
  └── hcd_route_stations (经停站点)

hcd_order (订单)
  ├── hcd_ticket (车票信息)
  └── 乘客信息
```

### API接口设计：
- 班次详情：`/schedule/detail/{id}`
- 经停站点：`/schedule/stations/{id}`
- 座位价格：`/schedule/seat-prices/{id}`
- 车票详情：`/ticket/detail/{id}`
- 我的车票：`/ticket/my-tickets`
- 订单详情：`/order/detail/{id}`

## 使用建议

### 1. 页面跳转
推荐使用工具函数进行页面跳转，便于统一管理和维护：
```javascript
import { 
  goToTrainDetail, 
  goToTicketDetail, 
  goToOrderDetail, 
  goToMyTickets 
} from '@/utils/navigation'
```

### 2. 数据格式化
使用统一的格式化函数，保持数据展示一致性：
```javascript
import { 
  formatTime, 
  formatDate, 
  formatPrice, 
  formatDuration,
  getSeatTypeName,
  getOrderStatusName
} from '@/utils/format'
```

### 3. 状态管理
使用计算属性处理复杂的数据展示逻辑，提高代码可维护性。

### 4. 错误处理
所有API调用都包含错误处理，确保用户体验。

## 后续优化建议

1. **性能优化**：添加图片懒加载、虚拟滚动等优化
2. **功能增强**：添加车票分享、行程提醒等功能
3. **交互优化**：添加动画效果、手势操作等
4. **数据缓存**：实现本地数据缓存，减少API请求
5. **离线支持**：添加离线查看车票功能

## 测试数据

项目中包含了`mock/ticket.js`文件，提供了完整的测试数据，包含：
- 模拟车票数据
- 模拟订单数据
- 模拟班次数据
- 模拟经停站点数据
- 模拟座位价格数据

可以直接使用这些数据进行页面测试和开发调试。