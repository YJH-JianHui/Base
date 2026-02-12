package cn.kmdckj.base.service.auth;

import cn.kmdckj.base.dto.auth.LoginDTO;
import cn.kmdckj.base.dto.auth.UserInfoDTO;

/**
 * 认证授权服务接口。
 * 定义登录、登出、Token 验证逻辑。
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 用户信息（包含Token）
     */
    UserInfoDTO login(LoginDTO loginDTO);

    /**
     * 用户登出
     *
     * @param token JWT Token
     * @return 是否成功
     */
    boolean logout(String token);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserInfoDTO getCurrentUserInfo();
}
