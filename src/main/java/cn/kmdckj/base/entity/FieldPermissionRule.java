package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段权限规则表实体。
 * 无租户 ID，继承 BaseEntity。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("field_permission_rule")
public class FieldPermissionRule extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 字段资源ID
     */
    private Long fieldResourceId;

    /**
     * 权限类型:HIDDEN-隐藏 VISIBLE-可见 EDITABLE-可编辑 MASKED-脱敏显示
     */
    private String permissionType;

    /**
     * 脱敏规则
     */
    private String maskRule;
}
