package com.bit.srb.core.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "借款人信息展示")
public class BorrowerDetailVO {

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "学历")
    private String education;

    @Schema(description = "结婚")
    private String marry;

    @Schema(description = "行业")
    private String industry;

    @Schema(description = "收入")
    private String income;

    @Schema(description = "还款来源")
    private String returnSource;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "联系人姓名")
    private String contactsName;

    @Schema(description = "联系人关系")
    private String contactsRelation;

    @Schema(description = "联系人手机")
    private String contactsMobile;

    @Schema(description = "附件列表")
    private List<BorrowerAttachVO> borrowerAttachVOList;

    //
    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "审核状态")
    private String status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime createTime;

}
