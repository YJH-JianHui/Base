package cn.kmdckj.base.common.constant;

/**
 * 缓存常量
 */
public class CacheConstants {

    /**
     * 缓存key前缀
     */
    public static final String CACHE_PREFIX = "base:";

    /**
     * 用户缓存key前缀
     */
    public static final String USER_PREFIX = CACHE_PREFIX + "user:";

    /**
     * 角色缓存key前缀
     */
    public static final String ROLE_PREFIX = CACHE_PREFIX + "role:";

    /**
     * 权限缓存key前缀
     */
    public static final String PERMISSION_PREFIX = CACHE_PREFIX + "permission:";

    /**
     * 部门缓存key前缀
     */
    public static final String DEPT_PREFIX = CACHE_PREFIX + "dept:";

    /**
     * 自定义字段定义缓存key前缀
     */
    public static final String CUSTOM_FIELD_DEFINE_PREFIX = CACHE_PREFIX + "custom_field:define:";

    /**
     * 登录token缓存key前缀
     */
    public static final String LOGIN_TOKEN_PREFIX = CACHE_PREFIX + "login:token:";
    /**
     * 默认缓存过期时间（秒）- 30分钟
     */
    public static final long DEFAULT_EXPIRE_TIME = 30 * 60;
    /**
     * 权限缓存过期时间（秒）- 1小时
     */
    public static final long PERMISSION_EXPIRE_TIME = 60 * 60;
    /**
     * token缓存过期时间（秒）- 24小时
     */
    public static final long TOKEN_EXPIRE_TIME = 24 * 60 * 60;
    /**
     * 部门树缓存过期时间（秒）- 1小时
     */
    public static final long DEPT_TREE_EXPIRE_TIME = 60 * 60;
    /**
     * 自定义字段定义缓存过期时间（秒）- 1小时
     */
    public static final long CUSTOM_FIELD_DEFINE_EXPIRE_TIME = 60 * 60;

    /**
     * 用户权限缓存key
     * key格式: base:user:permission:{userId}
     */
    public static String getUserPermissionKey(Long userId) {
        return USER_PREFIX + "permission:" + userId;
    }

    /**
     * 用户角色缓存key
     * key格式: base:user:roles:{userId}
     */
    public static String getUserRolesKey(Long userId) {
        return USER_PREFIX + "roles:" + userId;
    }

    /**
     * 用户数据权限缓存key
     * key格式: base:user:data_scope:{userId}:{entityCode}
     */
    public static String getUserDataScopeKey(Long userId, String entityCode) {
        return USER_PREFIX + "data_scope:" + userId + ":" + entityCode;
    }

    /**
     * 用户字段权限缓存key
     * key格式: base:user:field_permission:{userId}:{entityCode}
     */
    public static String getUserFieldPermissionKey(Long userId, String entityCode) {
        return USER_PREFIX + "field_permission:" + userId + ":" + entityCode;
    }

    /**
     * 角色权限缓存key
     * key格式: base:role:permission:{roleId}
     */
    public static String getRolePermissionKey(Long roleId) {
        return ROLE_PREFIX + "permission:" + roleId;
    }

    /**
     * 部门树缓存key
     * key格式: base:dept:tree:{tenantId}
     */
    public static String getDeptTreeKey(Long tenantId) {
        return DEPT_PREFIX + "tree:" + tenantId;
    }

    /**
     * 部门子节点缓存key
     * key格式: base:dept:children:{deptId}
     */
    public static String getDeptChildrenKey(Long deptId) {
        return DEPT_PREFIX + "children:" + deptId;
    }

    /**
     * 自定义字段定义缓存key
     * key格式: base:custom_field:define:{tenantId}:{entityCode}
     */
    public static String getCustomFieldDefineKey(Long tenantId, String entityCode) {
        return CUSTOM_FIELD_DEFINE_PREFIX + tenantId + ":" + entityCode;
    }

    /**
     * 登录token缓存key
     * key格式: base:login:token:{token}
     */
    public static String getLoginTokenKey(String token) {
        return LOGIN_TOKEN_PREFIX + token;
    }
}