package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sidha.api.model.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface OrderItemDb extends JpaRepository<OrderItem, UUID> {
        @Query("SELECT phi FROM OrderItem phi WHERE phi.sopir.id = :sopir")
        List<OrderItem> findByIdSopir(@Param("sopir") UUID sopir);

        List<OrderItem> findByStatusOrder(int i);

        @Query("SELECT phi FROM OrderItem phi WHERE phi.order.id = :idOrder")
        List<OrderItem> findByIdOrder(@Param("idOrder") UUID idOrder);

        // start of dashboard perusahaan
        @Query("SELECT SUM(CASE WHEN oi.statusOrder >= 6 THEN oi.price ELSE oi.price * 0.6 END) " +
                        "FROM OrderItem oi " +
                        "WHERE oi.createdAt BETWEEN :startDate AND :endDate " +
                        "AND oi.statusOrder >= 3")
        Long getTotalRevenueForToday(@Param("startDate") java.util.Date startDate,
                        @Param("endDate") java.util.Date endDate);

        @Query("SELECT EXTRACT(WEEK FROM oi.createdAt) AS week, COALESCE(SUM(CASE WHEN oi.statusOrder >= 6 THEN oi.price ELSE oi.price * 0.6 END), 0) AS revenue "
                        + "FROM OrderItem oi "
                        + "WHERE EXTRACT(YEAR FROM oi.createdAt) = :year AND EXTRACT(MONTH FROM oi.createdAt) = :month "
                        + "GROUP BY EXTRACT(WEEK FROM oi.createdAt)")
        List<Object[]> getWeeklyRevenueInMonth(@Param("year") int year, @Param("month") int month);

        @Query("SELECT MONTH(oi.createdAt) AS month, SUM(CASE WHEN oi.statusOrder >= 6 THEN oi.price ELSE oi.price * 0.6 END) AS revenue "
                        +
                        "FROM OrderItem oi " +
                        "WHERE YEAR(oi.createdAt) = :year AND oi.statusOrder >= 3 " +
                        "GROUP BY MONTH(oi.createdAt)")
        List<Object[]> getMonthlyRevenueInYear(@Param("year") int year);

        @Query("SELECT YEAR(oi.createdAt) AS year, SUM(CASE WHEN oi.statusOrder >= 6 THEN oi.price ELSE oi.price * 0.6 END) AS revenue "
                        +
                        "FROM OrderItem oi " +
                        "WHERE YEAR(oi.createdAt) BETWEEN :startYear AND :endYear AND oi.statusOrder >= 3 " +
                        "GROUP BY YEAR(oi.createdAt)")
        List<Object[]> getYearlyRevenueInRange(@Param("startYear") int startYear, @Param("endYear") int endYear);

        @Query("SELECT COUNT(oi) AS count, oi.statusOrder " +
                        "FROM OrderItem oi " +
                        "GROUP BY oi.statusOrder")
        List<Object[]> getOrderCountByStatus();
        // end of dashboard perusahaan
}
