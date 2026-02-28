package cn.kmdckj.base.mapper;

import cn.kmdckj.base.annotation.DataScope;
import cn.kmdckj.base.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户访问层接口。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username} LIMIT 1")
    User selectByUsername(@Param("username") String username);

    @DataScope(entityCode = "user", entityAlias = "u")
    @Select("SELECT * FROM user u")
    List<User> selectWithDataScope();
}
