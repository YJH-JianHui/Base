package cn.kmdckj.base.util;

import cn.hutool.core.util.StrUtil;

/**
 * SQL工具类
 * 用于防止SQL注入
 */
public class SqlUtil {

    /**
     * SQL关键字
     */
    private static final String[] SQL_KEYWORDS = {
            "select", "insert", "update", "delete", "drop", "create", "alter",
            "exec", "execute", "script", "javascript", "union", "and", "or",
            "xp_", "sp_", "0x", "char", "declare", "cast", "set", "exec"
    };

    /**
     * 仅支持字母、数字、下划线、逗号（用于排序字段）
     */
    private static final String SQL_PATTERN = "[a-zA-Z0-9_,]+";

    /**
     * 检查排序字段是否合法
     *
     * @param sortField 排序字段
     * @return 是否合法
     */
    public static boolean isValidSortField(String sortField) {
        if (StrUtil.isBlank(sortField)) {
            return false;
        }
        return sortField.matches(SQL_PATTERN);
    }

    /**
     * 转义特殊字符
     *
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    public static String escapeSpecialChar(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        return str.replace("'", "''")
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * 检查是否包含SQL关键字
     *
     * @param str 待检查字符串
     * @return 是否包含SQL关键字
     */
    public static boolean containsSqlKeyword(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        String lowerStr = str.toLowerCase();
        for (String keyword : SQL_KEYWORDS) {
            if (lowerStr.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 过滤SQL关键字
     *
     * @param str 原始字符串
     * @return 过滤后的字符串
     */
    public static String filterSqlKeyword(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        String result = str;
        for (String keyword : SQL_KEYWORDS) {
            result = result.toLowerCase().replace(keyword, "");
        }
        return result;
    }
}