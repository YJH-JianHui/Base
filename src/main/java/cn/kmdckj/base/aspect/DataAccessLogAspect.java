package cn.kmdckj.base.aspect;

import cn.hutool.json.JSONUtil;
import cn.kmdckj.base.annotation.DataScope;
import cn.kmdckj.base.annotation.Log;
import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.common.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据访问日志切面
 * 记录敏感数据的访问日志
 *
 * @author kmdck
 */
@Slf4j
@Aspect
@Component
public class DataAccessLogAspect {

    /**
     * 环绕通知：记录数据访问日志
     * 拦截带有 @DataScope 或 @Log 注解的方法
     */
    @Around("@annotation(cn.kmdckj.base.annotation.DataScope) || @annotation(cn.kmdckj.base.annotation.Log)")
    public Object recordDataAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        DataScope dataScope = method.getAnnotation(DataScope.class);
        Log logAnnotation = method.getAnnotation(Log.class);

        // 执行方法
        Object result = null;
        Exception exception = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            // 计算响应时间
            long responseTime = System.currentTimeMillis() - startTime;

            // 异步记录日志
            recordLogAsync(joinPoint, dataScope, logAnnotation, result, exception, responseTime);
        }
    }

    /**
     * 异步记录日志
     */
    @Async("logTaskExecutor")
    public void recordLogAsync(ProceedingJoinPoint joinPoint, DataScope dataScope,
                               Log logAnnotation, Object result, Exception exception,
                               long responseTime) {
        try {
            Map<String, Object> logData = buildLogData(joinPoint, dataScope, logAnnotation,
                    result, exception, responseTime);

            // TODO: 保存到数据库 data_access_log 表
            // dataAccessLogService.save(logData);

            log.info("数据访问日志: {}", JSONUtil.toJsonStr(logData));
        } catch (Exception e) {
            log.error("记录数据访问日志失败", e);
        }
    }

    /**
     * 构建日志数据
     */
    private Map<String, Object> buildLogData(ProceedingJoinPoint joinPoint, DataScope dataScope,
                                             Log logAnnotation, Object result, Exception exception,
                                             long responseTime) {
        Map<String, Object> logData = new HashMap<>();

        // 用户信息
        logData.put("userId", SecurityContext.getUserId());
        logData.put("username", SecurityContext.getUsername());
        logData.put("tenantId", TenantContext.getTenantId());

        // 方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        logData.put("className", signature.getDeclaringTypeName());
        logData.put("methodName", signature.getName());

        // 请求信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logData.put("requestUrl", request.getRequestURI());
            logData.put("requestMethod", request.getMethod());
            logData.put("ipAddress", getIpAddress(request));
            logData.put("userAgent", request.getHeader("User-Agent"));
        }

        // 操作信息
        if (logAnnotation != null) {
            logData.put("module", logAnnotation.module());
            logData.put("operation", logAnnotation.type().getCode());
            logData.put("description", logAnnotation.description());

            // 请求参数
            if (logAnnotation.saveRequest()) {
                logData.put("requestParams", Arrays.toString(joinPoint.getArgs()));
            }

            // 响应结果
            if (logAnnotation.saveResponse() && result != null) {
                logData.put("responseData", JSONUtil.toJsonStr(result));
            }
        }

        // 数据权限信息
        if (dataScope != null) {
            logData.put("entityCode", dataScope.entityCode());
            logData.put("entityAlias", dataScope.entityAlias());
        }

        // 时间信息
        logData.put("accessTime", LocalDateTime.now());
        logData.put("responseTime", responseTime);

        // 异常信息
        if (exception != null) {
            logData.put("exception", exception.getClass().getName());
            logData.put("exceptionMessage", exception.getMessage());
        }

        return logData;
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}