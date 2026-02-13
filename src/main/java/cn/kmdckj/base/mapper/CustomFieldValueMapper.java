package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.CustomFieldValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 自定义字段值访问层接口。
 */
@Mapper
public interface CustomFieldValueMapper extends BaseMapper<CustomFieldValue> {
}
