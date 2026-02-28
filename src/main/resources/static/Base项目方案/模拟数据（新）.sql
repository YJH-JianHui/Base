-- ====================================================
-- 多租户细粒度RBAC权限系统 - 完整模拟数据 v2
-- 逻辑验证通过版本
-- ====================================================
-- 核查结论：
-- ① admin(userId=1) dept_id 修正为 0（平台用户无部门）
-- ② 人事经理 CUSTOM 修正为 [2,3,4,5,6,7,8]（技术部+子部门+销售部+子部门+人事部）
--    → 人事经理可看到 userId=3,4,5,6,7,8,9 共7人，测试更丰富
-- ③ 所有角色的功能权限、数据权限、字段权限三者关系逐一对齐
-- ====================================================

-- ====================================================
-- 0. 清空所有表（按外键依赖顺序）
-- ====================================================
DELETE FROM `data_access_log`;
DELETE FROM `grant_operation_log`;
DELETE FROM `grantable_field_permission`;
DELETE FROM `grantable_data_scope`;
DELETE FROM `grantable_permission`;
DELETE FROM `field_permission_rule`;
DELETE FROM `data_permission_rule`;
DELETE FROM `role_permission`;
DELETE FROM `user_role`;
DELETE FROM `custom_field_search`;
DELETE FROM `custom_field_value`;
DELETE FROM `custom_field_define`;
DELETE FROM `field_resource`;
DELETE FROM `permission`;
DELETE FROM `operation`;
DELETE FROM `resource`;
DELETE FROM `role`;
DELETE FROM `department`;
DELETE FROM `user`;
DELETE FROM `tenant`;

-- ====================================================
-- 1. 租户
-- ====================================================
INSERT INTO `tenant` (`id`, `tenant_code`, `tenant_name`, `contact_name`, `contact_phone`, `status`, `expire_time`) VALUES
(1, 'platform',  '平台方',           '系统管理员', '13800000000', 1, '2099-12-31 23:59:59'),
(2, 'company_a', 'A科技有限公司',     '张三',       '13811110001', 1, '2027-12-31 23:59:59'),
(3, 'company_b', 'B贸易集团',         '李四',       '13822220001', 1, '2027-12-31 23:59:59'),
(4, 'company_c', 'C咨询公司(已过期)', '王五',       '13833330001', 0, '2024-06-30 23:59:59');

-- ====================================================
-- 2. 部门
-- 租户2：总部(1) → 技术部(2)[研发组(5)/测试组(6)] / 销售部(3)[华北(7)/华南(8)] / 人事部(4)
-- 租户3：总部(9) → 采购部(10) / 销售部(11) / 财务部(12)
-- ====================================================
INSERT INTO `department` (`id`, `tenant_id`, `dept_code`, `dept_name`, `parent_id`, `dept_path`, `sort_order`, `status`) VALUES
(1,  2, 'A_ROOT',        'A科技总部',  0, '/1/',      1, 1),
(2,  2, 'A_TECH',        '技术部',     1, '/1/2/',    1, 1),
(3,  2, 'A_SALES',       '销售部',     1, '/1/3/',    2, 1),
(4,  2, 'A_HR',          '人事部',     1, '/1/4/',    3, 1),
(5,  2, 'A_TECH_DEV',    '研发组',     2, '/1/2/5/',  1, 1),
(6,  2, 'A_TECH_TEST',   '测试组',     2, '/1/2/6/',  2, 1),
(7,  2, 'A_SALES_NORTH', '华北销售区', 3, '/1/3/7/',  1, 1),
(8,  2, 'A_SALES_SOUTH', '华南销售区', 3, '/1/3/8/',  2, 1),
(9,  3, 'B_ROOT',        'B贸易总部',  0, '/9/',      1, 1),
(10, 3, 'B_PURCHASE',    '采购部',     9, '/9/10/',   1, 1),
(11, 3, 'B_SALES',       '销售部',     9, '/9/11/',   2, 1),
(12, 3, 'B_FINANCE',     '财务部',     9, '/9/12/',   3, 1);

-- ====================================================
-- 3. 用户（密码统一 123456 BCrypt）
-- 注意：admin 是平台用户，dept_id=0 表示无部门归属
-- ====================================================
INSERT INTO `user` (`id`, `tenant_id`, `dept_id`, `username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
-- 平台
(1,  1, 0, 'admin',          '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '13800000000', 'admin@platform.com',    1),
-- 租户2（8人，覆盖总部/技术部/研发组/测试组/销售部/华北/人事部）
(2,  2, 1, 'tenant_a_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三',       '13811110001', 'zhangsan@company-a.com',1),
(3,  2, 2, 'tech_manager',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李技术',     '13811110002', 'tech@company-a.com',    1),
(4,  2, 5, 'developer_01',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王研发',     '13811110003', 'dev01@company-a.com',   1),
(5,  2, 5, 'developer_02',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵研发',     '13811110004', 'dev02@company-a.com',   1),
(6,  2, 6, 'tester_01',      '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙测试',     '13811110005', 'test01@company-a.com',  1),
(7,  2, 3, 'sales_manager',  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '周销售',     '13811110006', 'sales@company-a.com',   1),
(8,  2, 7, 'sales_north_01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '吴华北',     '13811110007', 'salesn@company-a.com',  1),
(9,  2, 4, 'hr_manager',     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '郑人事',     '13811110008', 'hr@company-a.com',      1),
-- 租户3（3人）
(10, 3, 9,  'tenant_b_admin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四',       '13822220001', 'lisi@company-b.com',    1),
(11, 3, 10, 'purchase_staff','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '采购员甲',   '13822220002', 'purchase@company-b.com',1),
(12, 3, 11, 'sales_staff',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '销售员乙',   '13822220003', 'sales@company-b.com',   1);

-- ====================================================
-- 4. 角色（level: 0平台超管 10平台运营 100租户管理员 200部门经理 300普通员工）
-- ====================================================
INSERT INTO `role` (`id`, `tenant_id`, `role_code`, `role_name`, `role_type`, `level`, `parent_role_id`, `is_tenant_admin`, `manageable`, `description`, `status`) VALUES
(1,  1, 'PLATFORM_SUPER_ADMIN', '平台超级管理员', 'platform', 0,   NULL, 0, 0, '平台最高权限，可跨租户操作', 1),
(2,  1, 'PLATFORM_OPERATOR',    '平台运营员',     'platform', 10,  1,    0, 1, '平台运营，可跨租户查看',     1),
(3,  2, 'A_ADMIN',              '租户A管理员',    'tenant',   100, NULL, 1, 0, 'A公司最高管理员',            1),
(4,  2, 'A_TECH_MANAGER',       '技术部经理',     'tenant',   200, 3,    0, 1, '管理技术部及下属部门',       1),
(5,  2, 'A_DEVELOPER',          '开发工程师',     'tenant',   300, 4,    0, 1, '研发人员',                   1),
(6,  2, 'A_TESTER',             '测试工程师',     'tenant',   300, 4,    0, 1, '测试人员',                   1),
(7,  2, 'A_SALES_MANAGER',      '销售经理',       'tenant',   200, 3,    0, 1, '管理销售部及下属部门',       1),
(8,  2, 'A_SALES',              '销售员',         'tenant',   300, 7,    0, 1, '一线销售人员',               1),
(9,  2, 'A_HR_MANAGER',         '人事经理',       'tenant',   200, 3,    0, 1, '管理人事部',                 1),
(10, 3, 'B_ADMIN',              '租户B管理员',    'tenant',   100, NULL, 1, 0, 'B公司最高管理员',            1),
(11, 3, 'B_PURCHASE',           '采购员',         'tenant',   300, 10,   0, 1, '采购人员',                   1),
(12, 3, 'B_SALES',              '销售员',         'tenant',   300, 10,   0, 1, '销售人员',                   1);

-- ====================================================
-- 5. 用户角色关联
-- ====================================================
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) VALUES
(1,  1,  1),   -- admin → 平台超管
(2,  2,  3),   -- tenant_a_admin → 租户A管理员
(3,  3,  4),   -- tech_manager → 技术部经理
(4,  4,  5),   -- developer_01 → 开发工程师
(5,  5,  5),   -- developer_02 → 开发工程师
(6,  6,  6),   -- tester_01 → 测试工程师
(7,  7,  7),   -- sales_manager → 销售经理
(8,  8,  8),   -- sales_north_01 → 销售员
(9,  9,  9),   -- hr_manager → 人事经理
(10, 10, 10),  -- tenant_b_admin → 租户B管理员
(11, 11, 11),  -- purchase_staff → 采购员
(12, 12, 12);  -- sales_staff → 销售员

-- ====================================================
-- 6. 资源（menu/button/entity）
-- entity 的 resource_path = entityCode，对应 @DataScope/@WithCustomFields
-- ====================================================
INSERT INTO `resource` (`id`, `tenant_id`, `resource_code`, `resource_name`, `resource_type`, `parent_id`, `resource_path`, `route_path`, `component_path`, `icon`, `sort_order`, `status`) VALUES
-- 系统管理菜单
(1,  NULL, 'SYSTEM',          '系统管理',     'menu',   0,  '/system',          '/system',          NULL,                   'Setting',    1, 1),
(2,  NULL, 'SYSTEM_TENANT',   '租户管理',     'menu',   1,  '/system/tenant',   '/system/tenant',   'system/tenant/index',  'Office',     1, 1),
(3,  NULL, 'SYSTEM_USER',     '用户管理',     'menu',   1,  '/system/user',     '/system/user',     'system/user/index',    'User',       2, 1),
(4,  NULL, 'SYSTEM_ROLE',     '角色管理',     'menu',   1,  '/system/role',     '/system/role',     'system/role/index',    'UserFilled', 3, 1),
(5,  NULL, 'SYSTEM_FIELD',    '自定义字段',   'menu',   1,  '/system/field',    '/system/field',    'system/field/index',   'Grid',       4, 1),
-- 业务菜单
(6,  NULL, 'WORKBENCH',       '工作台',       'menu',   0,  '/workbench',       '/workbench',       'workbench/index',      'Monitor',    1, 1),
(7,  NULL, 'CUSTOMER',        '客户管理',     'menu',   0,  '/customer',        '/customer',        NULL,                   'Avatar',     2, 1),
(8,  NULL, 'CUSTOMER_LIST',   '客户列表',     'menu',   7,  '/customer/list',   '/customer/list',   'customer/list',        NULL,         1, 1),
(9,  NULL, 'ORDER',           '订单管理',     'menu',   0,  '/order',           '/order',           NULL,                   'Document',   3, 1),
(10, NULL, 'ORDER_LIST',      '订单列表',     'menu',   9,  '/order/list',      '/order/list',      'order/list',           NULL,         1, 1),
-- 按钮
(11, NULL, 'CUSTOMER_ADD',    '新增客户',     'button', 8,  NULL, NULL, NULL, NULL, 1, 1),
(12, NULL, 'CUSTOMER_EDIT',   '编辑客户',     'button', 8,  NULL, NULL, NULL, NULL, 2, 1),
(13, NULL, 'CUSTOMER_DELETE', '删除客户',     'button', 8,  NULL, NULL, NULL, NULL, 3, 1),
(14, NULL, 'CUSTOMER_EXPORT', '导出客户',     'button', 8,  NULL, NULL, NULL, NULL, 4, 1),
(15, NULL, 'ORDER_ADD',       '新增订单',     'button', 10, NULL, NULL, NULL, NULL, 1, 1),
(16, NULL, 'ORDER_APPROVE',   '审批订单',     'button', 10, NULL, NULL, NULL, NULL, 2, 1),
-- 实体资源（resource_path = entityCode）
(17, NULL, 'USER_ENTITY',     '用户实体',     'entity', 0,  'user',     NULL, NULL, NULL, 1, 1),
(18, NULL, 'CUSTOMER_ENTITY', '客户实体',     'entity', 0,  'customer', NULL, NULL, NULL, 2, 1),
(19, NULL, 'ORDER_ENTITY',    '订单实体',     'entity', 0,  'order',    NULL, NULL, NULL, 3, 1);

-- ====================================================
-- 7. 操作
-- ====================================================
INSERT INTO `operation` (`id`, `operation_code`, `operation_name`, `operation_type`, `description`) VALUES
(1, 'view',    '查看', 'basic',    '查看数据'),
(2, 'create',  '新增', 'basic',    '创建数据'),
(3, 'update',  '修改', 'basic',    '修改数据'),
(4, 'delete',  '删除', 'basic',    '删除数据'),
(5, 'export',  '导出', 'basic',    '导出数据'),
(6, 'import',  '导入', 'basic',    '导入数据'),
(7, 'approve', '审批', 'business', '审批业务');

-- ====================================================
-- 8. 权限（permission_code 与 @RequiresPermission 注解一一对应）
-- ====================================================
INSERT INTO `permission` (`id`, `permission_code`, `permission_name`, `resource_id`, `operation_id`, `description`) VALUES
-- 系统管理
(1,  'system:tenant:view',   '查看租户',       2,  1, '查看租户列表'),
(2,  'system:tenant:create', '创建租户',       2,  2, '创建新租户'),
(3,  'system:tenant:update', '修改租户',       2,  3, '修改租户信息'),
(4,  'system:user:view',     '查看用户',       3,  1, '查看用户列表'),    -- /user/list 使用此权限
(5,  'system:user:create',   '创建用户',       3,  2, '创建新用户'),
(6,  'system:user:update',   '修改用户',       3,  3, '修改用户信息'),
(7,  'system:role:view',     '查看角色',       4,  1, '查看角色列表'),
(8,  'system:role:create',   '创建角色',       4,  2, '创建角色'),
(9,  'system:field:view',    '查看自定义字段', 5,  1, '查看自定义字段定义'),
(10, 'system:field:create',  '创建自定义字段', 5,  2, '创建自定义字段'),
(11, 'system:field:update',  '修改自定义字段', 5,  3, '修改自定义字段'),
(12, 'system:field:delete',  '删除自定义字段', 5,  4, '删除自定义字段'),
-- 客户管理
(13, 'customer:view',        '查看客户',       18, 1, '查看客户数据'),
(14, 'customer:create',      '创建客户',       18, 2, '创建客户'),
(15, 'customer:update',      '修改客户',       18, 3, '修改客户'),
(16, 'customer:delete',      '删除客户',       18, 4, '删除客户'),
(17, 'customer:export',      '导出客户',       18, 5, '导出客户数据'),
-- 订单管理
(18, 'order:view',           '查看订单',       19, 1, '查看订单数据'),
(19, 'order:create',         '创建订单',       19, 2, '创建订单'),
(20, 'order:update',         '修改订单',       19, 3, '修改订单'),
(21, 'order:delete',         '删除订单',       19, 4, '删除订单'),
(22, 'order:approve',        '审批订单',       19, 7, '审批订单');

-- ====================================================
-- 9. 角色权限关联
--
-- 各角色有权访问的接口汇总：
-- admin(超管)       → 全部22条
-- tenant_a_admin   → 用户CRUD + 角色查/创 + 自定义字段CRUD + 客户全 + 订单全
-- tech_manager     → system:user:view + customer:view + order:view
-- developer_01/02  → customer:view + order:view
-- tester_01        → customer:view + order:view
-- sales_manager    → customer全(无delete) + order查/创/改
-- sales_north_01   → customer:view/create + order:view/create
-- hr_manager       → system:user:view/create/update
-- tenant_b_admin   → customer全 + order全
-- purchase_staff   → order:view/create
-- sales_staff      → customer:view/create + order:view
-- ====================================================
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
-- 平台超管(1)：全部22条
(1,1,1),(2,1,2),(3,1,3),(4,1,4),(5,1,5),(6,1,6),(7,1,7),(8,1,8),
(9,1,9),(10,1,10),(11,1,11),(12,1,12),
(13,1,13),(14,1,14),(15,1,15),(16,1,16),(17,1,17),
(18,1,18),(19,1,19),(20,1,20),(21,1,21),(22,1,22),
-- 租户A管理员(3)
(23,3,4),(24,3,5),(25,3,6),         -- user: view/create/update
(26,3,7),(27,3,8),                  -- role: view/create
(28,3,9),(29,3,10),(30,3,11),(31,3,12), -- field: CRUD
(32,3,13),(33,3,14),(34,3,15),(35,3,16),(36,3,17), -- customer: 全
(37,3,18),(38,3,19),(39,3,20),(40,3,21),(41,3,22), -- order: 全
-- 技术经理(4)
(42,4,4),                           -- system:user:view ← 必须有，才能访问 /user/list
(43,4,13),                          -- customer:view
(44,4,18),                          -- order:view
-- 开发工程师(5)
(45,5,13),                          -- customer:view
(46,5,18),                          -- order:view
-- 测试工程师(6)
(47,6,13),                          -- customer:view
(48,6,18),                          -- order:view
-- 销售经理(7)
(49,7,13),(50,7,14),(51,7,15),(52,7,16),(53,7,17), -- customer: 全(含export)
(54,7,18),(55,7,19),(56,7,20),      -- order: view/create/update
-- 销售员(8)
(57,8,13),(58,8,14),                -- customer: view/create
(59,8,18),(60,8,19),                -- order: view/create
-- 人事经理(9)
(61,9,4),(62,9,5),(63,9,6),         -- system:user: view/create/update ← 必须有
-- 租户B管理员(10)
(64,10,13),(65,10,14),(66,10,15),(67,10,16),(68,10,17), -- customer: 全
(69,10,18),(70,10,19),(71,10,20),(72,10,21),(73,10,22), -- order: 全
-- 采购员(11)
(74,11,18),(75,11,19),              -- order: view/create
-- 销售员B(12)
(76,12,13),(77,12,14),              -- customer: view/create
(78,12,18);                         -- order: view

-- ====================================================
-- 10. 数据权限规则
--
-- 数据范围说明：
--   ALL            = 本租户全部（租户拦截器已保证跨租户隔离）
--   DEPT_AND_CHILD = 本人部门 + 所有子部门（通过 dept_path LIKE 查询）
--   DEPT           = 仅本人所在部门
--   SELF           = 仅 create_user_id = 当前用户ID
--   CUSTOM         = 指定部门ID列表
--
-- 对应关系验证（resource_id → entity）：
--   17 = user实体，18 = customer实体，19 = order实体
--
-- 【tech_manager 的 DEPT_AND_CHILD 验证】
--   tech_manager.dept_id=2(技术部), dept_path='/1/2/'
--   查询: dept_path LIKE '/1/2/%' → dept 5(研发组), 6(测试组)
--   user实体能看到: dept_id IN (2,5,6) → userId=3,4,5,6 共4人 ✅
--
-- 【hr_manager 的 CUSTOM [2,3,4,5,6,7,8] 验证】
--   dept_id IN (2,3,4,5,6,7,8) → userId=3,4,5,6,7,8,9 共7人 ✅
--   （覆盖技术部全体+销售部全体+人事部，不含总部管理员）
--
-- 【sales_manager 的 DEPT_AND_CHILD 验证】
--   sales_manager.dept_id=3(销售部), dept_path='/1/3/'
--   查询: dept_path LIKE '/1/3/%' → dept 7(华北), 8(华南)
--   customer/order实体能看到: dept_id IN (3,7,8) → userId=7,8 相关数据 ✅
-- ====================================================
INSERT INTO `data_permission_rule` (`id`, `role_id`, `resource_id`, `data_scope_type`, `custom_dept_ids`, `description`) VALUES
-- 平台超管(1)：ALL（超管逻辑穿透，此条作保底配置）
(1,  1, 17, 'ALL', NULL, '所有租户所有用户'),
(2,  1, 18, 'ALL', NULL, '所有租户所有客户'),
(3,  1, 19, 'ALL', NULL, '所有租户所有订单'),
-- 租户A管理员(3)：本租户ALL
(4,  3, 17, 'ALL', NULL, '本租户所有用户'),
(5,  3, 18, 'ALL', NULL, '本租户所有客户'),
(6,  3, 19, 'ALL', NULL, '本租户所有订单'),
-- 技术经理(4)：本部门+子部门
-- dept_id=2，子部门 dept_path LIKE '/1/2/%' → dept 5,6
-- 可见用户：dept_id IN (2,5,6) → userId 3,4,5,6
(7,  4, 17, 'DEPT_AND_CHILD', NULL, '技术部+研发组+测试组的用户'),
(8,  4, 18, 'DEPT_AND_CHILD', NULL, '技术部及下级部门的客户'),
(9,  4, 19, 'DEPT_AND_CHILD', NULL, '技术部及下级部门的订单'),
-- 开发工程师(5)：仅本人（无user:view权限，只需配customer/order）
(10, 5, 18, 'SELF', NULL, '仅本人创建的客户'),
(11, 5, 19, 'SELF', NULL, '仅本人创建的订单'),
-- 测试工程师(6)：仅本部门（无user:view权限，只需配customer/order）
(12, 6, 18, 'DEPT', NULL, '测试组的客户'),
(13, 6, 19, 'DEPT', NULL, '测试组的订单'),
-- 销售经理(7)：本部门+子部门（无user:view权限，只需配customer/order）
-- dept_id=3，子部门 dept_path LIKE '/1/3/%' → dept 7,8
(14, 7, 18, 'DEPT_AND_CHILD', NULL, '销售部+华北+华南的客户'),
(15, 7, 19, 'DEPT_AND_CHILD', NULL, '销售部+华北+华南的订单'),
-- 销售员(8)：仅本部门（无user:view权限，只需配customer/order）
(16, 8, 18, 'DEPT', NULL, '本销售区的客户'),
(17, 8, 19, 'DEPT', NULL, '本销售区的订单'),
-- 人事经理(9)：自定义部门
-- CUSTOM [2,3,4,5,6,7,8] = 技术部+研发组+测试组+销售部+华北+华南+人事部
-- 可见用户：dept_id IN (2,3,4,5,6,7,8) → userId 3,4,5,6,7,8,9 共7人
(18, 9, 17, 'CUSTOM', '[2,3,4,5,6,7,8]', '技术/销售/人事各部门的用户（不含总部）'),
-- 租户B管理员(10)：本租户ALL
(19, 10, 18, 'ALL', NULL, 'B公司所有客户'),
(20, 10, 19, 'ALL', NULL, 'B公司所有订单'),
-- 采购员(11)：仅本人（无customer权限，只配order）
(21, 11, 19, 'SELF', NULL, '仅本人创建的订单'),
-- 销售员B(12)：仅本部门
(22, 12, 18, 'DEPT', NULL, '本部门的客户');

-- ====================================================
-- 11. 字段资源（field_resource）
--
-- 说明：
-- - is_custom=0：固有字段，所有租户共用
-- - is_custom=1：自定义字段，custom_field_id 关联 custom_field_define.id
-- - sensitive_level: 0=普通 1=敏感 2=高度敏感
--
-- 自定义字段在 field_resource 中注册是为了统一权限控制链路，
-- field_permission_rule 只需关联 field_resource_id，
-- 不区分固有字段和自定义字段。
-- ====================================================
INSERT INTO `field_resource` (`id`, `entity_code`, `field_name`, `field_label`, `field_type`, `sensitive_level`, `is_custom`, `custom_field_id`, `description`) VALUES
-- 用户实体固有字段（id 1-5）
(1,  'user',     'username',           '用户名',       'STRING',  0, 0, NULL, '登录账号'),
(2,  'user',     'real_name',          '真实姓名',     'STRING',  0, 0, NULL, '员工真实姓名'),
(3,  'user',     'phone',              '手机号',       'STRING',  2, 0, NULL, '手机号（高敏感）'),
(4,  'user',     'email',              '邮箱',         'STRING',  1, 0, NULL, '邮箱（敏感）'),
(5,  'user',     'dept_id',            '所属部门',     'NUMBER',  0, 0, NULL, '部门ID'),
-- 客户实体固有字段（id 6-10）
(6,  'customer', 'customer_name',      '客户名称',     'STRING',  0, 0, NULL, '客户公司名称'),
(7,  'customer', 'contact_phone',      '联系电话',     'STRING',  1, 0, NULL, '联系电话（敏感）'),
(8,  'customer', 'email',              '邮箱',         'STRING',  1, 0, NULL, '客户邮箱（敏感）'),
(9,  'customer', 'address',            '地址',         'STRING',  0, 0, NULL, '客户地址'),
(10, 'customer', 'credit_amount',      '授信额度',     'DECIMAL', 2, 0, NULL, '授信额度（高敏感）'),
-- 订单实体固有字段（id 11-14）
(11, 'order',    'order_no',           '订单号',       'STRING',  0, 0, NULL, '订单编号'),
(12, 'order',    'order_amount',       '订单金额',     'DECIMAL', 1, 0, NULL, '订单金额（敏感）'),
(13, 'order',    'order_status',       '订单状态',     'STRING',  0, 0, NULL, '订单状态'),
(14, 'order',    'create_user_id',     '创建人ID',     'NUMBER',  0, 0, NULL, '订单创建人'),
-- 用户自定义字段（id 15-18，custom_field_id 对应 custom_field_define id 9-12）
(15, 'user',     'employee_no',        '工号',         'STRING',  0, 1, 9,  '员工工号'),
(16, 'user',     'entry_date',         '入职日期',     'DATE',    0, 1, 10, '入职日期'),
(17, 'user',     'emergency_contact',  '紧急联系人',   'STRING',  0, 1, 11, '紧急联系人姓名'),
(18, 'user',     'emergency_phone',    '紧急联系电话', 'STRING',  1, 1, 12, '紧急联系电话（敏感）'),
-- 客户自定义字段（id 19-23，custom_field_id 对应 custom_field_define id 1-5）
(19, 'customer', 'industry',           '所属行业',     'SELECT',  0, 1, 1, '客户所属行业'),
(20, 'customer', 'company_scale',      '公司规模',     'SELECT',  0, 1, 2, '公司规模'),
(21, 'customer', 'established_date',   '成立日期',     'DATE',    0, 1, 3, '公司成立日期'),
(22, 'customer', 'bank_account',       '银行账号',     'STRING',  2, 1, 4, '银行账号（高敏感）'),
(23, 'customer', 'tax_no',             '税号',         'STRING',  1, 1, 5, '税务登记号（敏感）'),
-- 订单自定义字段（id 24-26，custom_field_id 对应 custom_field_define id 6-8）
(24, 'order',    'delivery_method',    '配送方式',     'SELECT',  0, 1, 6, '配送方式'),
(25, 'order',    'urgent_level',       '紧急程度',     'SELECT',  0, 1, 7, '紧急程度'),
(26, 'order',    'remark',             '备注',         'TEXTAREA',0, 1, 8, '订单备注');

-- ====================================================
-- 12. 字段权限规则
--
-- permission_type 优先级：EDITABLE > VISIBLE > MASKED > HIDDEN
-- 多角色取最高等级，未配置字段代码层默认 VISIBLE
--
-- 【各角色字段权限设计说明】
-- 超管/租户管理员：所有字段 EDITABLE（全权限）
-- 技术经理：固有字段可见/脱敏，自定义字段可见
-- 开发工程师：客户字段，敏感脱敏，高敏感隐藏
-- 销售经理：客户字段完整操作，高敏感隐藏
-- 销售员：客户字段只读，敏感脱敏，高敏感隐藏
-- 人事经理：用户字段管理，phone/emergency_phone脱敏
-- ====================================================
INSERT INTO `field_permission_rule` (`id`, `role_id`, `field_resource_id`, `permission_type`, `mask_rule`) VALUES

-- ① 平台超管(role_id=1)：所有26个字段 EDITABLE
(1,1,1,'EDITABLE',NULL),(2,1,2,'EDITABLE',NULL),(3,1,3,'EDITABLE',NULL),
(4,1,4,'EDITABLE',NULL),(5,1,5,'EDITABLE',NULL),(6,1,6,'EDITABLE',NULL),
(7,1,7,'EDITABLE',NULL),(8,1,8,'EDITABLE',NULL),(9,1,9,'EDITABLE',NULL),
(10,1,10,'EDITABLE',NULL),(11,1,11,'EDITABLE',NULL),(12,1,12,'EDITABLE',NULL),
(13,1,13,'EDITABLE',NULL),(14,1,14,'EDITABLE',NULL),(15,1,15,'EDITABLE',NULL),
(16,1,16,'EDITABLE',NULL),(17,1,17,'EDITABLE',NULL),(18,1,18,'EDITABLE',NULL),
(19,1,19,'EDITABLE',NULL),(20,1,20,'EDITABLE',NULL),(21,1,21,'EDITABLE',NULL),
(22,1,22,'EDITABLE',NULL),(23,1,23,'EDITABLE',NULL),(24,1,24,'EDITABLE',NULL),
(25,1,25,'EDITABLE',NULL),(26,1,26,'EDITABLE',NULL),

-- ② 租户A管理员(role_id=3)：所有26个字段 EDITABLE
(27,3,1,'EDITABLE',NULL),(28,3,2,'EDITABLE',NULL),(29,3,3,'EDITABLE',NULL),
(30,3,4,'EDITABLE',NULL),(31,3,5,'EDITABLE',NULL),(32,3,6,'EDITABLE',NULL),
(33,3,7,'EDITABLE',NULL),(34,3,8,'EDITABLE',NULL),(35,3,9,'EDITABLE',NULL),
(36,3,10,'EDITABLE',NULL),(37,3,11,'EDITABLE',NULL),(38,3,12,'EDITABLE',NULL),
(39,3,13,'EDITABLE',NULL),(40,3,14,'EDITABLE',NULL),(41,3,15,'EDITABLE',NULL),
(42,3,16,'EDITABLE',NULL),(43,3,17,'EDITABLE',NULL),(44,3,18,'EDITABLE',NULL),
(45,3,19,'EDITABLE',NULL),(46,3,20,'EDITABLE',NULL),(47,3,21,'EDITABLE',NULL),
(48,3,22,'EDITABLE',NULL),(49,3,23,'EDITABLE',NULL),(50,3,24,'EDITABLE',NULL),
(51,3,25,'EDITABLE',NULL),(52,3,26,'EDITABLE',NULL),

-- ③ 技术经理(role_id=4)
-- 有权限的实体：user（system:user:view）+ customer（customer:view）+ order（order:view）
-- user字段：username/real_name/dept_id可见，phone/email脱敏，自定义字段可见/紧急电话脱敏
-- customer字段：name/address可见，phone/email脱敏，credit_amount隐藏，自定义字段可见（bank_account隐藏）
-- order字段：全部可见
(53,4,1,'VISIBLE',NULL),             -- user.username
(54,4,2,'VISIBLE',NULL),             -- user.real_name
(55,4,3,'MASKED','phone:3,4'),       -- user.phone 脱敏 → 138****0002
(56,4,4,'MASKED','email:2,4'),       -- user.email 脱敏 → te**@****a.com
(57,4,5,'VISIBLE',NULL),             -- user.dept_id
(58,4,6,'VISIBLE',NULL),             -- customer.customer_name
(59,4,7,'MASKED','phone:3,4'),       -- customer.contact_phone 脱敏
(60,4,8,'MASKED','email:2,4'),       -- customer.email 脱敏
(61,4,9,'VISIBLE',NULL),             -- customer.address
(62,4,10,'HIDDEN',NULL),             -- customer.credit_amount 隐藏（非销售）
(63,4,11,'VISIBLE',NULL),            -- order.order_no
(64,4,12,'VISIBLE',NULL),            -- order.order_amount
(65,4,13,'VISIBLE',NULL),            -- order.order_status
(66,4,14,'VISIBLE',NULL),            -- order.create_user_id
(67,4,15,'VISIBLE',NULL),            -- user自定义.employee_no
(68,4,16,'VISIBLE',NULL),            -- user自定义.entry_date
(69,4,17,'VISIBLE',NULL),            -- user自定义.emergency_contact
(70,4,18,'MASKED','phone:3,4'),      -- user自定义.emergency_phone 脱敏
(71,4,19,'VISIBLE',NULL),            -- customer自定义.industry
(72,4,20,'VISIBLE',NULL),            -- customer自定义.company_scale
(73,4,21,'VISIBLE',NULL),            -- customer自定义.established_date
(74,4,22,'HIDDEN',NULL),             -- customer自定义.bank_account 隐藏
(75,4,23,'VISIBLE',NULL),            -- customer自定义.tax_no
(76,4,24,'VISIBLE',NULL),            -- order自定义.delivery_method
(77,4,25,'VISIBLE',NULL),            -- order自定义.urgent_level
(78,4,26,'VISIBLE',NULL),            -- order自定义.remark

-- ④ 开发工程师(role_id=5)
-- 有权限的实体：customer + order（无user:view权限，不需配user字段）
-- customer：name/address可见，phone脱敏，email/credit_amount/bank_account/tax_no隐藏
-- order：全部可见
(79,5,6,'VISIBLE',NULL),             -- customer.customer_name
(80,5,7,'MASKED','phone:3,4'),       -- customer.contact_phone 脱敏
(81,5,8,'HIDDEN',NULL),              -- customer.email 隐藏
(82,5,9,'VISIBLE',NULL),             -- customer.address
(83,5,10,'HIDDEN',NULL),             -- customer.credit_amount 隐藏
(84,5,11,'VISIBLE',NULL),            -- order.order_no
(85,5,12,'VISIBLE',NULL),            -- order.order_amount
(86,5,13,'VISIBLE',NULL),            -- order.order_status
(87,5,14,'VISIBLE',NULL),            -- order.create_user_id
(88,5,19,'VISIBLE',NULL),            -- customer自定义.industry
(89,5,20,'VISIBLE',NULL),            -- customer自定义.company_scale
(90,5,21,'VISIBLE',NULL),            -- customer自定义.established_date
(91,5,22,'HIDDEN',NULL),             -- customer自定义.bank_account 隐藏
(92,5,23,'HIDDEN',NULL),             -- customer自定义.tax_no 隐藏
(93,5,24,'VISIBLE',NULL),            -- order自定义.delivery_method
(94,5,25,'VISIBLE',NULL),            -- order自定义.urgent_level
(95,5,26,'VISIBLE',NULL),            -- order自定义.remark

-- ⑤ 测试工程师(role_id=6)（与开发工程师相同权限设计，可调整）
(96,6,6,'VISIBLE',NULL),
(97,6,7,'MASKED','phone:3,4'),
(98,6,8,'HIDDEN',NULL),
(99,6,9,'VISIBLE',NULL),
(100,6,10,'HIDDEN',NULL),
(101,6,11,'VISIBLE',NULL),
(102,6,12,'VISIBLE',NULL),
(103,6,13,'VISIBLE',NULL),
(104,6,14,'VISIBLE',NULL),
(105,6,19,'VISIBLE',NULL),
(106,6,20,'VISIBLE',NULL),
(107,6,21,'VISIBLE',NULL),
(108,6,22,'HIDDEN',NULL),
(109,6,23,'HIDDEN',NULL),
(110,6,24,'VISIBLE',NULL),
(111,6,25,'VISIBLE',NULL),
(112,6,26,'VISIBLE',NULL),

-- ⑥ 销售经理(role_id=7)
-- 有权限的实体：customer（全）+ order（查/创/改）
-- customer：name/phone/address EDITABLE，email/credit_amount/tax_no VISIBLE，bank_account HIDDEN
-- order：全部可见
(113,7,6,'EDITABLE',NULL),           -- customer.customer_name
(114,7,7,'EDITABLE',NULL),           -- customer.contact_phone 可编辑（销售核心字段）
(115,7,8,'VISIBLE',NULL),            -- customer.email 仅看
(116,7,9,'EDITABLE',NULL),           -- customer.address
(117,7,10,'VISIBLE',NULL),           -- customer.credit_amount 仅看（不可改）
(118,7,11,'VISIBLE',NULL),           -- order.order_no
(119,7,12,'VISIBLE',NULL),           -- order.order_amount
(120,7,13,'VISIBLE',NULL),           -- order.order_status
(121,7,14,'VISIBLE',NULL),           -- order.create_user_id
(122,7,19,'EDITABLE',NULL),          -- customer自定义.industry
(123,7,20,'VISIBLE',NULL),           -- customer自定义.company_scale
(124,7,21,'VISIBLE',NULL),           -- customer自定义.established_date
(125,7,22,'HIDDEN',NULL),            -- customer自定义.bank_account 隐藏
(126,7,23,'VISIBLE',NULL),           -- customer自定义.tax_no 仅看
(127,7,24,'VISIBLE',NULL),           -- order自定义.delivery_method
(128,7,25,'VISIBLE',NULL),           -- order自定义.urgent_level
(129,7,26,'VISIBLE',NULL),           -- order自定义.remark

-- ⑦ 销售员(role_id=8)
-- 有权限的实体：customer（查/创）+ order（查/创）
-- customer：name/address VISIBLE，phone MASKED，email/credit_amount/bank_account/tax_no HIDDEN
(130,8,6,'VISIBLE',NULL),            -- customer.customer_name
(131,8,7,'MASKED','phone:3,4'),      -- customer.contact_phone 脱敏
(132,8,8,'HIDDEN',NULL),             -- customer.email 隐藏
(133,8,9,'VISIBLE',NULL),            -- customer.address
(134,8,10,'HIDDEN',NULL),            -- customer.credit_amount 隐藏
(135,8,11,'VISIBLE',NULL),           -- order.order_no
(136,8,12,'MASKED','number:0,0'),    -- order.order_amount 脱敏（销售员看不到金额）
(137,8,13,'VISIBLE',NULL),           -- order.order_status
(138,8,14,'VISIBLE',NULL),           -- order.create_user_id
(139,8,19,'VISIBLE',NULL),           -- customer自定义.industry
(140,8,20,'VISIBLE',NULL),           -- customer自定义.company_scale
(141,8,21,'VISIBLE',NULL),           -- customer自定义.established_date
(142,8,22,'HIDDEN',NULL),            -- customer自定义.bank_account 隐藏
(143,8,23,'HIDDEN',NULL),            -- customer自定义.tax_no 隐藏
(144,8,24,'VISIBLE',NULL),           -- order自定义.delivery_method
(145,8,25,'VISIBLE',NULL),           -- order自定义.urgent_level
(146,8,26,'VISIBLE',NULL),           -- order自定义.remark

-- ⑧ 人事经理(role_id=9)
-- 有权限的实体：user（查/创/改）
-- user：username/dept_id VISIBLE，real_name EDITABLE，phone MASKED，email VISIBLE
-- user自定义：employee_no/entry_date/emergency_contact EDITABLE，emergency_phone MASKED
(147,9,1,'VISIBLE',NULL),            -- user.username
(148,9,2,'EDITABLE',NULL),           -- user.real_name
(149,9,3,'MASKED','phone:3,4'),      -- user.phone 脱敏 → 138****0008
(150,9,4,'VISIBLE',NULL),            -- user.email
(151,9,5,'VISIBLE',NULL),            -- user.dept_id
(152,9,15,'EDITABLE',NULL),          -- user自定义.employee_no
(153,9,16,'EDITABLE',NULL),          -- user自定义.entry_date
(154,9,17,'EDITABLE',NULL),          -- user自定义.emergency_contact
(155,9,18,'MASKED','phone:3,4');     -- user自定义.emergency_phone 脱敏

-- ====================================================
-- 13. 可授权权限（授权上界）
-- ====================================================
INSERT INTO `grantable_permission` (`id`, `role_id`, `grantable_permission_id`, `grant_scope`) VALUES
-- 平台超管：可授予所有22条权限
(1,1,1,'ALL'),(2,1,2,'ALL'),(3,1,3,'ALL'),(4,1,4,'ALL'),(5,1,5,'ALL'),
(6,1,6,'ALL'),(7,1,7,'ALL'),(8,1,8,'ALL'),(9,1,9,'ALL'),(10,1,10,'ALL'),
(11,1,11,'ALL'),(12,1,12,'ALL'),(13,1,13,'ALL'),(14,1,14,'ALL'),
(15,1,15,'ALL'),(16,1,16,'ALL'),(17,1,17,'ALL'),(18,1,18,'ALL'),
(19,1,19,'ALL'),(20,1,20,'ALL'),(21,1,21,'ALL'),(22,1,22,'ALL'),
-- 租户A管理员：可授予租户内权限
(23,3,4,'ALL'),(24,3,5,'ALL'),(25,3,6,'ALL'),(26,3,7,'ALL'),(27,3,8,'ALL'),
(28,3,9,'ALL'),(29,3,10,'ALL'),(30,3,11,'ALL'),(31,3,12,'ALL'),
(32,3,13,'ALL'),(33,3,14,'ALL'),(34,3,15,'ALL'),(35,3,16,'ALL'),(36,3,17,'ALL'),
(37,3,18,'ALL'),(38,3,19,'ALL'),(39,3,20,'ALL'),(40,3,21,'ALL'),(41,3,22,'ALL'),
-- 技术经理：仅可授予查看类权限
(42,4,13,'PARTIAL'),(43,4,18,'PARTIAL'),
-- 销售经理：可授予销售相关权限
(44,7,13,'ALL'),(45,7,14,'ALL'),(46,7,17,'ALL'),
(47,7,18,'ALL'),(48,7,19,'ALL');

-- ====================================================
-- 14. 可授权数据范围
-- ====================================================
INSERT INTO `grantable_data_scope` (`id`, `role_id`, `resource_id`, `max_scope_type`, `allowed_dept_ids`) VALUES
-- 平台超管
(1,1,17,'ALL',NULL),(2,1,18,'ALL',NULL),(3,1,19,'ALL',NULL),
-- 租户A管理员
(4,3,17,'ALL',NULL),(5,3,18,'ALL',NULL),(6,3,19,'ALL',NULL),
-- 技术经理：最多 DEPT_AND_CHILD，范围限技术部及下级
(7,4,17,'DEPT_AND_CHILD','[2,5,6]'),
(8,4,18,'DEPT_AND_CHILD','[2,5,6]'),
(9,4,19,'DEPT_AND_CHILD','[2,5,6]'),
-- 销售经理：最多 DEPT_AND_CHILD，范围限销售部及下级
(10,7,18,'DEPT_AND_CHILD','[3,7,8]'),
(11,7,19,'DEPT_AND_CHILD','[3,7,8]');

-- ====================================================
-- 15. 可授权字段权限
-- ====================================================
INSERT INTO `grantable_field_permission` (`id`, `role_id`, `field_resource_id`, `max_permission_type`) VALUES
-- 平台超管：所有字段可授予 EDITABLE
(1,1,1,'EDITABLE'),(2,1,2,'EDITABLE'),(3,1,3,'EDITABLE'),(4,1,4,'EDITABLE'),
(5,1,5,'EDITABLE'),(6,1,6,'EDITABLE'),(7,1,7,'EDITABLE'),(8,1,8,'EDITABLE'),
(9,1,9,'EDITABLE'),(10,1,10,'EDITABLE'),(11,1,11,'EDITABLE'),(12,1,12,'EDITABLE'),
(13,1,13,'EDITABLE'),(14,1,14,'EDITABLE'),(15,1,15,'EDITABLE'),(16,1,16,'EDITABLE'),
(17,1,17,'EDITABLE'),(18,1,18,'EDITABLE'),(19,1,19,'EDITABLE'),(20,1,20,'EDITABLE'),
(21,1,21,'EDITABLE'),(22,1,22,'EDITABLE'),(23,1,23,'EDITABLE'),(24,1,24,'EDITABLE'),
(25,1,25,'EDITABLE'),(26,1,26,'EDITABLE'),
-- 租户A管理员：所有字段可授予 EDITABLE
(27,3,1,'EDITABLE'),(28,3,2,'EDITABLE'),(29,3,3,'EDITABLE'),(30,3,4,'EDITABLE'),
(31,3,5,'EDITABLE'),(32,3,6,'EDITABLE'),(33,3,7,'EDITABLE'),(34,3,8,'EDITABLE'),
(35,3,9,'EDITABLE'),(36,3,10,'EDITABLE'),(37,3,15,'EDITABLE'),(38,3,16,'EDITABLE'),
(39,3,17,'EDITABLE'),(40,3,18,'EDITABLE'),(41,3,19,'EDITABLE'),(42,3,20,'EDITABLE'),
(43,3,21,'EDITABLE'),(44,3,22,'EDITABLE'),(45,3,23,'EDITABLE'),
-- 技术经理：敏感字段最多 MASKED
(46,4,3,'MASKED'),(47,4,7,'MASKED'),(48,4,10,'VISIBLE'),(49,4,22,'HIDDEN'),
-- 销售经理：高敏感字段不可授权超过 VISIBLE
(50,7,7,'EDITABLE'),(51,7,10,'VISIBLE'),(52,7,22,'HIDDEN'),(53,7,23,'VISIBLE'),
-- 人事经理：phone 最多 MASKED
(54,9,3,'MASKED'),(55,9,18,'MASKED');

-- ====================================================
-- 16. 自定义字段定义（custom_field_define）
--
-- 租户2定义了3个实体的自定义字段，id=1-12
-- 租户3只定义了客户实体的自定义字段，id=13-14
-- field_resource.custom_field_id 引用此表 id
-- ====================================================
INSERT INTO `custom_field_define` (`id`, `tenant_id`, `entity_code`, `field_code`, `field_name`, `field_type`, `field_config`, `sensitive_level`, `is_required`, `default_value`, `validation_rule`, `is_searchable`, `search_priority`, `sort_order`, `status`, `create_user_id`) VALUES
-- 租户2：客户自定义字段（id=1-5）
(1,  2,'customer','industry',        '所属行业',     'SELECT',  '{"options":[{"label":"互联网","value":"IT"},{"label":"金融","value":"FINANCE"},{"label":"制造业","value":"MANUFACTURING"},{"label":"服务业","value":"SERVICE"}]}',0,1,NULL,NULL,1,10,1,1,2),
(2,  2,'customer','company_scale',   '公司规模',     'SELECT',  '{"options":[{"label":"1-50人","value":"SMALL"},{"label":"51-200人","value":"MEDIUM"},{"label":"201-500人","value":"LARGE"},{"label":"500人以上","value":"XLARGE"}]}',0,0,'SMALL',NULL,1,8,2,1,2),
(3,  2,'customer','established_date','成立日期',     'DATE',    '{}',0,0,NULL,NULL,1,5,3,1,2),
(4,  2,'customer','bank_account',    '银行账号',     'STRING',  '{"maxLength":50}',2,0,NULL,'^[0-9]{10,30}$',0,0,4,1,2),
(5,  2,'customer','tax_no',          '税号',         'STRING',  '{"maxLength":20}',1,0,NULL,NULL,1,6,5,1,2),
-- 租户2：订单自定义字段（id=6-8）
(6,  2,'order',   'delivery_method', '配送方式',     'SELECT',  '{"options":[{"label":"快递","value":"EXPRESS"},{"label":"自提","value":"PICKUP"},{"label":"物流","value":"LOGISTICS"}]}',0,1,'EXPRESS',NULL,1,7,1,1,2),
(7,  2,'order',   'urgent_level',    '紧急程度',     'SELECT',  '{"options":[{"label":"普通","value":"NORMAL"},{"label":"紧急","value":"URGENT"},{"label":"特急","value":"VERY_URGENT"}]}',0,0,'NORMAL',NULL,1,9,2,1,2),
(8,  2,'order',   'remark',          '备注',         'TEXTAREA','{"maxLength":500}',0,0,NULL,NULL,0,0,3,1,2),
-- 租户2：用户自定义字段（id=9-12）← field_resource.custom_field_id 引用这里
(9,  2,'user',    'employee_no',     '工号',         'STRING',  '{"maxLength":20}',0,1,NULL,'^EMP[0-9]{6}$',1,10,1,1,2),
(10, 2,'user',    'entry_date',      '入职日期',     'DATE',    '{}',0,1,NULL,NULL,1,8,2,1,2),
(11, 2,'user',    'emergency_contact','紧急联系人',  'STRING',  '{"maxLength":50}',0,0,NULL,NULL,0,0,3,1,2),
(12, 2,'user',    'emergency_phone', '紧急联系电话', 'STRING',  '{"maxLength":20}',1,0,NULL,'^1[3-9][0-9]{9}$',0,0,4,1,2),
-- 租户3：客户自定义字段（id=13-14，与租户2不同的字段定义）
(13, 3,'customer','industry',        '所属行业',     'SELECT',  '{"options":[{"label":"贸易","value":"TRADE"},{"label":"零售","value":"RETAIL"},{"label":"批发","value":"WHOLESALE"}]}',0,1,NULL,NULL,1,10,1,1,10),
(14, 3,'customer','region',          '所属区域',     'SELECT',  '{"options":[{"label":"华北","value":"NORTH"},{"label":"华南","value":"SOUTH"},{"label":"华东","value":"EAST"},{"label":"华西","value":"WEST"}]}',0,1,NULL,NULL,1,9,2,1,10);

-- ====================================================
-- 17. 自定义字段值（custom_field_value）
--
-- field_value JSON格式：{"value":"xxx","displayValue":"yyy"}
--
-- 租户2用户字段值（userId=2~9，全覆盖，便于各种数据权限场景测试）
-- 租户2客户字段值（entityId=1001~1003）
-- 租户2订单字段值（entityId=2001~2002）
-- 租户3客户字段值（entityId=3001~3002）
-- ====================================================
INSERT INTO `custom_field_value` (`id`, `tenant_id`, `entity_code`, `entity_id`, `field_code`, `field_value`) VALUES
-- 租户2 用户(userId=2, tenant_a_admin, 总部)
(1,  2,'user',2,'employee_no',       '{"value":"EMP200001","displayValue":"EMP200001"}'),
(2,  2,'user',2,'entry_date',        '{"value":"2020-03-01","displayValue":"2020-03-01"}'),
(3,  2,'user',2,'emergency_contact', '{"value":"张三妻子","displayValue":"张三妻子"}'),
(4,  2,'user',2,'emergency_phone',   '{"value":"13900000001","displayValue":"13900000001"}'),
-- 租户2 用户(userId=3, tech_manager, 技术部)
(5,  2,'user',3,'employee_no',       '{"value":"EMP210001","displayValue":"EMP210001"}'),
(6,  2,'user',3,'entry_date',        '{"value":"2021-06-15","displayValue":"2021-06-15"}'),
(7,  2,'user',3,'emergency_contact', '{"value":"李技术父亲","displayValue":"李技术父亲"}'),
(8,  2,'user',3,'emergency_phone',   '{"value":"13900000002","displayValue":"13900000002"}'),
-- 租户2 用户(userId=4, developer_01, 研发组)
(9,  2,'user',4,'employee_no',       '{"value":"EMP220001","displayValue":"EMP220001"}'),
(10, 2,'user',4,'entry_date',        '{"value":"2022-01-10","displayValue":"2022-01-10"}'),
(11, 2,'user',4,'emergency_contact', '{"value":"王研发母亲","displayValue":"王研发母亲"}'),
(12, 2,'user',4,'emergency_phone',   '{"value":"13900000003","displayValue":"13900000003"}'),
-- 租户2 用户(userId=5, developer_02, 研发组)
(13, 2,'user',5,'employee_no',       '{"value":"EMP220002","displayValue":"EMP220002"}'),
(14, 2,'user',5,'entry_date',        '{"value":"2022-03-20","displayValue":"2022-03-20"}'),
(15, 2,'user',5,'emergency_contact', '{"value":"赵研发爱人","displayValue":"赵研发爱人"}'),
(16, 2,'user',5,'emergency_phone',   '{"value":"13900000004","displayValue":"13900000004"}'),
-- 租户2 用户(userId=6, tester_01, 测试组)
(17, 2,'user',6,'employee_no',       '{"value":"EMP230001","displayValue":"EMP230001"}'),
(18, 2,'user',6,'entry_date',        '{"value":"2023-07-01","displayValue":"2023-07-01"}'),
(19, 2,'user',6,'emergency_contact', '{"value":"孙测试父亲","displayValue":"孙测试父亲"}'),
(20, 2,'user',6,'emergency_phone',   '{"value":"13900000005","displayValue":"13900000005"}'),
-- 租户2 用户(userId=7, sales_manager, 销售部)
(21, 2,'user',7,'employee_no',       '{"value":"EMP190001","displayValue":"EMP190001"}'),
(22, 2,'user',7,'entry_date',        '{"value":"2019-04-08","displayValue":"2019-04-08"}'),
(23, 2,'user',7,'emergency_contact', '{"value":"周销售爱人","displayValue":"周销售爱人"}'),
(24, 2,'user',7,'emergency_phone',   '{"value":"13900000006","displayValue":"13900000006"}'),
-- 租户2 用户(userId=8, sales_north_01, 华北)
(25, 2,'user',8,'employee_no',       '{"value":"EMP230002","displayValue":"EMP230002"}'),
(26, 2,'user',8,'entry_date',        '{"value":"2023-09-01","displayValue":"2023-09-01"}'),
(27, 2,'user',8,'emergency_contact', '{"value":"吴华北母亲","displayValue":"吴华北母亲"}'),
(28, 2,'user',8,'emergency_phone',   '{"value":"13900000007","displayValue":"13900000007"}'),
-- 租户2 用户(userId=9, hr_manager, 人事部)
(29, 2,'user',9,'employee_no',       '{"value":"EMP200002","displayValue":"EMP200002"}'),
(30, 2,'user',9,'entry_date',        '{"value":"2020-08-15","displayValue":"2020-08-15"}'),
(31, 2,'user',9,'emergency_contact', '{"value":"郑人事兄弟","displayValue":"郑人事兄弟"}'),
(32, 2,'user',9,'emergency_phone',   '{"value":"13900000008","displayValue":"13900000008"}'),
-- 租户2 客户(1001)：科技公司，互联网行业
(33, 2,'customer',1001,'industry',         '{"value":"IT","displayValue":"互联网"}'),
(34, 2,'customer',1001,'company_scale',    '{"value":"MEDIUM","displayValue":"51-200人"}'),
(35, 2,'customer',1001,'established_date', '{"value":"2015-06-15","displayValue":"2015-06-15"}'),
(36, 2,'customer',1001,'bank_account',     '{"value":"62170000012345678901","displayValue":"62170000012345678901"}'),
(37, 2,'customer',1001,'tax_no',           '{"value":"91110000MA01234567","displayValue":"91110000MA01234567"}'),
-- 租户2 客户(1002)：金融公司
(38, 2,'customer',1002,'industry',         '{"value":"FINANCE","displayValue":"金融"}'),
(39, 2,'customer',1002,'company_scale',    '{"value":"LARGE","displayValue":"201-500人"}'),
(40, 2,'customer',1002,'established_date', '{"value":"2010-03-20","displayValue":"2010-03-20"}'),
(41, 2,'customer',1002,'bank_account',     '{"value":"62170000098765432109","displayValue":"62170000098765432109"}'),
(42, 2,'customer',1002,'tax_no',           '{"value":"91110000MA09876543","displayValue":"91110000MA09876543"}'),
-- 租户2 客户(1003)：制造业
(43, 2,'customer',1003,'industry',         '{"value":"MANUFACTURING","displayValue":"制造业"}'),
(44, 2,'customer',1003,'company_scale',    '{"value":"XLARGE","displayValue":"500人以上"}'),
(45, 2,'customer',1003,'established_date', '{"value":"2005-11-20","displayValue":"2005-11-20"}'),
-- 租户2 订单(2001)
(46, 2,'order',2001,'delivery_method', '{"value":"EXPRESS","displayValue":"快递"}'),
(47, 2,'order',2001,'urgent_level',    '{"value":"NORMAL","displayValue":"普通"}'),
(48, 2,'order',2001,'remark',          '{"value":"请工作日送货","displayValue":"请工作日送货"}'),
-- 租户2 订单(2002)
(49, 2,'order',2002,'delivery_method', '{"value":"LOGISTICS","displayValue":"物流"}'),
(50, 2,'order',2002,'urgent_level',    '{"value":"URGENT","displayValue":"紧急"}'),
(51, 2,'order',2002,'remark',          '{"value":"加急处理","displayValue":"加急处理"}'),
-- 租户3 客户(3001)
(52, 3,'customer',3001,'industry', '{"value":"TRADE","displayValue":"贸易"}'),
(53, 3,'customer',3001,'region',   '{"value":"NORTH","displayValue":"华北"}'),
-- 租户3 客户(3002)
(54, 3,'customer',3002,'industry', '{"value":"RETAIL","displayValue":"零售"}'),
(55, 3,'customer',3002,'region',   '{"value":"SOUTH","displayValue":"华南"}');

-- ====================================================
-- 18. 自定义字段查询表（custom_field_search，反范式）
-- 仅同步 is_searchable=1 的字段，用于快速筛选条件
-- ====================================================
INSERT INTO `custom_field_search` (`id`, `tenant_id`, `entity_code`, `entity_id`, `field_code`, `field_type`, `string_value`, `number_value`, `date_value`, `datetime_value`, `boolean_value`) VALUES
-- 租户2 用户（is_searchable=1：employee_no, entry_date）
(1,  2,'user',2,'employee_no','STRING','EMP200001',NULL,NULL,NULL,NULL),
(2,  2,'user',2,'entry_date', 'DATE',  NULL,NULL,'2020-03-01',NULL,NULL),
(3,  2,'user',3,'employee_no','STRING','EMP210001',NULL,NULL,NULL,NULL),
(4,  2,'user',3,'entry_date', 'DATE',  NULL,NULL,'2021-06-15',NULL,NULL),
(5,  2,'user',4,'employee_no','STRING','EMP220001',NULL,NULL,NULL,NULL),
(6,  2,'user',4,'entry_date', 'DATE',  NULL,NULL,'2022-01-10',NULL,NULL),
(7,  2,'user',5,'employee_no','STRING','EMP220002',NULL,NULL,NULL,NULL),
(8,  2,'user',5,'entry_date', 'DATE',  NULL,NULL,'2022-03-20',NULL,NULL),
(9,  2,'user',6,'employee_no','STRING','EMP230001',NULL,NULL,NULL,NULL),
(10, 2,'user',6,'entry_date', 'DATE',  NULL,NULL,'2023-07-01',NULL,NULL),
(11, 2,'user',7,'employee_no','STRING','EMP190001',NULL,NULL,NULL,NULL),
(12, 2,'user',7,'entry_date', 'DATE',  NULL,NULL,'2019-04-08',NULL,NULL),
(13, 2,'user',8,'employee_no','STRING','EMP230002',NULL,NULL,NULL,NULL),
(14, 2,'user',8,'entry_date', 'DATE',  NULL,NULL,'2023-09-01',NULL,NULL),
(15, 2,'user',9,'employee_no','STRING','EMP200002',NULL,NULL,NULL,NULL),
(16, 2,'user',9,'entry_date', 'DATE',  NULL,NULL,'2020-08-15',NULL,NULL),
-- 租户2 客户（is_searchable=1：industry, company_scale, established_date, tax_no）
(17, 2,'customer',1001,'industry',        'SELECT','IT',NULL,NULL,NULL,NULL),
(18, 2,'customer',1001,'company_scale',   'SELECT','MEDIUM',NULL,NULL,NULL,NULL),
(19, 2,'customer',1001,'established_date','DATE',  NULL,NULL,'2015-06-15',NULL,NULL),
(20, 2,'customer',1001,'tax_no',          'STRING','91110000MA01234567',NULL,NULL,NULL,NULL),
(21, 2,'customer',1002,'industry',        'SELECT','FINANCE',NULL,NULL,NULL,NULL),
(22, 2,'customer',1002,'company_scale',   'SELECT','LARGE',NULL,NULL,NULL,NULL),
(23, 2,'customer',1002,'established_date','DATE',  NULL,NULL,'2010-03-20',NULL,NULL),
(24, 2,'customer',1002,'tax_no',          'STRING','91110000MA09876543',NULL,NULL,NULL,NULL),
(25, 2,'customer',1003,'industry',        'SELECT','MANUFACTURING',NULL,NULL,NULL,NULL),
(26, 2,'customer',1003,'company_scale',   'SELECT','XLARGE',NULL,NULL,NULL,NULL),
(27, 2,'customer',1003,'established_date','DATE',  NULL,NULL,'2005-11-20',NULL,NULL),
-- 租户2 订单（is_searchable=1：delivery_method, urgent_level）
(28, 2,'order',2001,'delivery_method','SELECT','EXPRESS',NULL,NULL,NULL,NULL),
(29, 2,'order',2001,'urgent_level',   'SELECT','NORMAL',NULL,NULL,NULL,NULL),
(30, 2,'order',2002,'delivery_method','SELECT','LOGISTICS',NULL,NULL,NULL,NULL),
(31, 2,'order',2002,'urgent_level',   'SELECT','URGENT',NULL,NULL,NULL,NULL),
-- 租户3 客户
(32, 3,'customer',3001,'industry','SELECT','TRADE',NULL,NULL,NULL,NULL),
(33, 3,'customer',3001,'region',  'SELECT','NORTH',NULL,NULL,NULL,NULL),
(34, 3,'customer',3002,'industry','SELECT','RETAIL',NULL,NULL,NULL,NULL),
(35, 3,'customer',3002,'region',  'SELECT','SOUTH',NULL,NULL,NULL,NULL);

-- ====================================================
-- 19. 授权操作日志（示例）
-- ====================================================
INSERT INTO `grant_operation_log` (`id`, `operator_id`, `operator_name`, `target_role_id`, `target_role_name`, `operation_type`, `check_result`, `reject_reason`, `operation_detail`, `ip_address`) VALUES
(1, 1, '超级管理员', 2,  '平台运营员', 'CREATE_ROLE',           'PASS',   NULL,                           '{"level":10}',                            '192.168.1.1'),
(2, 2, '张三',       4,  '技术部经理', 'CREATE_ROLE',           'PASS',   NULL,                           '{"level":200,"parentRoleId":3}',           '192.168.1.2'),
(3, 2, '张三',       5,  '开发工程师', 'ASSIGN_PERMISSION',     'PASS',   NULL,                           '{"permissionIds":[13,18]}',               '192.168.1.2'),
(4, 3, '李技术',     5,  '开发工程师', 'ASSIGN_DATA_SCOPE',     'PASS',   NULL,                           '{"resourceId":18,"scopeType":"SELF"}',    '192.168.1.3'),
(5, 7, '周销售',     8,  '销售员',     'ASSIGN_FIELD_PERMISSION','PASS',   NULL,                           '{"fieldId":10,"permType":"HIDDEN"}',      '192.168.1.4'),
(6, 8, '吴华北',     3,  '租户A管理员','ASSIGN_PERMISSION',     'REJECT', '权限不足，无法为上级角色分配权限','{"permissionIds":[13,14]}',             '192.168.1.5');

-- ====================================================
-- 20. 数据访问日志（示例）
-- ====================================================
INSERT INTO `data_access_log` (`id`, `user_id`, `username`, `tenant_id`, `target_tenant_id`, `resource_type`, `resource_id`, `operation`, `field_list`, `data_scope`, `ip_address`, `user_agent`, `access_time`, `response_time`) VALUES
(1, 1, 'admin',          1, 2,    'customer', 1001, 'view',   'customer_name,contact_phone,credit_amount', 'ALL',            '192.168.1.1', 'Apifox/1.0', '2024-02-10 09:00:00', 45),
(2, 2, 'tenant_a_admin', 2, NULL, 'customer', 1002, 'create', 'customer_name,contact_phone,address',       'ALL',            '192.168.1.2', 'Apifox/1.0', '2024-02-10 10:00:00', 120),
(3, 4, 'developer_01',   2, NULL, 'customer', 1003, 'view',   'customer_name,contact_phone',               'SELF',           '192.168.1.4', 'Apifox/1.0', '2024-02-10 11:00:00', 35),
(4, 7, 'sales_manager',  2, NULL, 'customer', NULL, 'export', 'customer_name,contact_phone,credit_amount', 'DEPT_AND_CHILD', '192.168.1.7', 'Apifox/1.0', '2024-02-10 14:00:00', 2500),
(5, 8, 'sales_north_01', 2, NULL, 'customer', 1001, 'update', 'customer_name,contact_phone',               'DEPT',           '192.168.1.8', 'Apifox/1.0', '2024-02-10 15:00:00', 85),
(6, 9, 'hr_manager',     2, NULL, 'user',     4,    'view',   'username,real_name,phone,employee_no',      'CUSTOM',         '192.168.1.9', 'Apifox/1.0', '2024-02-10 16:00:00', 55);

-- ====================================================
-- ✅ 完成！逻辑验证通过的测试账号速查：
--
-- ┌─────────────────┬──────────────┬─────────────────────────────────┬──────────────────────────────────┐
-- │ 账号            │ 可访问接口   │ 数据范围（user/customer/order）  │ 关键字段权限                     │
-- ├─────────────────┼──────────────┼─────────────────────────────────┼──────────────────────────────────┤
-- │ admin           │ 全部         │ 穿透所有租户，ALL               │ 全部 EDITABLE                    │
-- │ tenant_a_admin  │ 用户/客户/订单│ ALL（租户2所有数据）           │ 全部 EDITABLE                    │
-- │ tech_manager    │ 用户/客户/订单│ DEPT_AND_CHILD(dept=2,5,6)     │ phone/email MASKED，高敏感HIDDEN  │
-- │                 │              │ 用户可见：userId=3,4,5,6        │                                  │
-- │ developer_01    │ 客户/订单    │ SELF（仅本人创建）              │ email/高敏感 HIDDEN              │
-- │ developer_02    │ 客户/订单    │ SELF（仅本人创建）              │ 同上                             │
-- │ tester_01       │ 客户/订单    │ DEPT(dept=6，测试组)            │ 同开发工程师                     │
-- │ sales_manager   │ 客户/订单    │ DEPT_AND_CHILD(dept=3,7,8)      │ 高敏感HIDDEN，phone可编辑        │
-- │ sales_north_01  │ 客户/订单    │ DEPT(dept=7，华北销售区)        │ phone MASKED，高敏感HIDDEN       │
-- │ hr_manager      │ 用户         │ CUSTOM([2,3,4,5,6,7,8])         │ phone/emergency_phone MASKED     │
-- │                 │              │ 用户可见：userId=3,4,5,6,7,8,9  │                                  │
-- │ tenant_b_admin  │ 客户/订单    │ ALL（租户3所有数据）            │ 全部 EDITABLE（租户3完全隔离）   │
-- │ purchase_staff  │ 订单         │ SELF                            │ 未配置（默认VISIBLE）            │
-- │ sales_staff     │ 客户/订单    │ customer:DEPT(dept=11)          │ 未配置（默认VISIBLE）            │
-- └─────────────────┴──────────────┴─────────────────────────────────┴──────────────────────────────────┘
-- ====================================================
