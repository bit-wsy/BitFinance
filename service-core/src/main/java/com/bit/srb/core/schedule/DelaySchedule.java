package com.bit.srb.core.schedule;

import com.bit.srb.core.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DelaySchedule {

    private final OrderService orderService;

    public DelaySchedule(OrderService orderService) {
        this.orderService = orderService;
    }

    // 每5秒执行一次（生产环境建议30秒）
    @Scheduled(fixedRate = 5000)
    public void checkTimeoutOrders() {
        orderService.processExpiredOrders();
    }
}
