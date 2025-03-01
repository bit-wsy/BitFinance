package com.bit.srb.core.service;

import com.bit.srb.core.enums.OrderState;
import com.bit.srb.core.pojo.entity.Order;
import com.bit.srb.core.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Data
public class OrderService {

    private final RedissonClient redissonClient;
    private final OrderRepository orderRepository;
    private RDelayedQueue<Order> delayedQueue;

    // 使用@PostConstruct初始化队列
    @PostConstruct
    private void initQueue() {
        RQueue<Order> queue = redissonClient.getQueue("order:delayed:queue");
        this.delayedQueue = redissonClient.getDelayedQueue(queue);
    }

    /**
     * 创建订单并加入延时队列
     */
    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(OrderState.PENDING);
        Order savedOrder = orderRepository.save(order);

        // 30分钟未支付则自动取消
        delayedQueue.offer(savedOrder, 30, TimeUnit.MINUTES);
        return savedOrder;
    }

    /**
     * 支付成功处理
     */
    @Transactional
    public void payOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderState.PENDING) {
            throw new IllegalStateException("Order cannot be paid in current status");
        }

        order.setStatus(OrderState.PAID);
        orderRepository.save(order);

        // 从队列中移除
        delayedQueue.remove(order);
        log.info("Order {} paid successfully", orderId);
    }

    /**
     * 取消订单（超时/手动）
     */
    @Transactional
    public void cancelOrder(String orderId, boolean isTimeout) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderState.PENDING) {
            log.warn("Cannot cancel order {} in status {}", orderId, order.getStatus());
            return;
        }

        order.setStatus(isTimeout ? OrderState.TIMEOUT_CANCELED : OrderState.MANUAL_CANCELLED);
        orderRepository.save(order);

        // 确保从队列移除
        delayedQueue.remove(order);
        log.info("Order {} cancelled by {}", orderId, isTimeout ? "timeout" : "manual");
    }

    /**
     * 处理超时订单（定时任务调用）
     */
    @Transactional
    public void processExpiredOrders() {
        Order order;
        while ((order = delayedQueue.poll()) != null) {
            if (order.getStatus() == OrderState.PENDING) {
                cancelOrder(order.getLendItem().getLendItemNo(), true);
            }
        }
    }
}
