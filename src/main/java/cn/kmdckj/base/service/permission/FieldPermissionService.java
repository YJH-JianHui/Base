package cn.kmdckj.base.service.permission;

import cn.kmdckj.base.common.constant.FieldPermissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 字段权限处理服务接口。
 */
public interface FieldPermissionService {
    /**
     * 获取用户对某实体的字段权限Map
     * key: fieldName, value: FieldPermissionInfo
     */
    Map<String, FieldPermissionInfo> getUserFieldPermissions(Long userId, String entityCode);

    /**
     * 刷新缓存
     */
    Map<String, FieldPermissionInfo> refreshUserFieldPermissions(Long userId, String entityCode);

    /**
     * 字段权限信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class FieldPermissionInfo {
        private FieldPermissionType permissionType;
        private String maskRule;
    }
}
