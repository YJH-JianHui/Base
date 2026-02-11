package cn.kmdckj.base.common.exception;

import cn.kmdckj.base.common.result.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 系统基础异常基类。
 * 继承自 {@link RuntimeException}，是系统中所有自定义业务异常、权限异常等运行时异常的父类。
 * 包含了错误码 (code) 和错误信息 (message)，用于统一全局异常处理的响应结构。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     * 通常对应 {@link ResultCode#getCode()}
     */
    private String code;

    /**
     * 错误消息
     * 具体的异常描述信息
     */
    private String message;

    /**
     * 无参构造函数
     */
    public BaseException() {
        super();
    }

    /**
     * 根据错误消息构造异常
     * 默认使用 {@link ResultCode#SYSTEM_ERROR} 的错误码
     *
     * @param message 错误消息
     */
    public BaseException(String message) {
        super(message);
        this.code = ResultCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    /**
     * 根据错误码和错误消息构造异常
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BaseException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 根据响应枚举构造异常
     * 直接使用枚举中定义的 code 和 message
     *
     * @param resultCode 响应码枚举
     */
    public BaseException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 根据响应枚举和自定义消息构造异常
     * 使用枚举中的 code，但覆盖枚举默认的 message
     *
     * @param resultCode 响应码枚举
     * @param message    自定义错误消息
     */
    public BaseException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * 根据错误消息和原始异常构造异常
     * 默认使用 {@link ResultCode#SYSTEM_ERROR} 的错误码
     *
     * @param message 错误消息
     * @param cause   原始异常堆栈
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    /**
     * 根据响应枚举和原始异常构造异常
     *
     * @param resultCode 响应码枚举
     * @param cause      原始异常堆栈
     */
    public BaseException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
}