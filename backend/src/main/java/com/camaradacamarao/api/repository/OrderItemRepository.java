package com.camaradacamarao.api.repository;

import com.camaradacamarao.api.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi.menuItem, SUM(oi.quantity) as total FROM OrderItem oi " +
           "WHERE MONTH(oi.order.createdAt) = :month AND YEAR(oi.order.createdAt) = :year " +
           "GROUP BY oi.menuItem ORDER BY total DESC")
    List<Object[]> findTopItemsByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT oi.menuItem, SUM(oi.quantity) as total FROM OrderItem oi " +
           "WHERE YEAR(oi.order.createdAt) = :year " +
           "GROUP BY oi.menuItem ORDER BY total DESC")
    List<Object[]> findTopItemsByYear(@Param("year") int year);

    @Query("SELECT oi.menuItem, SUM(oi.quantity) as total FROM OrderItem oi " +
           "WHERE MONTH(oi.order.createdAt) = :month AND YEAR(oi.order.createdAt) = :year " +
           "GROUP BY oi.menuItem ORDER BY total ASC")
    List<Object[]> findLeastItemsByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT oi.menuItem, SUM(oi.quantity) as total FROM OrderItem oi " +
           "WHERE YEAR(oi.order.createdAt) = :year " +
           "GROUP BY oi.menuItem ORDER BY total ASC")
    List<Object[]> findLeastItemsByYear(@Param("year") int year);
}
