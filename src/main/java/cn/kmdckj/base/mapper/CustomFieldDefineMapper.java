package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.CustomFieldDefine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 自定义字段定义访问层接口。
 */
@Mapper
public interface CustomFieldDefineMapper extends BaseMapper<CustomFieldDefine> {
}
