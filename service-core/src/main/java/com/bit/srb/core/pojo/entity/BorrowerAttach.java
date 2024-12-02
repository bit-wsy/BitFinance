package com.bit.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 借款人上传资源表
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Getter
@Setter
@TableName("borrower_attach")
@Schema(name = "BorrowerAttach对象", description = "借款人上传资源表")
public class BorrowerAttach implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema( description = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema( description = "借款人id")
    private Long borrowerId;

    @Schema( description = "图片类型（idCard1：身份证正面，idCard2：身份证反面，house：房产证，car：车）")
    private String imageType;

    @Schema( description = "图片路径")
    private String imageUrl;

    @Schema( description = "图片名称")
    private String imageName;

    @Schema( description = "创建时间")
    private LocalDateTime createTime;

    @Schema( description = "更新时间")
    private LocalDateTime updateTime;

    @Schema( description = "逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
