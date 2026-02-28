package cn.kmdckj.base.service.permission;

/**
 * 数据权限计算与SQL生成服务接口。
 */
public interface DataPermissionService {
    /**
     * 根据用户权限规则构建数据范围SQL条件
     */
    String buildDataScopeCondition(Long userId, Long deptId,
                                   String entityCode, String entityAlias);
}
