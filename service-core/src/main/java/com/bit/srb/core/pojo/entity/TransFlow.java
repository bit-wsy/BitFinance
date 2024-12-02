package com.bit.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 交易流水表
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@TableName("trans_flow")
@Schema( name =  "TransFlow对象", description = "交易流水表")
public class TransFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema( description = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "用户id")
    private Long userId;

    @Schema( description = "用户名称")
    private String userName;

    @Schema( description = "交易单号")
    private String transNo;

    @Schema( description = "交易类型（1：充值 2：提现 3：投标 4：投资回款 ...）")
    private Integer transType;

    @Schema( description = "交易类型名称")
    private String transTypeName;

    @Schema( description = "交易金额")
    private BigDecimal transAmount;

    @Schema( description = "备注")
    private String memo;

    @Schema( description = "创建时间")
    private LocalDateTime createTime;

    @Schema( description = "更新时间")
    private LocalDateTime updateTime;

    @Schema( description = "逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
