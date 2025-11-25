// 模拟车票数据
export const mockTickets = [
  {
    id: 'T202412250001',
    trainNumber: 'G1234',
    trainType: 'G',
    seatType: 'SECOND_CLASS',
    seatNumber: '05A',
    startStation: '北京南',
    endStation: '上海虹桥',
    startTime: '2024-12-25T08:00:00',
    endTime: '2024-12-25T12:30:00',
    duration: '4小时30分钟',
    realName: '张三',
    idCard: '110101199001011234',
    cardType: 'ID_CARD',
    status: 1,
    money: '553.00',
    orderNumber: 'ORD202412250001',
    createTime: '2024-12-20T10:30:00',
    gateNumber: 'A12'
  },
  {
    id: 'T202412260002',
    trainNumber: 'D5678',
    trainType: 'D',
    seatType: 'FIRST_CLASS',
    seatNumber: '03B',
    startStation: '广州南',
    endStation: '深圳北',
    startTime: '2024-12-26T14:20:00',
    endTime: '2024-12-26T15:50:00',
    duration: '1小时30分钟',
    realName: '李四',
    idCard: '440301198512123456',
    cardType: 'ID_CARD',
    status: 1,
    money: '99.50',
    orderNumber: 'ORD202412260002',
    createTime: '2024-12-21T09:15:00',
    gateNumber: 'B08'
  }
]

// 模拟订单数据
export const mockOrders = [
  {
    orderNumber: 'ORD202412250001',
    totalPrice: '553.00',
    status: 1,
    trainNumber: 'G1234',
    trainType: '高铁',
    startStation: '北京南',
    endStation: '上海虹桥',
    startTime: '2024-12-25T08:00:00',
    endTime: '2024-12-25T12:30:00',
    duration: '4小时30分钟',
    passengers: [
      {
        realName: '张三',
        idCard: '110101199001011234',
        cardType: '身份证',
        tickets: [
          {
            id: 'T202412250001',
            seatType: 'SECOND_CLASS',
            seatNumber: '05A',
            money: '553.00',
            status: 1
          }
        ]
      }
    ],
    tickets: [
      {
        id: 'T202412250001',
        seatType: 'SECOND_CLASS',
        seatNumber: '05A',
        status: 1
      }
    ],
    contactName: '张三',
    contactPhone: '138****1234',
    createTime: '2024-12-20T10:30:00',
    payTime: '2024-12-20T10:35:00'
  }
]

// 模拟班次数据
export const mockSchedules = [
  {
    id: 'SCH2024122501',
    trainNumber: 'G1234',
    trainId: 'TRAIN001',
    routeId: 'ROUTE001',
    conductor: '王师傅',
    startStation: '北京南',
    endStation: '上海虹桥',
    startTime: '2024-12-25T08:00:00',
    endTime: '2024-12-25T12:30:00',
    availableTickets: 156,
    status: 1
  }
]

// 模拟列车数据
export const mockTrains = [
  {
    id: 'TRAIN001',
    trainName: '复兴号',
    trainModel: 'CR400AF',
    seatNum: 576,
    serviceYears: 3
  }
]

// 模拟线路数据
export const mockRoutes = [
  {
    id: 'ROUTE001',
    routeName: '京沪高铁',
    startStation: '北京南',
    endStation: '上海虹桥',
    stationCount: 24
  }
]

// 模拟经停站点数据
export const mockStations = [
  {
    id: 'STA001',
    stationName: '北京南',
    stationSort: 1,
    arrivalTime: null,
    departureTime: '2024-12-25T08:00:00',
    stopDuration: 0,
    isStart: true,
    isEnd: false
  },
  {
    id: 'STA002',
    stationName: '天津南',
    stationSort: 2,
    arrivalTime: '2024-12-25T08:30:00',
    departureTime: '2024-12-25T08:32:00',
    stopDuration: 2,
    isStart: false,
    isEnd: false
  },
  {
    id: 'STA003',
    stationName: '济南西',
    stationSort: 3,
    arrivalTime: '2024-12-25T09:45:00',
    departureTime: '2024-12-25T09:47:00',
    stopDuration: 2,
    isStart: false,
    isEnd: false
  },
  {
    id: 'STA004',
    stationName: '南京南',
    stationSort: 4,
    arrivalTime: '2024-12-25T11:30:00',
    departureTime: '2024-12-25T11:32:00',
    stopDuration: 2,
    isStart: false,
    isEnd: false
  },
  {
    id: 'STA005',
    stationName: '上海虹桥',
    stationSort: 5,
    arrivalTime: '2024-12-25T12:30:00',
    departureTime: null,
    stopDuration: 0,
    isStart: false,
    isEnd: true
  }
]

// 模拟座位价格数据
export const mockSeatPrices = [
  {
    seatType: 'BUSINESS',
    name: '商务座',
    price: 1748.00,
    originalPrice: 1748.00,
    discount: 1.0,
    remaining: 28
  },
  {
    seatType: 'FIRST_CLASS',
    name: '一等座',
    price: 933.00,
    originalPrice: 933.00,
    discount: 1.0,
    remaining: 56
  },
  {
    seatType: 'SECOND_CLASS',
    name: '二等座',
    price: 553.00,
    originalPrice: 553.00,
    discount: 1.0,
    remaining: 72
  }
]