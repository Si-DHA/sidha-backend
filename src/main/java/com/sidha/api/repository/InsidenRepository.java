package com.sidha.api.repository;

import com.sidha.api.model.Insiden;
import com.sidha.api.model.user.Sopir;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface InsidenRepository extends JpaRepository<Insiden, UUID> {
    // Method for fetching incidents by status
    List<Insiden> findByStatus(Insiden.InsidenStatus status);

    List<Insiden> findAllByIsDeletedFalse();

    List<Insiden> findBySopir(Sopir sopir);

    @Query("SELECT COUNT(i) AS count, DATE(i.createdAt) AS day " +
            "FROM Insiden i " +
            "WHERE i.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY day")
    Map<String, Long> getInsidenCountByDay(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COUNT(i) AS count, WEEK(i.createdAt) AS week, YEAR(i.createdAt) AS year, MONTH(i.createdAt) AS month "
            +
            "FROM Insiden i " +
            "WHERE YEAR(i.createdAt) = :year AND MONTH(i.createdAt) = :month " +
            "GROUP BY week, year, month")
    Map<String, Long> getInsidenCountByWeek(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(i) AS count, MONTH(i.createdAt) AS month, YEAR(i.createdAt) AS year " +
            "FROM Insiden i " +
            "WHERE YEAR(i.createdAt) = :year " +
            "GROUP BY month, year")
    Map<String, Long> getInsidenCountByMonth(@Param("year") int year);

    @Query("SELECT COUNT(i) AS count, YEAR(i.createdAt) AS year " +
            "FROM Insiden i " +
            "WHERE YEAR(i.createdAt) BETWEEN :startYear AND :endYear " +
            "GROUP BY year")
    Map<Integer, Long> getInsidenCountByYear(@Param("startYear") int startYear, @Param("endYear") int endYear);

}
