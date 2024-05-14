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
    Long getTotalNewClientForToday();
  
    Long getTotalNewClientForThisWeek();
  
    Long getTotalNewClientForThisMonth();
  
    Long getTotalNewClientForThisYear();
  
    List<List<Object>> getTotalNewClient();
  
    List<List<Object>>  getWeeklyTotalNewClientInMonth(int year, int month);
  
    List<List<Object>>  getMonthlyTotalNewClientInYear(int year);
  
    List<List<Object>>  getYearlyTotalNewClientInRange(int startYear, int endYear);
  
    List<UserModel> getListClientToday();

    List<UserModel> getListClientThisWeek();

    List<UserModel> getListClientThisMonth();

    List<UserModel> getListClientThisYear(); 

}
