package com.sidha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidha.api.model.enumerator.Role;
import com.sidha.api.model.user.UserModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

@Repository
public interface UserDb extends JpaRepository<UserModel, UUID> {
        @Query("SELECT u FROM UserModel u WHERE u.username = ?1 AND u.isDeleted = false")
        UserModel findByUsername(String username);

        @Query("SELECT u FROM UserModel u WHERE u.email = ?1 AND u.isDeleted = false")
        UserModel findByEmail(String email);

        @Query("SELECT u FROM UserModel u WHERE u.token = ?1 AND u.isDeleted = false")
        UserModel findByToken(String token);

        @Query("SELECT u FROM UserModel u WHERE u.role = ?1 AND ( u.isDeleted = false OR u.isDeleted = true )")
        List<UserModel> findAllByRole(Role role);

        @SuppressWarnings("null")
        List<UserModel> findAll();

        @SuppressWarnings("null")
        @Query("SELECT u FROM UserModel u WHERE u.id = ?1 AND u.isDeleted = false")
        Optional<UserModel> findById(UUID id);

        @Query("SELECT u FROM UserModel u WHERE u.id =?1")
        Optional<UserModel> getDetailUserById(UUID id);

        @Query("SELECT COUNT(u) FROM UserModel u WHERE u.role = ?1 AND u.createdAt BETWEEN ?2 AND ?3 AND u.isDeleted = false")
        Long countByRoleAndCreatedAtBetween(Role role, LocalDateTime startDate, LocalDateTime endDate);

        // query for get total new client in a certain period
        @Query("SELECT COUNT(u) " +
                        "FROM UserModel u " +
                        "WHERE u.role = 'KLIEN'  AND u.createdAt BETWEEN :startDate AND :endDate")
        Long getTotalClientForToday(@Param("startDate") java.util.Date startDate,
                        @Param("endDate") java.util.Date endDate);

        @Query("SELECT COUNT(u) FROM UserModel u WHERE u.role = 'KLIEN' AND EXTRACT (WEEK FROM u.createdAt) = EXTRACT (WEEK FROM CURRENT_DATE) AND EXTRACT (YEAR FROM u.createdAt) = EXTRACT (YEAR FROM CURRENT_DATE)")
        Long getTotalClientForThisWeek();


        @Query("SELECT COUNT(u) FROM UserModel u WHERE u.role = 'KLIEN' AND EXTRACT (MONTH FROM u.createdAt) = EXTRACT (MONTH FROM CURRENT_DATE)" +
                        "AND EXTRACT (YEAR FROM u.createdAt) = EXTRACT (YEAR FROM CURRENT_DATE)")
        Long getTotalClientForThisMonth();

        @Query("SELECT COUNT(u) FROM UserModel u WHERE u.role = 'KLIEN' AND EXTRACT (YEAR FROM u.createdAt) = EXTRACT (YEAR FROM CURRENT_DATE)")
        Long getTotalClientForThisYear();

        // query for get total new client in a certain range

        @Query("SELECT EXTRACT(WEEK FROM u.createdAt) AS week, COALESCE(COUNT(u), 0) AS revenue "
                        + "FROM UserModel u "
                        + "WHERE u.role = 'KLIEN' AND  EXTRACT(YEAR FROM u.createdAt) = :year AND EXTRACT(MONTH FROM u.createdAt) = :month "
                        + "GROUP BY EXTRACT(WEEK FROM u.createdAt)")
        List<Object[]> getWeeklyTotalNewClientInMonth(@Param("year") int year, @Param("month") int month);

        @Query("SELECT MONTH(u.createdAt) AS month, COUNT(u) AS revenue " +
                        "FROM UserModel u " +
                        "WHERE YEAR(u.createdAt) = :year AND u.role = 'KLIEN'" +
                        "GROUP BY MONTH(u.createdAt)")
        List<Object[]> getMonthlyTotalNewClientInYear(@Param("year") int year);

        @Query("SELECT YEAR(u.createdAt) AS year, COUNT(u) AS revenue " +
                        "FROM UserModel u " +
                        "WHERE YEAR(u.createdAt) BETWEEN :startYear AND :endYear AND  u.role = 'KLIEN' " +
                        "GROUP BY YEAR(u.createdAt)")
        List<Object[]> getYearlyTotalNewClientInRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
}