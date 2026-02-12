package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户访问层接口。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username} LIMIT 1")
    User selectByUsername(@Param("username") String username);

    /**
     * 查询用户的所有权限码
     */
    @Select("SELECT DISTINCT p.permission_code FROM user u " +
            "LEFT JOIN user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN role r ON ur.role_id = r.id " +
            "LEFT JOIN role_permission rp ON r.id = rp.role_id " +
            "LEFT JOIN permission p ON rp.permission_id = p.id " +
            "WHERE u.id = #{userId} AND p.permission_code IS NOT NULL")
    List<String> selectUserPermissions(@Param("userId") Long userId);

    /**
     * 查询用户的所有角色编码
     */
    @Select("SELECT r.role_code FROM user u " +
            "LEFT JOIN user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN role r ON ur.role_id = r.id " +
            "WHERE u.id = #{userId} AND r.role_code IS NOT NULL")
    List<String> selectUserRoles(@Param("userId") Long userId);
}
