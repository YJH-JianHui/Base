package cn.kmdckj.base.controller;

import cn.kmdckj.base.annotation.FieldFilter;
import cn.kmdckj.base.annotation.RequiresPermission;
import cn.kmdckj.base.common.result.Result;
import cn.kmdckj.base.entity.User;
import cn.kmdckj.base.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理控制器。
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/list")
    @RequiresPermission("system:user:view")
    @FieldFilter(entityCode = "user")
    public Result<List<User>> list() {
        List<User> users = userMapper.selectWithDataScope();
        return Result.success(users);
    }
}
