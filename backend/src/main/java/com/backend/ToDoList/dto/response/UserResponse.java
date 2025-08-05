package com.backend.ToDoList.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private int id;
    private String name;
    private String email;
    private String avatarUrl;
    private boolean active;
}
