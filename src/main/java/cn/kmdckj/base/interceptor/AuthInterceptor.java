package cn.kmdckj.base.interceptor;

import cn.kmdckj.base.common.constant.CacheConstants;
import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.common.context.TenantContext;
import cn.kmdckj.base.common.result.Result;
import cn.kmdckj.base.common.result.ResultCode;
import cn.kmdckj.base.service.cache.CacheService;
import cn.kmdckj.base.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 认证拦截器
 * 验证用户登录状态，设置上下文信息
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * Token 请求头名称
     */
    private static final String TOKEN_HEADER = "Authorization";

    /**
     * Bearer Token前缀
     */
    private static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private CacheService cacheService;

    /**
     * 前置处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 获取 Token
        String token = request.getHeader(TOKEN_HEADER);

        if (token == null || token.isEmpty()) {
            log.warn("请求未携带Token，URI: {}", request.getRequestURI());
            sendUnauthorizedResponse(response, "未登录或登录已过期");
            return false;
        }

        // 去除 Bearer 前缀（如果有）
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }

        // 验证 Token 有效性
        if (!TokenUtil.validateToken(token)) {
            log.warn("Token验证失败，URI: {}", request.getRequestURI());
            sendUnauthorizedResponse(response, "Token无效或已过期");
            return false;
        }

        // 从 Token 中解析用户信息
        Long userId = TokenUtil.getUserId(token);
        String username = TokenUtil.getUsername(token);
        Long tenantId = TokenUtil.getTenantId(token);
        Long deptId = TokenUtil.getDeptId(token);

        // 验证用户信息完整性
        if (userId == null || username == null) {
            log.warn("Token中缺少必要的用户信息，URI: {}", request.getRequestURI());
            sendUnauthorizedResponse(response, "Token信息不完整");
            return false;
        }

        // 校验缓存中的Token是否有效（处理登出失效）
        try {
            // 使用 CacheService 从缓存获取Token
            String cachedToken = cacheService.get(
                    CacheConstants.CACHE_LOGIN_TOKEN,
                    userId.toString(),
                    String.class
            );

            if (cachedToken == null || !cachedToken.equals(token)) {
                log.warn("Token已失效或被顶号，userId: {}", userId);
                sendUnauthorizedResponse(response, "Token已失效，请重新登录");
                return false;
            }
        } catch (Exception e) {
            log.error("缓存校验Token失败: {}", e.getMessage());
            // 缓存故障时的处理策略：
            // 1. 严格模式：拒绝访问（推荐生产环境）
            // 2. 宽松模式：只验证JWT本身的有效性
            // 这里采用严格模式
            sendUnauthorizedResponse(response, "系统繁忙，请稍后重试");
            return false;
        }

        // 设置上下文
        SecurityContext.setUserId(userId);
        SecurityContext.setUsername(username);
        SecurityContext.setDeptId(deptId);

        if (tenantId != null) {
            TenantContext.setTenantId(tenantId);
        }

        log.debug("认证通过，userId: {}, username: {}, tenantId: {}", userId, username, tenantId);

        return true;
    }

    /**
     * 后置处理
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        // 可以在这里记录访问日志
    }

    /**
     * 完成后处理
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        // 清除上下文，避免内存泄漏
        SecurityContext.clear();
        TenantContext.clear();
    }

    /**
     * 发送401未授权响应
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // 使用统一返回结果
        Result<Object> result = Result.error(ResultCode.USER_LOGIN_EXPIRED, message);

        // 使用 Jackson 序列化
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}