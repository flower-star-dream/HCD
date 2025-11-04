CREATE TABLE IF NOT EXISTS `hcd_user` (
  `id` bigint NOT NULL COMMENT '用户id',
  `username` varchar(30) NOT NULL COMMENT '用户名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `real_name` varchar(100) NOT NULL COMMENT '真实姓名',
  `card_type` varchar(10) DEFAULT NULL COMMENT '证件类型',
  `id_card` varchar(19) NOT NULL COMMENT '身份证',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像url',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_用户';

CREATE TABLE IF NOT EXISTS `hcd_admin` (
  `id` bigint NOT NULL COMMENT '管理员id',
  `username` varchar(30) NOT NULL COMMENT '用户名',
  `permission_level` varchar(10) NOT NULL COMMENT '权限等级',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `affiliated_site` varchar(100) DEFAULT NULL COMMENT '所属站点',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL COMMENT '创建人',
  `update_person` varchar(30) NOT NULL COMMENT '更新者',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像url',
  `nickname` varchar(30) NOT NULL COMMENT '昵称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='hcd_管理员';