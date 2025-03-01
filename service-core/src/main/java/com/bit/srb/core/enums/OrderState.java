package com.bit.srb.core.enums;

public enum OrderState {

    PENDING("待支付", 1),
    PAID("已支付", 2),
    TIMEOUT_CANCELED("超时取消", 3),
    MANUAL_CANCELLED("手动取消", 4);

    private final String desc;
    private final int code;

    OrderState(String desc, int code) {
        this.desc = desc;
        this.code = code;
    }
}
