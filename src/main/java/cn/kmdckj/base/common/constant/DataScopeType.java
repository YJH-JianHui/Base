package cn.kmdckj.base.common.constant;

/**
 * 数据权限范围类型枚举。
 */
public enum DataScopeType {
    /**
     * 本租户全部数据
     */
    ALL("ALL", "本租户全部数据"),

    /**
     * 本部门数据
     */
    DEPT("DEPT", "本部门数据"),

    /**
     * 本部门及下级部门数据
     */
    DEPT_AND_CHILD("DEPT_AND_CHILD", "本部门及下级部门数据"),

    /**
     * 仅本人数据
     */
    SELF("SELF", "仅本人数据"),

    /**
     * 自定义部门
     */
    CUSTOM("CUSTOM", "自定义部门");

    private final String code;
    private final String description;

    DataScopeType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
