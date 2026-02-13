package cn.kmdckj.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据访问日志表实体。
 * 独立的日志实体，不继承 BaseEntity或BaseImmutableEntity，因为它使用 access_time 而非 create_time。
 */
@Data
@TableName("data_access_log")
public class DataAccessLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 目标租户ID(跨租户访问时)
     */
    private Long targetTenantId;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 操作
     */
    private String operation;

    /**
     * 访问的字段列表
     */
    private String fieldList;

    /**
     * 数据范围
     */
    private String dataScope;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 访问时间
     * 插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessTime;

    /**
     * 响应时间(毫秒)
     */
    private Integer responseTime;
}
