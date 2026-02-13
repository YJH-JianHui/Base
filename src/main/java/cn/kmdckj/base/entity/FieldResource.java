package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段资源表实体。
 * 无租户 ID，继承 BaseEntity。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("field_resource")
public class FieldResource extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实体编码,如order、customer
     */
    private String entityCode;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段标签(显示名)
     */
    private String fieldLabel;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 敏感级别:0-普通 1-敏感 2-高度敏感
     */
    private Integer sensitiveLevel;

    /**
     * 是否自定义字段:0-固定字段 1-自定义字段
     */
    private Integer isCustom;

    /**
     * 关联自定义字段定义ID
     */
    private Long customFieldId;

    /**
     * 描述
     */
    private String description;
}
