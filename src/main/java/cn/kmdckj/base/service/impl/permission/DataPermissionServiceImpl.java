package cn.kmdckj.base.service.impl.permission;

import cn.kmdckj.base.common.constant.CacheConstants;
import cn.kmdckj.base.entity.DataPermissionRule;
import cn.kmdckj.base.entity.Department;
import cn.kmdckj.base.entity.Resource;
import cn.kmdckj.base.entity.UserRole;
import cn.kmdckj.base.mapper.DataPermissionRuleMapper;
import cn.kmdckj.base.mapper.DepartmentMapper;
import cn.kmdckj.base.mapper.ResourceMapper;
import cn.kmdckj.base.mapper.UserRoleMapper;
import cn.kmdckj.base.service.permission.DataPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据权限计算与SQL生成服务接口实现类。
 */
@Slf4j
@Service
public class DataPermissionServiceImpl implements DataPermissionService {

    @Autowired
    private DataPermissionRuleMapper dataPermissionRuleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    @Cacheable(
            value = CacheConstants.CACHE_DATA_SCOPE,
            key = "#userId + ':' + #entityCode",
            unless = "#result == null"
    )
    public String buildDataScopeCondition(Long userId, Long deptId,
                                          String entityCode, String entityAlias) {
        // 1. 查询用户的所有角色ID
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            // 没有角色，返回空结果条件
            return "1 = 0";
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        // 2. 查询这些角色对该实体的数据权限规则
        // 先查到 entityCode 对应的 resourceId
        Resource resource = resourceMapper.selectOne(
                new LambdaQueryWrapper<Resource>()
                        .eq(Resource::getResourcePath, entityCode)
                        .eq(Resource::getResourceType, "entity")
        );
        if (resource == null) {
            return ""; // 没找到资源配置，不限制
        }

        // 再用 resourceId 过滤规则
        List<DataPermissionRule> rules = dataPermissionRuleMapper.selectList(
                new LambdaQueryWrapper<DataPermissionRule>()
                        .in(DataPermissionRule::getRoleId, roleIds)
                        .eq(DataPermissionRule::getResourceId, resource.getId())
        );

        if (rules.isEmpty()) {
            return "1 = 0";
        }

        // 3. 取权限最大的规则（优先级：ALL > DEPT_AND_CHILD > DEPT > CUSTOM > SELF）
        DataPermissionRule bestRule = selectBestRule(rules);
        String alias = (entityAlias == null || entityAlias.isEmpty()) ? "" : entityAlias + ".";

        // 4. 根据规则类型构建 SQL 条件
        return buildConditionByType(bestRule, alias, userId, deptId);
    }

    /**
     * 选取权限最大的规则
     */
    private DataPermissionRule selectBestRule(List<DataPermissionRule> rules) {
        Map<String, Integer> priority = Map.of(
                "ALL", 5,
                "DEPT_AND_CHILD", 4,
                "DEPT", 3,
                "CUSTOM", 2,
                "SELF", 1
        );
        return rules.stream()
                .max(Comparator.comparingInt(r ->
                        priority.getOrDefault(r.getDataScopeType(), 0)))
                .orElse(rules.get(0));
    }

    /**
     * 根据规则类型构建 SQL 条件
     */
    private String buildConditionByType(DataPermissionRule rule, String alias,
                                        Long userId, Long deptId) {
        switch (rule.getDataScopeType()) {
            case "ALL":
                // 本租户全部数据，租户拦截器已处理，不需要额外条件
                return "";

            case "DEPT":
                return String.format("%sdept_id = %d", alias, deptId);

            case "DEPT_AND_CHILD":
                // 查询本部门及所有下级部门ID
                List<Long> deptIds = getDeptAndChildIds(deptId);
                String deptIdsStr = deptIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                return String.format("%sdept_id IN (%s)", alias, deptIdsStr);

            case "SELF":
                return String.format("%screate_user_id = %d", alias, userId);

            case "CUSTOM":
                String customDeptIds = rule.getCustomDeptIds();
                if (customDeptIds == null || customDeptIds.isEmpty()) {
                    return "1 = 0";
                }
                // customDeptIds 格式是 JSON 数组字符串 "[2,3,4]"，转成 "2,3,4"
                String ids = customDeptIds.replace("[", "").replace("]", "").trim();
                return String.format("%sdept_id IN (%s)", alias, ids);

            default:
                return "1 = 0";
        }
    }

    /**
     * 查询本部门及所有下级部门ID
     */
    private List<Long> getDeptAndChildIds(Long deptId) {
        // 利用 dept_path 做前缀查询，效率高
        Department dept = departmentMapper.selectById(deptId);
        if (dept == null) {
            return List.of(deptId);
        }

        // dept_path 格式如 /1/2/5/，查询所有path包含该节点的部门
        String pathPrefix = dept.getDeptPath();
        List<Department> children = departmentMapper.selectList(
                new LambdaQueryWrapper<Department>()
                        .like(Department::getDeptPath, pathPrefix)
        );

        List<Long> ids = children.stream()
                .map(Department::getId)
                .collect(Collectors.toList());

        if (!ids.contains(deptId)) {
            ids.add(deptId);
        }
        return ids;
    }
}