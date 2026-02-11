-- ====================================================
-- 多租户细粒度RBAC权限系统 - MySQL建表语句
-- ====================================================

-- 1. 租户表
CREATE TABLE `tenant` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                          `tenant_code` VARCHAR(50) NOT NULL COMMENT '租户编码',
                          `tenant_name` VARCHAR(100) NOT NULL COMMENT '租户名称',
                          `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
                          `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
                          `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
                          `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
                          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_tenant_code` (`tenant_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- 2. 部门表
CREATE TABLE `department` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
                              `dept_code` VARCHAR(50) NOT NULL COMMENT '部门编码',
                              `dept_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
                              `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID,0为根部门',
                              `dept_path` VARCHAR(500) NOT NULL COMMENT '部门路径,如/1/2/5/',
                              `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
                              `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
                              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_tenant_id` (`tenant_id`),
                              KEY `idx_parent_id` (`parent_id`),
                              KEY `idx_dept_path` (`tenant_id`, `dept_path`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 3. 用户表
CREATE TABLE `user` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
                        `dept_id` BIGINT NOT NULL COMMENT '部门ID',
                        `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                        `password` VARCHAR(100) NOT NULL COMMENT '密码(加密)',
                        `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
                        `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
                        `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
                        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`),
                        KEY `idx_tenant_id` (`tenant_id`),
                        KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 4. 角色表
CREATE TABLE `role` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `tenant_id` BIGINT DEFAULT NULL COMMENT '租户ID,平台角色为NULL',
                        `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
                        `role_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
                        `role_type` VARCHAR(20) NOT NULL COMMENT '角色类型:platform-平台 tenant-租户',
                        `level` INT NOT NULL COMMENT '角色层级,数字越小权限越大',
                        `parent_role_id` BIGINT DEFAULT NULL COMMENT '父角色ID,用于继承',
                        `is_tenant_admin` TINYINT NOT NULL DEFAULT 0 COMMENT '是否租户管理员:0-否 1-是',
                        `manageable` TINYINT NOT NULL DEFAULT 1 COMMENT '是否可被管理:0-否 1-是',
                        `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
                        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        KEY `idx_tenant_id` (`tenant_id`),
                        KEY `idx_role_type` (`role_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 5. 用户角色关联表
CREATE TABLE `user_role` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `user_id` BIGINT NOT NULL COMMENT '用户ID',
                             `role_id` BIGINT NOT NULL COMMENT '角色ID',
                             `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
                             KEY `idx_user_id` (`user_id`),
                             KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 6. 资源表
CREATE TABLE `resource` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `tenant_id` BIGINT DEFAULT NULL COMMENT '租户ID,平台资源为NULL',
                            `resource_code` VARCHAR(100) NOT NULL COMMENT '资源编码',
                            `resource_name` VARCHAR(100) NOT NULL COMMENT '资源名称',
                            `resource_type` VARCHAR(20) NOT NULL COMMENT '资源类型:menu-菜单 button-按钮 api-接口 entity-实体',
                            `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父资源ID',
                            `resource_path` VARCHAR(500) DEFAULT NULL COMMENT '资源路径',
                            `route_path` VARCHAR(200) DEFAULT NULL COMMENT '前端路由路径(菜单类型)',
                            `component_path` VARCHAR(200) DEFAULT NULL COMMENT '组件路径(菜单类型)',
                            `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
                            `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
                            `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_tenant_id` (`tenant_id`),
                            KEY `idx_parent_id` (`parent_id`),
                            KEY `idx_resource_type` (`resource_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源表';

-- 7. 操作表
CREATE TABLE `operation` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `operation_code` VARCHAR(50) NOT NULL COMMENT '操作编码',
                             `operation_name` VARCHAR(100) NOT NULL COMMENT '操作名称',
                             `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型:basic-基础 business-业务',
                             `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
                             `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_operation_code` (`operation_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作表';

-- 8. 权限表
CREATE TABLE `permission` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码,如order:view',
                              `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
                              `resource_id` BIGINT NOT NULL COMMENT '资源ID',
                              `operation_id` BIGINT NOT NULL COMMENT '操作ID',
                              `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
                              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_permission_code` (`permission_code`),
                              KEY `idx_resource_id` (`resource_id`),
                              KEY `idx_operation_id` (`operation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 9. 角色权限关联表
CREATE TABLE `role_permission` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `role_id` BIGINT NOT NULL COMMENT '角色ID',
                                   `permission_id` BIGINT NOT NULL COMMENT '权限ID',
                                   `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
                                   KEY `idx_role_id` (`role_id`),
                                   KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 10. 数据权限规则表
CREATE TABLE `data_permission_rule` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `role_id` BIGINT NOT NULL COMMENT '角色ID',
                                        `resource_id` BIGINT NOT NULL COMMENT '资源ID(实体类型的资源)',
                                        `data_scope_type` VARCHAR(20) NOT NULL COMMENT '数据范围类型:ALL-本租户全部 DEPT-本部门 DEPT_AND_CHILD-本部门及下级 SELF-仅本人 CUSTOM-自定义部门',
                                        `custom_dept_ids` VARCHAR(1000) DEFAULT NULL COMMENT '自定义部门ID列表(JSON数组)',
                                        `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
                                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_role_id` (`role_id`),
                                        KEY `idx_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据权限规则表';

-- 11. 字段资源表
CREATE TABLE `field_resource` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `entity_code` VARCHAR(100) NOT NULL COMMENT '实体编码,如order、customer',
                                  `field_name` VARCHAR(100) NOT NULL COMMENT '字段名称',
                                  `field_label` VARCHAR(100) NOT NULL COMMENT '字段标签(显示名)',
                                  `field_type` VARCHAR(50) NOT NULL COMMENT '字段类型',
                                  `sensitive_level` TINYINT NOT NULL DEFAULT 0 COMMENT '敏感级别:0-普通 1-敏感 2-高度敏感',
                                  `is_custom` TINYINT NOT NULL DEFAULT 0 COMMENT '是否自定义字段:0-固定字段 1-自定义字段',
                                  `custom_field_id` BIGINT DEFAULT NULL COMMENT '关联自定义字段定义ID',
                                  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
                                  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_entity_code` (`entity_code`),
                                  KEY `idx_custom_field` (`is_custom`, `custom_field_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段资源表';

-- 12. 字段权限规则表
CREATE TABLE `field_permission_rule` (
                                         `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `role_id` BIGINT NOT NULL COMMENT '角色ID',
                                         `field_resource_id` BIGINT NOT NULL COMMENT '字段资源ID',
                                         `permission_type` VARCHAR(20) NOT NULL COMMENT '权限类型:HIDDEN-隐藏 VISIBLE-可见 EDITABLE-可编辑 MASKED-脱敏显示',
                                         `mask_rule` VARCHAR(200) DEFAULT NULL COMMENT '脱敏规则',
                                         `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         PRIMARY KEY (`id`),
                                         KEY `idx_role_id` (`role_id`),
                                         KEY `idx_field_resource_id` (`field_resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段权限规则表';

-- 13. 可授权权限表
CREATE TABLE `grantable_permission` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `role_id` BIGINT NOT NULL COMMENT '授权者角色ID',
                                        `grantable_permission_id` BIGINT NOT NULL COMMENT '可授予的权限ID',
                                        `grant_scope` VARCHAR(20) NOT NULL COMMENT '授权范围:ALL-全部 PARTIAL-部分',
                                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_role_id` (`role_id`),
                                        KEY `idx_grantable_permission_id` (`grantable_permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='可授权权限表';

-- 14. 可授权数据范围表
CREATE TABLE `grantable_data_scope` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `role_id` BIGINT NOT NULL COMMENT '授权者角色ID',
                                        `resource_id` BIGINT NOT NULL COMMENT '资源ID',
                                        `max_scope_type` VARCHAR(20) NOT NULL COMMENT '最大可授予数据范围',
                                        `allowed_dept_ids` VARCHAR(2000) DEFAULT NULL COMMENT '可授权的部门ID列表(JSON数组)',
                                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_role_id` (`role_id`),
                                        KEY `idx_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='可授权数据范围表';

-- 15. 可授权字段权限表
CREATE TABLE `grantable_field_permission` (
                                              `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                              `role_id` BIGINT NOT NULL COMMENT '授权者角色ID',
                                              `field_resource_id` BIGINT NOT NULL COMMENT '字段资源ID',
                                              `max_permission_type` VARCHAR(20) NOT NULL COMMENT '最大可授予权限类型',
                                              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              PRIMARY KEY (`id`),
                                              KEY `idx_role_id` (`role_id`),
                                              KEY `idx_field_resource_id` (`field_resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='可授权字段权限表';

-- 16. 授权操作日志表
CREATE TABLE `grant_operation_log` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
                                       `operator_name` VARCHAR(50) NOT NULL COMMENT '操作人姓名',
                                       `target_role_id` BIGINT NOT NULL COMMENT '目标角色ID',
                                       `target_role_name` VARCHAR(100) NOT NULL COMMENT '目标角色名称',
                                       `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型:CREATE_ROLE-创建角色 UPDATE_ROLE-修改角色 DELETE_ROLE-删除角色 ASSIGN_PERMISSION-分配功能权限 ASSIGN_DATA_SCOPE-分配数据权限 ASSIGN_FIELD_PERMISSION-分配字段权限 ASSIGN_USER_ROLE-分配用户角色',
                                       `check_result` VARCHAR(20) NOT NULL COMMENT '校验结果:PASS-通过 REJECT-拒绝',
                                       `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '拒绝原因',
                                       `operation_detail` TEXT DEFAULT NULL COMMENT '操作详情(JSON)',
                                       `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
                                       `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                       PRIMARY KEY (`id`),
                                       KEY `idx_operator_id` (`operator_id`),
                                       KEY `idx_target_role_id` (`target_role_id`),
                                       KEY `idx_operation_type` (`operation_type`),
                                       KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权操作日志表';

-- 17. 数据访问日志表
CREATE TABLE `data_access_log` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                   `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                                   `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
                                   `target_tenant_id` BIGINT DEFAULT NULL COMMENT '目标租户ID(跨租户访问时)',
                                   `resource_type` VARCHAR(50) NOT NULL COMMENT '资源类型',
                                   `resource_id` BIGINT DEFAULT NULL COMMENT '资源ID',
                                   `operation` VARCHAR(50) NOT NULL COMMENT '操作',
                                   `field_list` VARCHAR(2000) DEFAULT NULL COMMENT '访问的字段列表',
                                   `data_scope` VARCHAR(20) DEFAULT NULL COMMENT '数据范围',
                                   `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
                                   `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
                                   `access_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
                                   `response_time` INT DEFAULT NULL COMMENT '响应时间(毫秒)',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_user_id` (`user_id`),
                                   KEY `idx_tenant_id` (`tenant_id`),
                                   KEY `idx_resource_type` (`resource_type`),
                                   KEY `idx_access_time` (`access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据访问日志表';

-- 18. 自定义字段定义表
CREATE TABLE `custom_field_define` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
                                       `entity_code` VARCHAR(100) NOT NULL COMMENT '关联实体,如user、department、order',
                                       `field_code` VARCHAR(100) NOT NULL COMMENT '字段编码,唯一标识',
                                       `field_name` VARCHAR(100) NOT NULL COMMENT '字段显示名称',
                                       `field_type` VARCHAR(50) NOT NULL COMMENT '数据类型:STRING-字符串 NUMBER-数字 DECIMAL-小数 DATE-日期 DATETIME-日期时间 BOOLEAN-布尔值 SELECT-单选下拉 MULTI_SELECT-多选下拉 TEXTAREA-文本域',
                                       `field_config` JSON DEFAULT NULL COMMENT '字段配置',
                                       `sensitive_level` TINYINT NOT NULL DEFAULT 0 COMMENT '敏感级别:0-普通 1-敏感 2-高度敏感',
                                       `is_required` TINYINT NOT NULL DEFAULT 0 COMMENT '是否必填:0-否 1-是',
                                       `default_value` VARCHAR(500) DEFAULT NULL COMMENT '默认值',
                                       `validation_rule` VARCHAR(500) DEFAULT NULL COMMENT '验证规则(正则表达式)',
                                       `is_searchable` TINYINT NOT NULL DEFAULT 0 COMMENT '是否可查询:0-否 1-是',
                                       `search_priority` INT NOT NULL DEFAULT 0 COMMENT '查询优先级,数字越大越优先建索引',
                                       `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
                                       `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
                                       `create_user_id` BIGINT NOT NULL COMMENT '创建人ID',
                                       `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_tenant_entity_code` (`tenant_id`, `entity_code`, `field_code`),
                                       KEY `idx_tenant_entity` (`tenant_id`, `entity_code`, `status`),
                                       KEY `idx_searchable` (`tenant_id`, `entity_code`, `is_searchable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义字段定义表';

-- 19. 自定义字段值表
CREATE TABLE `custom_field_value` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
                                      `entity_code` VARCHAR(100) NOT NULL COMMENT '实体类型',
                                      `entity_id` BIGINT NOT NULL COMMENT '实体ID(如user_id)',
                                      `field_code` VARCHAR(100) NOT NULL COMMENT '字段编码',
                                      `field_value` TEXT DEFAULT NULL COMMENT '字段值(JSON格式存储)',
                                      `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_tenant_entity_field` (`tenant_id`, `entity_code`, `entity_id`, `field_code`),
                                      KEY `idx_tenant_entity_id` (`tenant_id`, `entity_code`, `entity_id`),
                                      KEY `idx_field_code` (`field_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义字段值表';

-- 20. 自定义字段查询表(反范式设计)
CREATE TABLE `custom_field_search` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
                                       `entity_code` VARCHAR(100) NOT NULL COMMENT '实体类型',
                                       `entity_id` BIGINT NOT NULL COMMENT '实体ID',
                                       `field_code` VARCHAR(100) NOT NULL COMMENT '字段编码',
                                       `field_type` VARCHAR(50) NOT NULL COMMENT '字段类型',
                                       `string_value` VARCHAR(500) DEFAULT NULL COMMENT '字符串值',
                                       `number_value` DECIMAL(20, 6) DEFAULT NULL COMMENT '数字值',
                                       `date_value` DATE DEFAULT NULL COMMENT '日期值',
                                       `datetime_value` DATETIME DEFAULT NULL COMMENT '日期时间值',
                                       `boolean_value` TINYINT DEFAULT NULL COMMENT '布尔值',
                                       `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_tenant_entity_field` (`tenant_id`, `entity_code`, `entity_id`, `field_code`),
                                       KEY `idx_string_search` (`tenant_id`, `entity_code`, `field_code`, `string_value`(100)),
                                       KEY `idx_number_search` (`tenant_id`, `entity_code`, `field_code`, `number_value`),
                                       KEY `idx_date_search` (`tenant_id`, `entity_code`, `field_code`, `date_value`),
                                       KEY `idx_datetime_search` (`tenant_id`, `entity_code`, `field_code`, `datetime_value`),
                                       KEY `idx_boolean_search` (`tenant_id`, `entity_code`, `field_code`, `boolean_value`),
                                       KEY `idx_entity` (`tenant_id`, `entity_code`, `entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义字段查询表';