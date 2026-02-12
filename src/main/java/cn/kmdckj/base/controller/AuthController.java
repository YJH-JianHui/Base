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
 * 认证鉴权控制器。
 * 处理登录、获取用户信息等请求。
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
        try {
            UserInfoDTO userInfo = authService.login(loginDTO);
            log.info("用户登录成功，username: {}", loginDTO.getUsername());
            return Result.success("登录成功", userInfo);
        } catch (Exception e) {
            log.error("用户登录失败，username: {}, error: {}", loginDTO.getUsername(), e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        try {
            // 获取Token
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean success = authService.logout(token);
            if (success) {
                log.info("用户登出成功");
                return Result.success("登出成功");
            } else {
                return Result.error("登出失败");
            }
        } catch (Exception e) {
            log.error("用户登出失败，error: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/userInfo")
    public Result<UserInfoDTO> getUserInfo() {
        try {
            UserInfoDTO userInfo = authService.getCurrentUserInfo();
            return Result.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败，error: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
