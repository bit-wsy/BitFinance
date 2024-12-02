package com.bit.srb.core.pojo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "会员搜索对象")
public class UserInfoQuery {

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "1：出借人；2：借款人")
    private Integer userType;
}
