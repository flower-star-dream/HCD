CREATE DATABASE IF NOT EXISTS `hcd_train_seat`;
USE `hcd_train_seat`;
-- hcd_train_seat.hcd_route definition
CREATE TABLE IF NOT EXISTS  `hcd_route` (
  `id` bigint NOT NULL COMMENT '线路号',
  `route_name` varchar(20) NOT NULL COMMENT '线路名',
  `start_station_id` bigint NOT NULL COMMENT '起点站id',
  `end_station_id` bigint NOT NULL COMMENT '终点站id',
  `station_count` int NOT NULL COMMENT '站点数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_线路';


-- hcd_train_seat.hcd_route_stations definition

CREATE TABLE IF NOT EXISTS `hcd_route_stations` (
  `id` bigint NOT NULL COMMENT '路线站点号',
  `route_id` bigint NOT NULL COMMENT '线路号',
  `station_id` bigint NOT NULL COMMENT '站点号',
  `station_sorting` int NOT NULL COMMENT '站点顺序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_路线站点表';


-- hcd_train_seat.hcd_schedule definition

CREATE TABLE IF NOT EXISTS `hcd_schedule` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_班次';


-- hcd_train_seat.hcd_seat_reservation definition

CREATE TABLE IF NOT EXISTS `hcd_seat_reservation` (
  `id` bigint NOT NULL COMMENT '座位预订号',
  `schedule_id` bigint NOT NULL COMMENT '班次号',
  `seat_number` int NOT NULL COMMENT '座位号',
  `booking_status` int NOT NULL COMMENT '预订状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_座位预订';


-- hcd_train_seat.hcd_station definition

CREATE TABLE IF NOT EXISTS `hcd_station` (
  `id` bigint NOT NULL COMMENT '站点号',
  `station_name` varchar(50) NOT NULL COMMENT '站点名',
  `address` varchar(100) NOT NULL COMMENT '地址',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_站点';


-- hcd_train_seat.hcd_train definition

CREATE TABLE IF NOT EXISTS `hcd_train` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_列车';