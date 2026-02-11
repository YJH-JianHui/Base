package cn.kmdckj.base.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 用于标识需要进行数据权限过滤的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 实体类的别名
     * 例如：user, customer, order
     */
    String entityAlias() default "";

    /**
     * 实体编码
     * 对应resource表中的entity_code
     */
    String entityCode();
}
