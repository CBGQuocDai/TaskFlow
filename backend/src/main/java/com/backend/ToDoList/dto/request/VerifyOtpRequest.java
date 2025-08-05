package com.backend.ToDoList.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {
    @Email(message = "EMAIL_INVALID")
    private String email;
    private String otp;
    private String actionType;
}
