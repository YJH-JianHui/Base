package cn.kmdckj.base.service.customfield;

import cn.kmdckj.base.dto.customfield.CustomFieldDefineDTO;
import cn.kmdckj.base.entity.CustomFieldDefine;

import java.util.List;
import java.util.Map;

/**
 * 自定义字段 EAV 管理服务接口。
 */
public interface CustomFieldService {
    /**
     * 查询某实体下所有启用的自定义字段定义
     */
    List<CustomFieldDefine> getFieldDefines(String entityCode);

    /**
     * 查询单个自定义字段定义
     */
    CustomFieldDefine getFieldDefine(String entityCode, String fieldCode);

    /**
     * 创建自定义字段定义
     */
    CustomFieldDefine createFieldDefine(CustomFieldDefineDTO dto);

    /**
     * 修改自定义字段定义
     */
    CustomFieldDefine updateFieldDefine(Long id, CustomFieldDefineDTO dto);

    /**
     * 删除自定义字段定义（逻辑删除：status=0）
     */
    void deleteFieldDefine(Long id);

    /**
     * 获取实体的自定义字段值（单条记录）
     */
    Map<String, Object> getFieldValues(String entityCode, Long entityId);

    /**
     * 批量获取实体的自定义字段值（列表场景）
     */
    Map<Long, Map<String, Object>> batchGetFieldValues(String entityCode, List<Long> entityIds);

    /**
     * 保存自定义字段值
     */
    void saveFieldValues(String entityCode, Long entityId, Map<String, Object> fieldValues);
}
