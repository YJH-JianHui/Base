package cn.kmdckj.base.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录后用户信息上下文数据。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户权限码列表
     */
    private List<String> permissions;

    /**
     * 用户角色列表
     */
    private List<String> roles;
}
