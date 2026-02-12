package cn.kmdckj.base.service.auth;

import cn.kmdckj.base.dto.auth.LoginDTO;
import cn.kmdckj.base.dto.auth.UserInfoDTO;

import java.util.List;

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

    /**
     * 获取用户权限列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 获取用户角色列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 刷新用户权限缓存
     */
    List<String> refreshUserPermissions(Long userId);

    /**
     * 刷新用户角色缓存
     */
    List<String> refreshUserRoles(Long userId);

    /**
     * 清除用户所有缓存
     */
    void clearUserCache(Long userId);
}