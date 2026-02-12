package cn.kmdckj.base.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码工具类
 * 使用BCrypt加密算法
 */
public class PasswordUtil {

    /**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    /**
     * 检查密码强度
     *
     * @param password 密码
     * @return 密码强度等级 0-弱 1-中 2-强
     */
    public static int checkStrength(String password) {
        if (password == null || password.length() < 6) {
            return 0;
        }

        int strength = 0;

        // 包含数字
        if (password.matches(".*\\d+.*")) {
            strength++;
        }

        // 包含小写字母
        if (password.matches(".*[a-z]+.*")) {
            strength++;
        }

        // 包含大写字母
        if (password.matches(".*[A-Z]+.*")) {
            strength++;
        }

        // 包含特殊字符
        if (password.matches(".*[~!@#$%^&*()_+]+.*")) {
            strength++;
        }

        // 长度超过8位
        if (password.length() >= 8) {
            strength++;
        }

        // 0-2分：弱，3-4分：中，5分：强
        if (strength <= 2) {
            return 0;
        } else if (strength <= 4) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 验证密码格式
     * 至少6位，包含字母和数字
     *
     * @param password 密码
     * @return 是否符合格式
     */
    public static boolean validateFormat(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // 至少包含一个字母和一个数字
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#$%^&*()_+]{6,}$");
    }
}