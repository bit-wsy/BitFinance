package com.bit.srb.core.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "投标信息")
public class InvestVO {
    private Long lendId;
    private String investAmount;
    private Long investUserId;
    private String investName;
}
