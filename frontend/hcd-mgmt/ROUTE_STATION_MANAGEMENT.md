# 线路和站点管理组件说明

## 概述

本项目为hcd-mgmt管理后台创建了完整的线路和站点管理功能，包括：

1. **RouteList.vue** - 线路管理页面
2. **StationList.vue** - 站点管理页面  
3. **RouteStationDialog.vue** - 线路站点关联管理弹窗

## 功能特性

### 线路管理 (RouteList.vue)
- ✅ 线路的增删改查
- ✅ 起点站和终点站选择（下拉联动）
- ✅ 展示起点站、终点站、站点数信息
- ✅ 批量删除功能
- ✅ 分页和搜索功能
- ✅ 表单验证

### 站点管理 (StationList.vue)
- ✅ 站点的增删改查
- ✅ 地址信息维护
- ✅ 批量删除功能
- ✅ 分页和搜索功能
- ✅ 表单验证

### 线路站点关联管理 (RouteStationDialog.vue)
- ✅ 线路站点关联管理
- ✅ 拖拽排序功能（上移/下移）
- ✅ 添加/移除站点
- ✅ 批量保存排序
- ✅ 实时排序变化提示

## 技术实现

### 类型定义

#### 线路相关类型 (`src/types/route.ts`)
```typescript
interface Route {
  id: number
  routeName: string
  startStation: string
  endStation: string
  stationCount: number
  startStationId: number
  endStationId: number
}
```

#### 站点相关类型 (`src/types/station.ts`)
```typescript
interface Station {
  id: number
  stationName: string
  address: string
}
```

#### 线路站点关联类型 (`src/types/route-station.ts`)
```typescript
interface RouteStation {
  id: number
  routeId: number
  stationId: number
  stationSorting: number
}
```

### API 接口

#### 线路管理API (`src/api/route.ts`)
- `getRouteList` - 获取线路列表
- `getRouteDetail` - 获取线路详情
- `createRoute` - 创建线路
- `updateRoute` - 更新线路
- `deleteRoute` - 删除线路

#### 站点管理API (`src/api/station.ts`)
- `getStationList` - 获取站点列表
- `getStationDetail` - 获取站点详情
- `createStation` - 创建站点
- `updateStation` - 更新站点
- `deleteStation` - 删除站点
- `getAllStations` - 获取所有站点（用于下拉选择）

#### 线路站点关联API (`src/api/route-station.ts`)
- `getRouteStations` - 获取线路所有站点
- `addRouteStation` - 添加线路站点关联
- `deleteRouteStation` - 删除线路站点关联
- `updateRouteStationSort` - 更新线路站点排序

### 组件结构

```
src/
├── api/
│   ├── route.ts              # 线路管理API
│   ├── station.ts            # 站点管理API
│   └── route-station.ts      # 线路站点关联API
├── types/
│   ├── route.ts              # 线路类型定义
│   ├── station.ts            # 站点类型定义
│   └── route-station.ts      # 线路站点关联类型定义
├── views/
│   ├── Route/
│   │   └── Route-list-view/
│   │       ├── Route-list-view.vue    # 线路管理页面
│   │       └── Route-list-view.scss   # 样式文件
│   └── Station/
│       └── Station-list-view/
│           ├── Station-list-view.vue   # 站点管理页面
│           └── Station-list-view.scss  # 样式文件
└── components/
    └── RouteStationDialog/
        ├── RouteStationDialog.vue       # 线路站点关联管理弹窗
        └── RouteStationDialog.scss      # 样式文件
```

## 使用说明

### 1. 路由配置

在 `src/router/index.ts` 中已经添加了路由配置：

```typescript
{
  path: '/route',
  name: 'Route',
  meta: { title: '线路管理', icon: 'Connection' },
  children: [
    {
      path: '/route/list',
      name: 'RouteList',
      component: () => import('@/views/Route/Route-list-view/Route-list-view.vue'),
      meta: { title: '线路列表' }
    }
  ]
},
{
  path: '/station',
  name: 'Station',
  meta: { title: '站点管理', icon: 'Location' },
  children: [
    {
      path: '/station/list',
      name: 'StationList',
      component: () => import('@/views/Station/Station-list-view/Station-list-view.vue'),
      meta: { title: '站点列表' }
    }
  ]
}
```

### 2. 菜单配置

在 `src/stores/app.ts` 中已经添加了菜单配置：

```typescript
{
  path: '/transport',
  title: '运力中心',
  icon: 'TrainIcon',
  children: [
    {
      path: '/route',
      title: '线路管理',
      icon: 'TrainType'
    },
    {
      path: '/station',
      title: '站点管理',
      icon: 'Location'
    }
  ]
}
```

### 3. 组件使用

#### 线路管理页面
访问路径：`/route/list`

功能：
- 查看所有线路列表
- 新增线路（选择起点站和终点站）
- 编辑线路信息
- 删除线路
- 管理线路站点（打开RouteStationDialog弹窗）

#### 站点管理页面
访问路径：`/station/list`

功能：
- 查看所有站点列表
- 新增站点（包含地址信息）
- 编辑站点信息
- 删除站点

#### 线路站点关联管理弹窗
在RouteList.vue中通过点击"站点管理"按钮打开

功能：
- 查看线路的所有站点
- 添加新站点到线路
- 移除线路中的站点
- 通过上移/下移按钮调整站点顺序
- 保存排序更改

## 业务逻辑说明

### 线路管理业务逻辑

1. **创建线路**：
   - 输入线路名称
   - 选择起点站和终点站（从所有站点中选择）
   - 系统自动计算站点数（在线路站点关联中维护）

2. **编辑线路**：
   - 可以修改起点站和终点站
   - 线路名称不可编辑（作为唯一标识）

3. **删除线路**：
   - 删除线路时会级联删除相关的线路站点关联
   - 需要确认操作

### 站点管理业务逻辑

1. **创建站点**：
   - 输入站点名称和地址
   - 站点名称唯一性验证

2. **编辑站点**：
   - 可以修改站点名称和地址
   - 站点名称不可与其他站点重复

3. **删除站点**：
   - 检查站点是否被线路使用
   - 如果被使用，需要先删除相关的线路站点关联

### 线路站点关联业务逻辑

1. **添加站点到线路**：
   - 只能选择未被添加到该线路的站点
   - 可以选择插入位置（1表示最前面）

2. **调整站点顺序**：
   - 通过上移/下移按钮调整顺序
   - 实时更新排序值
   - 需要点击"保存排序"按钮保存更改

3. **移除站点**：
   - 从线路中移除站点关联
   - 不影响站点本身的数据

## 注意事项

1. **数据一致性**：
   - 删除站点时会检查是否被线路使用
   - 删除线路时会级联删除线路站点关联

2. **排序逻辑**：
   - 站点顺序通过`stationSorting`字段维护
   - 每次调整顺序后需要手动保存

3. **性能优化**：
   - 站点下拉选择使用懒加载
   - 分页查询减少数据传输量

4. **用户体验**：
   - 表单验证实时反馈
   - 操作确认对话框
   - 加载状态提示

## 后续扩展

可以根据业务需求进一步扩展：

1. **批量导入**：支持Excel批量导入站点和线路
2. **地图集成**：在站点管理中集成地图选择地址
3. **线路规划**：根据站点自动生成最优线路
4. **权限控制**：细粒度的权限管理
5. **数据导出**：支持导出线路和站点数据