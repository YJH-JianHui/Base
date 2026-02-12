package cn.kmdckj.base.common.constant;

import lombok.Getter;

/**
 * 操作类型枚举
 */
@Getter
public enum OperationType {

    /**
     * 查看
     */
    VIEW("view", "查看"),

    /**
     * 新增
     */
    CREATE("create", "新增"),

    /**
     * 修改
     */
    UPDATE("update", "修改"),

    /**
     * 删除
     */
    DELETE("delete", "删除"),

    /**
     * 导出
     */
    EXPORT("export", "导出"),

    /**
     * 导入
     */
    IMPORT("import", "导入"),

    /**
     * 审批
     */
    APPROVE("approve", "审批");

    private final String code;
    private final String description;

    OperationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static OperationType fromCode(String code) {
        for (OperationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}