package cn.kmdckj.base.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 安全上下文
 * 存储当前登录用户信息
 */
public class SecurityContext {

    /**
     * 用户ID线程变量
     */
    private static final ThreadLocal<Long> USER_ID = new TransmittableThreadLocal<>();

    /**
     * 用户名线程变量
     */
    private static final ThreadLocal<String> USERNAME = new TransmittableThreadLocal<>();

    /**
     * 部门ID线程变量
     */
    private static final ThreadLocal<Long> DEPT_ID = new TransmittableThreadLocal<>();

    /**
     * 平台超管变量
     */
    private static final ThreadLocal<Boolean> IS_SUPER_ADMIN = new TransmittableThreadLocal<>();

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 获取超管
     */
    public static Boolean isSuperAdmin() {
        return Boolean.TRUE.equals(IS_SUPER_ADMIN.get());
    }

    /**
     * 设置超管
     */
    public static void setSuperAdmin(Boolean isSuperAdmin) {
        IS_SUPER_ADMIN.set(isSuperAdmin);
    }

    /**
     * 设置用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        return USERNAME.get();
    }

    /**
     * 设置用户名
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    /**
     * 获取部门ID
     */
    public static Long getDeptId() {
        return DEPT_ID.get();
    }

    /**
     * 设置部门ID
     */
    public static void setDeptId(Long deptId) {
        DEPT_ID.set(deptId);
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        DEPT_ID.remove();
        IS_SUPER_ADMIN.remove();
    }
}
