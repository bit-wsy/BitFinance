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
 * 积分等级表
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@TableName("integral_grade")
@Schema( name = "IntegralGrade对象", description = "积分等级表")
public class IntegralGrade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema( description = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "积分区间开始")
    private Integer integralStart;

    @Schema( description = "积分区间结束")
    private Integer integralEnd;

    @Schema( description = "借款额度")
    private BigDecimal borrowAmount;

    @Schema( description = "创建时间")
    private LocalDateTime createTime;

    @Schema( description = "更新时间")
    private LocalDateTime updateTime;

    @Schema( description = "逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
