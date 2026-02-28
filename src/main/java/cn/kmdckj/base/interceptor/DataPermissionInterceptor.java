package cn.kmdckj.base.interceptor;

import cn.kmdckj.base.annotation.DataScope;
import cn.kmdckj.base.common.constant.DataScopeType;
import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.service.permission.DataPermissionService;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据权限拦截器
 * 根据用户角色的数据权限规则，自动在SQL中添加数据过滤条件
 */
@Slf4j
@Component
public class DataPermissionInterceptor implements InnerInterceptor {

    @Autowired
    @Lazy
    private DataPermissionService dataPermissionService;

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql)
            throws SQLException {

        // 获取方法上的 @DataScope 注解
        DataScope dataScope = getDataScopeAnnotation(ms);
        if (dataScope == null) {
            return;
        }

        // 超管跳过数据权限过滤
        if (SecurityContext.isSuperAdmin()) {
            log.debug("超管用户，跳过数据权限过滤");
            return;
        }

        // 获取当前用户信息
        Long userId = SecurityContext.getUserId();
        Long deptId = SecurityContext.getDeptId();
        if (userId == null) {
            log.warn("用户ID为空，跳过数据权限过滤");
            return;
        }

        // 查询该用户对该实体的数据权限规则
        String condition = dataPermissionService.buildDataScopeCondition(
                userId, deptId, dataScope.entityCode(), dataScope.entityAlias()
        );

        if (condition == null || condition.isEmpty()) {
            return;
        }

        // 用 JSQLParser 把条件拼到原始 SQL 的 WHERE 中
        try {
            String originalSql = boundSql.getSql();
            String newSql = injectCondition(originalSql, condition);
            // 反射替换 BoundSql 中的 sql 字段
            Field sqlField = BoundSql.class.getDeclaredField("sql");
            sqlField.setAccessible(true);
            sqlField.set(boundSql, newSql);
            log.debug("数据权限注入SQL条件: {}", condition);
        } catch (Exception e) {
            log.error("数据权限SQL注入失败", e);
        }
    }

    /**
     * 将条件注入到 SQL 的 WHERE 子句
     */
    private String injectCondition(String originalSql, String condition) throws JSQLParserException {
        Select select = (Select) CCJSqlParserUtil.parse(originalSql);
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

        Expression where = plainSelect.getWhere();
        Expression newCondition = CCJSqlParserUtil.parseCondExpression(condition);

        if (where == null) {
            plainSelect.setWhere(newCondition);
        } else {
            plainSelect.setWhere(new AndExpression(where, newCondition));
        }
        return select.toString();
    }

    /**
     * 获取方法上的 @DataScope 注解
     */
    private DataScope getDataScopeAnnotation(MappedStatement ms) {
        try {
            String id = ms.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1);

            Class<?> clazz = Class.forName(className);

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method.getAnnotation(DataScope.class);
                }
            }
        } catch (Exception e) {
            log.error("获取 @DataScope 注解失败", e);
        }
        return null;
    }

    /**
     * 构建数据权限SQL条件（示例）
     */
    private String buildDataScopeCondition(DataScopeType scopeType, String entityAlias,
                                           Long userId, Long deptId, List<Long> customDeptIds) {
        String alias = entityAlias.isEmpty() ? "" : entityAlias + ".";

        switch (scopeType) {
            case ALL:
                // 本租户全部数据，不需要额外条件（租户拦截器已处理）
                return "";

            case DEPT:
                // 本部门数据
                return String.format(" AND %sdept_id = %d", alias, deptId);

            case DEPT_AND_CHILD:
                // 本部门及下级部门数据（需要查询部门树）
                return String.format(" AND %sdept_id IN (SELECT id FROM department WHERE dept_path LIKE '%%/%d/%%')",
                        alias, deptId);

            case SELF:
                // 仅本人数据
                return String.format(" AND %screate_user_id = %d", alias, userId);

            case CUSTOM:
                // 自定义部门
                if (customDeptIds == null || customDeptIds.isEmpty()) {
                    return " AND 1=0"; // 没有自定义部门，返回空
                }
                String deptIdsStr = customDeptIds.toString().replace("[", "(").replace("]", ")");
                return String.format(" AND %sdept_id IN %s", alias, deptIdsStr);

            default:
                return "";
        }
    }
}
