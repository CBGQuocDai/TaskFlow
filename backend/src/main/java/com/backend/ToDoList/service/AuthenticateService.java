package com.backend.ToDoList.service;

import com.backend.ToDoList.dto.request.*;
import com.backend.ToDoList.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticateService {
    TokenResponse handleLogin(LoginRequest req);

    void handleRegister(RegisterRequest req);

    void handleForgotPassword(ForgotPasswordRequest req);

    void handleResetPassword(ResetPasswordRequest req);

    void handleLogout(HttpServletRequest request);

    TokenResponse verifyOtp(VerifyOtpRequest req);

    TokenResponse handleLoginWithGoogle(String code);

    void resendEmail(String email);
}
