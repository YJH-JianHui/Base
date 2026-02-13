package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.Operation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作访问层接口。
 */
@Mapper
public interface OperationMapper extends BaseMapper<Operation> {
}
