package org.monicandy.ecommerceapp.repository;

import org.monicandy.ecommerceapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 根据用户ID查询订单
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createTime DESC")
    List<Order> findByUserId(@Param("userId") Long userId);
}