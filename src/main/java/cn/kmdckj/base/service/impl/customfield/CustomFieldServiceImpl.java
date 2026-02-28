package cn.kmdckj.base.service.impl.customfield;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.kmdckj.base.common.constant.CacheConstants;
import cn.kmdckj.base.common.context.SecurityContext;
import cn.kmdckj.base.common.context.TenantContext;
import cn.kmdckj.base.common.exception.BusinessException;
import cn.kmdckj.base.dto.customfield.CustomFieldDefineDTO;
import cn.kmdckj.base.entity.CustomFieldDefine;
import cn.kmdckj.base.entity.CustomFieldSearch;
import cn.kmdckj.base.entity.CustomFieldValue;
import cn.kmdckj.base.entity.FieldResource;
import cn.kmdckj.base.mapper.CustomFieldDefineMapper;
import cn.kmdckj.base.mapper.CustomFieldSearchMapper;
import cn.kmdckj.base.mapper.CustomFieldValueMapper;
import cn.kmdckj.base.mapper.FieldResourceMapper;
import cn.kmdckj.base.service.customfield.CustomFieldService;
import cn.kmdckj.base.util.CacheUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自定义字段 EAV 管理服务接口实现类。
 */
@Slf4j
@Service
public class CustomFieldServiceImpl implements CustomFieldService {

    @Autowired
    private CustomFieldDefineMapper customFieldDefineMapper;

    @Autowired
    private CustomFieldValueMapper customFieldValueMapper;

    @Autowired
    private CustomFieldSearchMapper customFieldSearchMapper;

    @Autowired
    private FieldResourceMapper fieldResourceMapper;

    @Autowired
    private CacheUtil cacheUtil;

    // ==================== 字段定义相关 ====================

    @Override
    @Cacheable(
            value = CacheConstants.CACHE_CUSTOM_FIELD_DEFINE,
            key = "T(cn.kmdckj.base.common.context.TenantContext).getTenantId() + ':' + #entityCode"
    )
    public List<CustomFieldDefine> getFieldDefines(String entityCode) {
        return customFieldDefineMapper.selectList(
                new LambdaQueryWrapper<CustomFieldDefine>()
                        .eq(CustomFieldDefine::getEntityCode, entityCode)
                        .eq(CustomFieldDefine::getStatus, 1)
                        .orderByAsc(CustomFieldDefine::getSortOrder)
        );
    }

    @Override
    public CustomFieldDefine getFieldDefine(String entityCode, String fieldCode) {
        return customFieldDefineMapper.selectOne(
                new LambdaQueryWrapper<CustomFieldDefine>()
                        .eq(CustomFieldDefine::getEntityCode, entityCode)
                        .eq(CustomFieldDefine::getFieldCode, fieldCode)
                        .eq(CustomFieldDefine::getStatus, 1)
        );
    }

    @Override
    @Transactional
    public CustomFieldDefine createFieldDefine(CustomFieldDefineDTO dto) {
        Long tenantId = TenantContext.getTenantId();
        Long userId = SecurityContext.getUserId();

        // 1. 检查字段编码是否重复
        CustomFieldDefine existing = getFieldDefine(dto.getEntityCode(), dto.getFieldCode());
        if (existing != null) {
            throw new BusinessException("字段编码已存在: " + dto.getFieldCode());
        }

        // 2. 保存自定义字段定义
        CustomFieldDefine define = new CustomFieldDefine();
        define.setEntityCode(dto.getEntityCode());
        define.setFieldCode(dto.getFieldCode());
        define.setFieldName(dto.getFieldName());
        define.setFieldType(dto.getFieldType());
        define.setFieldConfig(dto.getFieldConfig());
        define.setSensitiveLevel(dto.getSensitiveLevel());
        define.setIsRequired(dto.getIsRequired());
        define.setDefaultValue(dto.getDefaultValue());
        define.setValidationRule(dto.getValidationRule());
        define.setIsSearchable(dto.getIsSearchable());
        define.setSearchPriority(dto.getSearchPriority());
        define.setSortOrder(dto.getSortOrder());
        define.setStatus(1);
        define.setCreateUserId(userId);
        customFieldDefineMapper.insert(define);

        // 3. 同步注册到 field_resource 表（用于字段权限控制）
        FieldResource fieldResource = new FieldResource();
        fieldResource.setEntityCode(dto.getEntityCode());
        fieldResource.setFieldName(dto.getFieldCode());
        fieldResource.setFieldLabel(dto.getFieldName());
        fieldResource.setFieldType(dto.getFieldType());
        fieldResource.setSensitiveLevel(dto.getSensitiveLevel());
        fieldResource.setIsCustom(1);
        fieldResource.setCustomFieldId(define.getId());
        fieldResourceMapper.insert(fieldResource);

        // 4. 清除字段定义缓存
        evictDefineCache(dto.getEntityCode());

        log.info("创建自定义字段: entityCode={}, fieldCode={}, tenantId={}",
                dto.getEntityCode(), dto.getFieldCode(), tenantId);
        return define;
    }

    @Override
    @Transactional
    public CustomFieldDefine updateFieldDefine(Long id, CustomFieldDefineDTO dto) {
        CustomFieldDefine define = customFieldDefineMapper.selectById(id);
        if (define == null) {
            throw new BusinessException("自定义字段不存在");
        }

        // 只允许修改名称、配置、敏感级别等，不允许修改fieldCode和fieldType
        define.setFieldName(dto.getFieldName());
        define.setFieldConfig(dto.getFieldConfig());
        define.setSensitiveLevel(dto.getSensitiveLevel());
        define.setIsRequired(dto.getIsRequired());
        define.setDefaultValue(dto.getDefaultValue());
        define.setValidationRule(dto.getValidationRule());
        define.setIsSearchable(dto.getIsSearchable());
        define.setSearchPriority(dto.getSearchPriority());
        define.setSortOrder(dto.getSortOrder());
        customFieldDefineMapper.updateById(define);

        // 同步更新 field_resource 表的敏感级别
        FieldResource fieldResource = fieldResourceMapper.selectOne(
                new LambdaQueryWrapper<FieldResource>()
                        .eq(FieldResource::getIsCustom, 1)
                        .eq(FieldResource::getCustomFieldId, id)
        );
        if (fieldResource != null) {
            fieldResource.setFieldLabel(dto.getFieldName());
            fieldResource.setSensitiveLevel(dto.getSensitiveLevel());
            fieldResourceMapper.updateById(fieldResource);
        }

        evictDefineCache(define.getEntityCode());
        return define;
    }

    @Override
    @Transactional
    public void deleteFieldDefine(Long id) {
        CustomFieldDefine define = customFieldDefineMapper.selectById(id);
        if (define == null) {
            throw new BusinessException("自定义字段不存在");
        }

        // 逻辑删除
        define.setStatus(0);
        customFieldDefineMapper.updateById(define);

        evictDefineCache(define.getEntityCode());
    }

    // ==================== 字段值相关 ====================

    @Override
    public Map<String, Object> getFieldValues(String entityCode, Long entityId) {
        List<CustomFieldValue> values = customFieldValueMapper.selectList(
                new LambdaQueryWrapper<CustomFieldValue>()
                        .eq(CustomFieldValue::getEntityCode, entityCode)
                        .eq(CustomFieldValue::getEntityId, entityId)
        );

        return parseFieldValues(values);
    }

    @Override
    public Map<Long, Map<String, Object>> batchGetFieldValues(String entityCode,
                                                              List<Long> entityIds) {
        if (entityIds == null || entityIds.isEmpty()) {
            return Map.of();
        }

        List<CustomFieldValue> values = customFieldValueMapper.selectList(
                new LambdaQueryWrapper<CustomFieldValue>()
                        .eq(CustomFieldValue::getEntityCode, entityCode)
                        .in(CustomFieldValue::getEntityId, entityIds)
        );

        // 按 entityId 分组
        Map<Long, Map<String, Object>> result = new HashMap<>();
        Map<Long, List<CustomFieldValue>> grouped = values.stream()
                .collect(Collectors.groupingBy(CustomFieldValue::getEntityId));

        for (Long entityId : entityIds) {
            List<CustomFieldValue> entityValues = grouped.getOrDefault(entityId, List.of());
            result.put(entityId, parseFieldValues(entityValues));
        }

        return result;
    }

    @Override
    @Transactional
    public void saveFieldValues(String entityCode, Long entityId,
                                Map<String, Object> fieldValues) {
        if (fieldValues == null || fieldValues.isEmpty()) {
            return;
        }

        // 查询字段定义用于校验
        List<CustomFieldDefine> defines = getFieldDefines(entityCode);
        Map<String, CustomFieldDefine> defineMap = defines.stream()
                .collect(Collectors.toMap(CustomFieldDefine::getFieldCode, d -> d));

        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            String fieldCode = entry.getKey();
            Object value = entry.getValue();

            CustomFieldDefine define = defineMap.get(fieldCode);
            if (define == null) {
                log.warn("字段不存在，跳过: entityCode={}, fieldCode={}", entityCode, fieldCode);
                continue;
            }

            String jsonValue = JSONUtil.toJsonStr(Map.of(
                    "value", value,
                    "displayValue", value,
                    "updateTime", LocalDateTime.now().toString()
            ));

            // upsert：存在则更新，不存在则插入
            CustomFieldValue existing = customFieldValueMapper.selectOne(
                    new LambdaQueryWrapper<CustomFieldValue>()
                            .eq(CustomFieldValue::getEntityCode, entityCode)
                            .eq(CustomFieldValue::getEntityId, entityId)
                            .eq(CustomFieldValue::getFieldCode, fieldCode)
            );

            if (existing != null) {
                existing.setFieldValue(jsonValue);
                customFieldValueMapper.updateById(existing);
            } else {
                CustomFieldValue newValue = new CustomFieldValue();
                newValue.setEntityCode(entityCode);
                newValue.setEntityId(entityId);
                newValue.setFieldCode(fieldCode);
                newValue.setFieldValue(jsonValue);
                customFieldValueMapper.insert(newValue);
            }

            // 如果字段可查询，同步到 custom_field_search 表
            if (define.getIsSearchable() == 1) {
                syncToSearchTable(entityCode, entityId, fieldCode,
                        define.getFieldType(), value);
            }
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 解析字段值列表为 Map
     */
    private Map<String, Object> parseFieldValues(List<CustomFieldValue> values) {
        Map<String, Object> result = new HashMap<>();
        for (CustomFieldValue value : values) {
            try {
                // fieldValue 存储格式: {"value": xxx, "displayValue": xxx}
                JSONObject json = JSONUtil.parseObj(value.getFieldValue());
                result.put(value.getFieldCode(), json.get("value"));
            } catch (Exception e) {
                log.warn("解析字段值失败: fieldCode={}", value.getFieldCode());
                result.put(value.getFieldCode(), value.getFieldValue());
            }
        }
        return result;
    }

    /**
     * 同步到反范式查询表
     */
    private void syncToSearchTable(String entityCode, Long entityId,
                                   String fieldCode, String fieldType, Object value) {
        CustomFieldSearch search = customFieldSearchMapper.selectOne(
                new LambdaQueryWrapper<CustomFieldSearch>()
                        .eq(CustomFieldSearch::getEntityCode, entityCode)
                        .eq(CustomFieldSearch::getEntityId, entityId)
                        .eq(CustomFieldSearch::getFieldCode, fieldCode)
        );

        if (search == null) {
            search = new CustomFieldSearch();
            search.setEntityCode(entityCode);
            search.setEntityId(entityId);
            search.setFieldCode(fieldCode);
            search.setFieldType(fieldType);
        }

        // 根据字段类型设置对应的值字段
        switch (fieldType) {
            case "NUMBER", "DECIMAL" ->
                    search.setNumberValue(new BigDecimal(value.toString()));
            case "DATE" ->
                    search.setDateValue(LocalDate.parse(value.toString()));
            case "DATETIME" ->
                    search.setDatetimeValue(LocalDateTime.parse(value.toString()));
            case "BOOLEAN" ->
                    search.setBooleanValue("true".equals(value.toString()) ? 1 : 0);
            default ->
                    search.setStringValue(value.toString());
        }

        if (search.getId() == null) {
            customFieldSearchMapper.insert(search);
        } else {
            customFieldSearchMapper.updateById(search);
        }
    }

    /**
     * 清除字段定义缓存
     */
    private void evictDefineCache(String entityCode) {
        Long tenantId = TenantContext.getTenantId();
        cacheUtil.evict(CacheConstants.CACHE_CUSTOM_FIELD_DEFINE,
                tenantId + ":" + entityCode);
    }
}
