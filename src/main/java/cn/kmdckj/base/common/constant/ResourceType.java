package cn.kmdckj.base.common.constant;

import lombok.Getter;

/**
 * 资源类型枚举
 */
@Getter
public enum ResourceType {

    /**
     * 菜单
     */
    MENU("menu", "菜单"),

    /**
     * 按钮
     */
    BUTTON("button", "按钮"),

    /**
     * API接口
     */
    API("api", "API接口"),

    /**
     * 实体（用于数据权限）
     */
    ENTITY("entity", "实体");

    private final String code;
    private final String description;

    ResourceType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ResourceType fromCode(String code) {
        for (ResourceType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}