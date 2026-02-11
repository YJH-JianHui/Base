package cn.kmdckj.base.interceptor;

import cn.kmdckj.base.annotation.DataScope;
import cn.kmdckj.base.common.constant.DataScopeType;
import cn.kmdckj.base.common.context.SecurityContext;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

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

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql)
            throws SQLException {

        // 获取方法上的 @DataScope 注解
        DataScope dataScope = getDataScopeAnnotation(ms);
        if (dataScope == null) {
            return;
        }

        // 获取当前用户信息
        Long userId = SecurityContext.getUserId();
        Long deptId = SecurityContext.getDeptId();
        if (userId == null) {
            log.warn("用户ID为空，跳过数据权限过滤");
            return;
        }

        // 获取用户的数据权限规则（这里简化处理，实际需要查询数据库）
        String entityCode = dataScope.entityCode();
        String entityAlias = dataScope.entityAlias();

        log.debug("数据权限拦截器生效，entityCode: {}, userId: {}, deptId: {}",
                entityCode, userId, deptId);

        // TODO:这里简化处理，实际实现需要：
        // 1. 根据 userId 查询用户的角色
        // 2. 根据角色查询 data_permission_rule 表，获取数据权限规则
        // 3. 根据 data_scope_type 构建不同的SQL条件
        // 4. 使用 JSQLParser 解析SQL并添加条件
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
