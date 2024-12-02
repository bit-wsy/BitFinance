package com.bit.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 标的出借回款记录表
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@TableName("lend_item_return")
@Schema( name =  "LendItemReturn对象", description = "标的出借回款记录表")
public class LendItemReturn implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema( description = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "标的还款id")
    private Long lendReturnId;

    @Schema( description = "标的项id")
    private Long lendItemId;

    @Schema( description = "标的id")
    private Long lendId;

    @Schema( description = "出借用户id")
    private Long investUserId;

    @Schema( description = "出借金额")
    private BigDecimal investAmount;

    @Schema( description = "当前的期数")
    private Integer currentPeriod;

    @Schema( description = "年化利率")
    private BigDecimal lendYearRate;

    @Schema( description = "还款方式 1-等额本息 2-等额本金 3-每月还息一次还本 4-一次还本")
    private Integer returnMethod;

    @Schema( description = "本金")
    private BigDecimal principal;

    @Schema( description = "利息")
    private BigDecimal interest;

    @Schema( description = "本息")
    private BigDecimal total;

    @Schema( description = "手续费")
    private BigDecimal fee;

    @Schema( description = "还款时指定的还款日期")
    private LocalDate returnDate;

    @Schema( description = "实际发生的还款时间")
    private LocalDateTime realReturnTime;

    @Schema( description = "是否逾期")
    @TableField("is_overdue")
    private Boolean overdue;

    @Schema( description = "逾期金额")
    private BigDecimal overdueTotal;

    @Schema( description = "状态（0-未归还 1-已归还）")
    private Integer status;

    @Schema( description = "创建时间")
    private LocalDateTime createTime;

    @Schema( description = "更新时间")
    private LocalDateTime updateTime;

    @Schema( description = "逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
