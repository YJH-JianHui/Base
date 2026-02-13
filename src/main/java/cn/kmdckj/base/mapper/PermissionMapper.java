package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限访问层接口。
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
