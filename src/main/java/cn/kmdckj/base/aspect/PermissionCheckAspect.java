package cn.kmdckj.base.aspect;

import cn.kmdckj.base.annotation.RequiresPermission;
import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.common.exception.PermissionException;
import cn.kmdckj.base.common.result.ResultCode;
import cn.kmdckj.base.service.permission.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限校验切面
 * 拦截 @RequiresPermission 注解，进行权限校验
 */
@Slf4j
@Aspect
@Component
public class PermissionCheckAspect {

    @Autowired
    private PermissionService permissionService;

    /**
     * 前置通知：执行方法前进行权限校验
     */
    @Before("@annotation(cn.kmdckj.base.annotation.RequiresPermission)")
    public void checkPermission(JoinPoint joinPoint) {
        // 获取当前用户ID
        Long userId = SecurityContext.getUserId();
        if (userId == null) {
            log.warn("用户未登录，权限校验失败");
            throw new PermissionException(ResultCode.USER_LOGIN_EXPIRED);
        }

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        RequiresPermission requiresPermission = method.getAnnotation(RequiresPermission.class);
        if (requiresPermission == null) {
            return;
        }

        // 获取需要的权限码
        String[] requiredPermissions = requiresPermission.value();
        if (requiredPermissions.length == 0) {
            return;
        }

        // 获取用户拥有的权限
        Set<String> userPermissions = getUserPermissions(userId);

        // 根据逻辑关系校验权限
        boolean hasPermission = false;
        if (requiresPermission.logical() == RequiresPermission.Logical.AND) {
            // AND：需要同时拥有所有权限
            hasPermission = userPermissions.containsAll(Arrays.asList(requiredPermissions));
        } else {
            // OR：拥有任意一个权限即可
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            log.warn("用户 {} 权限不足，需要权限: {}, 拥有权限: {}",
                    userId, Arrays.toString(requiredPermissions), userPermissions);
            throw new PermissionException(ResultCode.ACCESS_UNAUTHORIZED,
                    "权限不足！");
        }

        log.debug("用户 {} 权限校验通过，访问方法: {}", userId, method.getName());
    }

    /**
     * 获取用户权限
     */
    private Set<String> getUserPermissions(Long userId) {
        List<String> permissions = permissionService.getUserPermissions(userId);
        return new HashSet<>(permissions != null ? permissions : List.of());
    }
}