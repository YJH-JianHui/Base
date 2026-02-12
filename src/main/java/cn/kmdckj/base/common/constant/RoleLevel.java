package cn.kmdckj.base.common.constant;

/**
 * 角色层级常量
 * 数字越小，权限越大
 */
public class RoleLevel {

    /**
     * 平台超级管理员
     * 可跨租户管理
     */
    public static final int PLATFORM_SUPER_ADMIN = 0;

    /**
     * 平台运营管理员
     * 可跨租户查看
     */
    public static final int PLATFORM_OPERATOR = 10;

    /**
     * 租户超级管理员
     * 本租户全部权限
     */
    public static final int TENANT_SUPER_ADMIN = 100;

    /**
     * 租户部门管理员
     * 本部门权限
     */
    public static final int TENANT_DEPT_ADMIN = 200;

    /**
     * 租户普通角色
     * 受限权限
     */
    public static final int TENANT_NORMAL_ROLE = 300;

    /**
     * 判断是否为平台角色
     */
    public static boolean isPlatformRole(Integer level) {
        return level != null && level < TENANT_SUPER_ADMIN;
    }

    /**
     * 判断是否为租户管理员
     */
    public static boolean isTenantAdmin(Integer level) {
        return level != null && level.equals(TENANT_SUPER_ADMIN);
    }

    /**
     * 判断角色A是否可以管理角色B
     * 规则：只能管理层级更低的角色
     */
    public static boolean canManage(Integer levelA, Integer levelB) {
        if (levelA == null || levelB == null) {
            return false;
        }
        return levelA < levelB;
    }
}