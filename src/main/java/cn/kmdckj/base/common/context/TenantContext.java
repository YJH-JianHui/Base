package cn.kmdckj.base.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 租户上下文
 * 使用 TransmittableThreadLocal 支持线程池场景
 */
public class TenantContext {

    /**
     * 租户ID线程变量
     */
    private static final ThreadLocal<Long> TENANT_ID = new TransmittableThreadLocal<>();

    /**
     * 是否忽略租户隔离（平台管理员跨租户查询时使用）
     */
    private static final ThreadLocal<Boolean> IGNORE_TENANT = new TransmittableThreadLocal<>();

    /**
     * 获取租户ID
     */
    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * 设置租户ID
     */
    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * 清除租户ID
     */
    public static void clear() {
        TENANT_ID.remove();
        IGNORE_TENANT.remove();
    }

    /**
     * 设置忽略租户隔离
     */
    public static void setIgnoreTenant(Boolean ignore) {
        IGNORE_TENANT.set(ignore);
    }

    /**
     * 是否忽略租户隔离
     */
    public static Boolean isIgnoreTenant() {
        return Boolean.TRUE.equals(IGNORE_TENANT.get());
    }
}
