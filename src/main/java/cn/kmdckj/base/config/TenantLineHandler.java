package cn.kmdckj.base.config;

import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.common.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import java.util.Arrays;
import java.util.List;

/**
 * 租户处理器
 * 配合 TenantLineInnerInterceptor 使用
 */
@Slf4j
public class TenantLineHandler implements com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler {

    /**
     * 租户字段名
     */
    private static final String TENANT_ID_COLUMN = "tenant_id";

    /**
     * 不需要租户隔离的表
     * 注意：这里只需要配置真正不需要租户隔离的表
     */
    private static final List<String> IGNORE_TABLES = Arrays.asList(
            "tenant",
            "user_role",
            "operation",
            "permission",
            "resource",
            "role_permission",
            "data_permission_rule",
            "field_resource",
            "field_permission_rule",
            "grantable_permission",
            "grantable_data_scope",
            "grantable_field_permission",
            "grant_operation_log"
    );

    /**
     * 获取租户ID值
     * 返回租户ID的SQL表达式
     */
    @Override
    public Expression getTenantId() {
        // 从上下文获取租户ID
        Long tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            log.warn("租户ID为空，租户拦截器可能失效");
            // 返回一个不可能存在的租户ID，防止查询到其他租户数据
            return new LongValue(-1);
        }

        return new LongValue(tenantId);
    }

    /**
     * 获取租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return TENANT_ID_COLUMN;
    }

    /**
     * 判断表是否忽略租户隔离
     *
     * @param tableName 表名
     * @return true-忽略（不添加租户条件），false-不忽略（添加租户条件）
     */
    @Override
    public boolean ignoreTable(String tableName) {
        // 1. 如果设置了全局忽略租户隔离标识，则忽略
        if (TenantContext.isIgnoreTenant()) {
            log.debug("全局忽略租户隔离，表: {}", tableName);
            return true;
        }

        // 超管忽略所有表的租户过滤
        if (SecurityContext.isSuperAdmin()) {
            return true;
        }

        // 2. 检查是否在忽略表列表中
        boolean shouldIgnore = IGNORE_TABLES.stream()
                .anyMatch(ignoreTable -> ignoreTable.equalsIgnoreCase(tableName));

        if (shouldIgnore) {
            log.debug("表 {} 在忽略列表中，不添加租户条件", tableName);
        }

        return shouldIgnore;
    }

    /**
     * 是否忽略插入语句
     *
     * @param columns 插入的列
     * @param tenantIdColumn 租户字段名
     * @return true-忽略，false-不忽略
     */
    @Override
    public boolean ignoreInsert(List<net.sf.jsqlparser.schema.Column> columns, String tenantIdColumn) {
        // 如果全局忽略租户隔离，则忽略
        if (TenantContext.isIgnoreTenant()) {
            return true;
        }

        // 检查插入语句中是否已经包含租户ID字段
        // 如果已经包含，说明手动设置了租户ID，不需要自动添加
        boolean hasTenantColumn = columns.stream()
                .anyMatch(column -> tenantIdColumn.equalsIgnoreCase(column.getColumnName()));

        if (hasTenantColumn) {
            log.debug("INSERT语句已包含租户字段，不自动添加");
            return true;
        }

        return false;
    }
}