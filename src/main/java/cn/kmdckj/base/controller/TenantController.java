package cn.kmdckj.base.controller;

import cn.kmdckj.base.annotation.RequiresPermission;
import cn.kmdckj.base.common.result.Result;
import cn.kmdckj.base.entity.Tenant;
import cn.kmdckj.base.mapper.TenantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 租户管理控制器。
 */
@Slf4j
@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private TenantMapper tenantMapper;

    @GetMapping("/list")
    @RequiresPermission("system:tenant:view")
    public Result<List<Tenant>> list() {
        List<Tenant> tenants = tenantMapper.selectWithDataScope();
        return Result.success("登录成功", tenants);
    }
}