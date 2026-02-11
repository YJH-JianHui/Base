package cn.kmdckj.base.common.exception;

/**
 * 权限校验异常。
 * <p>
 * 当用户无权访问资源或数据时抛出。
 */
public class PermissionException extends BaseException {
    /**
     * 调用父类构造。
     *
     * @param message the message
     */
    public PermissionException(String message) {
        super(message);
    }
}
