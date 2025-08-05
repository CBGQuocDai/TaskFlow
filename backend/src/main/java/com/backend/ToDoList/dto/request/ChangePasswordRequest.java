package com.backend.ToDoList.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String oldPassword;
    @NotEmpty(message = "MISSING_FIELD")
    @Size(min=8, message = "PASSWORD_INVALID")
    private String newPassword;
}
