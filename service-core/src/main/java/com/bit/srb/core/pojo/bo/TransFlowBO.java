package com.bit.srb.core.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransFlowBO {
    private String bindCode;
    private String transNo;
    private Integer transType;
    private BigDecimal transAmount;
    private String memo;

}
