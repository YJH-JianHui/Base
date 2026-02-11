package cn.kmdckj.base.common.exception;

/**
 * 通用业务异常。
 * <p>
 * 用于处理业务逻辑校验失败等场景。
 */
public class BusinessException extends BaseException {
    /**
     * 调用父类构造。
     *
     * @param message the message
     */
    public BusinessException(String message) {
        super(message);
    }
}
