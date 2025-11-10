CREATE TABLE IF NOT EXISTS `hcd_user` (
  `id` bigint NOT NULL COMMENT '用户id',
  `username` varchar(30) NOT NULL COMMENT '用户名',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像url',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `passenger_id` bigint DEFAULT NULL COMMENT '乘车人id',
  `status` int NOT NULL COMMENT '状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  `openid` varchar(45) NOT NULL COMMENT '微信用户唯一标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_用户';

CREATE TABLE IF NOT EXISTS `hcd_employee` (
  `id` bigint NOT NULL COMMENT '员工id',
  `username` varchar(30) NOT NULL COMMENT '用户名',
  `nickname` varchar(30) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像url',
  `status` int NOT NULL COMMENT '状态',
  `permission_level` varchar(10) NOT NULL COMMENT '权限等级',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `affiliated_site` varchar(100) DEFAULT NULL COMMENT '所属站点',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_员工';

CREATE TABLE IF NOT EXISTS `hcd_passenger` (
  `id` bigint NOT NULL COMMENT '乘车人id',
  `real_name` varchar(100) NOT NULL COMMENT '真实姓名',
  `card_type` varchar(10) NOT NULL COMMENT '证件类型',
  `id_card` varchar(19) NOT NULL COMMENT '身份证',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_乘车人';

CREATE TABLE IF NOT EXISTS hcd_user_passenger(
    `id` BIGINT NOT NULL COMMENT '乘车人id',
    `user_id` BIGINT NOT NULL COMMENT '用户id',
    `passenger_id` BIGINT NOT NULL COMMENT '乘车人id',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    `create_person` VARCHAR(30) NOT NULL COMMENT '创建人',
    `update_person` VARCHAR(30) NOT NULL COMMENT '更新者',
    PRIMARY KEY (`id`)
) COMMENT 'hcd_用户与乘车人';