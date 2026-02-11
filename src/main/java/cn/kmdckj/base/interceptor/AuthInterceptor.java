package cn.kmdckj.base.interceptor;

import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.common.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
     * 前置处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 获取 Token
        String token = request.getHeader(TOKEN_HEADER);

        if (token == null || token.isEmpty()) {
            log.warn("请求未携带Token，URI: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
            return false;
        }

        // TODO:这里简化处理，实际需要：
        // 1. 解析 Token（JWT或Redis）
        // 2. 验证 Token 有效性
        // 3. 从 Token 中获取用户信息
        // 4. 设置到上下文中

        // 模拟设置用户信息（实际从Token中解析）
        Long userId = 1L;        // 从Token解析
        String username = "admin"; // 从Token解析
        Long tenantId = 1L;       // 从Token解析
        Long deptId = 1L;         // 从Token解析

        // 设置上下文
        SecurityContext.setUserId(userId);
        SecurityContext.setUsername(username);
        SecurityContext.setDeptId(deptId);
        TenantContext.setTenantId(tenantId);

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
}
