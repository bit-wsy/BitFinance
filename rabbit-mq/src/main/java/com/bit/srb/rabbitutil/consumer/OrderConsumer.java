package com.bit.srb.rabbitutil.consumer;

import com.bit.srb.core.handler.OrderWebSocketHandler;
import com.bit.srb.core.pojo.vo.InvestVO;
import com.bit.srb.core.service.LendItemService;
import com.bit.srb.core.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @Autowired
    private OrderService orderService;

    @Autowired
    private LendItemService lendItemService;

    @RabbitListener(queues = "order.queue")
    public void handleOrder(InvestVO investVO) {
        // 从 InvestVO 中提取必要的信息
        String userId = String.valueOf(investVO.getInvestUserId());
        String stockKey = "product:stock:" + investVO.getLendId();
        String orderKey = "product:orders:" + investVO.getLendId();

        String result = orderService.handleOrder(userId, stockKey, orderKey);

        if ("下单成功".equals(result)) {
            // 推送结果给前端
            try {
                OrderWebSocketHandler.sendMessage(investVO.getInvestUserId(), lendItemService.commitInvest(investVO));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
