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
 `user_unlock_time` datetime(0) NULL DEFAULT NULL COMMENT '账号解封时间',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE COMMENT '用户名称唯一',
 UNIQUE INDEX `uk_user_email`(`user_email`) USING BTREE COMMENT '用户邮箱唯一',
 INDEX `idx_user_tel`(`user_tel`) USING BTREE COMMENT '用户绑定的手机号'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户模块' ROW_FORMAT = DYNAMIC;

-- 用户属性表
DROP TABLE IF EXISTS `cloud-user`.`disk_user_attr`;
CREATE TABLE `cloud-user`.`disk_user_attr`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `user_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户系统内唯一标识',
 `attribute_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户属性名称',
 `attribute_value` bigint(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT '用户属性值',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 UNIQUE INDEX `uk_composite` (`user_id`, `attribute_name`) USING BTREE COMMENT '用户标识、属性名称组合索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户属性模块' ROW_FORMAT = DYNAMIC;

-- 系统权限表
DROP TABLE IF EXISTS `cloud-user`.`sys_authorization`;
CREATE TABLE `cloud-user`.`sys_authorization`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `auth_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '授权编码',
 `auth_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '授权名称',
 `auth_remarks` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '当前权限的描述',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 INDEX `idx_role_code`(`role_code`) USING BTREE COMMENT '角色编码索引',
 INDEX `idx_auth_code`(`auth_code`) USING BTREE COMMENT '权限编码索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色、权限关联模块' ROW_FORMAT = Dynamic;

INSERT INTO `cloud-user`.`sys_role_auth` (`business_id`, `role_code`, `auth_code`, `remarks`) VALUES ('22040717332416493240040290632222442', 'admin', 'admin', '绑定管理员权限');

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
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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

-- 用户属性表依据 user_id 平均分 50 个表
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_01` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_02` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_03` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_04` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_05` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_06` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_07` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_08` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_09` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_10` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_11` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_12` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_13` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_14` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_15` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_16` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_17` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_18` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_19` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_20` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_21` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_22` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_23` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_24` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_25` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_26` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_27` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_28` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_29` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_30` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_31` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_32` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_33` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_34` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_35` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_36` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_37` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_38` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_39` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_40` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_41` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_42` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_43` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_44` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_45` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_46` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_47` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_48` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_49` LIKE `cloud-user`.`disk_user_attr`;
CREATE TABLE IF NOT EXISTS `cloud-user`.`disk_user_attr_50` LIKE `cloud-user`.`disk_user_attr`;

-- 删除原数据表
DROP TABLE IF EXISTS `cloud-user`.`disk_user`;
DROP TABLE IF EXISTS `cloud-user`.`sys_user_role`;
DROP TABLE IF EXISTS `cloud-user`.`disk_user_attr`;

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
  `is_success` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '是否使用密码登录(0:false,1:true)',
  `msg` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
  `session_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '当前用户登录请求的sessionId',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块名称',
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
  `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '访问出现错误时获取到的异常信息',
  `error_cause` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '访问出现错误时获取到的异常原因',
  `is_success` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '访问的状态(0:false,1:true)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '访问时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '当前操作的用户名',
  PRIMARY KEY (`auto_id`) USING BTREE,
  UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识索引',
  UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
  INDEX `ip`(`ip`) USING BTREE COMMENT '当前访问的ip地址索引',
  INDEX `business_type`(`business_type`) USING BTREE COMMENT '业务类型索引',
  INDEX `title`(`title`) USING BTREE COMMENT '模块名称索引',
  INDEX `browser`(`browser`) USING BTREE COMMENT '浏览器内核类型索引',
  INDEX `platform`(`platform`) USING BTREE COMMENT '操作平台类型索引',
  INDEX `spider`(`spider`) USING BTREE COMMENT '爬虫类型索引',
  INDEX `request_uri`(`request_uri`) USING BTREE COMMENT '访问的URL除去host部分的路径索引',
  INDEX `create_time`(`create_time`) USING BTREE COMMENT '访问时间索引',
  INDEX `username`(`username`) USING BTREE COMMENT '用户名索引'
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'API操作日志' ROW_FORMAT = Dynamic;

-- 短信、邮件发送记录表
DROP TABLE IF EXISTS `cloud-log`.`sms_send_record`;
CREATE TABLE `cloud-log`.`sms_send_record` (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `sms_recipient` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收件人信息',
 `sms_title` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '消息发送标题',
 `sms_data_id` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '消息发送时的数据标识',
 `sms_error_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '消息发送失败时获取到的异常原因',
 `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '当前操作的用户名',
 `sms_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '消息类型(1:邮件；2:短信)',
 `sms_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '消息发送状态(1:待分发；2:发送成功；3:发送失败)',
 `sms_send_time` datetime(0) NOT NULL COMMENT '消息发送时间',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识'
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMS发送记录' ROW_FORMAT = Dynamic;

-- RocketMQ消息消费记录表 依据 创建时间 分表
DROP TABLE IF EXISTS `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE `cloud-log`.`rocketmq_consumer_record` (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `msg_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'MQ消息标识',
 `offset_msg_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'MQ消息偏移id',
 `retry_count` int(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '消息重试次数',
 `topic` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'MQ消息主题',
 `tag` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'MQ消息标签',
 `broker_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'MQ分片名',
 `queue_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'MQ消费队列id',
 `queue_offset` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'MQ逻辑队列偏移',
 `send_time` datetime(0) NOT NULL COMMENT '消息发送时间',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 UNIQUE INDEX `uk_msg_id`(`msg_id`) USING BTREE COMMENT 'MQ消息唯一标识'
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'RocketMQ消费记录' ROW_FORMAT = Dynamic;

-- 日志表依据 创建时间 分表
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202301` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202302` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202303` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202304` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202305` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202306` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202307` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202308` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202309` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202310` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202311` LIKE `cloud-log`.`log_login`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_login_202312` LIKE `cloud-log`.`log_login`;

CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202301` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202302` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202303` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202304` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202305` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202306` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202307` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202308` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202309` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202310` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202311` LIKE `cloud-log`.`log_op`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`log_op_202312` LIKE `cloud-log`.`log_op`;

CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202301` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202302` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202303` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202304` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202305` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202306` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202307` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202308` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202309` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202310` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202311` LIKE `cloud-log`.`sms_send_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`sms_send_record_202312` LIKE `cloud-log`.`sms_send_record`;

CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202301` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202302` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202303` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202304` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202305` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202306` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202307` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202308` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202309` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202310` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202311` LIKE `cloud-log`.`rocketmq_consumer_record`;
CREATE TABLE IF NOT EXISTS `cloud-log`.`rocketmq_consumer_record_202312` LIKE `cloud-log`.`rocketmq_consumer_record`;

-- 删除原数据表
DROP TABLE IF EXISTS `cloud-log`.`log_login`;
DROP TABLE IF EXISTS `cloud-log`.`log_op`;
DROP TABLE IF EXISTS `cloud-log`.`sms_send_record`;
DROP TABLE IF EXISTS `cloud-log`.`rocketmq_consumer_record`;

/**
 * ******************************************************* 配置库 *******************************************************
 */

-- 配置库
CREATE DATABASE IF NOT EXISTS `cloud-config` CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;
USE `cloud-config`;

-- 数据字典表
DROP TABLE IF EXISTS `cloud-config`.`disk_dictionary`;
CREATE TABLE `cloud-config`.`disk_dictionary` (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `dictionary_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字段名称',
 `dictionary_describe` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字段描述',
 `dictionary_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '字段内容',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 UNIQUE INDEX `uk_dictionary_name`(`dictionary_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据字典模块' ROW_FORMAT = Dynamic;

-- 添加系统默认配置
INSERT INTO `cloud-config`.`disk_dictionary` (`business_id`, `dictionary_name`, `dictionary_describe`, `dictionary_content`) VALUES ('22040719590316493327434200038792976', 'mailConfig', '邮件发送配置项', '{
	"host": "",
	"port": 0,
	"from": "",
	"user": "",
	"pass": "",
	"sslEnable": true,
	"socketFactoryClass": "javax.net.ssl.SSLSocketFactory",
	"timeout": 0,
	"auth": true,
	"sslProtocols": "TLSv1.2 TLSv1.3"
}');
INSERT INTO `cloud-config`.`disk_dictionary` (`business_id`, `dictionary_name`, `dictionary_describe`, `dictionary_content`) VALUES ('22042716251116510479117800011980439', 'throughput', '短信、邮件最大吞吐量配置', '{
	"mail": 10,
	"sms": 1
}');
INSERT INTO `cloud-config`.`disk_dictionary` (`business_id`, `dictionary_name`, `dictionary_describe`, `dictionary_content`) VALUES ('22042919000816512300085730006624275', 'smsConfig', '短信发送配置项', '{
	"appId": "",
	"appKey": "",
	"domain": "",
	"signName": "",
	"appType": ""
}');
INSERT INTO `cloud-config`.`disk_dictionary` (`business_id`, `dictionary_name`, `dictionary_describe`, `dictionary_content`) VALUES ('22051217075416523464741090092286650', 'defaultAvatar', '默认的用户头像', 'https://static-page-1255518771.cos.ap-shanghai.myqcloud.com/common/image/literaryMale.jpg');
INSERT INTO `cloud-config`.`disk_dictionary` (`business_id`, `dictionary_name`, `dictionary_describe`, `dictionary_content`) VALUES ('22052416064916533796092510022306824', 'cipher', '全局对称加密密钥', 'VP-EcBOmZHGkTT0vZfeSHg==');
INSERT INTO `cloud-config`.`disk_dictionary` (`business_id`, `dictionary_name`, `dictionary_describe`, `dictionary_content`) VALUES ('22052513162916534557896760046097518', 'qiNiuConfig', '七牛云对象存储相关配置', '{
	"callback": "",
	"qiNiuCallback": "",
	"path": "",
	"accessKey": "",
	"secretKey": "",
	"bucket": "",
	"expires": 0,
	"size": 0,
	"fileType": 0,
	"domain": {
		"picture": "",
		"download": "",
		"stream": ""
	},
	"region": {
		"region": "",
		"srcUpHosts": "",
		"accUpHost": "",
		"iovipHost": "",
		"rsHost": "",
		"rsfHost": "",
		"apiHost": ""
	}
}');
INSERT INTO `cloud-config`.`disk_dictionary` (`business_id`, `dictionary_name`, `dictionary_describe`, `dictionary_content`) VALUES ('22052611330416535359848070010794434', 'fileReviewConfig', '文件内容审核相关配置', '{
	"videoTypes": [""],
	"imageTypes": [""],
	"requestTimeout": 2
}');
INSERT INTO `cloud-config`.`disk_dictionary` (`business_id`, `dictionary_name`, `dictionary_describe`, `dictionary_content`) VALUES ('22061923530816556539886100086143191', 'threadPoolConfig', '内部线程池相关配置', '[
	{
		"corePoolSize": 5,
		"maxPoolSize": 5,
		"queueCapacity": 1,
		"allowCoreThreadTimeOut": true,
		"keepAliveTime": 60,
		"name": ""
	}
]');

/**
 * ******************************************************* 文件分享库 *******************************************************
 */

-- 文件分享库
CREATE DATABASE IF NOT EXISTS `cloud-share` CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;
USE `cloud-share`;

-- 文件分享记录表 依据 user_id 分表
DROP TABLE IF EXISTS `cloud-share`.`file_share`;
CREATE TABLE `cloud-share`.`file_share`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `user_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户系统内唯一标识',
 `share_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分享的短链接标识',
 `share_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分享的标题',
 `share_pwd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '分享链接的提取码',
 `share_salt` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用于计算文件key的盐值',
 `file_count` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '分享的文件数量',
 `browse_count` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览次数',
 `save_count` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '保存、转存次数',
 `download_count` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '下载次数',
 `expiration` datetime(0) NULL DEFAULT NULL COMMENT '到期时间',
 `expired` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已经过期(0:false；1:true)',
 `folder` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否包含文件夹(0:false；1:true)',
 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '状态(0:正常；1:关闭)',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 UNIQUE INDEX `uk_share_id`(`share_id`) USING BTREE COMMENT '分享的短链接标识',
 INDEX `idx_user_id`(`user_id`) USING BTREE COMMENT '分享的用户标识',
 INDEX `idx_expiration`(`expiration`) USING BTREE COMMENT '到期时间'
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件分享信息' ROW_FORMAT = Dynamic;

-- 文件分享记录详情表 依据 share_id 分表
DROP TABLE IF EXISTS `cloud-share`.`file_share_detail`;
CREATE TABLE `cloud-share`.`file_share_detail`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `user_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户系统内唯一标识',
 `share_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分享的短链接标识',
 `file_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件标识',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 INDEX `idx_share_id`(`share_id`) USING BTREE COMMENT '分享的短链接标识'
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件分享记录详情' ROW_FORMAT = Dynamic;

-- 文件分享信息表 依据 user_id 平均分20个表
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_01` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_02` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_03` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_04` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_05` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_06` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_07` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_08` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_09` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_10` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_11` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_12` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_13` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_14` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_15` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_16` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_17` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_18` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_19` LIKE `cloud-share`.`file_share`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_20` LIKE `cloud-share`.`file_share`;


-- 文件分享记录详情表 依据 share_id 平均分40个表
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_01` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_02` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_03` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_04` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_05` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_06` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_07` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_08` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_09` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_10` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_11` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_12` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_13` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_14` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_15` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_16` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_17` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_18` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_19` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_20` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_21` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_22` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_23` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_24` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_25` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_26` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_27` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_28` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_29` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_30` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_31` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_32` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_33` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_34` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_35` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_36` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_37` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_38` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_39` LIKE `cloud-share`.`file_share_detail`;
CREATE TABLE IF NOT EXISTS `cloud-share`.`file_share_detail_40` LIKE `cloud-share`.`file_share_detail`;

-- 删除原数据表
DROP TABLE IF EXISTS `cloud-share`.`file_share`;
DROP TABLE IF EXISTS `cloud-share`.`file_share_detail`;

/**
 * ******************************************************* 文件库 *******************************************************
 */

-- 文件库
CREATE DATABASE IF NOT EXISTS `cloud-file` CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;
USE `cloud-file`;

-- OSS文件对象存储表 依据 etag 分表
DROP TABLE IF EXISTS `cloud-file`.`oss_file`;
CREATE TABLE `cloud-file`.`oss_file`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `etag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件的ETag(资源的唯一标识)',
 `size` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '文件大小(字节)',
 `mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件的mime类型',
 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '文件当前状态(0:正常；1:待审核；2:审核失败；3:源文件已删除)',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 UNIQUE INDEX `uk_etag`(`etag`) USING BTREE,
 INDEX `idx_mime_type`(`mime_type`) USING BTREE,
 INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OSS文件对象存储' ROW_FORMAT = Dynamic;

-- 用户文件表 依据 user_id 分表
DROP TABLE IF EXISTS `cloud-file`.`disk_file`;
CREATE TABLE `cloud-file`.`disk_file`  (
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `user_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户系统内唯一标识',
 `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '新建文档' COMMENT '文件名',
 `file_parent_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '文件父级标识',
 `file_size` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '文件大小(字节)',
 `file_mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件的mime类型',
 `file_etag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件的ETag(资源的唯一标识)',
 `is_file_folder` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否为文件夹(0:false；1:true)',
 `is_forbidden` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '当前文件是否被禁止访问(0:false；1:true)',
 `is_collect` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '当前文件是否被收藏(0:false；1:true)',
 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '文件当前状态(0:已删除；1:正常)',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 INDEX `idx_user_id`(`user_id`) USING BTREE,
 INDEX `idx_file_name`(`file_name`) USING BTREE,
 INDEX `idx_file_parent_id`(`file_parent_id`) USING BTREE,
 INDEX `idx_file_etag`(`file_etag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户文件模块' ROW_FORMAT = Dynamic;

-- 文件操作记录模块
DROP TABLE IF EXISTS `cloud-file`.`file_record`;
CREATE TABLE `cloud-file`.`file_record`(
 `auto_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增长标识',
 `business_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '业务标识',
 `user_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户系统内唯一标识',
 `file_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件标识',
 `file_etag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '文件的ETag(资源的唯一标识)',
 `action` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '动作(0:文件状态；1:文件内容状态；2:文件复制；3:文件删除)',
 `action_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '动作类型(action为0:{0:文件被删除}；action为1:{0:待审核；1:待删除}；action为2:{0:任务待处理；1:任务处理中}；action为3:{0:任务待处理；1:任务处理中})',
 `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
 `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`auto_id`) USING BTREE,
 UNIQUE INDEX `pk_auto_id`(`auto_id`) USING BTREE COMMENT '自增长标识',
 UNIQUE INDEX `uk_business_id`(`business_id`) USING BTREE COMMENT '系统内唯一标识',
 INDEX `idx_action`(`action`, `action_type`) USING BTREE COMMENT '动作字段组合索引',
 INDEX `idx_file_etag`(`file_etag`) USING BTREE COMMENT '文件的ETag'
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件操作记录' ROW_FORMAT = Dynamic;

-- OSS文件对象存储表 根据 etag 平均分300个表
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_01` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_02` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_03` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_04` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_05` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_06` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_07` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_08` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_09` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_10` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_11` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_12` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_13` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_14` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_15` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_16` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_17` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_18` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_19` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_20` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_21` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_22` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_23` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_24` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_25` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_26` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_27` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_28` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_29` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_30` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_31` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_32` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_33` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_34` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_35` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_36` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_37` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_38` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_39` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_40` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_41` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_42` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_43` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_44` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_45` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_46` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_47` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_48` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_49` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_50` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_51` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_52` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_53` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_54` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_55` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_56` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_57` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_58` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_59` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_60` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_61` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_62` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_63` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_64` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_65` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_66` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_67` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_68` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_69` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_70` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_71` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_72` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_73` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_74` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_75` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_76` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_77` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_78` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_79` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_80` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_81` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_82` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_83` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_84` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_85` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_86` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_87` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_88` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_89` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_90` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_91` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_92` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_93` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_94` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_95` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_96` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_97` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_98` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_99` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_100` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_101` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_102` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_103` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_104` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_105` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_106` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_107` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_108` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_109` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_110` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_111` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_112` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_113` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_114` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_115` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_116` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_117` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_118` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_119` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_120` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_121` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_122` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_123` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_124` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_125` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_126` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_127` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_128` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_129` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_130` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_131` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_132` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_133` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_134` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_135` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_136` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_137` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_138` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_139` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_140` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_141` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_142` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_143` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_144` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_145` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_146` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_147` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_148` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_149` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_150` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_151` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_152` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_153` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_154` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_155` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_156` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_157` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_158` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_159` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_160` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_161` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_162` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_163` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_164` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_165` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_166` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_167` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_168` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_169` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_170` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_171` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_172` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_173` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_174` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_175` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_176` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_177` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_178` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_179` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_180` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_181` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_182` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_183` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_184` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_185` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_186` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_187` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_188` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_189` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_190` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_191` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_192` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_193` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_194` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_195` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_196` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_197` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_198` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_199` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_200` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_201` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_202` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_203` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_204` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_205` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_206` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_207` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_208` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_209` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_210` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_211` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_212` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_213` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_214` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_215` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_216` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_217` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_218` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_219` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_220` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_221` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_222` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_223` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_224` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_225` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_226` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_227` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_228` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_229` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_230` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_231` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_232` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_233` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_234` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_235` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_236` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_237` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_238` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_239` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_240` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_241` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_242` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_243` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_244` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_245` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_246` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_247` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_248` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_249` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_250` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_251` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_252` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_253` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_254` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_255` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_256` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_257` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_258` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_259` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_260` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_261` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_262` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_263` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_264` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_265` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_266` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_267` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_268` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_269` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_270` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_271` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_272` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_273` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_274` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_275` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_276` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_277` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_278` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_279` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_280` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_281` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_282` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_283` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_284` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_285` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_286` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_287` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_288` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_289` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_290` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_291` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_292` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_293` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_294` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_295` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_296` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_297` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_298` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_299` LIKE `cloud-file`.`oss_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`oss_file_300` LIKE `cloud-file`.`oss_file`;

-- 用户文件表 根据 user_id 平均分500个表
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_01` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_02` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_03` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_04` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_05` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_06` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_07` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_08` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_09` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_10` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_11` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_12` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_13` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_14` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_15` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_16` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_17` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_18` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_19` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_20` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_21` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_22` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_23` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_24` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_25` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_26` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_27` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_28` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_29` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_30` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_31` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_32` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_33` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_34` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_35` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_36` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_37` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_38` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_39` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_40` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_41` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_42` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_43` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_44` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_45` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_46` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_47` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_48` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_49` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_50` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_51` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_52` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_53` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_54` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_55` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_56` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_57` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_58` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_59` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_60` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_61` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_62` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_63` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_64` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_65` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_66` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_67` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_68` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_69` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_70` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_71` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_72` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_73` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_74` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_75` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_76` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_77` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_78` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_79` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_80` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_81` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_82` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_83` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_84` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_85` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_86` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_87` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_88` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_89` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_90` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_91` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_92` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_93` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_94` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_95` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_96` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_97` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_98` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_99` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_100` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_101` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_102` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_103` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_104` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_105` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_106` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_107` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_108` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_109` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_110` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_111` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_112` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_113` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_114` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_115` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_116` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_117` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_118` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_119` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_120` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_121` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_122` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_123` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_124` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_125` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_126` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_127` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_128` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_129` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_130` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_131` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_132` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_133` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_134` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_135` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_136` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_137` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_138` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_139` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_140` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_141` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_142` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_143` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_144` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_145` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_146` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_147` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_148` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_149` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_150` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_151` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_152` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_153` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_154` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_155` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_156` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_157` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_158` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_159` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_160` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_161` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_162` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_163` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_164` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_165` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_166` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_167` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_168` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_169` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_170` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_171` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_172` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_173` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_174` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_175` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_176` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_177` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_178` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_179` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_180` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_181` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_182` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_183` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_184` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_185` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_186` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_187` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_188` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_189` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_190` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_191` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_192` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_193` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_194` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_195` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_196` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_197` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_198` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_199` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_200` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_201` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_202` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_203` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_204` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_205` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_206` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_207` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_208` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_209` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_210` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_211` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_212` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_213` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_214` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_215` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_216` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_217` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_218` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_219` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_220` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_221` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_222` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_223` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_224` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_225` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_226` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_227` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_228` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_229` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_230` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_231` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_232` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_233` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_234` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_235` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_236` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_237` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_238` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_239` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_240` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_241` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_242` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_243` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_244` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_245` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_246` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_247` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_248` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_249` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_250` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_251` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_252` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_253` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_254` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_255` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_256` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_257` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_258` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_259` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_260` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_261` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_262` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_263` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_264` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_265` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_266` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_267` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_268` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_269` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_270` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_271` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_272` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_273` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_274` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_275` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_276` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_277` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_278` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_279` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_280` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_281` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_282` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_283` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_284` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_285` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_286` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_287` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_288` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_289` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_290` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_291` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_292` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_293` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_294` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_295` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_296` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_297` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_298` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_299` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_300` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_301` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_302` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_303` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_304` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_305` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_306` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_307` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_308` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_309` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_310` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_311` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_312` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_313` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_314` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_315` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_316` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_317` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_318` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_319` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_320` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_321` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_322` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_323` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_324` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_325` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_326` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_327` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_328` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_329` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_330` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_331` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_332` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_333` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_334` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_335` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_336` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_337` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_338` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_339` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_340` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_341` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_342` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_343` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_344` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_345` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_346` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_347` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_348` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_349` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_350` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_351` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_352` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_353` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_354` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_355` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_356` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_357` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_358` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_359` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_360` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_361` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_362` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_363` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_364` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_365` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_366` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_367` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_368` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_369` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_370` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_371` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_372` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_373` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_374` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_375` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_376` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_377` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_378` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_379` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_380` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_381` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_382` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_383` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_384` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_385` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_386` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_387` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_388` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_389` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_390` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_391` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_392` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_393` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_394` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_395` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_396` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_397` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_398` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_399` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_400` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_401` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_402` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_403` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_404` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_405` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_406` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_407` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_408` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_409` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_410` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_411` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_412` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_413` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_414` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_415` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_416` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_417` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_418` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_419` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_420` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_421` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_422` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_423` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_424` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_425` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_426` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_427` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_428` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_429` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_430` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_431` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_432` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_433` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_434` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_435` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_436` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_437` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_438` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_439` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_440` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_441` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_442` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_443` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_444` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_445` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_446` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_447` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_448` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_449` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_450` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_451` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_452` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_453` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_454` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_455` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_456` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_457` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_458` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_459` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_460` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_461` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_462` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_463` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_464` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_465` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_466` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_467` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_468` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_469` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_470` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_471` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_472` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_473` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_474` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_475` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_476` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_477` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_478` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_479` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_480` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_481` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_482` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_483` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_484` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_485` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_486` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_487` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_488` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_489` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_490` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_491` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_492` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_493` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_494` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_495` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_496` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_497` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_498` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_499` LIKE `cloud-file`.`disk_file`;
CREATE TABLE IF NOT EXISTS `cloud-file`.`disk_file_500` LIKE `cloud-file`.`disk_file`;

-- 删除原数据表
DROP TABLE IF EXISTS `cloud-file`.`oss_file`;
DROP TABLE IF EXISTS `cloud-file`.`disk_file`;

SET NAMES utf8mb4;