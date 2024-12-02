package com.bit.srb.core.pojo.vo;


import com.bit.srb.core.pojo.entity.BorrowerAttach;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "借款人认证信息")
public class BorrowerVO {

    @Schema(description = "性别（1：男 0：女）")
    private Integer sex;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "学历")
    private Integer education;

    @Schema(description = "是否结婚（1：是 0：否）")
    private Boolean marry;

    @Schema(description = "行业")
    private Integer industry;

    @Schema(description = "月收入")
    private Integer income;

    @Schema(description = "还款来源")
    private Integer returnSource;

    @Schema(description = "联系人名称")
    private String contactsName;

    @Schema(description = "联系人手机")
    private String contactsMobile;

    @Schema(description = "联系人关系")
    private Integer contactsRelation;

    @Schema(description = "借款人附件资料")
    private List<BorrowerAttach> borrowerAttachList;

}
