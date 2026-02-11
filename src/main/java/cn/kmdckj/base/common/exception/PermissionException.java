package cn.kmdckj.base.common.exception;

import cn.kmdckj.base.common.result.ResultCode;

import java.io.Serial;

/**
 * 权限校验异常。
 * 用于处理系统中的各类权限冲突情况，包括但不限于：
 * <ul>
 *     <li>功能权限校验不通过（无接口访问权限）</li>
 *     <li>数据权限校验不通过（尝试访问无权查看的行数据）</li>
 *     <li>授权操作越权（尝试分配自身不拥有的权限或更高级别的角色）</li>
 *     <li>身份验证失效或未登录</li>
 * </ul>
 */
public class PermissionException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 无参构造函数
     * 默认使用 {@link ResultCode#ACCESS_UNAUTHORIZED} 作为错误状态
     */
    public PermissionException() {
        super(ResultCode.ACCESS_UNAUTHORIZED);
    }

    /**
     * 根据自定义错误消息构造权限异常
     * 错误码固定为 {@link ResultCode#ACCESS_UNAUTHORIZED} 的编码
     *
     * @param message 具体的权限错误提示信息
     */
    public PermissionException(String message) {
        super(ResultCode.ACCESS_UNAUTHORIZED.getCode(), message);
    }

    /**
     * 根据响应枚举构造权限异常
     *
     * @param resultCode 响应码枚举
     */
    public PermissionException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 根据响应枚举和自定义消息构造权限异常
     * 适用于需要根据具体越权场景动态生成提示消息的场景
     *
     * @param resultCode 响应码枚举
     * @param message    自定义权限错误消息
     */
    public PermissionException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    /**
     * 根据错误消息和原始异常构造权限异常
     *
     * @param message 权限错误消息
     * @param cause   原始异常堆栈
     */
    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}