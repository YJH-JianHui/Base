package cn.kmdckj.base.service.impl.auth;

import cn.kmdckj.base.annotation.IgnoreTenant;
import cn.kmdckj.base.common.constant.CacheConstants;
import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.common.exception.BusinessException;
import cn.kmdckj.base.common.result.ResultCode;
import cn.kmdckj.base.dto.auth.LoginDTO;
import cn.kmdckj.base.dto.auth.UserInfoDTO;
import cn.kmdckj.base.entity.User;
import cn.kmdckj.base.mapper.UserMapper;
import cn.kmdckj.base.service.auth.AuthService;
import cn.kmdckj.base.service.permission.PermissionService;
import cn.kmdckj.base.service.role.RoleService;
import cn.kmdckj.base.util.CacheUtil;
import cn.kmdckj.base.util.PasswordUtil;
import cn.kmdckj.base.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证授权服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CacheUtil cacheUtil;

    /**
     * 用户登录
     */
    @IgnoreTenant
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

        // 4. 查询用户权限和角色（自动缓存）
        List<String> permissions = permissionService.getUserPermissions(user.getId());
        List<String> roles = roleService.getUserRoles(user.getId());

        // 5. 生成JWT Token
        String token = TokenUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getTenantId(),
                user.getDeptId()
        );

        // 6. 将Token存入缓存
        cacheUtil.put(
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
     * 用户登出（根据Token） 只能手动清理缓存
     */
    @Override
    public boolean logout(String token) {
        // 去除 Bearer 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证Token并获取用户ID
        if (!TokenUtil.validateToken(token)) {
            throw new BusinessException(ResultCode.USER_LOGIN_EXPIRED, "Token无效或已过期");
        }

        Long userId = TokenUtil.getUserId(token);
        if (userId == null) {
            throw new BusinessException(ResultCode.USER_LOGIN_EXPIRED, "Token信息不完整");
        }

        // 验证Redis中是否存在Token（防止重复登出）
        String cachedToken = cacheUtil.get(CacheConstants.CACHE_LOGIN_TOKEN, userId.toString(), String.class);
        if (cachedToken == null || !cachedToken.equals(token)) {
            throw new BusinessException(ResultCode.USER_LOGIN_EXPIRED, "用户已登出或Token已失效");
        }

        // 1. 清除Token缓存
        cacheUtil.evict(CacheConstants.CACHE_LOGIN_TOKEN, userId.toString());
        // 2. 清除用户权限缓存
        cacheUtil.evict(CacheConstants.CACHE_USER_PERMISSION, userId.toString());
        // 3. 清除用户角色缓存
        cacheUtil.evict(CacheConstants.CACHE_USER_ROLE, userId.toString());
        // 4. 清除用户信息缓存
        cacheUtil.evict(CacheConstants.CACHE_USER_INFO, userId.toString());

        return true;
    }

    /**
     * 获取当前登录用户信息
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
        List<String> permissions = permissionService.getUserPermissions(userId);
        List<String> roles = roleService.getUserRoles(userId);

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
     * 获取当前用户ID（供SpEL表达式使用）
     */
    public Long getCurrentUserId() {
        return SecurityContext.getUserId();
    }
}