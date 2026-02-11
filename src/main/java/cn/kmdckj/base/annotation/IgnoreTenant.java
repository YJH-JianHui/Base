package cn.kmdckj.base.annotation;

/**
 * 忽略租户隔离注解。
 * <p>
 * 用于特定方法，标识该操作跳过 tenant_id 过滤 (如平台超管操作)。
 */
public @interface IgnoreTenant {
}
