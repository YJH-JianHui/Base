package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.GrantOperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 授权操作日志访问层接口。
 */
@Mapper
public interface GrantOperationLogMapper extends BaseMapper<GrantOperationLog> {
}
