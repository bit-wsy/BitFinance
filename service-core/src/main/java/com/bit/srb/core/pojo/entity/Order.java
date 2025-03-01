package com.bit.srb.core.pojo.entity;

import com.bit.srb.core.enums.OrderState;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class Order {
    private OrderState status;

    @Column(unique = true)
    private LendItem lendItem;
}
