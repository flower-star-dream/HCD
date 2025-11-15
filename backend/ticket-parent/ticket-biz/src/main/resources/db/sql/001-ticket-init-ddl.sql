CREATE TABLE IF NOT EXISTS `hcd_ticket` (
  `id` bigint NOT NULL COMMENT '票号',
  `order_id` bigint NOT NULL COMMENT '所属订单号',
  `seat_reservation_id` bigint NOT NULL COMMENT '座位预订号',
  `status` int NOT NULL COMMENT '票使用状态',
  `money` decimal(10,2) NOT NULL COMMENT '票价',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  `start_time` datetime NOT NULL COMMENT '出发时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `passenger_id` bigint NOT NULL COMMENT '乘车人id',
  `start_station_id` varchar(50) NOT NULL COMMENT '出发站id',
  `end_station_id` varchar(50) NOT NULL COMMENT '到达站id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_车票';
