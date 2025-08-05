package com.backend.ToDoList.repository.client;

import com.backend.ToDoList.dto.response.ProfileGoogleUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "get-profile", url = "https://www.googleapis.com/oauth2/v3")
public interface GoogleGetProfile {
    @GetMapping("/userinfo")
    ProfileGoogleUserResponse getProfile(@RequestHeader("Authorization") String accessToken);
}
