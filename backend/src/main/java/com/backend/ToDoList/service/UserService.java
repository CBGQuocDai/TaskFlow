package com.backend.ToDoList.service;

import com.backend.ToDoList.dto.request.ChangeInfoRequest;
import com.backend.ToDoList.dto.request.ChangePasswordRequest;
import com.backend.ToDoList.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void changePassword(ChangePasswordRequest req);

    UserResponse getCurrentUser();

    UserResponse changeName(ChangeInfoRequest req);

    void changeEmail(String newEmail);

    UserResponse changeAvatar(MultipartFile file);
}
