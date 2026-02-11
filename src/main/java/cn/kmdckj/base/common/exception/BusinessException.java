package cn.kmdckj.base.common.exception;

import cn.kmdckj.base.common.result.ResultCode;

import java.io.Serial;

/**
 * 通用业务异常。
 * 用于处理业务逻辑中可预期的错误情况（如参数校验失败、前置条件不满足、数据状态错误等）。
 * 全局异常处理器通常会将此类异常捕获并返回 HTTP 200 状态码及相应的错误提示。
 */
public class BusinessException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 无参构造函数
     */
    public BusinessException() {
        super();
    }

    /**
     * 根据错误消息构造业务异常
     * 默认使用 {@link ResultCode#USER_ERROR} (用户端错误) 作为错误码。
     *
     * @param message 具体的业务错误提示信息
     */
    public BusinessException(String message) {
        super(ResultCode.USER_ERROR.getCode(), message);
    }

    /**
     * 根据响应枚举构造业务异常
     *
     * @param resultCode 响应码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 根据响应枚举和自定义消息构造业务异常
     * 适用于复用枚举的状态码，但需要动态调整错误信息的场景。
     *
     * @param resultCode 响应码枚举
     * @param message    自定义错误消息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    /**
     * 根据自定义错误码和消息构造业务异常
     *
     * @param code    自定义错误码
     * @param message 自定义错误消息
     */
    public BusinessException(String code, String message) {
        super(code, message);
    }

    /**
     * 根据错误消息和原始异常构造业务异常
     *
     * @param message 错误消息
     * @param cause   原始异常堆栈
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 根据响应枚举和原始异常构造业务异常
     *
     * @param resultCode 响应码枚举
     * @param cause      原始异常堆栈
     */
    public BusinessException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }
}