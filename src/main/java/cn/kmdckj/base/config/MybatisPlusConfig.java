package cn.kmdckj.base.config;

import cn.kmdckj.base.interceptor.DataPermissionInterceptor;
import cn.kmdckj.base.interceptor.TenantInterceptor;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis Plus 配置类。
 * 配置分页插件、租户拦截器插件等。
 */
@Configuration
@EnableTransactionManagement
@MapperScan("cn.kmdckj.base.mapper")
public class MybatisPlusConfig {

    /**
     * MyBatis Plus 插件配置
     * 配置顺序：租户拦截器 -> 数据权限拦截器 -> 分页插件 -> 乐观锁 -> 防全表更新删除
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(
            TenantInterceptor tenantInterceptor,
            DataPermissionInterceptor dataPermissionInterceptor) {

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 租户拦截器（最先执行）
        interceptor.addInnerInterceptor(tenantInterceptor);

        // 2. 数据权限拦截器
        interceptor.addInnerInterceptor(dataPermissionInterceptor);

        // 3. 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000L);
        paginationInnerInterceptor.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 4. 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 5. 防止全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    /**
     * 自动填充处理器
     * 用于自动填充 create_time 和 update_time
     */
    @Bean
    public MyMetaObjectHandler myMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }
}
