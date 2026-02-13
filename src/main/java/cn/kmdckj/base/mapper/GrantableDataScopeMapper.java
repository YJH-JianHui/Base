package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.GrantableDataScope;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 可授权数据范围访问层接口。
 */
@Mapper
public interface GrantableDataScopeMapper extends BaseMapper<GrantableDataScope> {
}
