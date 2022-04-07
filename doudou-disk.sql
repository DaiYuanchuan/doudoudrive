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

-- 系统权限表
DROP TABLE IF EXISTS `cloud-user`.`sys_authorization`;
CREATE TABLE `cloud-user`.`sys_authorization`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `auth_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '授权编码',
 `auth_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '授权名称',
 `auth_remarks` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '当前权限的描述',
 `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 UNIQUE INDEX `uk_auth_code`(`auth_code`) USING BTREE COMMENT '授权编码唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统权限管理模块' ROW_FORMAT = DYNAMIC;

-- 添加默认权限配置
INSERT INTO `cloud-user`.`sys_authorization` (`business_id`, `auth_code`, `auth_name`, `auth_remarks`) VALUES ('22040717153816493229384780461085968', 'admin', '系统管理员', '系统管理员，当前系统最大权限');
INSERT INTO `cloud-user`.`sys_authorization` (`business_id`, `auth_code`, `auth_name`, `auth_remarks`) VALUES ('22040717193416493231740980449751502', 'file:upload', '文件上传', '文件基础权限');
INSERT INTO `cloud-user`.`sys_authorization` (`business_id`, `auth_code`, `auth_name`, `auth_remarks`) VALUES ('22040717194516493231857540468398438', 'file:delete', '文件删除', '文件基础权限');
INSERT INTO `cloud-user`.`sys_authorization` (`business_id`, `auth_code`, `auth_name`, `auth_remarks`) VALUES ('22040717200016493232008380419446433', 'file:update', '文件修改', '文件基础权限');
INSERT INTO `cloud-user`.`sys_authorization` (`business_id`, `auth_code`, `auth_name`, `auth_remarks`) VALUES ('22040717201116493232112660467356767', 'file:select', '文件查询', '文件基础权限');
INSERT INTO `cloud-user`.`sys_authorization` (`business_id`, `auth_code`, `auth_name`, `auth_remarks`) VALUES ('22040717203416493232347010482008045', 'file:share', '文件分享', '对外分享权限');

-- 系统角色表
DROP TABLE IF EXISTS `cloud-user`.`sys_role`;
CREATE TABLE `cloud-user`.`sys_role`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `role_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色编码',
 `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '角色名称',
 `role_remarks` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '角色描述',
 `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 UNIQUE INDEX `uk_role_code`(`role_code`) USING BTREE COMMENT '角色编码唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色管理模块' ROW_FORMAT = Dynamic;

-- 添加默认角色配置
INSERT INTO `cloud-user`.`sys_role` (`business_id`, `role_code`, `role_name`, `role_remarks`) VALUES ('22040717265916493236196240559334142', 'admin', '系统管理员', '拥有系统最大权限');
INSERT INTO `cloud-user`.`sys_role` (`business_id`, `role_code`, `role_name`, `role_remarks`) VALUES ('22040717314016493239009530588204569', 'file', '文件基础角色', '拥有文件基础权限');
INSERT INTO `cloud-user`.`sys_role` (`business_id`, `role_code`, `role_name`, `role_remarks`) VALUES ('22040717315016493239104070528655046', 'share', '文件分享角色', '拥有对外分享权限');

-- 系统角色与权限关联表(通过角色编码、权限编码多对多关联)
DROP TABLE IF EXISTS `cloud-user`.`sys_role_auth`;
CREATE TABLE `cloud-user`.`sys_role_auth`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `role_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色编码',
 `auth_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '权限编码',
 `remarks` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '描述',
 `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 INDEX `idx_role_code`(`role_code`) USING BTREE COMMENT '角色编码索引',
 INDEX `idx_auth_code`(`auth_code`) USING BTREE COMMENT '权限编码索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色、权限关联模块' ROW_FORMAT = Dynamic;

INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717332416493240040290632222442', 'admin', 'admin', '绑定管理员权限');
INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717353616493241364760682472068', 'admin', 'file:upload', '绑定管理员权限');
INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717354716493241472280621403644', 'admin', 'file:delete', '绑定管理员权限');
INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717355816493241585010609673759', 'admin', 'file:update', '绑定管理员权限');
INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717360816493241688470697621755', 'admin', 'file:select', '绑定管理员权限');
INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717361816493241780130646523731', 'admin', 'file:share', '绑定管理员权限');

INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717381616493242966890696578219', 'file', 'file:upload', '绑定文件基础权限');
INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717383116493243113000625050858', 'file', 'file:delete', '绑定文件基础权限');
INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717384116493243211260601150119', 'file', 'file:update', '绑定文件基础权限');
INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717391116493243517380624037622', 'file', 'file:select', '绑定文件基础权限');

INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717392216493243625730638194608', 'share', 'file:share', '绑定对外分享权限');

-- 系统用户与角色关联表(通过用户业务标识、角色编码多对多关联)
DROP TABLE IF EXISTS `cloud-user`.`sys_user_role`;
CREATE TABLE `cloud-user`.`sys_user_role`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `user_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户系统内唯一标识',
 `role_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色编码',
 `remarks` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '描述',
 `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 INDEX `idx_user_id`(`user_id`) USING BTREE COMMENT '用户唯一标识索引',
 INDEX `idx_role_code`(`role_code`) USING BTREE COMMENT '角色编码索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户、角色关联模块' ROW_FORMAT = Dynamic;

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

-- 用户与角色关联表依据 user_id 平均分 50 个表
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_01` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_02` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_03` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_04` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_05` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_06` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_07` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_08` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_09` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_10` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_11` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_12` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_13` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_14` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_15` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_16` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_17` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_18` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_19` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_20` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_21` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_22` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_23` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_24` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_25` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_26` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_27` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_28` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_29` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_30` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_31` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_32` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_33` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_34` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_35` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_36` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_37` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_38` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_39` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_40` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_41` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_42` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_43` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_44` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_45` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_46` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_47` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_48` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_49` LIKE `cloud-user`.`sys_user_role`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`sys_user_role_50` LIKE `cloud-user`.`sys_user_role`;

-- 删除原数据表
DROP TABLE IF EXISTS `cloud-user`.`disk_user`;
DROP TABLE IF EXISTS `cloud-user`.`sys_user_role`;

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