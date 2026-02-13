package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限访问层接口。
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

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
}
