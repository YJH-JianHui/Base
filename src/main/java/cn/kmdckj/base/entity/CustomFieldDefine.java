package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.TenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义字段定义表实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "custom_field_define", autoResultMap = true)
public class CustomFieldDefine extends TenantEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联实体,如user、department、order
     */
    private String entityCode;

    /**
     * 字段编码,唯一标识
     */
    private String fieldCode;

    /**
     * 字段显示名称
     */
    private String fieldName;

    /**
     * 数据类型:STRING-字符串 NUMBER-数字 DECIMAL-小数 DATE-日期 DATETIME-日期时间 BOOLEAN-布尔值 SELECT-单选下拉 MULTI_SELECT-多选下拉 TEXTAREA-文本域
     */
    private String fieldType;

    /**
     * 字段配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String fieldConfig;

    /**
     * 敏感级别:0-普通 1-敏感 2-高度敏感
     */
    private Integer sensitiveLevel;

    /**
     * 是否必填:0-否 1-是
     */
    private Integer isRequired;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 验证规则(正则表达式)
     */
    private String validationRule;

    /**
     * 是否可查询:0-否 1-是
     */
    private Integer isSearchable;

    /**
     * 查询优先级,数字越大越优先建索引
     */
    private Integer searchPriority;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态:0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Long createUserId;
}
