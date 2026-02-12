package cn.kmdckj.base.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token工具类
 * 用于生成、解析和验证JWT Token
 */
@Slf4j
public class TokenUtil {

    /**
     * JWT密钥（实际使用时应从配置文件读取）
     * 密钥长度必须 >= 256位（32字节）
     */
    private static String SECRET_KEY = "Base_Project_JWT_Secret_Key_Must_Be_At_Least_256_Bits_Long";

    /**
     * Token过期时间（毫秒），默认7天
     */
    private static long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 设置密钥（供配置类调用）
     */
    public static void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    /**
     * 设置过期时间（供配置类调用）
     */
    public static void setExpirationTime(long expirationTime) {
        EXPIRATION_TIME = expirationTime;
    }

    /**
     * 生成密钥对象
     */
    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param tenantId 租户ID
     * @param deptId   部门ID
     * @return JWT Token字符串
     */
    public static String generateToken(Long userId, String username, Long tenantId, Long deptId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tenantId", tenantId);
        claims.put("deptId", deptId);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析JWT Token
     *
     * @param token JWT Token字符串
     * @return Claims对象（包含用户信息）
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT Token字符串
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                return false;
            }

            // 检查是否过期
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token字符串
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token字符串
     * @return 用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims != null ? (String) claims.get("username") : null;
    }

    /**
     * 从Token中获取租户ID
     *
     * @param token JWT Token字符串
     * @return 租户ID
     */
    public static Long getTenantId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object tenantId = claims.get("tenantId");
        if (tenantId instanceof Integer) {
            return ((Integer) tenantId).longValue();
        }
        return (Long) tenantId;
    }

    /**
     * 从Token中获取部门ID
     *
     * @param token JWT Token字符串
     * @return 部门ID
     */
    public static Long getDeptId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object deptId = claims.get("deptId");
        if (deptId instanceof Integer) {
            return ((Integer) deptId).longValue();
        }
        return (Long) deptId;
    }

    /**
     * 获取Token剩余有效时间（毫秒）
     *
     * @param token JWT Token字符串
     * @return 剩余有效时间（毫秒），如果Token无效返回0
     */
    public static long getTokenRemainingTime(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return 0;
        }

        Date expiration = claims.getExpiration();
        if (expiration == null) {
            return 0;
        }

        long remainingTime = expiration.getTime() - System.currentTimeMillis();
        return Math.max(0, remainingTime);
    }
}