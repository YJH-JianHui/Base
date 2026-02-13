package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.TenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 自定义字段查询表（反范式设计）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("custom_field_search")
public class CustomFieldSearch extends TenantEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实体类型
     */
    private String entityCode;

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字符串值
     */
    private String stringValue;

    /**
     * 数字值
     */
    private BigDecimal numberValue;

    /**
     * 日期值
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateValue;

    /**
     * 日期时间值
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datetimeValue;

    /**
     * 布尔值
     */
    private Integer booleanValue;
}
