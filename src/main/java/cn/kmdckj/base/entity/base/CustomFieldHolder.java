package cn.kmdckj.base.entity.base;

import java.util.Map;

/**
 * 所有需要支持自定义字段的实体，都需要实现该接口
 */
public interface CustomFieldHolder {
    Map<String, Object> getCustomFields();
    void setCustomFields(Map<String, Object> customFields);
}
