package com.backend.ToDoList.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Email(message = "EMAIL_INVALID")
    private String email;
    @NotEmpty(message = "MISSING_FIELD")
    private String password;
}
