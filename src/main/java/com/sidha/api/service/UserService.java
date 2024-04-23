package com.sidha.api.service;

import java.util.UUID;

import com.sidha.api.DTO.request.EditUserDetailRequestDTO;
import com.sidha.api.model.user.Sopir;
import com.sidha.api.model.user.UserModel;
import com.sidha.api.model.enumerator.Role;
import java.util.List;
import java.util.Map;

public interface UserService {

    UserModel findByEmail(String email);

    UserModel findByUsername(String username);

    UserModel findById(UUID id);

    UserModel save(UserModel user);

    UserModel getUserDetail(UUID id);

    UserModel editUserDetail(EditUserDetailRequestDTO requestDTO, UUID id);

    void changePassword(String currentPassword, String newPassword, UUID id);

    List<UserModel> getListRole(Role role);

    List<Sopir> getListSopirNoTruk();

    List<UserModel> findAllList();

    void deleteUser(UUID id);

    // dashboard perusahaan
    Long countUsersWithRoleCreatedInMonthAndYear(Role role, Integer month, Integer year);

    public Map<Integer, Long> getUserCountByWeekInMonth(int year, int month, Role role);

    public Map<Integer, Long> getUserCountByDayInMonth(int year, int month, Role role);

    public Map<Integer, Long> getUserCountByMonthInYear(int year, Role role);

    public Map<Integer, Long> getUserCountByYearRange(int startYear, int endYear, Role role);
    // end of dashboard perusahaan

}
