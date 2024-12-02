package com.bit.srb.core.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "借款人积分审核")
public class BorrowerApprovalVO {

    //
    @Schema(description = "借款人id")
    private Long borrowerId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "基本信息积分")
    private Integer infoIntegral;

    @Schema(description = "身份信息是否正确")
    private Boolean isIdCardOk;

    @Schema(description = "车辆信息是否正确")
    private Boolean isCarOk;

    @Schema(description = "房产信息是否正确")
    private Boolean isHouseOk;
}
