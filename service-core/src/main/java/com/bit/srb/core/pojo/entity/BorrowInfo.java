package com.bit.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 借款信息表
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@TableName("borrow_info")
@Schema(name = "BorrowInfo对象", description = "借款信息表")
public class BorrowInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema( description = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "借款用户id")
    private Long userId;

    @Schema( description = "借款金额")
    private BigDecimal amount;

    @Schema( description = "借款期限")
    private Integer period;

    @Schema( description = "年化利率")
    private BigDecimal borrowYearRate;

    @Schema( description = "还款方式 1-等额本息 2-等额本金 3-每月还息一次还本 4-一次还本")
    private Integer returnMethod;

    @Schema( description = "资金用途")
    private Integer moneyUse;

    @Schema( description = "状态（0：未提交，1：审核中， 2：审核通过， -1：审核不通过）")
    private Integer status;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Schema( description = "创建时间")
    private LocalDateTime createTime;

    @Schema( description = "更新时间")
    private LocalDateTime updateTime;

    @Schema( description = "逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;

    //
    @Schema(description = "姓名")
    @TableField(exist = false)
    private String name;

    @Schema(description = "手机")
    @TableField(exist = false)
    private String mobile;

    @Schema(description = "其他参数")
    @TableField(exist = false)
    private Map<String, Object> param = new HashMap<>();


}
