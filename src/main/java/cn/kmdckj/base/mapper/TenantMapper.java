package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.Tenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 租户访问层接口。
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {

    @Select("SELECT * FROM tenant t")
    List<Tenant> selectWithDataScope();
}
