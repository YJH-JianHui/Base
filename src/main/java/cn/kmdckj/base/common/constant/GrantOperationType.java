package cn.kmdckj.base.common.constant;

import lombok.Getter;

/**
 * 授权操作类型枚举
 */
@Getter
public enum GrantOperationType {

    /**
     * 创建角色
     */
    CREATE_ROLE("CREATE_ROLE", "创建角色"),

    /**
     * 修改角色
     */
    UPDATE_ROLE("UPDATE_ROLE", "修改角色"),

    /**
     * 删除角色
     */
    DELETE_ROLE("DELETE_ROLE", "删除角色"),

    /**
     * 分配功能权限
     */
    ASSIGN_PERMISSION("ASSIGN_PERMISSION", "分配功能权限"),

    /**
     * 分配数据权限
     */
    ASSIGN_DATA_SCOPE("ASSIGN_DATA_SCOPE", "分配数据权限"),

    /**
     * 分配字段权限
     */
    ASSIGN_FIELD_PERMISSION("ASSIGN_FIELD_PERMISSION", "分配字段权限"),

    /**
     * 分配用户角色
     */
    ASSIGN_USER_ROLE("ASSIGN_USER_ROLE", "分配用户角色");

    private final String code;
    private final String description;

    GrantOperationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

}