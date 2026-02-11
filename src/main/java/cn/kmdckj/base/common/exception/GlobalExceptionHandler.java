package cn.kmdckj.base.common.exception;

import cn.kmdckj.base.common.result.Result;
import cn.kmdckj.base.common.result.ResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器。
 * 基于 {@link RestControllerAdvice} 实现对系统内控制器抛出异常的统一捕获与处理。
 * 该类通过 AOP 机制拦截所有受管控制器的异常，将其封装为标准的 {@link Result} 响应结构，
 * 确保前后端交互协议的一致性，同时根据异常性质进行分类日志记录。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 捕获 {@link BusinessException}，记录业务警告日志，并返回业务定义的错误码与描述。
     *
     * @param e 业务异常
     * @return 统一响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理权限异常
     * 捕获 {@link PermissionException}，记录权限冲突日志。通常涉及未登录、功能越权或数据越权。
     *
     * @param e 权限异常
     * @return 统一响应结果
     */
    @ExceptionHandler(PermissionException.class)
    public Result<?> handlePermissionException(PermissionException e) {
        log.warn("权限异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     * 捕获使用 {@code @Valid} 注解对 JavaBean 进行校验失败时产生的 {@link MethodArgumentNotValidException}。
     * 将所有字段的错误信息拼接后返回。
     *
     * @param e 方法参数校验异常
     * @return 统一响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验异常: {}", message);
        return Result.error(ResultCode.REQUEST_PARAM_ERROR, message);
    }

    /**
     * 处理参数绑定异常
     * 捕获请求参数无法正确绑定到目标对象时抛出的 {@link BindException}。
     *
     * @param e 参数绑定异常
     * @return 统一响应结果
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定异常: {}", message);
        return Result.error(ResultCode.REQUEST_PARAM_ERROR, message);
    }

    /**
     * 处理约束违反异常
     * 捕获在方法级参数（如简单类型参数）上使用校验注解失败时抛出的 {@link ConstraintViolationException}。
     *
     * @param e 约束违反异常
     * @return 统一响应结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("约束违反异常: {}", message);
        return Result.error(ResultCode.REQUEST_PARAM_ERROR, message);
    }

    /**
     * 处理非法参数异常
     * 捕获标准库抛出的 {@link IllegalArgumentException}。
     *
     * @param e 非法参数异常
     * @return 统一响应结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: {}", e.getMessage());
        return Result.error(ResultCode.REQUEST_PARAM_ERROR, e.getMessage());
    }

    /**
     * 处理空指针异常
     * 捕获并记录 {@link NullPointerException} 的堆栈信息。处于安全考虑，不向前端暴露详细堆栈。
     *
     * @param e 空指针异常
     * @return 统一响应结果
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return Result.error(ResultCode.SYSTEM_ERROR, "系统内部错误");
    }

    /**
     * 处理资源不存在异常 (404 Not Found).
     * 捕获 {@link NoHandlerFoundException} 或 {@link NoResourceFoundException}。
     * 当访问未定义的接口路径或不存在的静态资源时触发。
     *
     * @param e 异常对象
     * @return 统一响应结果
     */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public Result<?> handleNotFoundException(Exception e) {
        log.warn("接口路径或资源不存在: {}", e.getMessage());
        return Result.error(ResultCode.REQUEST_PATH_NOT_EXIST, "请求的接口路径不存在");
    }

    /**
     * 处理请求方法不支持异常 (405 Method Not Allowed).
     * 捕获 {@link HttpRequestMethodNotSupportedException}。
     * 当接口定义为 POST 但前端使用 GET 访问时触发。
     *
     * @param e 请求方法不支持异常
     * @return 统一响应结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMessage());
        return Result.error(ResultCode.REQUEST_METHOD_NOT_SUPPORTED, "不支持的请求方式: " + e.getMethod());
    }

    /**
     * 处理系统兜底异常
     * 捕获所有未被明确识别的运行时异常或受检异常 {@link Exception}。
     * 记录详细错误堆栈并向用户提示通用错误消息。
     *
     * @param e 系统异常
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统未知异常", e);
        return Result.error(ResultCode.SYSTEM_ERROR, "系统内部错误，请联系管理员");
    }
}