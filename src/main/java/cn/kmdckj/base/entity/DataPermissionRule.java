package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据权限规则表实体。
 * 无租户 ID，继承 BaseEntity。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_permission_rule")
public class DataPermissionRule extends BaseEntity {

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
     * 资源ID(实体类型的资源)
     */
    private Long resourceId;

    /**
     * 数据范围类型:ALL-本租户全部 DEPT-本部门 DEPT_AND_CHILD-本部门及下级 SELF-仅本人 CUSTOM-自定义部门
     */
    private String dataScopeType;

    /**
     * 自定义部门ID列表(JSON数组)
     */
    private String customDeptIds;

    /**
     * 描述
     */
    private String description;
}
