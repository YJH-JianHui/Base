package cn.kmdckj.base.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * 用于方法级别的权限控制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {

    /**
     * 需要的权限码
     * 支持多个权限码，满足其一即可
     */
    String[] value();

    /**
     * 逻辑关系
     * AND: 需要同时拥有所有权限
     * OR: 拥有任意一个权限即可
     */
    Logical logical() default Logical.OR;

    /**
     * 逻辑枚举
     */
    enum Logical {
        /**
         * 且
         */
        AND,
        /**
         * 或
         */
        OR
    }
}