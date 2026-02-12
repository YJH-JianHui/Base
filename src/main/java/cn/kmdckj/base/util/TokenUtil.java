package cn.kmdckj.base.util;

import cn.hutool.core.util.IdUtil;

/**
 * Token工具类
 */
public class TokenUtil {

    /**
     * 生成Token
     * 使用UUID作为Token
     *
     * @return Token字符串
     */
    public static String generateToken() {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 生成Token
     * 带前缀
     *
     * @param prefix 前缀
     * @return Token字符串
     */
    public static String generateToken(String prefix) {
        return prefix + IdUtil.fastSimpleUUID();
    }

    /**
     * 验证Token格式
     *
     * @param token Token
     * @return 是否合法
     */
    public static boolean validateToken(String token) {
        if (token == null || token.length() != 32) {
            return false;
        }
        return token.matches("[a-f0-9]{32}");
    }
}