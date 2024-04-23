package com.sidha.api.service;

import java.util.UUID;

import com.sidha.api.model.user.Admin;
import com.sidha.api.model.user.Karyawan;
import com.sidha.api.model.user.Sopir;
import com.sidha.api.model.user.UserModel;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.sidha.api.repository.TrukDb;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.request.EditUserDetailRequestDTO;
import com.sidha.api.model.enumerator.Role;
import com.sidha.api.repository.UserDb;

import java.util.List;
import java.util.Map;

import static com.sidha.api.model.enumerator.Role.SOPIR;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDb userDb;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StorageService storageService;

    @Autowired
    private TrukDb trukDb;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${app.image.url}")
    private String IMAGE_URL;

    @Override
    public List<UserModel> findAllList() {
        return userDb.findAll();
    }

    @Override
    public UserModel findById(UUID id) {
        return userDb.findById(id).orElseThrow(() -> new RuntimeException("Akun tidak ditemukan"));
    }

    @Override
    public UserModel findByEmail(String email) {
        return userDb.findByEmail(email);
    }

    @Override
    public UserModel findByUsername(String username) {
        return userDb.findByUsername(username);
    }

    @Override
    public UserModel save(UserModel user) {
        return userDb.save(user);
    }

    @Override
    public UserModel  getUserDetail(UUID id) {
        return userDb.getDetailUserById(id).orElseThrow(() -> new RuntimeException("Akun tidak ditemukan"));

    }

    @Override
    public UserModel editUserDetail(EditUserDetailRequestDTO requestDTO, UUID id) {
        UserModel user = userDb.findById(id).orElseThrow(() -> new RuntimeException("Akun tidak ditemukan"));
        user.setName(null != requestDTO.getName() ? requestDTO.getName() : user.getName());
        user.setAddress(null != requestDTO.getAddress() ? requestDTO.getAddress() : user.getAddress());
        user.setPhone(null != requestDTO.getPhone() ? requestDTO.getPhone() : user.getPhone());
        if (null != requestDTO.getImageFile()) {
            try {
                storageService.updateProfileImage(requestDTO.getImageFile(), user);
            } catch (Exception e) {
                throw new RuntimeException("Gagal mengunggah gambar : " + e.getMessage()) ;
            }
        }

        var userRole = user.getRole();
        if (userRole.equals(Role.ADMIN)) {
            var userAdmin = (Admin) user;
            userAdmin.setSuperAdmin(requestDTO.isSuperAdmin() ? requestDTO.isSuperAdmin() : userAdmin.isSuperAdmin());
            return userDb.save(userAdmin);
        } else if (userRole.equals(Role.KARYAWAN)) {
            var userKaryawan = (Karyawan) user;
            userKaryawan.setPosition(
                    null != requestDTO.getPosition() ? requestDTO.getPosition() : userKaryawan.getPosition());
            return userDb.save(userKaryawan);
        } else {
            return userDb.save(user);
        }
    }

    @Override
    public void changePassword(String currentPassword, String newPassword, UUID id) {
        UserModel user = userDb.findById(id).orElseThrow(() -> new RuntimeException("Akun tidak ditemukan"));
        String userPasswordDb = user.getPassword();

        // Memeriksa apakah password yang diinput saat ini cocok dengan password yang
        // ada di database
        boolean passwordMatch = passwordEncoder.matches(currentPassword, userPasswordDb);

        if (passwordMatch) {
            // Memeriksa apakah password yang baru tidak sama dengan password yang lama
            if (!currentPassword.equals(newPassword)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userDb.save(user);
            } else {
                throw new RuntimeException("Password baru tidak boleh sama dengan password lama !");
            }
        } else {
            throw new RuntimeException("Tolong masukkan password Anda saat ini dengan benar !");
        }
    }

    @Override
    public List<UserModel> getListRole(Role role) {
        return userDb.findAllByRole(role);
    }

    @Override
    public List<Sopir> getListSopirNoTruk() {
        List<UserModel> listSopir = userDb.findAllByRole(Role.SOPIR);
        List<Sopir> listSopirNoTruk = new ArrayList<>();
        for (UserModel user : listSopir) {
            if (user.getRole() == SOPIR) {
                Sopir sopirConverted = modelMapper.map(user, Sopir.class);
                if (trukDb.findBySopir(sopirConverted).isEmpty()) {
                    listSopirNoTruk.add(sopirConverted);
                }
            }
        }
        return listSopirNoTruk;
    }

    @Override
    public void deleteUser(UUID id) {
        try {
            var user = userDb.findById(id).get();
            user.setIsDeleted(true);
            userDb.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Akun tidak ditemukan");

        }
    }

    @Override
    public Long countUsersWithRoleCreatedInMonthAndYear(Role role, Integer month, Integer year) {
        LocalDateTime startDateTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDateTime = startDateTime.plusMonths(1);
        return userDb.countByRoleAndCreatedAtBetween(role, startDateTime, endDateTime);
    }
    public Map<Integer, Long> getUserCountByWeekInMonth(int year, int month, Role role) {
        return userDb.getUserCountByWeekInMonth(year, month, role);
    }

    public Map<Integer, Long> getUserCountByDayInMonth(int year, int month, Role role) {
        return userDb.getUserCountByDayInMonth(year, month, role);
    }

    public Map<Integer, Long> getUserCountByMonthInYear(int year, Role role) {
        return userDb.getUserCountByMonthInYear(year, role);
    }

    public Map<Integer, Long> getUserCountByYearRange(int startYear, int endYear, Role role) {
        return userDb.getUserCountByYearRange(startYear, endYear, role);
    }

}
