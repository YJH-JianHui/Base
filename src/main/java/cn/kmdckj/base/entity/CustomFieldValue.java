package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.TenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义字段值表实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("custom_field_value")
public class CustomFieldValue extends TenantEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实体类型
     */
    private String entityCode;

    /**
     * 实体ID(如user_id)
     */
    private Long entityId;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 字段值(JSON格式存储)
     */
    private String fieldValue;
}
