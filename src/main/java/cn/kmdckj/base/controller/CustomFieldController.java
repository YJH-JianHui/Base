package cn.kmdckj.base.controller;

import cn.kmdckj.base.annotation.RequiresPermission;
import cn.kmdckj.base.common.result.Result;
import cn.kmdckj.base.dto.customfield.CustomFieldDefineDTO;
import cn.kmdckj.base.entity.CustomFieldDefine;
import cn.kmdckj.base.service.customfield.CustomFieldService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 自定义字段管理控制器。
 */
@Slf4j
@RestController
@RequestMapping("/custom-field")
public class CustomFieldController {
    @Autowired
    private CustomFieldService customFieldService;

    /**
     * 查询实体的自定义字段定义列表
     */
    @GetMapping("/defines/{entityCode}")
    @RequiresPermission("system:custom-field:view")
    public Result<List<CustomFieldDefine>> getDefines(@PathVariable String entityCode) {
        return Result.success(customFieldService.getFieldDefines(entityCode));
    }

    /**
     * 创建自定义字段定义
     */
    @PostMapping("/defines")
    @RequiresPermission("system:custom-field:create")
    public Result<CustomFieldDefine> create(@Valid @RequestBody CustomFieldDefineDTO dto) {
        return Result.success(customFieldService.createFieldDefine(dto));
    }

    /**
     * 修改自定义字段定义
     */
    @PutMapping("/defines/{id}")
    @RequiresPermission("system:custom-field:update")
    public Result<CustomFieldDefine> update(@PathVariable Long id,
                                            @Valid @RequestBody CustomFieldDefineDTO dto) {
        return Result.success(customFieldService.updateFieldDefine(id, dto));
    }

    /**
     * 删除自定义字段定义
     */
    @DeleteMapping("/defines/{id}")
    @RequiresPermission("system:custom-field:delete")
    public Result<Void> delete(@PathVariable Long id) {
        customFieldService.deleteFieldDefine(id);
        return Result.success();
    }

    /**
     * 查询某实体记录的自定义字段值
     */
    @GetMapping("/values/{entityCode}/{entityId}")
    public Result<Map<String, Object>> getValues(@PathVariable String entityCode,
                                                 @PathVariable Long entityId) {
        return Result.success(customFieldService.getFieldValues(entityCode, entityId));
    }

    /**
     * 保存某实体记录的自定义字段值
     */
    @PostMapping("/values/{entityCode}/{entityId}")
    public Result<Void> saveValues(@PathVariable String entityCode,
                                   @PathVariable Long entityId,
                                   @RequestBody Map<String, Object> fieldValues) {
        customFieldService.saveFieldValues(entityCode, entityId, fieldValues);
        return Result.success();
    }
}
