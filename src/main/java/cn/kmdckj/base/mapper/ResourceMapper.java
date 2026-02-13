package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资源访问层接口。
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {
}
