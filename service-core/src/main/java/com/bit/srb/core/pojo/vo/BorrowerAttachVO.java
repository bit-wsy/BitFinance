package com.bit.srb.core.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "上传附件")
public class BorrowerAttachVO {

    @Schema(description = "URL")
    private String imageUrl;

    @Schema(description = "图片类型（idCard1：身份证正面，idCard2：身份证反面，house：房产证，car：车）")
    private String imageType;
}
