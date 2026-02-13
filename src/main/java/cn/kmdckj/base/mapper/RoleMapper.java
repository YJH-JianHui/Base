package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色访问层接口。
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
