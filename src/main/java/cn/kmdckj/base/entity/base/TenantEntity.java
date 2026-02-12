package cn.kmdckj.base.entity.base;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 多租户基础实体类。
 * 继承 BaseEntity，增加 tenant_id 字段。
 * 需要租户隔离的实体类应继承此类。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TenantEntity extends BaseEntity {

    /**
     * 租户ID
     * 用于多租户数据隔离
     * 由TenantInterceptor自动填充
     */
    @TableField("tenant_id")
    private Long tenantId;
}
