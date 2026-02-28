package cn.kmdckj.base.aspect;

import cn.kmdckj.base.annotation.FieldFilter;
import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.common.result.PageResult;
import cn.kmdckj.base.common.result.Result;
import cn.kmdckj.base.service.permission.FieldPermissionService;
import cn.kmdckj.base.service.permission.FieldPermissionService.FieldPermissionInfo;
import cn.kmdckj.base.util.MaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 字段过滤切面
 * 根据用户的字段权限，对返回数据进行过滤和脱敏
 */
@Slf4j
@Aspect
@Component
public class FieldFilterAspect {

    @Autowired
    private FieldPermissionService fieldPermissionService;

    /**
     * 环绕通知：字段权限过滤
     */
    @Around("@annotation(cn.kmdckj.base.annotation.FieldFilter)")
    public Object filterFields(ProceedingJoinPoint joinPoint) throws Throwable {
        // 执行方法
        Object result = joinPoint.proceed();

        // 超管跳过字段过滤
        if (SecurityContext.isSuperAdmin()) {
            return result;
        }

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        FieldFilter fieldFilter = method.getAnnotation(FieldFilter.class);
        if (fieldFilter == null || !fieldFilter.filterResponse()) {
            return result;
        }

        // 获取当前用户ID
        Long userId = SecurityContext.getUserId();
        if (userId == null) {
            log.warn("用户未登录，跳过字段过滤");
            return result;
        }

        // 获取用户的字段权限
        String entityCode = fieldFilter.entityCode();
        Map<String, FieldPermissionInfo> fieldPermissions = getUserFieldPermissions(userId, entityCode);

        // 过滤字段
        return filterResult(result, fieldPermissions);
    }

    /**
     * 过滤返回结果
     */
    private Object filterResult(Object result, Map<String, FieldPermissionInfo> fieldPermissions) {
        if (result == null || fieldPermissions.isEmpty()) {
            return result;
        }

        try {
            // 处理统一返回结果 Result<T>
            if (result instanceof Result) {
                return filterResultObject((Result<?>) result, fieldPermissions);
            }
            // 直接返回的列表
            else if (result instanceof List) {
                return filterList((List<?>) result, fieldPermissions);
            }
            // 单个对象
            else {
                return filterObject(result, fieldPermissions);
            }
        } catch (Exception e) {
            log.error("字段过滤失败", e);
        }

        return result;
    }

    /**
     * 过滤Result对象
     */
    @SuppressWarnings("unchecked")
    private <T> Result<T> filterResultObject(Result<T> result, Map<String, FieldPermissionInfo> fieldPermissions) {
        Object data = result.getData();
        if (data == null) {
            return result;
        }

        // 处理分页结果
        if (data instanceof PageResult) {
            PageResult<Object> pageResult = (PageResult<Object>) data;
            List<?> records = pageResult.getRecords();
            if (records != null && !records.isEmpty()) {
                pageResult.setRecords(filterList(records, fieldPermissions));
            }
            result.setData((T) pageResult);
        }
        // 处理列表结果
        else if (data instanceof List) {
            List<Object> filteredList = filterList((List<?>) data, fieldPermissions);
            result.setData((T) filteredList);
        }
        // 处理单个对象
        else {
            try {
                Object filteredData = filterObject(data, fieldPermissions);
                result.setData((T) filteredData);
            } catch (IllegalAccessException e) {
                log.error("字段过滤失败", e);
            }
        }

        return result;
    }

    /**
     * 过滤列表
     */
    private List<Object> filterList(List<?> list, Map<String, FieldPermissionInfo> fieldPermissions) {
        List<Object> filteredList = new ArrayList<>();
        for (Object item : list) {
            try {
                filteredList.add(filterObject(item, fieldPermissions));
            } catch (IllegalAccessException e) {
                log.error("过滤对象失败", e);
                filteredList.add(item); // 过滤失败时保留原对象
            }
        }
        return filteredList;
    }

    /**
     * 过滤单个对象
     */
    private Object filterObject(Object obj, Map<String, FieldPermissionInfo> fieldPermissions)
            throws IllegalAccessException {
        if (obj == null) {
            return null;
        }

        Class<?> clazz = obj.getClass();

        // 只处理自定义类，跳过基本类型和Java内置类
        if (clazz.isPrimitive() || clazz.getName().startsWith("java.")) {
            return obj;
        }

        // 获取所有字段
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();

            // 跳过serialVersionUID等特殊字段
            if ("serialVersionUID".equals(fieldName)) {
                continue;
            }

            // 获取字段权限
            FieldPermissionInfo permission = fieldPermissions.get(fieldName);
            if (permission == null) {
                // 没有配置权限，默认可见
                continue;
            }

            field.setAccessible(true);
            Object fieldValue = field.get(obj);

            // 根据权限类型处理字段
            switch (permission.getPermissionType()) {
                case HIDDEN:
                    // 隐藏字段，设置为null
                    field.set(obj, null);
                    break;

                case MASKED:
                    // 脱敏显示
                    if (fieldValue != null) {
                        String maskedValue = maskFieldValue(fieldValue.toString(), permission.getMaskRule());
                        field.set(obj, maskedValue);
                    }
                    break;

                case VISIBLE:
                case EDITABLE:
                    // 可见和可编辑，不做处理
                    break;
            }
        }

        return obj;
    }

    /**
     * 脱敏字段值
     */
    private String maskFieldValue(String value, String maskRule) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        if (maskRule == null || maskRule.isEmpty()) {
            // 没有脱敏规则，默认全部脱敏
            return "***";
        }

        return MaskUtil.maskByRule(value, maskRule);
    }

    /**
     * 获取用户的字段权限
     */
    private Map<String, FieldPermissionInfo> getUserFieldPermissions(Long userId, String entityCode) {
        return fieldPermissionService.getUserFieldPermissions(userId, entityCode);
    }
}