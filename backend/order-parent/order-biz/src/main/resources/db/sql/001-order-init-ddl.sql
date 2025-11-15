CREATE TABLE IF NOT EXISTS `hcd_order` (
  `id` bigint NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '订购用户id',
  `status` int NOT NULL COMMENT '订单支付状态',
  `remarks` varchar(100) DEFAULT NULL COMMENT '订单备注',
  `total_price` decimal(10,2) NOT NULL COMMENT '总价',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_订单';