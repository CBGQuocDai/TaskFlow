package com.backend.ToDoList.mapper;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.backend.ToDoList.dto.request.RegisterRequest;
import com.backend.ToDoList.dto.response.ProfileGoogleUserResponse;
import com.backend.ToDoList.dto.response.UserResponse;
import com.backend.ToDoList.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    private final AmazonS3 amazonS3;
    @Value("${aws.bucket-name}")
    private String BUCKET_NAME;
    public User toUser(RegisterRequest req) {
        return User.builder().email(req.getEmail())
                .name(req.getName())
                .password(passwordEncoder.encode(req.getPassword())).build();
    }
    public User googleProfileToUser(ProfileGoogleUserResponse profile) {
        return User.builder()
                .email(profile.getEmail())
                .name(profile.getName())
                .build();
    }
    public UserResponse userToUserResponse(User u) {
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(BUCKET_NAME, u.getAvatarUrl())
                .withExpiration(new Date(Instant.now().plusSeconds(3600).toEpochMilli()))
                .withMethod(HttpMethod.GET);
        URL url = amazonS3.generatePresignedUrl(req);
        return UserResponse.builder()
                .id(u.getId())
                .avatarUrl(url.toString())
                .email(u.getEmail())
                .name(u.getName())
                .active(u.isActive())
                .build();
    }
}
