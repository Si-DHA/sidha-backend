package com.sidha.api.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.DTO.UserMapper;
import com.sidha.api.DTO.request.ForgotPassUserRequestDTO;
import com.sidha.api.DTO.request.LoginUserRequestDTO;
import com.sidha.api.DTO.request.SignUpUserRequestDTO;
import com.sidha.api.DTO.response.UserResponse;
import com.sidha.api.model.Admin;
import com.sidha.api.model.Karyawan;
import com.sidha.api.model.Klien;
import com.sidha.api.model.Sopir;
import com.sidha.api.repository.UserDb;
import com.sidha.api.security.jwt.JwtUtils;
import com.sidha.api.utils.MailSenderUtils;

import java.util.UUID;
import java.time.LocalDateTime;
import java.time.Duration;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserDb userDb;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MailSenderUtils mailSenderUtils;

    private static final long EXPIRE_TOKEN=30;

    @Override
    public UserResponse register(SignUpUserRequestDTO request) {
        var user = userMapper.toUserModel(request);
        var jwt = jwtUtils.generateJwtToken(user);
        var userResponse = new UserResponse();
        userResponse.setToken(jwt);
        userResponse.setUser(user);
        saveUser(request);
        return userResponse;
    }

    private void saveUser(SignUpUserRequestDTO request) {
        switch (request.getRole()) {
            case ADMIN:
                userDb.save(modelMapper.map(request, Admin.class));
                break;
            case KARYAWAN:
                userDb.save(modelMapper.map(request, Karyawan.class));
                break;
            case SOPIR:
                userDb.save(modelMapper.map(request, Sopir.class));
                break;
            case KLIEN:
                userDb.save(modelMapper.map(request, Klien.class));
                break;
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }

    @Override
    public UserResponse login(LoginUserRequestDTO request) {
        var user = userDb.findByEmail(request.getEmail());
        var jwt = jwtUtils.generateJwtToken(user);
        var UserResponse = new UserResponse();
        UserResponse.setToken(jwt);
        UserResponse.setUser(user);
        return UserResponse;
    }

    @Override
    public void forgotPassword(ForgotPassUserRequestDTO request) {
        var token = generateToken();
        var user = userDb.findByEmail(request.getEmail());
        user.setToken(token);
        user.setTokenCreatedAt(LocalDateTime.now());
        userDb.save(user);
        mailSenderUtils.sendMail(request.getEmail(), "Reset Password", generateMessage(token, user.getName()));
    }

    private String generateMessage(String token, String name) {
        var message = new StringBuilder();
        message.append("Hai, ").append(name).append("\n\n");
        message.append("Lupa password?").append("\n");
        message.append("Kami menerima permintaan untuk mereset password akun anda").append("\n\n");
        message.append("Untuk melanjutkan proses reset password, silahkan klik link berikut").append("\n");
        message.append("https://sidha-frontend.vercel.app/reset-password?token=").append(token).append("\n\n");
        message.append("Jika anda tidak merasa melakukan permintaan ini, abaikan email ini.").append("\n\n");
        message.append("Terima kasih!").append("\n");
        return message.toString();
    }

    @Override
    public void resetPassword(String token, String password) {
        var user = userDb.findByToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        if (isTokenExpired(user.getTokenCreatedAt())) {
            throw new IllegalArgumentException("Token expired");
        }
        user.setPassword(password);
        user.setToken(null);
        user.setTokenCreatedAt(null);
        userDb.save(user);
    }

    private String generateToken() {
        StringBuilder token = new StringBuilder();

        return token.append(UUID.randomUUID().toString())
                .append(UUID.randomUUID().toString()).toString();
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);

        return diff.toMinutes() >= EXPIRE_TOKEN;
    }
}