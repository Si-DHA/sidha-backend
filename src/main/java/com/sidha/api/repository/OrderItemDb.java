package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sidha.api.model.order.OrderItem;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@Repository
public interface OrderItemDb extends JpaRepository<OrderItem, UUID> {
        @Query("SELECT phi FROM OrderItem phi WHERE phi.sopir.id = :sopir")
        List<OrderItem> findByIdSopir(@Param("sopir") UUID sopir);

        List<OrderItem> findByStatusOrder(int i);

        @Query("SELECT phi FROM OrderItem phi WHERE phi.order.id = :idOrder")
        List<OrderItem> findByIdOrder(@Param("idOrder") UUID idOrder);

        // start of dashboard perusahaan

        @Query("SELECT COUNT(oi) " +
                        "FROM OrderItem oi " +
                        "WHERE oi.statusOrder = :status AND oi.updatedAt BETWEEN :startDate AND :endDate")
        Long getTotalCompletedOrdersForToday(@Param("startDate") java.util.Date startDate,
                        @Param("endDate") java.util.Date endDate, @Param("status") int status);

        @Query("SELECT SUM(CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END) " +
                        "FROM OrderItem oi " +
                        "WHERE oi.updatedAt BETWEEN :startDate AND :endDate " +
                        "AND oi.statusOrder >= 3")
        Long getTotalRevenueForToday(@Param("startDate") java.util.Date startDate,
                        @Param("endDate") java.util.Date endDate);

        // Revenue for today
        @Query("SELECT SUM(CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END) " +
                        "FROM OrderItem oi " +
                        "WHERE oi.updatedAt >= :startOfDay AND oi.updatedAt <= :endOfDay " +
                        "AND oi.statusOrder >= 3")
        Long getRevenueForToday(@Param("startOfDay") java.util.Date startOfDay,
                        @Param("endOfDay") java.util.Date endOfDay);

        // Revenue for this week
        @Query("SELECT SUM(CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END) " +
                        "FROM OrderItem oi " +
                        "WHERE EXTRACT(WEEK FROM oi.updatedAt) = EXTRACT(WEEK FROM CURRENT_DATE) " +
                        "AND oi.statusOrder >= 3")
        Long getRevenueForThisWeek();

        // Revenue for this month
        @Query("SELECT SUM(CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END) " +
                        "FROM OrderItem oi " +
                        "WHERE EXTRACT(MONTH FROM oi.updatedAt) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                        "AND oi.statusOrder >= 3")
        Long getRevenueForThisMonth();

        // Revenue for this year
        @Query("SELECT SUM(CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END) " +
                        "FROM OrderItem oi " +
                        "WHERE EXTRACT(YEAR FROM oi.updatedAt) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                        "AND oi.statusOrder >= 3")
        Long getRevenueForThisYear();

        @Query("SELECT EXTRACT(WEEK FROM oi.updatedAt) AS week, COALESCE(SUM(CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END), 0) AS revenue "
                        + "FROM OrderItem oi "
                        + "WHERE EXTRACT(YEAR FROM oi.updatedAt) = :year AND EXTRACT(MONTH FROM oi.updatedAt) = :month AND oi.statusOrder >= 3"
                        + "GROUP BY EXTRACT(WEEK FROM oi.updatedAt)")
        List<Object[]> getWeeklyRevenueInMonth(@Param("year") int year, @Param("month") int month);

        @Query("SELECT MONTH(oi.updatedAt) AS month, SUM(CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END) AS revenue "
                        +
                        "FROM OrderItem oi " +
                        "WHERE YEAR(oi.updatedAt) = :year AND oi.statusOrder >= 3 " +
                        "GROUP BY MONTH(oi.updatedAt)")
        List<Object[]> getMonthlyRevenueInYear(@Param("year") int year);

        @Query("SELECT YEAR(oi.updatedAt) AS year, SUM(CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END) AS revenue "
                        +
                        "FROM OrderItem oi " +
                        "WHERE YEAR(oi.updatedAt) BETWEEN :startYear AND :endYear AND oi.statusOrder>= 3 " +
                        "GROUP BY YEAR(oi.updatedAt)")
        List<Object[]> getYearlyRevenueInRange(@Param("startYear") int startYear, @Param("endYear") int endYear);

        @Query("SELECT COUNT(oi) AS count, oi.statusOrder " +
                        "FROM OrderItem oi " +
                        "GROUP BY oi.statusOrder")
        List<Object[]> getOrderCountByStatus();

        // get total completed order item for certain time range
        @Query("SELECT EXTRACT(WEEK FROM oi.updatedAt) AS week, COUNT(oi) AS count " +
                        "FROM OrderItem oi " +
                        "WHERE oi.statusOrder = :status AND EXTRACT(YEAR FROM oi.updatedAt) = :year AND EXTRACT(MONTH FROM oi.updatedAt) = :month "
                        +
                        "GROUP BY EXTRACT(WEEK FROM oi.updatedAt)")
        List<Object[]> getWeeklyOrdersInMonth(@Param("year") int year, @Param("month") int month,
                        @Param("status") int status);

        @Query("SELECT MONTH(oi.updatedAt) AS month, COUNT(oi) AS count " +
                        "FROM OrderItem oi " +
                        "WHERE oi.statusOrder = :status AND YEAR(oi.updatedAt) = :year " +
                        "GROUP BY MONTH(oi.updatedAt)")
        List<Object[]> getMonthlyOrdersInYear(@Param("year") int year, @Param("status") int status);

        @Query("SELECT YEAR(oi.updatedAt) AS year, COUNT(oi) AS count " +
                        "FROM OrderItem oi " +
                        "WHERE oi.statusOrder = :status AND YEAR(oi.updatedAt) BETWEEN :startYear AND :endYear " +
                        "GROUP BY YEAR(oi.updatedAt)")
        List<Object[]> getYearlyOrdersInRange(@Param("startYear") int startYear,
                        @Param("endYear") int endYear, @Param("status") int status);

        @Query("SELECT oi FROM OrderItem oi WHERE oi.order.klien.id = :klienId AND oi.statusOrder NOT IN (-1, 0)")
        List<OrderItem> findByKlienIdAndStatusNotIn(@Param("klienId") UUID klienId);

        @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.klien.id = :klienId AND oi.statusOrder = 5")
        int countCompletedOrderItemsByKlienId(@Param("klienId") UUID klienId);

        // List order item for today, this week, this month, and this year
        @Query("SELECT new map(oi as orderItem, " +
                        "CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END as price) " +
                        "FROM OrderItem oi " +
                        "WHERE oi.updatedAt >= :startOfDay AND oi.updatedAt <= :endOfDay " +
                        "AND oi.statusOrder >= 3")
        List<Map<String, Object>> getListRevenueForToday(@Param("startOfDay") java.util.Date startOfDay,
                        @Param("endOfDay") java.util.Date endOfDay);

        @Query("SELECT new map(oi as orderItem, " +
                        "CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END as price) " +
                        "FROM OrderItem oi " +
                        "WHERE EXTRACT(WEEK FROM oi.updatedAt) = EXTRACT(WEEK FROM CURRENT_DATE) " +
                        "AND oi.statusOrder >= 3")
        List<Map<String, Object>> getListRevenueForThisWeek();

        @Query("SELECT new map(oi as orderItem, " +
                        "CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END as price) " +
                        "FROM OrderItem oi " +
                        "WHERE EXTRACT(MONTH FROM oi.updatedAt) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                        "AND oi.statusOrder >= 3")
        List<Map<String, Object>> getListRevenueForThisMonth();

        @Query("SELECT new map(oi as orderItem, " +
                        "CASE WHEN oi.statusOrder >= 5 THEN oi.price ELSE oi.price * 0.6 END as price) " +
                        "FROM OrderItem oi " +
                        "WHERE EXTRACT(YEAR FROM oi.updatedAt) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                        "AND oi.statusOrder >= 3")
        List<Map<String, Object>> getListRevenueForThisYear();

        // List order item that completed for today, this week, this month, and this
        // year
        @Query("SELECT oi " +
                        "FROM OrderItem oi " +
                        "WHERE oi.statusOrder = :status AND oi.updatedAt BETWEEN :startDate AND :endDate")
        List<OrderItem> getListOrdersForToday(@Param("startDate") java.util.Date startDate,
                        @Param("endDate") java.util.Date endDate, @Param("status") int status);

        @Query("SELECT oi " +
                        "FROM OrderItem oi " +
                        "WHERE oi.statusOrder = :status AND EXTRACT(YEAR FROM oi.updatedAt) = EXTRACT (YEAR FROM CURRENT_DATE) AND EXTRACT(MONTH FROM oi.updatedAt) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(WEEK FROM oi.updatedAt) = EXTRACT(WEEK FROM CURRENT_DATE)")
        List<OrderItem> getListOrdersForThisWeek(@Param("status") int status);

        @Query("SELECT oi " +
                        "FROM OrderItem oi " +
                        "WHERE oi.statusOrder = :status AND EXTRACT(YEAR FROM oi.updatedAt) = EXTRACT (YEAR FROM CURRENT_DATE) AND  EXTRACT(MONTH FROM oi.updatedAt) = EXTRACT(MONTH FROM CURRENT_DATE)")
        List<OrderItem> getListOrdersForThisMonth(@Param("status") int status);

        @Query("SELECT oi " +
                        "FROM OrderItem oi " +
                        "WHERE oi.statusOrder = :status AND EXTRACT(YEAR FROM oi.updatedAt) = EXTRACT(YEAR FROM CURRENT_DATE)")
        List<OrderItem> getListOrdersForThisYear(@Param("status") int status);

}
