package com.bit.srb.core.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "账户绑定")
public class UserBindVO {

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "银行类型")
    private String bankType;

    @Schema(description = "银行卡号")
    private String bankNo;

    @Schema(description = "手机号")
    private String mobile;
}
