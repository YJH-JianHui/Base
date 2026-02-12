package cn.kmdckj.base.common.constant;

import lombok.Getter;

/**
 * 字段权限类型枚举
 */
@Getter
public enum FieldPermissionType {

    /**
     * 隐藏（不返回该字段）
     */
    HIDDEN("HIDDEN", "隐藏", 0),

    /**
     * 可见（只读）
     */
    VISIBLE("VISIBLE", "可见", 1),

    /**
     * 脱敏显示
     */
    MASKED("MASKED", "脱敏显示", 2),

    /**
     * 可编辑
     */
    EDITABLE("EDITABLE", "可编辑", 3);

    private final String code;
    private final String description;
    private final int level;

    FieldPermissionType(String code, String description, int level) {
        this.code = code;
        this.description = description;
        this.level = level;
    }

    /**
     * 根据code获取枚举
     */
    public static FieldPermissionType fromCode(String code) {
        for (FieldPermissionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return HIDDEN;
    }

    /**
     * 比较权限等级
     * 返回true表示当前权限等级高于目标权限
     */
    public boolean isHigherThan(FieldPermissionType target) {
        return this.level > target.level;
    }

    /**
     * 比较权限等级
     * 返回true表示当前权限等级低于目标权限
     */
    public boolean isLowerThan(FieldPermissionType target) {
        return this.level < target.level;
    }
}