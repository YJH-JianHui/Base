package cn.kmdckj.base.service.auth;

import cn.kmdckj.base.dto.auth.LoginDTO;
import cn.kmdckj.base.dto.auth.UserInfoDTO;

/**
 * 认证授权服务接口
 *
 * @author kmdck
 */
public interface AuthService {

    /**
     * 用户登录
     */
    UserInfoDTO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    boolean logout(String token);

    /**
     * 获取当前登录用户信息
     */
    UserInfoDTO getCurrentUserInfo();
}