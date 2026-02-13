package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联访问层接口。
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
