package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.TenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门表实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("department")
public class Department extends TenantEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 父部门ID,0为根部门
     */
    private Long parentId;

    /**
     * 部门路径,如/1/2/5/
     */
    private String deptPath;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态:0-禁用 1-启用
     */
    private Integer status;
}
