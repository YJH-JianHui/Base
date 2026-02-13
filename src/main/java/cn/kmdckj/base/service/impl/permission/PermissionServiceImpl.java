package cn.kmdckj.base.service.impl.permission;

import cn.kmdckj.base.common.constant.CacheConstants;
import cn.kmdckj.base.mapper.PermissionMapper;
import cn.kmdckj.base.service.permission.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    @Cacheable(
            value = CacheConstants.CACHE_USER_PERMISSION,
            key = "#userId",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<String> getUserPermissions(Long userId) {
        log.info("从数据库查询用户权限，userId: {}", userId);
        return permissionMapper.selectUserPermissions(userId);
    }

    @Override
    @CachePut(
            value = CacheConstants.CACHE_USER_PERMISSION,
            key = "#userId"
    )
    public List<String> refreshUserPermissions(Long userId) {
        log.info("刷新用户权限缓存，userId: {}", userId);
        return permissionMapper.selectUserPermissions(userId);
    }
}
