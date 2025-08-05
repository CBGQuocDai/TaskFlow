package com.backend.ToDoList.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @Size(min = 8, message = "PASSWORD_INVALID")
    private String password;
}
