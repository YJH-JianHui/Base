package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.GrantableFieldPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 可授权字段权限访问层接口。
 */
@Mapper
public interface GrantableFieldPermissionMapper extends BaseMapper<GrantableFieldPermission> {
}
