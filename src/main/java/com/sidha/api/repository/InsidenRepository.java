package com.sidha.api.repository;

import com.sidha.api.model.Insiden;
import com.sidha.api.model.user.Sopir;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InsidenRepository extends JpaRepository<Insiden, UUID> {
        // Method for fetching incidents by status
        List<Insiden> findByStatus(Insiden.InsidenStatus status);

        List<Insiden> findAllByIsDeletedFalse();

        List<Insiden> findBySopir(Sopir sopir);

        // query for get total insiden in a certain period
        @Query("SELECT COUNT(u) " +
                        "FROM Insiden u " +
                        "WHERE  u.createdAt BETWEEN :startDate AND :endDate")
        Long getTotalInsidenForToday(@Param("startDate") java.time.LocalDateTime startDate,
                        @Param("endDate") java.time.LocalDateTime endDate);

        @Query("SELECT COUNT(u) FROM Insiden u WHERE EXTRACT (WEEK FROM u.createdAt) = EXTRACT (WEEK FROM CURRENT_DATE) AND EXTRACT (YEAR FROM u.createdAt) = EXTRACT (YEAR FROM CURRENT_DATE)")
        Long getTotalInsidenForThisWeek();

        @Query("SELECT COUNT(u) FROM Insiden u WHERE EXTRACT (MONTH FROM u.createdAt) = EXTRACT (MONTH FROM CURRENT_DATE)"
                        +
                        "AND EXTRACT (YEAR FROM u.createdAt) = EXTRACT (YEAR FROM CURRENT_DATE)")
        Long getTotalInsidenForThisMonth();

        @Query("SELECT COUNT(u) FROM Insiden u WHERE EXTRACT (YEAR FROM u.createdAt) = EXTRACT (YEAR FROM CURRENT_DATE)")
        Long getTotalInsidenForThisYear();

        // query for get total insiden in a certain range

        @Query("SELECT EXTRACT(WEEK FROM u.createdAt) AS week, COALESCE(COUNT(u), 0) AS insiden "
                        + "FROM Insiden u "
                        + "WHERE  EXTRACT(YEAR FROM u.createdAt) = :year AND EXTRACT(MONTH FROM u.createdAt) = :month "
                        + "GROUP BY EXTRACT(WEEK FROM u.createdAt)")
        List<Object[]> getWeeklyTotalInsidenInMonth(@Param("year") int year, @Param("month") int month);

        @Query(value = "SELECT MONTH(u.createdAt) AS month, COUNT(u) AS insiden " +
                        "FROM Insiden u " +
                        "WHERE YEAR(u.createdAt) = :year " +
                        "GROUP BY MONTH(u.createdAt)", nativeQuery = true)
        List<Object[]> getMonthlyTotalInsidenInYear(@Param("year") int year);

        @Query("SELECT YEAR(u.createdAt) AS year, COUNT(u) AS insiden " +
                        "FROM Insiden u " +
                        "WHERE YEAR(u.createdAt) BETWEEN :startYear AND :endYear  " +
                        "GROUP BY YEAR(u.createdAt)")
        List<Object[]> getYearlyTotalInsidenInRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}
