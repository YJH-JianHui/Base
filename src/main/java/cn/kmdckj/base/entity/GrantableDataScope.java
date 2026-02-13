package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 可授权数据范围表实体。
 * 无租户 ID，继承 BaseEntity。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grantable_data_scope")
public class GrantableDataScope extends BaseEntity {

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
     * 资源ID
     */
    private Long resourceId;

    /**
     * 最大可授予数据范围
     */
    private String maxScopeType;

    /**
     * 可授权的部门ID列表(JSON数组)
     */
    private String allowedDeptIds;
}
