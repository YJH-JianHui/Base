package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限表实体。
 * 无租户 ID，继承 BaseEntity。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission")
public class Permission extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 权限编码,如order:view
     */
    private String permissionCode;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 操作ID
     */
    private Long operationId;

    /**
     * 描述
     */
    private String description;
}
