package com.backend.ToDoList.service;


import com.backend.ToDoList.dto.request.LoginRequest;
import com.backend.ToDoList.dto.response.TokenResponse;
import com.backend.ToDoList.entity.User;
import com.backend.ToDoList.errors.AppException;
import com.backend.ToDoList.mapper.UserMapper;
import com.backend.ToDoList.repository.UserRepository;
import com.backend.ToDoList.repository.client.GoogleExchangeToken;
import com.backend.ToDoList.repository.client.GoogleGetProfile;
import com.backend.ToDoList.service.impl.AuthenticateServiceImpl;
import com.backend.ToDoList.utils.EmailUtils;
import com.backend.ToDoList.utils.JwtUtils;
import com.backend.ToDoList.utils.OtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;


@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AuthenticateServiceImpl.class},
webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.yaml")
public class AuthenticateServiceImplTest {
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private UserMapper userMapper;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private RedisTemplate<String,Object> redisTemplate;
    @MockitoBean
    private OtpUtils otpUtils;
    @MockitoBean
    private EmailUtils emailUtils;
    @MockitoBean
    private GoogleExchangeToken googleExchangeToken;
    @MockitoBean
    private GoogleGetProfile googleGetProfile;

    @Autowired
    private AuthenticateServiceImpl authenticateServiceImpl;
    private LoginRequest loginRequest;
    private TokenResponse tokenResponse;
    private User u;
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("12345678");
        u = User.builder()
                .name("test")
                .email("test@test.com")
                .id(1)
                .password("12345678")
                .build();

        tokenResponse = TokenResponse.builder().token("hehehe").build();
    }
    @Test
    void testHandleLogin_Success() {
        //given
        given(userRepository.getByEmail(loginRequest.getEmail())).willReturn(u);
        given(passwordEncoder.matches(loginRequest.getPassword(), u.getPassword())).willReturn(true);
        given(jwtUtils.generateToken(u.getEmail())).willReturn(tokenResponse.getToken());
        //when
        TokenResponse  resp = authenticateServiceImpl.handleLogin(loginRequest);
        //then
        Assertions.assertThat(resp.getToken()).isEqualTo(tokenResponse.getToken());
    }
    @Test
    void testHandleLogin_EmailNotFound() {
        //given
        given(userRepository.getByEmail(loginRequest.getEmail())).willReturn(null);
        //when
        AppException resp = assertThrows(AppException.class,
                () -> authenticateServiceImpl.handleLogin(loginRequest));
        //then
        Assertions.assertThat(resp.getErrorCode().getCode()).isEqualTo(1006);
        Assertions.assertThat(resp.getErrorCode().getMessage()).isEqualTo("Email or password is incorrect");
    }
    @Test
    void testHandleLogin_PasswordIncorrect() {
        //given
        given(userRepository.getByEmail(loginRequest.getEmail())).willReturn(null);
        given(passwordEncoder.matches(loginRequest.getPassword(),
                u.getPassword())).willReturn(false);
        //when
        AppException resp = assertThrows(AppException.class,
                () -> authenticateServiceImpl.handleLogin(loginRequest));
        //then
        Assertions.assertThat(resp.getErrorCode().getCode()).isEqualTo(1006);
        Assertions.assertThat(resp.getErrorCode().getMessage()).isEqualTo("Email or password is incorrect");
    }

}
