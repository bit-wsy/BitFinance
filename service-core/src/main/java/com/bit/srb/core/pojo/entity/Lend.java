package com.bit.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 标的准备表
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@Schema( name =  "Lend对象", description = "标的准备表")
public class Lend implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema( description = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "借款用户id")
    private Long userId;

    @Schema( description = "借款信息id")
    private Long borrowInfoId;

    @Schema( description = "标的编号")
    private String lendNo;

    @Schema( description = "标题")
    private String title;

    @Schema( description = "标的金额")
    private BigDecimal amount;

    @Schema( description = "投资期数")
    private Integer period;

    @Schema( description = "年化利率")
    private BigDecimal lendYearRate;

    @Schema( description = "平台服务费率")
    private BigDecimal serviceRate;

    @Schema( description = "还款方式")
    private Integer returnMethod;

    @Schema( description = "最低投资金额")
    private BigDecimal lowestAmount;

    @Schema( description = "已投金额")
    private BigDecimal investAmount;

    @Schema( description = "投资人数")
    private Integer investNum;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema( description = "发布日期")
    private LocalDateTime publishDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema( description = "开始日期")
    private LocalDate lendStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema( description = "结束日期")
    private LocalDate lendEndDate;

    @Schema( description = "说明")
    private String lendInfo;

    @Schema( description = "平台预期收益")
    private BigDecimal expectAmount;

    @Schema( description = "实际收益")
    private BigDecimal realAmount;

    @Schema( description = "状态")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema( description = "审核时间")
    private LocalDateTime checkTime;

    @Schema( description = "审核用户id")
    private Long checkAdminId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema( description = "放款时间")
    private LocalDateTime paymentTime;

    @Schema( description = "放款人id")
    private LocalDateTime paymentAdminId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema( description = "创建时间")
    private LocalDateTime createTime;

    @Schema( description = "更新时间")
    private LocalDateTime updateTime;

    @Schema( description = "逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;

    @Schema( description = "其他参数")
    @TableField(exist = false)
    private Map<String, Object> param = new HashMap<>();
}
