package cn.kmdckj.base.common.result;

/**
 * 统一返回码枚举
 * 基于阿里巴巴Java开发手册错误码规范
 *
 * 错误码格式：
 * - 00000：成功
 * - A开头：用户端错误
 * - B开头：系统执行出错
 * - C开头：调用第三方服务出错
 */
public enum ResultCode {

    // ========== 成功 ==========
    SUCCESS("00000", "成功"),

    // ========== A类：用户端错误 ==========
    USER_ERROR("A0001", "用户端错误"),

    // A01xx 用户注册错误
    USER_REGISTER_ERROR("A0100", "用户注册错误"),
    USER_AGREEMENT_NOT_ACCEPTED("A0101", "用户未同意隐私协议"),
    USER_REGION_LIMITED("A0102", "注册国家或地区受限"),
    USERNAME_VERIFY_FAILED("A0110", "用户名校验失败"),
    USERNAME_ALREADY_EXISTS("A0111", "用户名已存在"),
    USERNAME_CONTAINS_SENSITIVE_WORD("A0112", "用户名包含敏感词"),
    USERNAME_CONTAINS_SPECIAL_CHAR("A0113", "用户名包含特殊字符"),
    PASSWORD_VERIFY_FAILED("A0120", "密码校验失败"),
    PASSWORD_LENGTH_NOT_ENOUGH("A0121", "密码长度不够"),
    PASSWORD_STRENGTH_NOT_ENOUGH("A0122", "密码强度不够"),

    // A02xx 用户登录异常
    USER_LOGIN_ERROR("A0200", "用户登录异常"),
    USER_ACCOUNT_NOT_EXIST("A0201", "用户账户不存在"),
    USER_ACCOUNT_FROZEN("A0202", "用户账户被冻结"),
    USER_ACCOUNT_CANCELED("A0203", "用户账户已作废"),
    USER_PASSWORD_ERROR("A0210", "用户密码错误"),
    USER_PASSWORD_ERROR_EXCEED_LIMIT("A0211", "用户输入密码错误次数超限"),
    USER_IDENTITY_VERIFY_FAILED("A0220", "用户身份校验失败"),
    USER_LOGIN_EXPIRED("A0230", "用户登录已过期"),
    USER_VERIFY_CODE_ERROR("A0240", "用户验证码错误"),
    USER_VERIFY_CODE_EXCEED_LIMIT("A0241", "用户验证码尝试次数超限"),

    // A03xx 访问权限异常
    ACCESS_PERMISSION_ERROR("A0300", "访问权限异常"),
    ACCESS_UNAUTHORIZED("A0301", "访问未授权"),
    ACCESS_AUTHORIZING("A0302", "正在授权中"),
    ACCESS_AUTHORIZATION_REJECTED("A0303", "用户授权申请被拒绝"),
    ACCESS_BLOCKED_BY_PRIVACY("A0310", "因访问对象隐私设置被拦截"),
    ACCESS_AUTHORIZATION_EXPIRED("A0311", "授权已过期"),
    ACCESS_NO_API_PERMISSION("A0312", "无权限使用API"),
    ACCESS_BLOCKED("A0320", "用户访问被拦截"),
    ACCESS_BLACKLIST_USER("A0321", "黑名单用户"),
    ACCESS_ACCOUNT_FROZEN("A0322", "账号被冻结"),
    ACCESS_ILLEGAL_IP("A0323", "非法IP地址"),
    ACCESS_GATEWAY_LIMITED("A0324", "网关访问受限"),
    ACCESS_REGION_BLACKLIST("A0325", "地域黑名单"),
    ACCESS_SERVICE_ARREARS("A0330", "服务已欠费"),
    ACCESS_SIGNATURE_ERROR("A0340", "用户签名异常"),
    ACCESS_RSA_SIGNATURE_ERROR("A0341", "RSA签名错误"),

    // A04xx 用户请求参数错误
    REQUEST_PARAM_ERROR("A0400", "用户请求参数错误"),
    REQUEST_CONTAINS_ILLEGAL_LINK("A0401", "包含非法恶意跳转链接"),
    REQUEST_INVALID_INPUT("A0402", "无效的用户输入"),
    REQUEST_PATH_NOT_EXIST("A0403", "请求地址不存在"),
    REQUEST_METHOD_NOT_SUPPORTED("A0404", "请求方法不支持"),
    REQUEST_REQUIRED_PARAM_EMPTY("A0410", "请求必填参数为空"),
    REQUEST_ORDER_NO_EMPTY("A0411", "用户订单号为空"),
    REQUEST_ORDER_QUANTITY_EMPTY("A0412", "订购数量为空"),
    REQUEST_TIMESTAMP_MISSING("A0413", "缺少时间戳参数"),
    REQUEST_TIMESTAMP_ILLEGAL("A0414", "非法的时间戳参数"),
    REQUEST_PARAM_OUT_OF_RANGE("A0420", "请求参数值超出允许的范围"),
    REQUEST_PARAM_FORMAT_MISMATCH("A0421", "参数格式不匹配"),
    REQUEST_ADDRESS_OUT_OF_SERVICE("A0422", "地址不在服务范围"),
    REQUEST_TIME_OUT_OF_SERVICE("A0423", "时间不在服务范围"),
    REQUEST_AMOUNT_EXCEED_LIMIT("A0424", "金额超出限制"),
    REQUEST_QUANTITY_EXCEED_LIMIT("A0425", "数量超出限制"),
    REQUEST_BATCH_EXCEED_LIMIT("A0426", "请求批量处理总个数超出限制"),
    REQUEST_JSON_PARSE_FAILED("A0427", "请求JSON解析失败"),
    REQUEST_CONTENT_ILLEGAL("A0430", "用户输入内容非法"),
    REQUEST_CONTAINS_SENSITIVE_WORD("A0431", "包含违禁敏感词"),
    REQUEST_IMAGE_ILLEGAL("A0432", "图片包含违禁信息"),
    REQUEST_FILE_COPYRIGHT_INFRINGEMENT("A0433", "文件侵犯版权"),
    REQUEST_OPERATION_ABNORMAL("A0440", "用户操作异常"),
    REQUEST_PAYMENT_TIMEOUT("A0441", "用户支付超时"),
    REQUEST_CONFIRM_ORDER_TIMEOUT("A0442", "确认订单超时"),
    REQUEST_ORDER_CLOSED("A0443", "订单已关闭"),

    // A05xx 用户请求服务异常
    REQUEST_SERVICE_ERROR("A0500", "用户请求服务异常"),
    REQUEST_TIMES_EXCEED_LIMIT("A0501", "请求次数超出限制"),
    REQUEST_CONCURRENT_EXCEED_LIMIT("A0502", "请求并发数超出限制"),
    REQUEST_WAIT("A0503", "用户操作请等待"),
    REQUEST_WEBSOCKET_ERROR("A0504", "WebSocket连接异常"),
    REQUEST_WEBSOCKET_DISCONNECTED("A0505", "WebSocket连接断开"),
    REQUEST_DUPLICATE("A0506", "用户重复请求"),

    // A06xx 用户资源异常
    USER_RESOURCE_ERROR("A0600", "用户资源异常"),
    USER_BALANCE_NOT_ENOUGH("A0601", "账户余额不足"),
    USER_DISK_SPACE_NOT_ENOUGH("A0602", "用户磁盘空间不足"),
    USER_MEMORY_SPACE_NOT_ENOUGH("A0603", "用户内存空间不足"),
    USER_OSS_CAPACITY_NOT_ENOUGH("A0604", "用户OSS容量不足"),
    USER_QUOTA_USED_UP("A0605", "用户配额已用光"),

    // A07xx 用户上传文件异常
    USER_UPLOAD_ERROR("A0700", "用户上传文件异常"),
    USER_UPLOAD_FILE_TYPE_MISMATCH("A0701", "用户上传文件类型不匹配"),
    USER_UPLOAD_FILE_TOO_LARGE("A0702", "用户上传文件太大"),
    USER_UPLOAD_IMAGE_TOO_LARGE("A0703", "用户上传图片太大"),
    USER_UPLOAD_VIDEO_TOO_LARGE("A0704", "用户上传视频太大"),
    USER_UPLOAD_ZIP_TOO_LARGE("A0705", "用户上传压缩文件太大"),

    // ========== B类：系统执行出错 ==========
    SYSTEM_ERROR("B0001", "系统执行出错"),

    // B01xx 系统执行超时
    SYSTEM_TIMEOUT("B0100", "系统执行超时"),
    SYSTEM_ORDER_TIMEOUT("B0101", "系统订单处理超时"),

    // B02xx 系统容灾功能被触发
    SYSTEM_DISASTER_RECOVERY("B0200", "系统容灾功能被触发"),
    SYSTEM_RATE_LIMIT("B0210", "系统限流"),
    SYSTEM_DOWNGRADE("B0220", "系统功能降级"),

    // B03xx 系统资源异常
    SYSTEM_RESOURCE_ERROR("B0300", "系统资源异常"),
    SYSTEM_RESOURCE_EXHAUSTED("B0310", "系统资源耗尽"),
    SYSTEM_DISK_EXHAUSTED("B0311", "系统磁盘空间耗尽"),
    SYSTEM_MEMORY_EXHAUSTED("B0312", "系统内存耗尽"),
    SYSTEM_FILE_HANDLE_EXHAUSTED("B0313", "文件句柄耗尽"),
    SYSTEM_CONNECTION_POOL_EXHAUSTED("B0314", "系统连接池耗尽"),
    SYSTEM_THREAD_POOL_EXHAUSTED("B0315", "系统线程池耗尽"),
    SYSTEM_RESOURCE_ACCESS_ERROR("B0320", "系统资源访问异常"),
    SYSTEM_READ_DISK_FILE_FAILED("B0321", "系统读取磁盘文件失败"),

    // ========== C类：调用第三方服务出错 ==========
    THIRD_PARTY_SERVICE_ERROR("C0001", "调用第三方服务出错"),

    // C01xx 中间件服务出错
    MIDDLEWARE_SERVICE_ERROR("C0100", "中间件服务出错"),
    RPC_SERVICE_ERROR("C0110", "RPC服务出错"),
    RPC_SERVICE_NOT_FOUND("C0111", "RPC服务未找到"),
    RPC_SERVICE_NOT_REGISTERED("C0112", "RPC服务未注册"),
    RPC_INTERFACE_NOT_EXIST("C0113", "接口不存在"),
    MESSAGE_SERVICE_ERROR("C0120", "消息服务出错"),
    MESSAGE_DELIVERY_ERROR("C0121", "消息投递出错"),
    MESSAGE_CONSUMPTION_ERROR("C0122", "消息消费出错"),
    MESSAGE_SUBSCRIPTION_ERROR("C0123", "消息订阅出错"),
    MESSAGE_GROUP_NOT_FOUND("C0124", "消息分组未查到"),
    CACHE_SERVICE_ERROR("C0130", "缓存服务出错"),
    CACHE_KEY_LENGTH_EXCEED_LIMIT("C0131", "key长度超过限制"),
    CACHE_VALUE_LENGTH_EXCEED_LIMIT("C0132", "value长度超过限制"),
    CACHE_STORAGE_FULL("C0133", "存储容量已满"),
    CACHE_DATA_FORMAT_NOT_SUPPORTED("C0134", "不支持的数据格式"),
    CONFIG_SERVICE_ERROR("C0140", "配置服务出错"),
    NETWORK_RESOURCE_SERVICE_ERROR("C0150", "网络资源服务出错"),
    VPN_SERVICE_ERROR("C0151", "VPN服务出错"),
    CDN_SERVICE_ERROR("C0152", "CDN服务出错"),
    DNS_SERVICE_ERROR("C0153", "域名解析服务出错"),
    GATEWAY_SERVICE_ERROR("C0154", "网关服务出错"),

    // C02xx 第三方系统执行超时
    THIRD_PARTY_TIMEOUT("C0200", "第三方系统执行超时"),
    RPC_TIMEOUT("C0210", "RPC执行超时"),
    MESSAGE_DELIVERY_TIMEOUT("C0220", "消息投递超时"),
    CACHE_SERVICE_TIMEOUT("C0230", "缓存服务超时"),
    CONFIG_SERVICE_TIMEOUT("C0240", "配置服务超时"),
    DATABASE_SERVICE_TIMEOUT("C0250", "数据库服务超时"),

    // C03xx 数据库服务出错
    DATABASE_SERVICE_ERROR("C0300", "数据库服务出错"),
    DATABASE_TABLE_NOT_EXIST("C0311", "表不存在"),
    DATABASE_COLUMN_NOT_EXIST("C0312", "列不存在"),
    DATABASE_DUPLICATE_COLUMN_NAME("C0321", "多表关联中存在多个相同名称的列"),
    DATABASE_DEADLOCK("C0331", "数据库死锁"),
    DATABASE_PRIMARY_KEY_CONFLICT("C0341", "主键冲突"),

    // C04xx 第三方容灾系统被触发
    THIRD_PARTY_DISASTER_RECOVERY("C0400", "第三方容灾系统被触发"),
    THIRD_PARTY_RATE_LIMIT("C0401", "第三方系统限流"),
    THIRD_PARTY_DOWNGRADE("C0402", "第三方功能降级"),

    // C05xx 通知服务出错
    NOTIFICATION_SERVICE_ERROR("C0500", "通知服务出错"),
    SMS_NOTIFICATION_FAILED("C0501", "短信提醒服务失败"),
    VOICE_NOTIFICATION_FAILED("C0502", "语音提醒服务失败"),
    EMAIL_NOTIFICATION_FAILED("C0503", "邮件提醒服务失败");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误描述
     */
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
