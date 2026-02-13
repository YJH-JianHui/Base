package cn.kmdckj.base.service.permission;

import java.util.List;

/**
 * 功能权限校验与查询服务接口。
 */
public interface PermissionService {

    /**
     * 获取用户权限列表（自动缓存）
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 刷新用户权限缓存
     */
    List<String> refreshUserPermissions(Long userId);
}
