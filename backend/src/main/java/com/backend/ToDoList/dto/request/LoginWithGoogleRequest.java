package com.backend.ToDoList.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginWithGoogleRequest {
    private String code;
}
