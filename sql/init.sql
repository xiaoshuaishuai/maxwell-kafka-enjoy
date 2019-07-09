use maxwell;
CREATE TABLE `dynamic_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `db_database` varchar(50) NOT NULL COMMENT 'database name',
  `pool_name` varchar(255) NOT NULL DEFAULT 'com.zaxxer.hikari.HikariDataSource' COMMENT 'com.zaxxer.hikari.HikariDataSource',
  `pool_config` text COMMENT '连接池的json配置',
  `driver_class_name` varchar(50) NOT NULL DEFAULT 'com.mysql.jdbc.Driver' COMMENT '驱动',
  `url` varchar(255) NOT NULL COMMENT 'jdbc url',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `jndi_name` varchar(50) DEFAULT NULL,
  `is_enable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:启用  1:禁用',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:保留 1:删除',
  `gmt_create` datetime DEFAULT NULL COMMENT 'now()',
  `gmt_modify` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(50) NOT NULL,
  `modify_by` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_database_name` (`db_database`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='动态数据源配置表';
CREATE TABLE `redis_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `db_database` varchar(255) NOT NULL DEFAULT '0' COMMENT '数据库',
  `db_table` varchar(255) NOT NULL DEFAULT '0' COMMENT '表',
  `primary_expire` bigint(18) DEFAULT '-1' COMMENT '主键缓存过期时间，默认永不过期',
  `table_expire` bigint(18) DEFAULT '-1' COMMENT '全表缓存过期时间, 默认-1永不过期, 单位：秒',
  `table_order_by` varchar(1024) DEFAULT NULL COMMENT ' 全表缓存排序规则SQL  ORDER BY GMT_MODIFY DESC, XX ASC\r\n',
  `rule` varchar(255) DEFAULT '0' COMMENT ' 缓存生成规则,可以配置多个,多个使用,分割, 1,2 ... 1,3 ...  1,2,3\r\n 默认0.无缓存\r\n 1.单表主键引导缓存\r\n 2.全表缓存\r\n 3.自定义缓存\r\n',
  `template` varchar(1024) DEFAULT '' COMMENT '当rule包含3时,template, template ,分割\r\n 自定义缓存模板\r\n字段1:字段2:字段3(过期时间)\r\n字段1:字段2:字段3,字段1:字段2(过期时间)\r\n 字段必须是table里对应的数据库字段,否则无法映射成功\r\n',
  `template_sql` varchar(1024) DEFAULT NULL COMMENT '自定义模板对应sql,分割\r\n与template一一对应',
  `template_order_by` varchar(1024) DEFAULT NULL COMMENT '\r\n 自定义模板对应sql,分割\r\n 与template一一对应\r\n ORDER BY GMT_MODIFY DESC,ORDER BY GMT_MODIFY DESC,ORDER BY GMT_MODIFY DESC\r\nORDER BY GMT_MODIFY DESC,,\r\n,,,\r\n可空，如果为空不排序\r\n理论上template多少个分割符template_order_by就需要多少个分隔符，否则排序不生效',
  `remark` varchar(1024) DEFAULT '' COMMENT '备注',
  `is_enable` tinyint(1) DEFAULT '0' COMMENT '0:启用 1:禁用',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '0:保留 1:删除',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_by` varchar(255) DEFAULT '0' COMMENT '创建人',
  `modify_by` varchar(255) DEFAULT '0' COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_db_database_db_table` (`db_database`,`db_table`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='redis和数据库表映射关系';
CREATE TABLE `elasticsearch_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `db_database` varchar(255) NOT NULL DEFAULT '0' COMMENT '数据库',
  `db_table` varchar(255) NOT NULL DEFAULT '0' COMMENT '表',
  `remark` varchar(1024) DEFAULT '' COMMENT '备注',
  `is_enable` tinyint(1) DEFAULT '0' COMMENT '0:启用 1:禁用',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '0:保留 1:删除',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_by` varchar(255) DEFAULT '0' COMMENT '创建人',
  `modify_by` varchar(255) DEFAULT '0' COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_db_database_db_table` (`db_database`,`db_table`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='es数据库表映射关系';
-- 测试初始化配置test数据库
INSERT INTO `maxwell`.`dynamic_datasource` (`id`, `db_database`, `pool_name`, `pool_config`, `driver_class_name`, `url`, `username`, `password`, `jndi_name`, `is_enable`, `is_deleted`, `gmt_create`, `gmt_modify`, `create_by`, `modify_by`) VALUES ('1', 'master', 'com.zaxxer.hikari.HikariDataSource', '{\"minIdle\":5,\"maxPoolSize\":15,\"isAutoCommit\":true,\"idleTimeout\":30000,\"maxLifetime\":1800000,\"connectionTimeout\":30000,\"connectionTestQuery\":\"SELECT 1\"}', 'com.mysql.jdbc.Driver', 'jdbc:mysql://192.168.225.1:3306/maxwell?useUnicode=true&characterEncoding=UTF8&useSSL=false&allowMultiQueries=true&autoReconnect=true&failOverReadOnly=false&maxReconnects=10&tinyInt1isBit=false', 'root', 'root', NULL, '0', '0', now(), now(), '110', '110');
INSERT INTO `maxwell`.`dynamic_datasource` (`id`, `db_database`, `pool_name`, `pool_config`, `driver_class_name`, `url`, `username`, `password`, `jndi_name`, `is_enable`, `is_deleted`, `gmt_create`, `gmt_modify`, `create_by`, `modify_by`) VALUES ('2', 'test', 'com.zaxxer.hikari.HikariDataSource', '{\"minIdle\":5,\"maxPoolSize\":15,\"isAutoCommit\":true,\"idleTimeout\":30000,\"maxLifetime\":1800000,\"connectionTimeout\":30000,\"connectionTestQuery\":\"SELECT 1\"}', 'com.mysql.jdbc.Driver', 'jdbc:mysql://192.168.225.1:3306/test?useUnicode=true&characterEncoding=UTF8&useSSL=false&allowMultiQueries=true&autoReconnect=true&failOverReadOnly=false&maxReconnects=10&tinyInt1isBit=false', 'root', 'root', NULL, '0', '0', now(), now(), '110', '110');
-- 测试test数据库下sys_order表进行redis缓存同步
INSERT INTO `maxwell`.`redis_mapping` (`id`, `db_database`, `db_table`, `primary_expire`, `table_expire`, `rule`, `template`, `template_sql`, `remark`, `is_enable`, `is_deleted`, `gmt_create`, `gmt_modify`, `create_by`, `modify_by`) VALUES ('1', 'test', 'sys_order', '-1', '200', '1,2,3', ':order_code:is_deleted(1800),:goods_name:is_deleted(3600)', NULL, '1.单表主键引导缓存\n2.全表缓存\n3.自定义缓存 {order_code}:{is_del}=订单号缓存{goods_name}:{is_del}=商品名称缓存  ', '0', '0', now(), now(), '0', '0');
-- 创建test数据库
CREATE DATABASE `test` CHARACTER SET utf8 COLLATE utf8_general_ci;
use test;
-- 创建test数据库下sys_order测试表
CREATE TABLE `sys_order` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_code` varchar(255) NOT NULL DEFAULT '0' COMMENT '订单号',
  `category` smallint(1) NOT NULL DEFAULT '0' COMMENT '0/正常订单,1/促销订单',
  `goods_name` varchar(255) NOT NULL DEFAULT '0' COMMENT '商品名称',
  `is_send_express` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否发货0/未 ,1/已发货',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0/保留,1/删除',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
-- 初始化测试数据
INSERT INTO `test`.`sys_order` (`id`, `order_code`, `category`, `goods_name`, `is_send_express`, `is_deleted`, `gmt_create`, `gmt_modify`) VALUES ('1', 'code1', '0', '牙膏', '0', '0', '2019-06-04 17:21:45', '2019-07-08 13:49:23');
INSERT INTO `test`.`sys_order` (`id`, `order_code`, `category`, `goods_name`, `is_send_express`, `is_deleted`, `gmt_create`, `gmt_modify`) VALUES ('2', 'code2', '0', '海飞丝洗发水', '0', '0', '2019-06-04 17:21:45', '2019-06-14 16:09:30');
INSERT INTO `test`.`sys_order` (`id`, `order_code`, `category`, `goods_name`, `is_send_express`, `is_deleted`, `gmt_create`, `gmt_modify`) VALUES ('3', 'code3', '0', '耳机', '0', '0', '2019-06-11 17:01:06', '2019-06-14 17:57:00');
INSERT INTO `test`.`sys_order` (`id`, `order_code`, `category`, `goods_name`, `is_send_express`, `is_deleted`, `gmt_create`, `gmt_modify`) VALUES ('4', 'code4', '0', '马甲', '0', '0', '2019-06-14 15:01:04', '2019-06-14 15:01:04');
INSERT INTO `test`.`sys_order` (`id`, `order_code`, `category`, `goods_name`, `is_send_express`, `is_deleted`, `gmt_create`, `gmt_modify`) VALUES ('5', 'code5', '0', '法海', '0', '0', '2019-06-14 15:01:42', '2019-06-14 15:01:42');
INSERT INTO `test`.`sys_order` (`id`, `order_code`, `category`, `goods_name`, `is_send_express`, `is_deleted`, `gmt_create`, `gmt_modify`) VALUES ('6', 'code6', '0', '梳子', '0', '0', '2019-06-14 17:39:23', '2019-07-08 16:57:08');
INSERT INTO `test`.`sys_order` (`id`, `order_code`, `category`, `goods_name`, `is_send_express`, `is_deleted`, `gmt_create`, `gmt_modify`) VALUES ('7', 'code7', '0', 'brother', '0', '0', '2019-07-08 10:48:51', '2019-07-08 15:16:34');
INSERT INTO `test`.`sys_order` (`id`, `order_code`, `category`, `goods_name`, `is_send_express`, `is_deleted`, `gmt_create`, `gmt_modify`) VALUES ('8', 'code8', '0', 'brother', '0', '0', '2019-07-09 17:10:29', '2019-07-09 17:10:34');
