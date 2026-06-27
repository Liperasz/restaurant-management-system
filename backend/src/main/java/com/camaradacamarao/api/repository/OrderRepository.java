package com.camaradacamarao.api.repository;

import com.camaradacamarao.api.model.Order;
import com.camaradacamarao.api.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByAttendantId(Long attendantId);
    List<Order> findByStatusNot(OrderStatus status);
    List<Order> findByStatusNotIn(List<OrderStatus> statuses);
    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}
