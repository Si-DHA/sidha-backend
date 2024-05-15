package com.sidha.api.service;

import java.util.UUID;

import com.sidha.api.model.user.Admin;
import com.sidha.api.model.user.Karyawan;
import com.sidha.api.model.user.Sopir;
import com.sidha.api.model.user.UserModel;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Map;
import java.util.HashMap;

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
    public UserModel getUserDetail(UUID id) {
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
                throw new RuntimeException("Gagal mengunggah gambar : " + e.getMessage());
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
    public Long getTotalNewClientForToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        return userDb.getTotalClientForToday(startDate, endDate);
    }

    @Override
    public Long getTotalNewClientForThisWeek() {
        return userDb.getTotalClientForThisWeek();
    }

    @Override
    public Long getTotalNewClientForThisMonth() {
        return userDb.getTotalClientForThisMonth();
    }

    @Override
    public Long getTotalNewClientForThisYear() {
        return userDb.getTotalClientForThisYear();
    }

    @Override
    public List<List<Object>> getTotalNewClient() {
        try {
            Long today = getTotalNewClientForToday();
            Long weekly = getTotalNewClientForThisWeek();
            Long monthly = getTotalNewClientForThisMonth();
            Long yearly = getTotalNewClientForThisYear();
            List<List<Object>> result = new ArrayList<>();
            result.add(Arrays.asList("today", today));
            result.add(Arrays.asList("weekly", weekly));
            result.add(Arrays.asList("monthly", monthly));
            result.add(Arrays.asList("yearly", yearly));
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil data:\n" + e.getMessage());
        }
    }

    private List<Integer> generateAllWeeksInMonth(int year, int month) {
        List<Integer> weeks = new ArrayList<>();
        LocalDate date = LocalDate.of(year, month, 1);
        while (date.getMonthValue() == month) {
            int week = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            if (!weeks.contains(week)) {
                weeks.add(week);
            }
            date = date.plusDays(1);
        }
        return weeks;
    }

    @Override
    public List<List<Object>> getWeeklyTotalNewClientInMonth(int year, int month) {
        List<Integer> allWeeks = generateAllWeeksInMonth(year, month);
        List<Object[]> data = userDb.getWeeklyTotalNewClientInMonth(year, month);
        Map<Integer, Object[]> dataMap = new HashMap<>();
        for (Object[] row : data) {
            int week = ((Number) row[0]).intValue();
            dataMap.put(week, row);
        }

        int firstWeek = allWeeks.get(0);

        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Minggu", "Jumlah Klien Baru"));
        for (int week : allWeeks) {
            List<Object> row = new ArrayList<>();
            if (dataMap.containsKey(week)) {
                Object[] rowValue = dataMap.get(week);
                row.add(((Number) rowValue[0]).intValue() - firstWeek + 1);
                row.add(rowValue[1]);
            } else {
                row.add(week - firstWeek + 1);
                row.add(0);
            }
            result.add(row);
        }
        return result;
    }

    @Override
    public List<List<Object>> getMonthlyTotalNewClientInYear(int year) {
        List<Object[]> data = userDb.getMonthlyTotalNewClientInYear(year);
        Map<Integer, Object[]> dataMap = new HashMap<>();
        for (Object[] row : data) {
            int month = ((Number) row[0]).intValue();
            dataMap.put(month, row);
        }

        String[] monthNames = new String[] { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus",
                "September", "Oktober", "November", "Desember" };

        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Bulan", "Jumlah Klien Baru"));

        for (int month = 1; month <= 12; month++) {
            List<Object> row = new ArrayList<>();
            if (dataMap.containsKey(month)) {
                Object[] rowValue = dataMap.get(month);
                row.add(monthNames[month - 1]);
                row.add(rowValue[1]);
            } else {
                row.add(monthNames[month - 1]);
                row.add(0);
            }
            result.add(row);
        }

        return result;
    }

    @Override
    public List<List<Object>> getYearlyTotalNewClientInRange(int startYear, int endYear) {
        List<Object[]> clientData = userDb.getYearlyTotalNewClientInRange(startYear, endYear);

        Map<Integer, Object[]> clientDataMap = new HashMap<>();
        for (Object[] data : clientData) {
            int year = ((Number) data[0]).intValue();
            clientDataMap.put(year, data);
        }

        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList("Bulan", "Jumlah Klien Baru"));

        for (int year = startYear; year <= endYear; year++) {
            List<Object> row = new ArrayList<>();
            row.add(year);
            if (clientDataMap.containsKey(year)) {
                row.add(clientDataMap.get(year)[1]);
            } else {
                row.add(0);
            }
            result.add(row);
        }

        return result;
    }

    @Override
    public List<UserModel> getListClientToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        return userDb.getListClientForToday(startDate, endDate);
    }

    @Override
    public List<UserModel> getListClientThisWeek() {
        return userDb.getListClientForThisWeek();
    }

    @Override
    public List<UserModel> getListClientThisMonth() {
        return userDb.getListClientForThisMonth();
    }

    @Override
    public List<UserModel> getListClientThisYear() {
        return userDb.getListClientForThisYear();
    }
 

}
