package com.backend.ToDoList.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegisterRequest {
    @NotEmpty(message = "MISSING_FIELD")
    private String name;
    @Email(message = "EMAIL_INVALID")
    private String email;
    @Size(min = 8,message = "PASSWORD_INVALID")
    private String password;
}
