package cn.kmdckj.base.aspect;

import cn.kmdckj.base.annotation.IgnoreTenant;
import cn.kmdckj.base.common.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 租户切面
 * 处理 @IgnoreTenant 注解，临时忽略租户隔离
 */
@Slf4j
@Aspect
@Component
@Order(1) // 优先级最高，最先执行
public class TenantAspect {

    /**
     * 环绕通知：处理忽略租户隔离
     */
    @Around("@annotation(cn.kmdckj.base.annotation.IgnoreTenant) || " +
            "@within(cn.kmdckj.base.annotation.IgnoreTenant)")
    public Object handleIgnoreTenant(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 检查是否有 @IgnoreTenant 注解
        boolean hasAnnotation = method.isAnnotationPresent(IgnoreTenant.class) ||
                method.getDeclaringClass().isAnnotationPresent(IgnoreTenant.class);

        if (!hasAnnotation) {
            return joinPoint.proceed();
        }

        // 保存原始状态
        Boolean originalIgnore = TenantContext.isIgnoreTenant();

        try {
            // 设置忽略租户隔离
            TenantContext.setIgnoreTenant(true);
            log.debug("方法 {} 忽略租户隔离", method.getName());

            // 执行方法
            return joinPoint.proceed();
        } finally {
            // 恢复原始状态
            TenantContext.setIgnoreTenant(originalIgnore);
        }
    }
}