package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.GrantablePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 可授权功能权限范围访问层接口。
 */
@Mapper
public interface GrantablePermissionMapper extends BaseMapper<GrantablePermission> {
}
