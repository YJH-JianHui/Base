package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 可授权字段权限表实体。
 * 无租户 ID，继承 BaseEntity。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grantable_field_permission")
public class GrantableFieldPermission extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 授权者角色ID
     */
    private Long roleId;

    /**
     * 字段资源ID
     */
    private Long fieldResourceId;

    /**
     * 最大可授予权限类型
     */
    private String maxPermissionType;
}
