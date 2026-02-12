package cn.kmdckj.base.service.impl.auth;

import cn.kmdckj.base.common.constant.CacheConstants;
import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.common.exception.BusinessException;
import cn.kmdckj.base.common.result.ResultCode;
import cn.kmdckj.base.dto.auth.LoginDTO;
import cn.kmdckj.base.dto.auth.UserInfoDTO;
import cn.kmdckj.base.entity.User;
import cn.kmdckj.base.mapper.UserMapper;
import cn.kmdckj.base.service.auth.AuthService;
import cn.kmdckj.base.service.cache.CacheService;
import cn.kmdckj.base.util.PasswordUtil;
import cn.kmdckj.base.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证授权服务实现类
 * 使用 Spring Cache 注解 + CacheService
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CacheService cacheService;

    /**
     * 用户登录
     */
    @Override
    public UserInfoDTO login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }

        // 2. 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 3. 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_FROZEN);
        }

        // 4. 查询用户权限和角色（这里会自动缓存）
        List<String> permissions = getUserPermissions(user.getId());
        List<String> roles = getUserRoles(user.getId());

        // 5. 生成JWT Token
        String token = TokenUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getTenantId(),
                user.getDeptId()
        );

        // 6. 将Token存入缓存（使用CacheService）
        cacheService.put(
                CacheConstants.CACHE_LOGIN_TOKEN,
                user.getId().toString(),
                token
        );

        // 7. 构建返回结果
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .tenantId(user.getTenantId())
                .deptId(user.getDeptId())
                .token(token)
                .permissions(permissions)
                .roles(roles)
                .build();

        log.info("用户登录成功，userId: {}, username: {}", user.getId(), user.getUsername());
        return userInfo;
    }

    /**
     * 用户登出（根据userId）
     * 清除所有相关缓存
     */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CACHE_USER_PERMISSION, key = "#userId"),
            @CacheEvict(value = CacheConstants.CACHE_USER_ROLE, key = "#userId"),
            @CacheEvict(value = CacheConstants.CACHE_USER_INFO, key = "#userId")
    })
    public boolean logout(Long userId) {
        try {
            // 清除Token缓存（使用CacheService）
            cacheService.evict(CacheConstants.CACHE_LOGIN_TOKEN, userId.toString());

            // 其他缓存通过 @Caching 注解自动清除

            log.info("用户登出成功，userId: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("用户登出失败，userId: {}", userId, e);
            return false;
        }
    }

    /**
     * 用户登出（根据Token）
     */
    @Override
    public boolean logout(String token) {
        try {
            // 去除 Bearer 前缀（如果有）
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 从Token中获取用户ID
            Long userId = TokenUtil.getUserId(token);
            if (userId == null) {
                log.warn("Token中无法解析用户ID");
                return false;
            }

            // 调用基于userId的登出方法
            return logout(userId);
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return false;
        }
    }

    /**
     * 获取当前登录用户信息
     * 自动缓存用户信息
     */
    @Override
    @Cacheable(
            value = CacheConstants.CACHE_USER_INFO,
            key = "#root.target.getCurrentUserId()",
            unless = "#result == null"
    )
    public UserInfoDTO getCurrentUserInfo() {
        Long userId = SecurityContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.USER_LOGIN_EXPIRED);
        }

        return getUserInfo(userId);
    }

    /**
     * 获取用户信息（内部方法）
     */
    private UserInfoDTO getUserInfo(Long userId) {
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }

        // 查询用户权限和角色（自动走缓存）
        List<String> permissions = getUserPermissions(userId);
        List<String> roles = getUserRoles(userId);

        // 构建返回结果
        return UserInfoDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .tenantId(user.getTenantId())
                .deptId(user.getDeptId())
                .permissions(permissions)
                .roles(roles)
                .build();
    }

    /**
     * 获取用户权限列表
     * 自动缓存到 userPermission 空间
     */
    @Cacheable(
            value = CacheConstants.CACHE_USER_PERMISSION,
            key = "#userId",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<String> getUserPermissions(Long userId) {
        log.info("从数据库查询用户权限，userId: {}", userId);
        return userMapper.selectUserPermissions(userId);
    }

    /**
     * 获取用户角色列表
     * 自动缓存到 userRole 空间
     */
    @Cacheable(
            value = CacheConstants.CACHE_USER_ROLE,
            key = "#userId",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<String> getUserRoles(Long userId) {
        log.info("从数据库查询用户角色，userId: {}", userId);
        return userMapper.selectUserRoles(userId);
    }

    /**
     * 刷新用户权限缓存
     */
    @CachePut(
            value = CacheConstants.CACHE_USER_PERMISSION,
            key = "#userId"
    )
    public List<String> refreshUserPermissions(Long userId) {
        log.info("刷新用户权限缓存，userId: {}", userId);
        return userMapper.selectUserPermissions(userId);
    }

    /**
     * 刷新用户角色缓存
     */
    @CachePut(
            value = CacheConstants.CACHE_USER_ROLE,
            key = "#userId"
    )
    public List<String> refreshUserRoles(Long userId) {
        log.info("刷新用户角色缓存，userId: {}", userId);
        return userMapper.selectUserRoles(userId);
    }

    /**
     * 清除用户所有缓存
     */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CACHE_USER_PERMISSION, key = "#userId"),
            @CacheEvict(value = CacheConstants.CACHE_USER_ROLE, key = "#userId"),
            @CacheEvict(value = CacheConstants.CACHE_USER_INFO, key = "#userId")
    })
    public void clearUserCache(Long userId) {
        log.info("清除用户缓存，userId: {}", userId);
        // 同时清除Token缓存
        cacheService.evict(CacheConstants.CACHE_LOGIN_TOKEN, userId.toString());
    }

    /**
     * 获取当前用户ID（供SpEL表达式使用）
     */
    public Long getCurrentUserId() {
        return SecurityContext.getUserId();
    }
}