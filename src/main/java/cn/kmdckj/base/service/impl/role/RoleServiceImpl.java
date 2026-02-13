package cn.kmdckj.base.service.impl.role;

import cn.kmdckj.base.common.constant.CacheConstants;
import cn.kmdckj.base.mapper.RoleMapper;
import cn.kmdckj.base.service.role.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色管理服务接口实现类
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Cacheable(
            value = CacheConstants.CACHE_USER_ROLE,
            key = "#userId",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<String> getUserRoles(Long userId) {
        log.info("从数据库查询用户角色，userId: {}", userId);
        return roleMapper.selectUserRoles(userId);
    }

    @Override
    @CachePut(
            value = CacheConstants.CACHE_USER_ROLE,
            key = "#userId"
    )
    public List<String> refreshUserRoles(Long userId) {
        log.info("刷新用户角色缓存，userId: {}", userId);
        return roleMapper.selectUserRoles(userId);
    }
}
