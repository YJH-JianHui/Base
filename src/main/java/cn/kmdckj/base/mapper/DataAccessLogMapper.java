package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.DataAccessLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据访问日志访问层接口。
 */
@Mapper
public interface DataAccessLogMapper extends BaseMapper<DataAccessLog> {
}
