package cn.kmdckj.base.entity;

import cn.kmdckj.base.entity.base.BaseImmutableEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 授权操作日志表实体。
 * 仅包含创建时间，不可修改。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grant_operation_log")
public class GrantOperationLog extends BaseImmutableEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 目标角色ID
     */
    private Long targetRoleId;

    /**
     * 目标角色名称
     */
    private String targetRoleName;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 校验结果:PASS-通过 REJECT-拒绝
     */
    private String checkResult;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 操作详情(JSON)
     */
    private String operationDetail;

    /**
     * IP地址
     */
    private String ipAddress;
}
