package cn.kmdckj.base.service.role;

import java.util.List;

/**
 * 角色管理服务接口。
 */
public interface RoleService {

    /**
     * 获取用户角色列表（自动缓存）
     */
    List<String> getUserRoles(Long userId);

    /**
     * 刷新用户角色缓存
     */
    List<String> refreshUserRoles(Long userId);
}
