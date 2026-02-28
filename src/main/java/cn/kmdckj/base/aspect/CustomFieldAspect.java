package cn.kmdckj.base.aspect;

import cn.kmdckj.base.annotation.WithCustomFields;
import cn.kmdckj.base.common.constant.FieldPermissionType;
import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.common.result.Result;
import cn.kmdckj.base.entity.CustomFieldDefine;
import cn.kmdckj.base.entity.base.CustomFieldHolder;
import cn.kmdckj.base.service.customfield.CustomFieldService;
import cn.kmdckj.base.service.permission.FieldPermissionService;
import cn.kmdckj.base.util.MaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义字段切片 实现查询相关实体自动加上自定义字段的内容
 */
@Order(1)
@Slf4j
@Aspect
@Component
public class CustomFieldAspect {

    @Autowired
    private CustomFieldService customFieldService;

    @Autowired
    private FieldPermissionService fieldPermissionService;

    @Around("@annotation(withCustomFields)")
    public Object around(ProceedingJoinPoint pjp, WithCustomFields withCustomFields) throws Throwable {
        Object result = pjp.proceed();
        if (result == null) {
            return null;
        }

        String entityCode = withCustomFields.entityCode();
        String idField = withCustomFields.idField();

        try {
            // 1. 查该实体的自定义字段定义（无定义则跳过）
            List<CustomFieldDefine> defines = customFieldService.getFieldDefines(entityCode);
            if (defines.isEmpty()) {
                return result;
            }

            // 2. 查当前用户对自定义字段的权限
            Long userId = SecurityContext.getUserId();
            Map<String, FieldPermissionService.FieldPermissionInfo> permissions =
                    fieldPermissionService.getUserFieldPermissions(userId, entityCode);

            // 3. 过滤出可见的自定义字段（排除 HIDDEN）
            Set<String> visibleFields = defines.stream()
                    .map(CustomFieldDefine::getFieldCode)
                    .filter(fieldCode -> {
                        FieldPermissionService.FieldPermissionInfo perm = permissions.get(fieldCode);
                        // 无权限配置默认可见
                        return perm == null || perm.getPermissionType() != FieldPermissionType.HIDDEN;
                    })
                    .collect(Collectors.toSet());

            if (visibleFields.isEmpty()) {
                return result;
            }

            // 4. 提取结果中的实体列表
            List<CustomFieldHolder> entities = extractEntities(result);
            if (entities.isEmpty()) {
                return result;
            }

            // 5. 批量查自定义字段值
            List<Long> entityIds = entities.stream()
                    .map(e -> extractId(e, idField))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Map<Long, Map<String, Object>> allValues =
                    customFieldService.batchGetFieldValues(entityCode, entityIds);

            // 6. 填充到实体 + 脱敏处理
            for (CustomFieldHolder entity : entities) {
                Long entityId = extractId(entity, idField);
                Map<String, Object> values = allValues.getOrDefault(entityId, Map.of());

                Map<String, Object> filtered = new LinkedHashMap<>();
                for (String fieldCode : visibleFields) {
                    Object value = values.get(fieldCode);
                    if (value == null) continue;

                    FieldPermissionService.FieldPermissionInfo perm = permissions.get(fieldCode);
                    if (perm != null && perm.getPermissionType() == FieldPermissionType.MASKED
                            && perm.getMaskRule() != null) {
                        value = MaskUtil.maskByRule(value.toString(), perm.getMaskRule());
                    }

                    filtered.put(fieldCode, value);
                }
                entity.setCustomFields(filtered);
            }

        } catch (Exception e) {
            log.error("自定义字段填充失败: entityCode={}", entityCode, e);
            // 不影响主流程，直接返回原始结果
        }

        return result;
    }

    /**
     * 从返回值中提取实体列表
     * 支持：List<Entity>、Result<List<Entity>>、Result<Entity>
     */
    @SuppressWarnings("unchecked")
    private List<CustomFieldHolder> extractEntities(Object result) {
        // 直接是 List
        if (result instanceof List<?> list) {
            return list.stream()
                    .filter(o -> o instanceof CustomFieldHolder)
                    .map(o -> (CustomFieldHolder) o)
                    .collect(Collectors.toList());
        }

        // Result 包装类
        if (result instanceof Result<?> r) {
            Object data = r.getData();
            if (data instanceof List<?> list) {
                return list.stream()
                        .filter(o -> o instanceof CustomFieldHolder)
                        .map(o -> (CustomFieldHolder) o)
                        .collect(Collectors.toList());
            }
            if (data instanceof CustomFieldHolder holder) {
                return List.of(holder);
            }
        }

        return List.of();
    }

    /**
     * 反射获取 id 字段值
     */
    private Long extractId(Object entity, String idField) {
        try {
            Field field = entity.getClass().getDeclaredField(idField);
            field.setAccessible(true);
            Object val = field.get(entity);
            return val instanceof Long l ? l : Long.parseLong(val.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
