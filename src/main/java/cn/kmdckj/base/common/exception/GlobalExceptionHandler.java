package cn.kmdckj.base.common.exception;

import cn.kmdckj.base.common.result.Result;
import cn.kmdckj.base.common.result.ResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author kmdck
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)  // 业务异常返回200，通过code区分
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 权限异常
     */
    @ExceptionHandler(PermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)  // 403
    public Result<?> handlePermissionException(PermissionException e) {
        log.warn("权限异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常 - @Valid 注解校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验异常: {}", message);
        return Result.error(ResultCode.REQUEST_PARAM_ERROR, message);
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定异常: {}", message);
        return Result.error(ResultCode.REQUEST_PARAM_ERROR, message);
    }

    /**
     * 约束违反异常 - @Validated 注解校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("约束违反异常: {}", message);
        return Result.error(ResultCode.REQUEST_PARAM_ERROR, message);
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: {}", e.getMessage());
        return Result.error(ResultCode.REQUEST_PARAM_ERROR, e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
    public Result<?> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        // 生产环境不暴露具体错误信息
        return Result.error(ResultCode.SYSTEM_ERROR, "系统内部错误");
    }

    /**
     * 不支持的请求方法异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED) // 405
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("不支持的请求方法: {} {}", e.getMethod(), e.getMessage());
        return Result.error(ResultCode.REQUEST_METHOD_NOT_SUPPORTED, "该接口不支持 " + e.getMethod() + " 请求");
    }

    /**
     * 静态资源不存在异常 (404)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("资源不存在: {}", e.getResourcePath());
        return Result.error(ResultCode.REQUEST_PATH_NOT_EXIST, "资源不存在: " + e.getResourcePath());
    }

    /**
     * Redis相关异常
     */
    @ExceptionHandler({
            org.springframework.data.redis.connection.PoolException.class,
            org.springframework.data.redis.RedisConnectionFailureException.class,
            org.springframework.data.redis.RedisSystemException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleRedisException(RuntimeException e) {
        log.error("Redis服务异常", e);
        return Result.error(ResultCode.CACHE_SERVICE_ERROR, "缓存服务异常");
    }

    /**
     * MyBatis系统异常
     */
    @ExceptionHandler(org.mybatis.spring.MyBatisSystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleMyBatisSystemException(org.mybatis.spring.MyBatisSystemException e) {
        log.error("MyBatis系统异常", e);
        Throwable cause = e.getCause();
        if (cause instanceof org.apache.ibatis.exceptions.PersistenceException) {
            Throwable secondCause = cause.getCause();
            if (secondCause instanceof java.sql.SQLTimeoutException) {
                 return Result.error(ResultCode.DATABASE_SERVICE_TIMEOUT);
            }
        }
        return Result.error(ResultCode.DATABASE_SERVICE_ERROR);
    }

    /**
     * 数据库唯一键冲突异常
     */
    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleDuplicateKeyException(org.springframework.dao.DuplicateKeyException e) {
        log.warn("数据重复: {}", e.getMessage());
        return Result.error(ResultCode.DATABASE_PRIMARY_KEY_CONFLICT, "数据已存在，请勿重复操作");
    }

    /**
     * 数据库操作异常
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleDataAccessException(org.springframework.dao.DataAccessException e) {
        log.error("数据库操作异常", e);
        if (e.getCause() instanceof java.sql.SQLTimeoutException) {
            return Result.error(ResultCode.DATABASE_SERVICE_TIMEOUT);
        }
        return Result.error(ResultCode.DATABASE_SERVICE_ERROR);
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleRuntimeException(RuntimeException e) {
        // 如果是自定义异常，不应该走到这里
        // 打印完整堆栈信息便于排查
        log.error("运行时异常: {}", e.getMessage(), e);
        return Result.error(ResultCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
    }

    /**
     * 其他未捕获的异常
     * 最后执行，兜底处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("未知异常: {}", e.getMessage(), e);
        return Result.error(ResultCode.SYSTEM_ERROR, "系统内部错误，请联系管理员");
    }
}