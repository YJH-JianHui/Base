package cn.kmdckj.base.annotation;

import java.lang.annotation.*;

/**
 * 字段过滤注解
 * 用于标识需要进行字段权限过滤的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldFilter {

    /**
     * 实体编码
     * 对应 field_resource 表中的 entity_code
     */
    String entityCode();

    /**
     * 是否过滤响应结果
     * true: 过滤返回给前端的数据
     * false: 不过滤
     */
    boolean filterResponse() default true;
}