package com.bit.srb.core.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "借款审核信息")
public class BorrowInfoApprovalVO {

    @Schema(description =  "id")
    private Long id;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "审批内容")
    private String content;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "年化利率")
    private BigDecimal lendYearRate;

    @Schema(description = "平台服务费率")
    private BigDecimal serviceRate;

    @Schema(description = "开始日期")
    private String lendStartDate;

    @Schema(description = "描述信息")
    private String lendInfo;
}
