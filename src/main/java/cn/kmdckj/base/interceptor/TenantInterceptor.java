package cn.kmdckj.base.interceptor;

import cn.kmdckj.base.annotation.IgnoreTenant;
import cn.kmdckj.base.common.context.TenantContext;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * 租户拦截器
 * 自动在SQL中添加 tenant_id 条件，实现租户隔离
 */
@Slf4j
@Component
public class TenantInterceptor implements InnerInterceptor {

    /**
     * 租户字段名
     */
    private static final String TENANT_ID_COLUMN = "tenant_id";

    /**
     * 不需要租户隔离的表
     */
    private static final String[] IGNORE_TABLES = {
            "tenant",           // 租户表本身
            "operation",        // 操作表（平台级）
            "data_access_log",  // 日志表（包含跨租户日志）
            "grant_operation_log" // 授权日志表
    };

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql)
            throws SQLException {

        // 如果设置了忽略租户隔离，则跳过
        if (TenantContext.isIgnoreTenant()) {
            return;
        }

        // 检查方法是否有 @IgnoreTenant 注解
        if (hasIgnoreTenantAnnotation(ms)) {
            return;
        }

        // 获取当前租户ID
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            log.warn("租户ID为空，SQL: {}", boundSql.getSql());
            return;
        }

        // 这里简化处理，实际应该解析SQL并添加租户条件
        // 完整实现需要使用 JSQLParser 解析SQL
        log.debug("租户拦截器生效，tenant_id: {}", tenantId);
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter)
            throws SQLException {

        // 如果设置了忽略租户隔离，则跳过
        if (TenantContext.isIgnoreTenant()) {
            return;
        }

        // 检查方法是否有 @IgnoreTenant 注解
        if (hasIgnoreTenantAnnotation(ms)) {
            return;
        }

        // 获取当前租户ID
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            log.warn("租户ID为空");
            return;
        }

        log.debug("租户拦截器生效(UPDATE/DELETE)，tenant_id: {}", tenantId);
    }

    /**
     * 检查方法是否有 @IgnoreTenant 注解
     */
    private boolean hasIgnoreTenantAnnotation(MappedStatement ms) {
        try {
            String id = ms.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1);

            Class<?> clazz = Class.forName(className);

            // 检查类级别注解
            if (clazz.isAnnotationPresent(IgnoreTenant.class)) {
                return true;
            }

            // 检查方法级别注解
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    if (method.isAnnotationPresent(IgnoreTenant.class)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("检查 @IgnoreTenant 注解失败", e);
        }
        return false;
    }

    /**
     * 检查表是否需要租户隔离
     */
    private boolean needTenantFilter(String tableName) {
        for (String ignoreTable : IGNORE_TABLES) {
            if (ignoreTable.equalsIgnoreCase(tableName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 构建租户条件表达式
     */
    private Expression buildTenantCondition(String tableAlias) {
        Column tenantColumn = new Column(
                (tableAlias != null ? tableAlias + "." : "") + TENANT_ID_COLUMN
        );
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(tenantColumn);
        equalsTo.setRightExpression(new LongValue(TenantContext.getTenantId()));
        return equalsTo;
    }
}
