package com.bit.srb.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LendItemStatusEnum {
    DEFAULT_STATUS(0,"默认"),
    PALLED(1, "已支付"),
    RETURNED(2,"已还款");

    private Integer status;
    private String msg;

    public static String getMsgByStatus(int status) {
        LendItemStatusEnum arrObj[] = LendItemStatusEnum.values();
        for (LendItemStatusEnum obj : arrObj) {
            if (status == obj.getStatus().intValue()) {
                return obj.getMsg();
            }
        }
        return "";
    }
}
