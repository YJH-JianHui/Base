package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联访问层接口。
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
