package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.Tenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户访问层接口。
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
