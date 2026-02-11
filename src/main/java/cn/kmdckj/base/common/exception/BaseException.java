package cn.kmdckj.base.common.exception;

/**
 * 系统基础异常基类。
 * <p>
 * 继承自 RuntimeException，所有自定义异常的父类。
 */
public class BaseException extends RuntimeException{
    /**
     * 调用父类构造。
     *
     * @param message the message
     */
    public BaseException(String message) {
        super(message);
    }
}
