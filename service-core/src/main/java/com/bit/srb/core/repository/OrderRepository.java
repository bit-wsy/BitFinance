package com.bit.srb.core.repository;

import com.bit.srb.core.pojo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
