/**
 * ******************************************************* 用户库 *******************************************************
 */

-- 用户库
CREATE DATABASE IF NOT EXISTS `cloud-user` CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;
USE `cloud-user`;

-- 用户表
DROP TABLE IF EXISTS `cloud-user`.`disk_user`;
CREATE TABLE `cloud-user`.`disk_user`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户系统内唯一标识',
 `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户名',
 `user_avatar` varchar(170) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'https://static-page-1255518771.cos.ap-shanghai.myqcloud.com/common/image/literaryMale.jpg' COMMENT '用户头像',
 `user_email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户邮箱',
 `user_tel` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户手机号',
 `user_pwd` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户密码',
 `user_salt` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用于登录密码校验的盐值',
 `is_available` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '当前账号是否可用(0:false,1:true)',
 `user_reason` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '当前账号不可用原因',
 `user_ban_time` int(10) NOT NULL DEFAULT 0 COMMENT '账号被封禁的时间(单位:秒)(-1:永久)最大2144448000',
 `user_unlock_time` timestamp(0) NULL DEFAULT NULL COMMENT '账号解封时间',
 `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE COMMENT '用户名称唯一',
 UNIQUE INDEX `uk_user_email`(`user_email`) USING BTREE COMMENT '用户邮箱唯一',
 INDEX `idx_user_tel`(`user_tel`) USING BTREE COMMENT '用户绑定的手机号'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户模块' ROW_FORMAT = DYNAMIC;

-- 用户表依据 user_id 平均分 20 个表
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_01` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_02` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_03` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_04` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_05` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_06` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_07` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_08` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_09` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_10` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_11` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_12` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_13` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_14` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_15` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_16` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_17` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_18` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_19` LIKE `cloud-user`.`disk_user`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_20` LIKE `cloud-user`.`disk_user`;

-- 删除原数据表
DROP TABLE IF EXISTS `cloud-user`.`disk_user`;

/**
 * ******************************************************* 日志库 *******************************************************
 */

-- 日志库
CREATE DATABASE IF NOT EXISTS `cloud-log` CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;
USE `cloud-log`;

-- 登录日志系统表
DROP TABLE IF EXISTS `cloud-log`.`log_login`;
CREATE TABLE `cloud-log`.`log_login` (
  `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
  `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录日志系统内唯一标识',
  `ip` bigint(15) UNSIGNED NULL DEFAULT NULL COMMENT '当前访问的ip地址',
  `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'ip地址的实际地理位置',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器内核类型',
  `browser_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器内核版本',
  `browser_engine` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器解析引擎的类型',
  `browser_engine_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器解析引擎的版本',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器的UA标识',
  `is_mobile` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '是否为移动平台(0:false,1:true)',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统类型',
  `platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作平台类型',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登陆的用户名',
  `is_success` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '是否登录成功(0:false,1:true)',
  `is_password` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '是否使用密码登录(0:false,1:true)',
  `msg` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
  `session_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '当前用户登录请求的sessionId',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`auto_id`) USING BTREE,
  UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
  UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
  INDEX `idx_ip`(`ip`) USING BTREE COMMENT '当前访问的ip地址',
  INDEX `idx_username`(`username`) USING BTREE COMMENT '当前登陆的用户名',
  INDEX `idx_session_id`(`session_id`) USING BTREE COMMENT '当前用户登录请求的sessionId'
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录日志' ROW_FORMAT = Dynamic;

-- API操作日志系统表
DROP TABLE IF EXISTS `cloud-log`.`log_op`;
CREATE TABLE `cloud-log`.`log_op` (
  `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
  `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'API操作日志系统内唯一标识',
  `ip` bigint(15) UNSIGNED NULL DEFAULT NULL COMMENT '当前访问的ip地址',
  `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'ip地址的实际地理位置',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '业务类型',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '执行操作的类名称',
  `method_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '执行操作的方法名称',
  `parameter` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求时的url参数',
  `spider` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '爬虫的类型(如果有)',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统类型',
  `platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作平台类型',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器内核类型',
  `browser_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器内核版本',
  `browser_engine` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器解析引擎的类型',
  `browser_engine_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器解析引擎的版本',
  `method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '发出此请求的HTTP方法的名称(如 GET|POST|PUT)',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器的UA标识',
  `is_mobile` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '是否为移动平台(0:false,1:true)',
  `request_uri` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '访问的URL除去host部分的路径',
  `referer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求头中的referer信息',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块名称',
  `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '访问出现错误时获取到的异常信息',
  `error_cause` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '访问出现错误时获取到的异常原因',
  `is_success` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '访问的状态(0:false,1:true)',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '访问时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '当前操作的用户名',
  PRIMARY KEY (`auto_id`) USING BTREE,
  UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识索引',
  UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
  INDEX `ip`(`ip`) USING BTREE COMMENT '当前访问的ip地址索引',
  INDEX `business_type`(`business_type`) USING BTREE COMMENT '业务类型索引',
  INDEX `browser`(`browser`) USING BTREE COMMENT '浏览器内核类型索引',
  INDEX `platform`(`platform`) USING BTREE COMMENT '操作平台类型索引',
  INDEX `spider`(`spider`) USING BTREE COMMENT '爬虫类型索引',
  INDEX `request_uri`(`request_uri`) USING BTREE COMMENT '访问的URL除去host部分的路径索引',
  INDEX `title`(`title`) USING BTREE COMMENT '模块名称索引',
  INDEX `create_time`(`create_time`) USING BTREE COMMENT '访问时间索引',
  INDEX `username`(`username`) USING BTREE COMMENT '用户名索引'
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'API操作日志' ROW_FORMAT = Dynamic;

-- 日志表依据 创建时间 分表
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202201` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202202` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202203` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202204` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202205` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202206` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202207` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202208` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202209` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202210` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202211` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202212` LIKE `cloud-log`.`log_login`;

CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202201` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202202` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202203` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202204` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202205` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202206` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202207` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202208` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202209` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202210` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202211` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202212` LIKE `cloud-log`.`log_op`;

-- 删除原数据表
DROP TABLE IF EXISTS `cloud-log`.`log_login`;
DROP TABLE IF EXISTS `cloud-log`.`log_op`;

SET NAMES utf8mb4;