-- ====================================================
-- 多租户细粒度RBAC权限系统 - 模拟数据
-- ====================================================

-- 1. 租户表数据
INSERT INTO `tenant` (`id`, `tenant_code`, `tenant_name`, `contact_name`, `contact_phone`, `status`, `expire_time`) VALUES
                                                                                                                        (1, 'platform', '平台方', '系统管理员', '13800000000', 1, '2099-12-31 23:59:59'),
                                                                                                                        (2, 'company_a', 'A科技有限公司', '张三', '13800001111', 1, '2026-12-31 23:59:59'),
                                                                                                                        (3, 'company_b', 'B贸易集团', '李四', '13800002222', 1, '2026-12-31 23:59:59'),
                                                                                                                        (4, 'company_c', 'C咨询公司', '王五', '13800003333', 1, '2025-06-30 23:59:59');

-- 2. 部门表数据
INSERT INTO `department` (`id`, `tenant_id`, `dept_code`, `dept_name`, `parent_id`, `dept_path`, `sort_order`, `status`) VALUES
-- 租户2的部门结构
(1, 2, 'DEPT_A_ROOT', 'A科技总部', 0, '/1/', 1, 1),
(2, 2, 'DEPT_A_TECH', '技术部', 1, '/1/2/', 1, 1),
(3, 2, 'DEPT_A_SALES', '销售部', 1, '/1/3/', 2, 1),
(4, 2, 'DEPT_A_HR', '人力资源部', 1, '/1/4/', 3, 1),
(5, 2, 'DEPT_A_TECH_DEV', '研发组', 2, '/1/2/5/', 1, 1),
(6, 2, 'DEPT_A_TECH_TEST', '测试组', 2, '/1/2/6/', 2, 1),
(7, 2, 'DEPT_A_SALES_NORTH', '华北销售区', 3, '/1/3/7/', 1, 1),
(8, 2, 'DEPT_A_SALES_SOUTH', '华南销售区', 3, '/1/3/8/', 2, 1),
-- 租户3的部门结构
(9, 3, 'DEPT_B_ROOT', 'B贸易总部', 0, '/9/', 1, 1),
(10, 3, 'DEPT_B_PURCHASE', '采购部', 9, '/9/10/', 1, 1),
(11, 3, 'DEPT_B_SALES', '销售部', 9, '/9/11/', 2, 1),
(12, 3, 'DEPT_B_FINANCE', '财务部', 9, '/9/12/', 3, 1);

-- 3. 用户表数据
INSERT INTO `user` (`id`, `tenant_id`, `dept_id`, `username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
-- 平台管理员
(1, 1, 1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '13800000000', 'admin@platform.com', 1),
-- 租户2用户
(2, 2, 1, 'tenant_a_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', '13800001111', 'zhangsan@companya.com', 1),
(3, 2, 2, 'tech_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '技术经理', '13800001112', 'tech@companya.com', 1),
(4, 2, 5, 'developer_01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '开发工程师A', '13800001113', 'dev01@companya.com', 1),
(5, 2, 5, 'developer_02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '开发工程师B', '13800001114', 'dev02@companya.com', 1),
(6, 2, 6, 'tester_01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试工程师', '13800001115', 'test01@companya.com', 1),
(7, 2, 3, 'sales_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '销售经理', '13800001116', 'sales@companya.com', 1),
(8, 2, 7, 'sales_north_01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '华北销售员', '13800001117', 'salesn01@companya.com', 1),
(9, 2, 4, 'hr_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '人事经理', '13800001118', 'hr@companya.com', 1),
-- 租户3用户
(10, 3, 9, 'tenant_b_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', '13800002222', 'lisi@companyb.com', 1),
(11, 3, 10, 'purchase_staff', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '采购员', '13800002223', 'purchase@companyb.com', 1),
(12, 3, 11, 'sales_staff', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '销售员', '13800002224', 'sales@companyb.com', 1);

-- 4. 角色表数据
INSERT INTO `role` (`id`, `tenant_id`, `role_code`, `role_name`, `role_type`, `level`, `parent_role_id`, `is_tenant_admin`, `manageable`, `description`, `status`) VALUES
-- 平台角色
(1, NULL, 'PLATFORM_SUPER_ADMIN', '平台超级管理员', 'platform', 0, NULL, 0, 0, '平台最高权限,可跨租户管理', 1),
(2, NULL, 'PLATFORM_OPERATOR', '平台运营管理员', 'platform', 10, 1, 0, 1, '平台运营人员,可跨租户查看', 1),
-- 租户2角色
(3, 2, 'TENANT_A_ADMIN', '租户管理员', 'tenant', 100, NULL, 1, 0, 'A公司超级管理员', 1),
(4, 2, 'TENANT_A_TECH_MANAGER', '技术部经理', 'tenant', 200, 3, 0, 1, '技术部门管理角色', 1),
(5, 2, 'TENANT_A_DEVELOPER', '开发工程师', 'tenant', 300, 4, 0, 1, '研发人员角色', 1),
(6, 2, 'TENANT_A_TESTER', '测试工程师', 'tenant', 300, 4, 0, 1, '测试人员角色', 1),
(7, 2, 'TENANT_A_SALES_MANAGER', '销售经理', 'tenant', 200, 3, 0, 1, '销售部门管理角色', 1),
(8, 2, 'TENANT_A_SALES', '销售员', 'tenant', 300, 7, 0, 1, '销售人员角色', 1),
(9, 2, 'TENANT_A_HR_MANAGER', '人事经理', 'tenant', 200, 3, 0, 1, '人事部门管理角色', 1),
-- 租户3角色
(10, 3, 'TENANT_B_ADMIN', '租户管理员', 'tenant', 100, NULL, 1, 0, 'B公司超级管理员', 1),
(11, 3, 'TENANT_B_PURCHASE', '采购员', 'tenant', 300, 10, 0, 1, '采购人员角色', 1),
(12, 3, 'TENANT_B_SALES', '销售员', 'tenant', 300, 10, 0, 1, '销售人员角色', 1);

-- 5. 用户角色关联表数据
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) VALUES
                                                         (1, 1, 1),   -- 平台超管
                                                         (2, 2, 3),   -- 租户A管理员
                                                         (3, 3, 4),   -- 技术经理
                                                         (4, 4, 5),   -- 开发工程师A
                                                         (5, 5, 5),   -- 开发工程师B
                                                         (6, 6, 6),   -- 测试工程师
                                                         (7, 7, 7),   -- 销售经理
                                                         (8, 8, 8),   -- 华北销售员
                                                         (9, 9, 9),   -- 人事经理
                                                         (10, 10, 10), -- 租户B管理员
                                                         (11, 11, 11), -- 采购员
                                                         (12, 12, 12); -- 销售员

-- 6. 资源表数据
INSERT INTO `resource` (`id`, `tenant_id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `resource_path`, `route_path`, `component_path`, `icon`, `sort_order`, `status`) VALUES
-- 平台级菜单资源
(1, NULL, 'SYSTEM_MANAGE', '系统管理', 'menu', 0, '/system', '/system', NULL, 'el-icon-setting', 1, 1),
(2, NULL, 'TENANT_MANAGE', '租户管理', 'menu', 1, '/system/tenant', '/system/tenant', 'system/tenant/index', 'el-icon-office-building', 1, 1),
(3, NULL, 'USER_MANAGE', '用户管理', 'menu', 1, '/system/user', '/system/user', 'system/user/index', 'el-icon-user', 2, 1),
(4, NULL, 'ROLE_MANAGE', '角色管理', 'menu', 1, '/system/role', '/system/role', 'system/role/index', 'el-icon-user-solid', 3, 1),
-- 租户通用菜单资源
(5, NULL, 'WORKBENCH', '工作台', 'menu', 0, '/workbench', '/workbench', 'workbench/index', 'el-icon-monitor', 1, 1),
(6, NULL, 'CUSTOMER_MANAGE', '客户管理', 'menu', 0, '/customer', '/customer', NULL, 'el-icon-user', 2, 1),
(7, NULL, 'CUSTOMER_LIST', '客户列表', 'menu', 6, '/customer/list', '/customer/list', 'customer/list', NULL, 1, 1),
(8, NULL, 'ORDER_MANAGE', '订单管理', 'menu', 0, '/order', '/order', NULL, 'el-icon-document', 3, 1),
(9, NULL, 'ORDER_LIST', '订单列表', 'menu', 8, '/order/list', '/order/list', 'order/list', NULL, 1, 1),
-- 按钮资源
(10, NULL, 'CUSTOMER_ADD_BTN', '新增客户按钮', 'button', 7, NULL, NULL, NULL, NULL, 1, 1),
(11, NULL, 'CUSTOMER_EDIT_BTN', '编辑客户按钮', 'button', 7, NULL, NULL, NULL, NULL, 2, 1),
(12, NULL, 'CUSTOMER_DELETE_BTN', '删除客户按钮', 'button', 7, NULL, NULL, NULL, NULL, 3, 1),
(13, NULL, 'ORDER_ADD_BTN', '新增订单按钮', 'button', 9, NULL, NULL, NULL, NULL, 1, 1),
(14, NULL, 'ORDER_AUDIT_BTN', '审核订单按钮', 'button', 9, NULL, NULL, NULL, NULL, 2, 1),
-- API资源
(15, NULL, 'CUSTOMER_API', '客户API', 'api', 0, '/api/customer/**', NULL, NULL, NULL, 1, 1),
(16, NULL, 'ORDER_API', '订单API', 'api', 0, '/api/order/**', NULL, NULL, NULL, 2, 1),
-- 实体资源
(17, NULL, 'CUSTOMER_ENTITY', '客户实体', 'entity', 0, 'customer', NULL, NULL, NULL, 1, 1),
(18, NULL, 'ORDER_ENTITY', '订单实体', 'entity', 0, 'order', NULL, NULL, NULL, 2, 1),
(19, NULL, 'USER_ENTITY', '用户实体', 'entity', 0, 'user', NULL, NULL, NULL, 3, 1);

-- 7. 操作表数据
INSERT INTO `operation` (`id`, `operation_code`, `operation_name`, `operation_type`, `description`) VALUES
                                                                                                        (1, 'view', '查看', 'basic', '查看数据'),
                                                                                                        (2, 'create', '新增', 'basic', '创建数据'),
                                                                                                        (3, 'update', '修改', 'basic', '修改数据'),
                                                                                                        (4, 'delete', '删除', 'basic', '删除数据'),
                                                                                                        (5, 'export', '导出', 'basic', '导出数据'),
                                                                                                        (6, 'import', '导入', 'basic', '导入数据'),
                                                                                                        (7, 'approve', '审批', 'business', '审批业务');

-- 8. 权限表数据
INSERT INTO `permission` (`id`, `permission_code`, `permission_name`, `resource_id`, `operation_id`, `description`) VALUES
-- 系统管理权限
(1, 'system:tenant:view', '查看租户', 2, 1, '查看租户列表'),
(2, 'system:tenant:create', '创建租户', 2, 2, '创建新租户'),
(3, 'system:tenant:update', '修改租户', 2, 3, '修改租户信息'),
(4, 'system:user:view', '查看用户', 3, 1, '查看用户列表'),
(5, 'system:user:create', '创建用户', 3, 2, '创建新用户'),
(6, 'system:role:view', '查看角色', 4, 1, '查看角色列表'),
(7, 'system:role:create', '创建角色', 4, 2, '创建新角色'),
-- 客户管理权限
(8, 'customer:view', '查看客户', 17, 1, '查看客户数据'),
(9, 'customer:create', '创建客户', 17, 2, '创建客户数据'),
(10, 'customer:update', '修改客户', 17, 3, '修改客户数据'),
(11, 'customer:delete', '删除客户', 17, 4, '删除客户数据'),
(12, 'customer:export', '导出客户', 17, 5, '导出客户数据'),
-- 订单管理权限
(13, 'order:view', '查看订单', 18, 1, '查看订单数据'),
(14, 'order:create', '创建订单', 18, 2, '创建订单数据'),
(15, 'order:update', '修改订单', 18, 3, '修改订单数据'),
(16, 'order:delete', '删除订单', 18, 4, '删除订单数据'),
(17, 'order:approve', '审批订单', 18, 7, '审批订单数据');

-- 9. 角色权限关联表数据
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
-- 平台超管拥有所有权限
(1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), (5, 1, 5), (6, 1, 6), (7, 1, 7),
(8, 1, 8), (9, 1, 9), (10, 1, 10), (11, 1, 11), (12, 1, 12), (13, 1, 13),
(14, 1, 14), (15, 1, 15), (16, 1, 16), (17, 1, 17),
-- 租户A管理员权限
(18, 3, 4), (19, 3, 5), (20, 3, 6), (21, 3, 7),
(22, 3, 8), (23, 3, 9), (24, 3, 10), (25, 3, 11), (26, 3, 12),
(27, 3, 13), (28, 3, 14), (29, 3, 15), (30, 3, 16), (31, 3, 17),
-- 技术经理权限
(32, 4, 4), (33, 4, 8), (34, 4, 13),
-- 开发工程师权限
(35, 5, 8), (36, 5, 13),
-- 销售经理权限
(37, 7, 8), (38, 7, 9), (39, 7, 10), (40, 7, 12),
(41, 7, 13), (42, 7, 14), (43, 7, 15),
-- 销售员权限
(44, 8, 8), (45, 8, 9), (46, 8, 13), (47, 8, 14),
-- 人事经理权限
(48, 9, 4), (49, 9, 5);

-- 10. 数据权限规则表数据
INSERT INTO `data_permission_rule` (`id`, `role_id`, `resource_id`, `data_scope_type`, `custom_dept_ids`, `description`) VALUES
-- 平台超管:全部租户数据
(1, 1, 17, 'ALL', NULL, '所有客户数据'),
(2, 1, 18, 'ALL', NULL, '所有订单数据'),
(3, 1, 19, 'ALL', NULL, '所有用户数据'),
-- 租户A管理员:本租户全部数据
(4, 3, 17, 'ALL', NULL, '本租户所有客户'),
(5, 3, 18, 'ALL', NULL, '本租户所有订单'),
(6, 3, 19, 'ALL', NULL, '本租户所有用户'),
-- 技术经理:本部门及下级部门
(7, 4, 17, 'DEPT_AND_CHILD', NULL, '技术部及下级部门客户'),
(8, 4, 18, 'DEPT_AND_CHILD', NULL, '技术部及下级部门订单'),
(9, 4, 19, 'DEPT_AND_CHILD', NULL, '技术部及下级部门用户'),
-- 开发工程师:仅本人
(10, 5, 17, 'SELF', NULL, '仅本人创建的客户'),
(11, 5, 18, 'SELF', NULL, '仅本人创建的订单'),
-- 销售经理:本部门及下级部门
(12, 7, 17, 'DEPT_AND_CHILD', NULL, '销售部及下级部门客户'),
(13, 7, 18, 'DEPT_AND_CHILD', NULL, '销售部及下级部门订单'),
-- 销售员:仅本部门
(14, 8, 17, 'DEPT', NULL, '本部门客户'),
(15, 8, 18, 'DEPT', NULL, '本部门订单'),
-- 人事经理:自定义部门
(16, 9, 19, 'CUSTOM', '[2,3,4]', '技术部、销售部、人力资源部用户');

-- 11. 字段资源表数据
INSERT INTO `field_resource` (`id`, `entity_code`, `field_name`, `field_label`, `field_type`, `sensitive_level`, `is_custom`, `custom_field_id`, `description`) VALUES
-- 客户实体固定字段
(1, 'customer', 'customer_name', '客户名称', 'STRING', 0, 0, NULL, '客户名称'),
(2, 'customer', 'contact_phone', '联系电话', 'STRING', 1, 0, NULL, '客户联系电话'),
(3, 'customer', 'email', '邮箱', 'STRING', 1, 0, NULL, '客户邮箱'),
(4, 'customer', 'address', '地址', 'STRING', 0, 0, NULL, '客户地址'),
(5, 'customer', 'credit_amount', '授信额度', 'DECIMAL', 2, 0, NULL, '客户授信额度'),
-- 订单实体固定字段
(6, 'order', 'order_no', '订单号', 'STRING', 0, 0, NULL, '订单编号'),
(7, 'order', 'order_amount', '订单金额', 'DECIMAL', 1, 0, NULL, '订单金额'),
(8, 'order', 'order_status', '订单状态', 'STRING', 0, 0, NULL, '订单状态'),
(9, 'order', 'create_user', '创建人', 'STRING', 0, 0, NULL, '订单创建人'),
-- 用户实体固定字段
(10, 'user', 'username', '用户名', 'STRING', 0, 0, NULL, '用户名'),
(11, 'user', 'real_name', '真实姓名', 'STRING', 0, 0, NULL, '真实姓名'),
(12, 'user', 'phone', '手机号', 'STRING', 2, 0, NULL, '手机号'),
(13, 'user', 'email', '邮箱', 'STRING', 1, 0, NULL, '邮箱'),
(14, 'user', 'id_card', '身份证号', 'STRING', 2, 0, NULL, '身份证号');

-- 12. 字段权限规则表数据
INSERT INTO `field_permission_rule` (`id`, `role_id`, `field_resource_id`, `permission_type`, `mask_rule`) VALUES
-- 平台超管:所有字段可编辑
(1, 1, 1, 'EDITABLE', NULL),
(2, 1, 2, 'EDITABLE', NULL),
(3, 1, 3, 'EDITABLE', NULL),
(4, 1, 4, 'EDITABLE', NULL),
(5, 1, 5, 'EDITABLE', NULL),
(6, 1, 12, 'EDITABLE', NULL),
(7, 1, 13, 'EDITABLE', NULL),
(8, 1, 14, 'EDITABLE', NULL),
-- 租户A管理员:所有字段可编辑
(9, 3, 1, 'EDITABLE', NULL),
(10, 3, 2, 'EDITABLE', NULL),
(11, 3, 3, 'EDITABLE', NULL),
(12, 3, 5, 'EDITABLE', NULL),
(13, 3, 12, 'EDITABLE', NULL),
(14, 3, 14, 'EDITABLE', NULL),
-- 销售经理:敏感字段脱敏
(15, 7, 1, 'EDITABLE', NULL),
(16, 7, 2, 'MASKED', 'phone:3,4'),
(17, 7, 3, 'VISIBLE', NULL),
(18, 7, 5, 'VISIBLE', NULL),
-- 销售员:高敏感字段隐藏
(19, 8, 1, 'EDITABLE', NULL),
(20, 8, 2, 'MASKED', 'phone:3,4'),
(21, 8, 3, 'VISIBLE', NULL),
(22, 8, 5, 'HIDDEN', NULL),
-- 人事经理:用户敏感字段脱敏
(23, 9, 10, 'VISIBLE', NULL),
(24, 9, 11, 'EDITABLE', NULL),
(25, 9, 12, 'MASKED', 'phone:3,4'),
(26, 9, 13, 'VISIBLE', NULL),
(27, 9, 14, 'MASKED', 'idcard:6,4');

-- 13. 可授权权限表数据
INSERT INTO `grantable_permission` (`id`, `role_id`, `grantable_permission_id`, `grant_scope`) VALUES
-- 平台超管可授予所有权限
(1, 1, 1, 'ALL'), (2, 1, 2, 'ALL'), (3, 1, 3, 'ALL'), (4, 1, 4, 'ALL'),
(5, 1, 8, 'ALL'), (6, 1, 9, 'ALL'), (7, 1, 13, 'ALL'), (8, 1, 14, 'ALL'),
-- 租户A管理员可授予租户内权限
(9, 3, 4, 'ALL'), (10, 3, 5, 'ALL'), (11, 3, 6, 'ALL'), (12, 3, 7, 'ALL'),
(13, 3, 8, 'ALL'), (14, 3, 9, 'ALL'), (15, 3, 13, 'ALL'), (16, 3, 14, 'ALL'),
-- 技术经理可授予部分权限
(17, 4, 8, 'PARTIAL'), (18, 4, 13, 'PARTIAL'),
-- 销售经理可授予销售相关权限
(19, 7, 8, 'ALL'), (20, 7, 9, 'ALL'), (21, 7, 13, 'ALL'), (22, 7, 14, 'ALL');

-- 14. 可授权数据范围表数据
INSERT INTO `grantable_data_scope` (`id`, `role_id`, `resource_id`, `max_scope_type`, `allowed_dept_ids`) VALUES
-- 平台超管:可授予全部范围
(1, 1, 17, 'ALL', NULL),
(2, 1, 18, 'ALL', NULL),
(3, 1, 19, 'ALL', NULL),
-- 租户A管理员:可授予本租户全部范围
(4, 3, 17, 'ALL', NULL),
(5, 3, 18, 'ALL', NULL),
(6, 3, 19, 'ALL', NULL),
-- 技术经理:最多可授予本部门及下级
(7, 4, 17, 'DEPT_AND_CHILD', '[2,5,6]'),
(8, 4, 18, 'DEPT_AND_CHILD', '[2,5,6]'),
-- 销售经理:最多可授予本部门及下级
(9, 7, 17, 'DEPT_AND_CHILD', '[3,7,8]'),
(10, 7, 18, 'DEPT_AND_CHILD', '[3,7,8]');

-- 15. 可授权字段权限表数据
INSERT INTO `grantable_field_permission` (`id`, `role_id`, `field_resource_id`, `max_permission_type`) VALUES
-- 平台超管:可授予所有字段的可编辑权限
(1, 1, 1, 'EDITABLE'), (2, 1, 2, 'EDITABLE'), (3, 1, 3, 'EDITABLE'),
(4, 1, 5, 'EDITABLE'), (5, 1, 12, 'EDITABLE'), (6, 1, 14, 'EDITABLE'),
-- 租户A管理员:可授予所有字段的可编辑权限
(7, 3, 1, 'EDITABLE'), (8, 3, 2, 'EDITABLE'), (9, 3, 3, 'EDITABLE'),
(10, 3, 5, 'EDITABLE'), (11, 3, 12, 'EDITABLE'), (12, 3, 14, 'EDITABLE'),
-- 销售经理:敏感字段最多授予可见权限
(13, 7, 1, 'EDITABLE'), (14, 7, 2, 'VISIBLE'), (15, 7, 3, 'VISIBLE'),
(16, 7, 5, 'VISIBLE'),
-- 人事经理:高敏感字段最多授予脱敏显示
(17, 9, 10, 'VISIBLE'), (18, 9, 11, 'EDITABLE'),
(19, 9, 12, 'MASKED'), (20, 9, 14, 'MASKED');

-- 16. 授权操作日志表数据
INSERT INTO `grant_operation_log` (`id`, `operator_id`, `operator_name`, `target_role_id`, `target_role_name`, `operation_type`, `check_result`, `reject_reason`, `operation_detail`, `ip_address`) VALUES
                                                                                                                                                                                                        (1, 1, '超级管理员', 2, '平台运营管理员', 'CREATE_ROLE', 'PASS', NULL, '{"level": 10, "permissions": [1,2,3]}', '192.168.1.100'),
                                                                                                                                                                                                        (2, 2, '张三', 4, '技术部经理', 'CREATE_ROLE', 'PASS', NULL, '{"level": 200, "permissions": [4,8,13]}', '192.168.1.101'),
                                                                                                                                                                                                        (3, 2, '张三', 5, '开发工程师', 'ASSIGN_PERMISSION', 'PASS', NULL, '{"permissions": [8,13]}', '192.168.1.101'),
                                                                                                                                                                                                        (4, 3, '技术经理', 5, '开发工程师', 'ASSIGN_DATA_SCOPE', 'PASS', NULL, '{"resource_id": 17, "scope_type": "SELF"}', '192.168.1.102'),
                                                                                                                                                                                                        (5, 7, '销售经理', 8, '销售员', 'ASSIGN_FIELD_PERMISSION', 'PASS', NULL, '{"field_id": 5, "permission_type": "HIDDEN"}', '192.168.1.103'),
                                                                                                                                                                                                        (6, 8, '华北销售员', 3, '租户管理员', 'ASSIGN_PERMISSION', 'REJECT', '权限不足,无法为上级角色分配权限', '{"permissions": [8,9]}', '192.168.1.104');

-- 17. 数据访问日志表数据
INSERT INTO `data_access_log` (`id`, `user_id`, `username`, `tenant_id`, `target_tenant_id`, `resource_type`, `resource_id`, `operation`, `field_list`, `data_scope`, `ip_address`, `user_agent`, `access_time`, `response_time`) VALUES
                                                                                                                                                                                                                                      (1, 1, 'admin', 1, 2, 'customer', 1001, 'view', 'customer_name,contact_phone,email', 'ALL', '192.168.1.100', 'Mozilla/5.0', '2024-02-10 09:15:23', 45),
                                                                                                                                                                                                                                      (2, 2, 'tenant_a_admin', 2, NULL, 'customer', 1002, 'create', 'customer_name,contact_phone,address', 'ALL', '192.168.1.101', 'Mozilla/5.0', '2024-02-10 10:22:15', 120),
                                                                                                                                                                                                                                      (3, 4, 'developer_01', 2, NULL, 'order', 2001, 'view', 'order_no,order_amount,order_status', 'SELF', '192.168.1.105', 'Mozilla/5.0', '2024-02-10 11:35:42', 35),
                                                                                                                                                                                                                                      (4, 7, 'sales_manager', 2, NULL, 'customer', NULL, 'export', 'customer_name,contact_phone,address,credit_amount', 'DEPT_AND_CHILD', '192.168.1.103', 'Mozilla/5.0', '2024-02-10 14:20:18', 2500),
                                                                                                                                                                                                                                      (5, 8, 'sales_north_01', 2, NULL, 'customer', 1003, 'update', 'customer_name,contact_phone', 'DEPT', '192.168.1.104', 'Mozilla/5.0', '2024-02-10 15:45:33', 85),
                                                                                                                                                                                                                                      (6, 9, 'hr_manager', 2, NULL, 'user', 4, 'view', 'username,real_name,phone,email', 'CUSTOM', '192.168.1.106', 'Mozilla/5.0', '2024-02-10 16:12:50', 55);

-- 18. 自定义字段定义表数据
INSERT INTO `custom_field_define` (`id`, `tenant_id`, `entity_code`, `field_code`, `field_name`, `field_type`, `field_config`, `sensitive_level`, `is_required`, `default_value`, `validation_rule`, `is_searchable`, `search_priority`, `sort_order`, `status`, `create_user_id`) VALUES
-- 租户2的客户自定义字段
(1, 2, 'customer', 'industry', '所属行业', 'SELECT', '{"options": [{"label": "互联网", "value": "IT"}, {"label": "金融", "value": "FINANCE"}, {"label": "制造业", "value": "MANUFACTURING"}, {"label": "服务业", "value": "SERVICE"}]}', 0, 1, NULL, NULL, 1, 10, 1, 1, 2),
(2, 2, 'customer', 'company_scale', '公司规模', 'SELECT', '{"options": [{"label": "1-50人", "value": "SMALL"}, {"label": "51-200人", "value": "MEDIUM"}, {"label": "201-500人", "value": "LARGE"}, {"label": "500人以上", "value": "XLARGE"}]}', 0, 0, 'SMALL', NULL, 1, 8, 2, 1, 2),
(3, 2, 'customer', 'established_date', '成立日期', 'DATE', '{}', 0, 0, NULL, NULL, 1, 5, 3, 1, 2),
(4, 2, 'customer', 'bank_account', '银行账号', 'STRING', '{"maxLength": 50}', 2, 0, NULL, '^[0-9]{10,30}$', 0, 0, 4, 1, 2),
(5, 2, 'customer', 'tax_no', '税号', 'STRING', '{"maxLength": 20}', 1, 0, NULL, NULL, 1, 6, 5, 1, 2),
-- 租户2的订单自定义字段
(6, 2, 'order', 'delivery_method', '配送方式', 'SELECT', '{"options": [{"label": "快递", "value": "EXPRESS"}, {"label": "自提", "value": "PICKUP"}, {"label": "物流", "value": "LOGISTICS"}]}', 0, 1, 'EXPRESS', NULL, 1, 7, 1, 1, 2),
(7, 2, 'order', 'urgent_level', '紧急程度', 'SELECT', '{"options": [{"label": "普通", "value": "NORMAL"}, {"label": "紧急", "value": "URGENT"}, {"label": "特急", "value": "VERY_URGENT"}]}', 0, 0, 'NORMAL', NULL, 1, 9, 2, 1, 2),
(8, 2, 'order', 'remark', '备注信息', 'TEXTAREA', '{"maxLength": 500}', 0, 0, NULL, NULL, 0, 0, 3, 1, 2),
-- 租户2的用户自定义字段
(9, 2, 'user', 'employee_no', '工号', 'STRING', '{"maxLength": 20}', 0, 1, NULL, '^EMP[0-9]{6}$', 1, 10, 1, 1, 2),
(10, 2, 'user', 'entry_date', '入职日期', 'DATE', '{}', 0, 1, NULL, NULL, 1, 8, 2, 1, 2),
(11, 2, 'user', 'emergency_contact', '紧急联系人', 'STRING', '{"maxLength": 50}', 0, 0, NULL, NULL, 0, 0, 3, 1, 2),
(12, 2, 'user', 'emergency_phone', '紧急联系电话', 'STRING', '{"maxLength": 20}', 1, 0, NULL, '^1[3-9][0-9]{9}$', 0, 0, 4, 1, 2),
-- 租户3的客户自定义字段
(13, 3, 'customer', 'industry', '所属行业', 'SELECT', '{"options": [{"label": "贸易", "value": "TRADE"}, {"label": "零售", "value": "RETAIL"}, {"label": "批发", "value": "WHOLESALE"}]}', 0, 1, NULL, NULL, 1, 10, 1, 1, 10),
(14, 3, 'customer', 'region', '所属区域', 'SELECT', '{"options": [{"label": "华北", "value": "NORTH"}, {"label": "华南", "value": "SOUTH"}, {"label": "华东", "value": "EAST"}, {"label": "华西", "value": "WEST"}]}', 0, 1, NULL, NULL, 1, 9, 2, 1, 10);

-- 19. 自定义字段值表数据
INSERT INTO `custom_field_value` (`id`, `tenant_id`, `entity_code`, `entity_id`, `field_code`, `field_value`) VALUES
-- 租户2的客户自定义字段值
(1, 2, 'customer', 1001, 'industry', '{"value": "IT", "displayValue": "互联网", "updateTime": "2024-02-10 10:30:00"}'),
(2, 2, 'customer', 1001, 'company_scale', '{"value": "MEDIUM", "displayValue": "51-200人", "updateTime": "2024-02-10 10:30:00"}'),
(3, 2, 'customer', 1001, 'established_date', '{"value": "2015-06-15", "displayValue": "2015-06-15", "updateTime": "2024-02-10 10:30:00"}'),
(4, 2, 'customer', 1001, 'bank_account', '{"value": "62170000012345678901", "displayValue": "62170000012345678901", "updateTime": "2024-02-10 10:30:00"}'),
(5, 2, 'customer', 1001, 'tax_no', '{"value": "91110000MA01234567", "displayValue": "91110000MA01234567", "updateTime": "2024-02-10 10:30:00"}'),
(6, 2, 'customer', 1002, 'industry', '{"value": "FINANCE", "displayValue": "金融", "updateTime": "2024-02-10 11:15:00"}'),
(7, 2, 'customer', 1002, 'company_scale', '{"value": "LARGE", "displayValue": "201-500人", "updateTime": "2024-02-10 11:15:00"}'),
(8, 2, 'customer', 1003, 'industry', '{"value": "MANUFACTURING", "displayValue": "制造业", "updateTime": "2024-02-10 14:20:00"}'),
(9, 2, 'customer', 1003, 'company_scale', '{"value": "XLARGE", "displayValue": "500人以上", "updateTime": "2024-02-10 14:20:00"}'),
-- 租户2的订单自定义字段值
(10, 2, 'order', 2001, 'delivery_method', '{"value": "EXPRESS", "displayValue": "快递", "updateTime": "2024-02-10 11:40:00"}'),
(11, 2, 'order', 2001, 'urgent_level', '{"value": "NORMAL", "displayValue": "普通", "updateTime": "2024-02-10 11:40:00"}'),
(12, 2, 'order', 2001, 'remark', '{"value": "请在工作日送货", "displayValue": "请在工作日送货", "updateTime": "2024-02-10 11:40:00"}'),
(13, 2, 'order', 2002, 'delivery_method', '{"value": "LOGISTICS", "displayValue": "物流", "updateTime": "2024-02-10 15:20:00"}'),
(14, 2, 'order', 2002, 'urgent_level', '{"value": "URGENT", "displayValue": "紧急", "updateTime": "2024-02-10 15:20:00"}'),
-- 租户2的用户自定义字段值
(15, 2, 'user', 4, 'employee_no', '{"value": "EMP202401", "displayValue": "EMP202401", "updateTime": "2024-01-15 09:00:00"}'),
(16, 2, 'user', 4, 'entry_date', '{"value": "2024-01-15", "displayValue": "2024-01-15", "updateTime": "2024-01-15 09:00:00"}'),
(17, 2, 'user', 4, 'emergency_contact', '{"value": "张三家属", "displayValue": "张三家属", "updateTime": "2024-01-15 09:00:00"}'),
(18, 2, 'user', 4, 'emergency_phone', '{"value": "13900001111", "displayValue": "13900001111", "updateTime": "2024-01-15 09:00:00"}'),
(19, 2, 'user', 5, 'employee_no', '{"value": "EMP202402", "displayValue": "EMP202402", "updateTime": "2024-01-20 09:00:00"}'),
(20, 2, 'user', 5, 'entry_date', '{"value": "2024-01-20", "displayValue": "2024-01-20", "updateTime": "2024-01-20 09:00:00"}'),
-- 租户3的客户自定义字段值
(21, 3, 'customer', 3001, 'industry', '{"value": "TRADE", "displayValue": "贸易", "updateTime": "2024-02-11 10:00:00"}'),
(22, 3, 'customer', 3001, 'region', '{"value": "NORTH", "displayValue": "华北", "updateTime": "2024-02-11 10:00:00"}'),
(23, 3, 'customer', 3002, 'industry', '{"value": "RETAIL", "displayValue": "零售", "updateTime": "2024-02-11 11:30:00"}'),
(24, 3, 'customer', 3002, 'region', '{"value": "SOUTH", "displayValue": "华南", "updateTime": "2024-02-11 11:30:00"}');

-- 20. 自定义字段查询表数据(反范式)
INSERT INTO `custom_field_search` (`id`, `tenant_id`, `entity_code`, `entity_id`, `field_code`, `field_type`, `string_value`, `number_value`, `date_value`, `datetime_value`, `boolean_value`) VALUES
-- 租户2的客户可查询字段
(1, 2, 'customer', 1001, 'industry', 'SELECT', 'IT', NULL, NULL, NULL, NULL),
(2, 2, 'customer', 1001, 'company_scale', 'SELECT', 'MEDIUM', NULL, NULL, NULL, NULL),
(3, 2, 'customer', 1001, 'established_date', 'DATE', NULL, NULL, '2015-06-15', NULL, NULL),
(4, 2, 'customer', 1001, 'tax_no', 'STRING', '91110000MA01234567', NULL, NULL, NULL, NULL),
(5, 2, 'customer', 1002, 'industry', 'SELECT', 'FINANCE', NULL, NULL, NULL, NULL),
(6, 2, 'customer', 1002, 'company_scale', 'SELECT', 'LARGE', NULL, NULL, NULL, NULL),
(7, 2, 'customer', 1003, 'industry', 'SELECT', 'MANUFACTURING', NULL, NULL, NULL, NULL),
(8, 2, 'customer', 1003, 'company_scale', 'SELECT', 'XLARGE', NULL, NULL, NULL, NULL),
-- 租户2的订单可查询字段
(9, 2, 'order', 2001, 'delivery_method', 'SELECT', 'EXPRESS', NULL, NULL, NULL, NULL),
(10, 2, 'order', 2001, 'urgent_level', 'SELECT', 'NORMAL', NULL, NULL, NULL, NULL),
(11, 2, 'order', 2002, 'delivery_method', 'SELECT', 'LOGISTICS', NULL, NULL, NULL, NULL),
(12, 2, 'order', 2002, 'urgent_level', 'SELECT', 'URGENT', NULL, NULL, NULL, NULL),
-- 租户2的用户可查询字段
(13, 2, 'user', 4, 'employee_no', 'STRING', 'EMP202401', NULL, NULL, NULL, NULL),
(14, 2, 'user', 4, 'entry_date', 'DATE', NULL, NULL, '2024-01-15', NULL, NULL),
(15, 2, 'user', 5, 'employee_no', 'STRING', 'EMP202402', NULL, NULL, NULL, NULL),
(16, 2, 'user', 5, 'entry_date', 'DATE', NULL, NULL, '2024-01-20', NULL, NULL),
-- 租户3的客户可查询字段
(17, 3, 'customer', 3001, 'industry', 'SELECT', 'TRADE', NULL, NULL, NULL, NULL),
(18, 3, 'customer', 3001, 'region', 'SELECT', 'NORTH', NULL, NULL, NULL, NULL),
(19, 3, 'customer', 3002, 'industry', 'SELECT', 'RETAIL', NULL, NULL, NULL, NULL),
(20, 3, 'customer', 3002, 'region', 'SELECT', 'SOUTH', NULL, NULL, NULL, NULL);

-- 注册自定义字段到字段资源表(用于权限控制)
INSERT INTO `field_resource` (`id`, `entity_code`, `field_name`, `field_label`, `field_type`, `sensitive_level`, `is_custom`, `custom_field_id`, `description`) VALUES
                                                                                                                                                                    (15, 'customer', 'industry', '所属行业', 'SELECT', 0, 1, 1, '客户所属行业'),
                                                                                                                                                                    (16, 'customer', 'company_scale', '公司规模', 'SELECT', 0, 1, 2, '客户公司规模'),
                                                                                                                                                                    (17, 'customer', 'established_date', '成立日期', 'DATE', 0, 1, 3, '公司成立日期'),
                                                                                                                                                                    (18, 'customer', 'bank_account', '银行账号', 'STRING', 2, 1, 4, '银行账号'),
                                                                                                                                                                    (19, 'customer', 'tax_no', '税号', 'STRING', 1, 1, 5, '税务登记号'),
                                                                                                                                                                    (20, 'order', 'delivery_method', '配送方式', 'SELECT', 0, 1, 6, '订单配送方式'),
                                                                                                                                                                    (21, 'order', 'urgent_level', '紧急程度', 'SELECT', 0, 1, 7, '订单紧急程度'),
                                                                                                                                                                    (22, 'order', 'remark', '备注信息', 'TEXTAREA', 0, 1, 8, '订单备注'),
                                                                                                                                                                    (23, 'user', 'employee_no', '工号', 'STRING', 0, 1, 9, '员工工号'),
                                                                                                                                                                    (24, 'user', 'entry_date', '入职日期', 'DATE', 0, 1, 10, '员工入职日期'),
                                                                                                                                                                    (25, 'user', 'emergency_contact', '紧急联系人', 'STRING', 0, 1, 11, '紧急联系人'),
                                                                                                                                                                    (26, 'user', 'emergency_phone', '紧急联系电话', 'STRING', 1, 1, 12, '紧急联系电话');

-- 为自定义字段配置权限规则
INSERT INTO `field_permission_rule` (`id`, `role_id`, `field_resource_id`, `permission_type`, `mask_rule`) VALUES
-- 租户A管理员对自定义字段的权限
(28, 3, 15, 'EDITABLE', NULL),
(29, 3, 16, 'EDITABLE', NULL),
(30, 3, 17, 'EDITABLE', NULL),
(31, 3, 18, 'EDITABLE', NULL),
(32, 3, 19, 'EDITABLE', NULL),
-- 销售经理对客户自定义字段的权限
(33, 7, 15, 'EDITABLE', NULL),
(34, 7, 16, 'VISIBLE', NULL),
(35, 7, 17, 'VISIBLE', NULL),
(36, 7, 18, 'HIDDEN', NULL),
(37, 7, 19, 'VISIBLE', NULL),
-- 销售员对客户自定义字段的权限
(38, 8, 15, 'EDITABLE', NULL),
(39, 8, 16, 'VISIBLE', NULL),
(40, 8, 17, 'VISIBLE', NULL),
(41, 8, 18, 'HIDDEN', NULL),
(42, 8, 19, 'HIDDEN', NULL),
-- 人事经理对用户自定义字段的权限
(43, 9, 23, 'EDITABLE', NULL),
(44, 9, 24, 'EDITABLE', NULL),
(45, 9, 25, 'EDITABLE', NULL),
(46, 9, 26, 'MASKED', 'phone:3,4');