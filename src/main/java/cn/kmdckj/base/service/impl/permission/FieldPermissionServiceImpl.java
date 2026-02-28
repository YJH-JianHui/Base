package cn.kmdckj.base.service.impl.permission;

import cn.kmdckj.base.common.constant.CacheConstants;
import cn.kmdckj.base.common.constant.FieldPermissionType;
import cn.kmdckj.base.entity.FieldPermissionRule;
import cn.kmdckj.base.entity.FieldResource;
import cn.kmdckj.base.entity.UserRole;
import cn.kmdckj.base.mapper.FieldPermissionRuleMapper;
import cn.kmdckj.base.mapper.FieldResourceMapper;
import cn.kmdckj.base.mapper.UserRoleMapper;
import cn.kmdckj.base.service.permission.FieldPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字段权限处理服务接口实现类。
 */
@Slf4j
@Service
public class FieldPermissionServiceImpl implements FieldPermissionService {
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private FieldPermissionRuleMapper fieldPermissionRuleMapper;

    @Autowired
    private FieldResourceMapper fieldResourceMapper;

    @Override
    @Cacheable(
            value = CacheConstants.CACHE_FIELD_PERMISSION,
            key = "#userId + ':' + #entityCode",
            unless = "#result == null"
    )
    public Map<String, FieldPermissionInfo> getUserFieldPermissions(Long userId, String entityCode) {
        // 1. 查询用户的所有角色ID
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return Map.of();
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        // 2. 查询该实体下所有字段资源
        List<FieldResource> fieldResources = fieldResourceMapper.selectList(
                new LambdaQueryWrapper<FieldResource>()
                        .eq(FieldResource::getEntityCode, entityCode)
        );
        if (fieldResources.isEmpty()) {
            return Map.of();
        }

        // 构建 fieldResourceId → fieldName 映射
        Map<Long, String> resourceIdToFieldName = fieldResources.stream()
                .collect(Collectors.toMap(FieldResource::getId, FieldResource::getFieldName));

        List<Long> fieldResourceIds = new ArrayList<>(resourceIdToFieldName.keySet());

        // 3. 查询这些角色对这些字段的权限规则
        List<FieldPermissionRule> rules = fieldPermissionRuleMapper.selectList(
                new LambdaQueryWrapper<FieldPermissionRule>()
                        .in(FieldPermissionRule::getRoleId, roleIds)
                        .in(FieldPermissionRule::getFieldResourceId, fieldResourceIds)
        );
        if (rules.isEmpty()) {
            return Map.of();
        }

        // 4. 合并规则：同一字段有多个角色规则时，取权限等级最高的
        // 按 fieldResourceId 分组，取最高权限
        Map<Long, FieldPermissionRule> bestRules = new HashMap<>();
        for (FieldPermissionRule rule : rules) {
            Long fieldResourceId = rule.getFieldResourceId();
            FieldPermissionRule existing = bestRules.get(fieldResourceId);
            if (existing == null) {
                bestRules.put(fieldResourceId, rule);
            } else {
                // 比较权限等级，取更高的
                FieldPermissionType currentType = FieldPermissionType.fromCode(rule.getPermissionType());
                FieldPermissionType existingType = FieldPermissionType.fromCode(existing.getPermissionType());
                if (currentType.isHigherThan(existingType)) {
                    bestRules.put(fieldResourceId, rule);
                }
            }
        }

        // 5. 转换成 fieldName → FieldPermissionInfo 的Map
        Map<String, FieldPermissionInfo> result = new HashMap<>();
        for (Map.Entry<Long, FieldPermissionRule> entry : bestRules.entrySet()) {
            String fieldName = resourceIdToFieldName.get(entry.getKey());
            FieldPermissionRule rule = entry.getValue();
            if (fieldName != null) {
                result.put(fieldName, new FieldPermissionInfo(
                        FieldPermissionType.fromCode(rule.getPermissionType()),
                        rule.getMaskRule()
                ));
            }
        }

        log.debug("用户 {} 对实体 {} 的字段权限: {}", userId, entityCode, result);
        return result;
    }

    @Override
    @CachePut(
            value = CacheConstants.CACHE_FIELD_PERMISSION,
            key = "#userId + ':' + #entityCode"
    )
    public Map<String, FieldPermissionInfo> refreshUserFieldPermissions(Long userId, String entityCode) {
        // 清除缓存重新查询，复用上面的逻辑
        return getUserFieldPermissions(userId, entityCode);
    }
}
