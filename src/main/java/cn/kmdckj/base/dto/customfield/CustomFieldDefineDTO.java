package cn.kmdckj.base.dto.customfield;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 自定义字段定义参数。
 */
@Data
public class CustomFieldDefineDTO {
    @NotBlank(message = "实体编码不能为空")
    private String entityCode;

    @NotBlank(message = "字段编码不能为空")
    private String fieldCode;

    @NotBlank(message = "字段名称不能为空")
    private String fieldName;

    @NotBlank(message = "字段类型不能为空")
    private String fieldType;

    /**
     * 字段配置（JSON字符串）
     * SELECT类型需要传options
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object fieldConfig;

    /**
     * 敏感级别：0-普通 1-敏感 2-高度敏感
     */
    private Integer sensitiveLevel = 0;

    private Integer isRequired = 0;

    private String defaultValue;

    private String validationRule;

    /**
     * 是否可查询（决定是否同步到custom_field_search表）
     */
    private Integer isSearchable = 0;

    private Integer searchPriority = 0;

    private Integer sortOrder = 0;
}
