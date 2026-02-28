package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.CustomFieldHolder;
import cn.kmdckj.base.entity.base.TenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 用户表实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends TenantEntity implements CustomFieldHolder {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储，返回时不做序列化）
     */
    @JsonIgnore
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /** 自定义字段，不持久化 */
    @TableField(exist = false)
    private Map<String, Object> customFields;
}
