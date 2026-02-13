package cn.kmdckj.base.mapper;

import cn.kmdckj.base.entity.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门信息访问层接口。
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}
