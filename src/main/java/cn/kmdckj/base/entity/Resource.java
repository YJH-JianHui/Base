package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.TenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源表实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resource")
public class Resource extends TenantEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源编码
     */
    private String resourceCode;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源类型:menu-菜单 button-按钮 api-接口 entity-实体
     */
    private String resourceType;

    /**
     * 父资源ID
     */
    private Long parentId;

    /**
     * 资源路径
     */
    private String resourcePath;

    /**
     * 前端路由路径(菜单类型)
     */
    private String routePath;

    /**
     * 组件路径(菜单类型)
     */
    private String componentPath;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态:0-禁用 1-启用
     */
    private Integer status;
}
