package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.TenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role")
public class Role extends TenantEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色类型:platform-平台 tenant-租户
     */
    private String roleType;

    /**
     * 角色层级,数字越小权限越大
     */
    private Integer level;

    /**
     * 父角色ID,用于继承
     */
    private Long parentRoleId;

    /**
     * 是否租户管理员:0-否 1-是
     */
    private Integer isTenantAdmin;

    /**
     * 是否可被管理:0-否 1-是
     */
    private Integer manageable;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态:0-禁用 1-启用
     */
    private Integer status;
}
