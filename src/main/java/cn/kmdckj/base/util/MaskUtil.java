package cn.kmdckj.base.util;

import cn.hutool.core.util.StrUtil;

/**
 * 数据脱敏工具类
 */
public class MaskUtil {

    /**
     * 默认脱敏字符
     */
    private static final String MASK_CHAR = "*";

    /**
     * 手机号脱敏
     * 保留前3后4位，中间4位脱敏
     * 例如：138****5678
     */
    public static String maskPhone(String phone) {
        if (StrUtil.isBlank(phone) || phone.length() != 11) {
            return phone;
        }
        return mask(phone, 3, 4);
    }

    /**
     * 身份证号脱敏
     * 保留前6后4位
     * 例如：110101********1234
     */
    public static String maskIdCard(String idCard) {
        if (StrUtil.isBlank(idCard)) {
            return idCard;
        }
        if (idCard.length() == 15) {
            return mask(idCard, 6, 3);
        } else if (idCard.length() == 18) {
            return mask(idCard, 6, 4);
        }
        return idCard;
    }

    /**
     * 邮箱脱敏
     * 符号@前面保留前2位，@后面全部保留
     * 例如：ab****@example.com
     */
    public static String maskEmail(String email) {
        if (StrUtil.isBlank(email) || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) {
            return email;
        }
        return mask(parts[0], 2, 0) + "@" + parts[1];
    }

    /**
     * 银行卡号脱敏
     * 保留前6后4位
     * 例如：622202******1234
     */
    public static String maskBankCard(String bankCard) {
        if (StrUtil.isBlank(bankCard) || bankCard.length() < 10) {
            return bankCard;
        }
        return mask(bankCard, 6, 4);
    }

    /**
     * 姓名脱敏
     * 保留姓，名字全部脱敏
     * 例如：张**
     */
    public static String maskName(String name) {
        if (StrUtil.isBlank(name) || name.length() <= 1) {
            return name;
        }
        return name.charAt(0) + StrUtil.repeat(MASK_CHAR, name.length() - 1);
    }

    /**
     * 地址脱敏
     * 保留前6位
     * 例如：北京市朝阳区****
     */
    public static String maskAddress(String address) {
        if (StrUtil.isBlank(address) || address.length() <= 6) {
            return address;
        }
        return mask(address, 6, 0);
    }

    /**
     * 通用脱敏方法
     *
     * @param str 原始字符串
     * @param prefixKeep 前面保留位数
     * @param suffixKeep 后面保留位数
     * @return 脱敏后的字符串
     */
    public static String mask(String str, int prefixKeep, int suffixKeep) {
        if (StrUtil.isBlank(str)) {
            return str;
        }

        int length = str.length();
        if (length <= prefixKeep + suffixKeep) {
            return str;
        }

        String prefix = str.substring(0, prefixKeep);
        String suffix = str.substring(length - suffixKeep);
        String middle = StrUtil.repeat(MASK_CHAR, length - prefixKeep - suffixKeep);

        return prefix + middle + suffix;
    }

    /**
     * 根据脱敏规则进行脱敏
     * 规则格式：phone:3,4 表示保留前3后4
     *
     * @param value 原始值
     * @param maskRule 脱敏规则
     * @return 脱敏后的值
     */
    public static String maskByRule(String value, String maskRule) {
        if (StrUtil.isBlank(value) || StrUtil.isBlank(maskRule)) {
            return value;
        }

        String[] parts = maskRule.split(":");
        if (parts.length != 2) {
            return value;
        }

        String type = parts[0];
        String rule = parts[1];

        // 特殊类型直接处理
        switch (type.toLowerCase()) {
            case "phone":
                return maskPhone(value);
            case "idcard":
                return maskIdCard(value);
            case "email":
                return maskEmail(value);
            case "bankcard":
                return maskBankCard(value);
            case "name":
                return maskName(value);
            case "address":
                return maskAddress(value);
            default:
                // 通用规则：prefix,suffix
                if (rule.contains(",")) {
                    String[] nums = rule.split(",");
                    int prefix = Integer.parseInt(nums[0]);
                    int suffix = Integer.parseInt(nums[1]);
                    return mask(value, prefix, suffix);
                } else if ("*".equals(rule)) {
                    // 全部脱敏
                    return StrUtil.repeat(MASK_CHAR, value.length());
                }
                return value;
        }
    }
}