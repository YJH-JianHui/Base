package cn.kmdckj.base.annotation;

import java.lang.annotation.*;

/**
 * 查询实体时自动包含自定义字段注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WithCustomFields {
    /**
     * 实体编码，如 "customer"、"user"
     */
    String entityCode();

    /**
     * 结果中用于提取 entityId 的字段名，默认 "id"
     */
    String idField() default "id";
}
