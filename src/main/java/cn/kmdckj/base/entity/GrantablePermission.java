package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.BaseImmutableEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 可授权功能权限范围表实体。
 * 仅包含创建时间，不可修改。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grantable_permission")
public class GrantablePermission extends BaseImmutableEntity {

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
     * 可授予的权限ID
     */
    private Long grantablePermissionId;

    /**
     * 授权范围:ALL-全部 PARTIAL-部分
     */
    private String grantScope;
}
