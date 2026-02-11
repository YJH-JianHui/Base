package cn.kmdckj.base.annotation;

import java.lang.annotation.*;

/**
 * 忽略租户隔离注解
 * 用于平台管理员跨租户查询等场景
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreTenant {
}
