package cn.kmdckj.base.controller;

import cn.kmdckj.base.common.result.Result;
import cn.kmdckj.base.dto.auth.LoginDTO;
import cn.kmdckj.base.dto.auth.UserInfoDTO;
import cn.kmdckj.base.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证鉴权控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserInfoDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求，username: {}", loginDTO.getUsername());
        UserInfoDTO userInfo = authService.login(loginDTO);
        log.info("用户登录成功，username: {}, userId: {}", loginDTO.getUsername(), userInfo.getUserId());
        return Result.success("登录成功", userInfo);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        log.info("用户登出请求");

        // 获取Token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 直接调用Service
        boolean success = authService.logout(token);

        if (success) {
            log.info("用户登出成功");
            return Result.success("登出成功");
        } else {
            // 登出失败也可以抛异常，让全局异常处理器处理
            log.warn("用户登出失败");
            return Result.error("登出失败");
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/userInfo")
    public Result<UserInfoDTO> getUserInfo() {
        log.debug("获取当前用户信息请求");
        UserInfoDTO userInfo = authService.getCurrentUserInfo();
        return Result.success(userInfo);
    }
}