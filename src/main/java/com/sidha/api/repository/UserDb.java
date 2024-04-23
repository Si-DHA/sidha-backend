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

        @Query("SELECT COUNT(u) AS count, WEEK(u.createdAt) AS week " +
                        "FROM UserModel u " +
                        "WHERE YEAR(u.createdAt) = :year AND MONTH(u.createdAt) = :month " +
                        "AND (:role IS NULL OR u.role = :role) " +
                        "GROUP BY WEEK(u.createdAt)")
        Map<Integer, Long> getUserCountByWeekInMonth(@Param("year") int year, @Param("month") int month,
                        @Param("role") Role role);

        @Query("SELECT COUNT(u) AS count, DAY(u.createdAt) AS day " +
                        "FROM UserModel u " +
                        "WHERE YEAR(u.createdAt) = :year AND MONTH(u.createdAt) = :month " +
                        "AND (:role IS NULL OR u.role = :role) " +
                        "GROUP BY DAY(u.createdAt)")
        Map<Integer, Long> getUserCountByDayInMonth(@Param("year") int year, @Param("month") int month,
                        @Param("role") Role role);

        @Query("SELECT COUNT(u) AS count, MONTH(u.createdAt) AS month " +
                        "FROM UserModel u " +
                        "WHERE YEAR(u.createdAt) = :year " +
                        "AND (:role IS NULL OR u.role = :role) " +
                        "GROUP BY MONTH(u.createdAt)")
        Map<Integer, Long> getUserCountByMonthInYear(@Param("year") int year, @Param("role") Role role);

        @Query("SELECT COUNT(u) AS count, YEAR(u.createdAt) AS year " +
                        "FROM UserModel u " +
                        "WHERE YEAR(u.createdAt) BETWEEN :startYear AND :endYear " +
                        "AND (:role IS NULL OR u.role = :role) " +
                        "GROUP BY YEAR(u.createdAt)")
        Map<Integer, Long> getUserCountByYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear,
                        @Param("role") Role role);
}