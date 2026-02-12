package cn.kmdckj.base.service.impl.auth;

import cn.kmdckj.base.common.constant.CacheConstants;
import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.dto.auth.LoginDTO;
import cn.kmdckj.base.dto.auth.UserInfoDTO;
import cn.kmdckj.base.entity.User;
import cn.kmdckj.base.mapper.UserMapper;
import cn.kmdckj.base.service.auth.AuthService;
import cn.kmdckj.base.util.PasswordUtil;
import cn.kmdckj.base.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证授权服务接口实现类。
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 用户登录
     */
    @Override
    public UserInfoDTO login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        // 4. 查询用户权限和角色
        List<String> permissions = userMapper.selectUserPermissions(user.getId());
        List<String> roles = userMapper.selectUserRoles(user.getId());

        // 5. 生成JWT Token
        String token = TokenUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getTenantId(),
                user.getDeptId()
        );

        // 6. 将Token存入Redis（可选，用于登出控制）
        // key格式: base:user:token:{userId}
        String redisKey = CacheConstants.getUserTokenKey(user.getId());
        redisTemplate.opsForValue().set(redisKey, token, CacheConstants.TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        // 7. 缓存用户权限到Redis
        if (permissions != null && !permissions.isEmpty()) {
            // key格式: base:user:permission:{userId}
            String permissionKey = CacheConstants.getUserPermissionKey(user.getId());
            redisTemplate.opsForSet().add(permissionKey, permissions.toArray(new String[0]));
            redisTemplate.expire(permissionKey, CacheConstants.PERMISSION_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        // 8. 构建返回结果
        return UserInfoDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .tenantId(user.getTenantId())
                .deptId(user.getDeptId())
                .token(token)
                .permissions(permissions)
                .roles(roles)
                .build();
    }

    /**
     * 用户登出
     */
    @Override
    public boolean logout(String token) {
        try {
            // 从Token中获取用户ID
            Long userId = TokenUtil.getUserId(token);
            if (userId == null) {
                return false;
            }

            // 删除Redis中的Token和权限缓存
            String tokenKey = CacheConstants.getUserTokenKey(userId);
            String permissionKey = CacheConstants.getUserPermissionKey(userId);
            
            redisTemplate.delete(tokenKey);
            redisTemplate.delete(permissionKey);

            log.info("用户登出成功，userId: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return false;
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @Override
    public UserInfoDTO getCurrentUserInfo() {
        // 从上下文获取当前用户ID
        Long userId = SecurityContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("未登录");
        }

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 查询用户权限和角色
        List<String> permissions = userMapper.selectUserPermissions(userId);
        List<String> roles = userMapper.selectUserRoles(userId);

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
}
