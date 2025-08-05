package com.backend.ToDoList.service.impl;

import com.backend.ToDoList.dto.request.*;
import com.backend.ToDoList.dto.response.ExchangeTokenResponse;
import com.backend.ToDoList.dto.response.ProfileGoogleUserResponse;
import com.backend.ToDoList.dto.response.TokenResponse;
import com.backend.ToDoList.entity.User;
import com.backend.ToDoList.errors.AppException;
import com.backend.ToDoList.enums.ErrorCode;
import com.backend.ToDoList.mapper.UserMapper;
import com.backend.ToDoList.repository.UserRepository;
import com.backend.ToDoList.repository.client.GoogleExchangeToken;
import com.backend.ToDoList.repository.client.GoogleGetProfile;
import com.backend.ToDoList.service.AuthenticateService;
import com.backend.ToDoList.utils.EmailUtils;
import com.backend.ToDoList.utils.JwtUtils;
import com.backend.ToDoList.utils.OtpUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticateServiceImpl implements AuthenticateService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String,Object> redisTemplate;
    private final OtpUtils otpUtils;
    private final EmailUtils emailUtils;
    private final GoogleExchangeToken googleExchangeToken;
    private final GoogleGetProfile googleGetProfile;

    @Value("${google.client-secret}")
    protected String CLIENT_SECRET;
    @Value("${google.redirect-uri}")
    protected String REDIRECT_URI;
    private String GRANT_TYPE = "authorization_code";
    @Value("${google.client-id}")
    protected String CLIENT_ID;

    @Override
    @Transactional
    public TokenResponse handleLogin(LoginRequest req) {
        User u = null;
        try {
            u = userRepository.getByEmail(req.getEmail());
            if(passwordEncoder.matches(req.getPassword(), u.getPassword())) {
                return TokenResponse.builder().token(jwtUtils.generateToken(u.getEmail())).build();
            }
            else{
                throw new AppException(ErrorCode.LOGIN_FAILED);
            }
        }
        catch (Exception e) {
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }
    }
    @Override
    @Transactional
    public void handleRegister(RegisterRequest req) {
        User user = userRepository.findByEmail(req.getEmail());
//        log.info(String.valueOf(userRepository.findByEmail(u.getEmail()) !=null));
        if(user != null) {
            if(user.isActive()) {
//                System.out.println("hehhe");
                throw new AppException(ErrorCode.EMAIL_EXISTS);
            }
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            user.setName(req.getName());
            user.setEmail(req.getEmail());

            userRepository.save(user);
        } else {
            User u = userMapper.toUser(req);
            u.setActive(false);
            u.setEmail(req.getEmail());
            u.setAvatarUrl("default.jpg");
            user = userRepository.save(u);
        }

        String code = otpUtils.generateOtpCode();
        emailUtils.sendEmail(user.getEmail(), "Welcome to app", code);
        redisTemplate.opsForValue().set(req.getEmail(), code, 2, TimeUnit.MINUTES);
    }
    @Override
    @Transactional
    public void handleForgotPassword(ForgotPasswordRequest req){
        if(userRepository.existsByEmailAndActive(req.getEmail(),true)) {
            String otp = otpUtils.generateOtpCode();
            emailUtils.sendEmail(req.getEmail(), "Recover password", otp);
            redisTemplate.opsForValue().set(req.getEmail(), otp, 2, TimeUnit.MINUTES);
        }
        else {
            throw new AppException(ErrorCode.EMAIL_NOT_EXISTS);
        }
    }
    @Override
    @Transactional
    public void handleResetPassword(ResetPasswordRequest req) {
        log.info("hheeh");
        SecurityContext context = SecurityContextHolder.getContext();
        User u = (User) context.getAuthentication().getPrincipal();

        u.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(u);
    }
    @Override
    @Transactional
    public void handleLogout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Claims body = jwtUtils.getContent(token);
            String jit = body.getId();
            long ttl = body.getExpiration().getTime()  - new Date().getTime();
            if(ttl>0) {
                redisTemplate.opsForValue().set(jit,"BlackList" ,ttl, TimeUnit.MILLISECONDS);
            }
        }
    }
    @Override
    @Transactional
    public TokenResponse verifyOtp(VerifyOtpRequest req) {
        log.info("{} {} {}", req.getEmail(), req.getOtp(), req.getActionType());
        if(redisTemplate.hasKey(req.getEmail())) {
            String otp = (String) redisTemplate.opsForValue().get(req.getEmail());
            if(otp!= null &&  otp.equals(req.getOtp())) {
                if(req.getActionType().equals("register")) {
                    User u = userRepository.getByEmail(req.getEmail());
                    u.setActive(true);
                    userRepository.save(u);
                }
                if(req.getActionType().equals("change_email")) {
                    try {
                        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
                        if (aut.getName() == null) throw new AppException(ErrorCode.UNAUTHORIZED);
                        User u = userRepository.getByEmail(aut.getName());
                        u.setEmail(req.getEmail());
                        userRepository.save(u);
                    } catch (Exception e) {
                        throw new AppException(ErrorCode.UNAUTHORIZED);
                    }
                }
                return TokenResponse.builder().token(jwtUtils.generateToken(req.getEmail())).build();
            }
            else {
                throw new AppException(ErrorCode.OTP_INVALID);
            }
        } else {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }
    }
    @Override
    @Transactional
    public TokenResponse handleLoginWithGoogle(String code) {
        ExchangeTokenRequest req = ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build();
        try {
            ExchangeTokenResponse resp = googleExchangeToken.exchangeToken(req);
            ProfileGoogleUserResponse profile = googleGetProfile.getProfile("Bearer "+resp.getAccessToken());
            User u = userMapper.googleProfileToUser(profile);
            if(!userRepository.existsByEmail(profile.getEmail())) {
                u.setActive(true);
                u.setPassword("");
                u.setAvatarUrl("default.jpg");
                userRepository.save(u);
            }
            return TokenResponse.builder().token(jwtUtils.generateToken(u.getEmail())).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.GG_CODE_INVALID);
        }

    }
    @Override
    @Transactional
    public void resendEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user != null) {
            if(!user.isActive()) {
                String otp = otpUtils.generateOtpCode();
                emailUtils.sendEmail(user.getEmail(), "Welcome to app", otp);
                redisTemplate.opsForValue().set(email, otp, 2, TimeUnit.MINUTES);
            }else {
                throw new AppException(ErrorCode.EMAIL_EXISTS);
            }
        }else {
            throw new AppException(ErrorCode.EMAIL_NOT_EXISTS);
        }
    }
}
