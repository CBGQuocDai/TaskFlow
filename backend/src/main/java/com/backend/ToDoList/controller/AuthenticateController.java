package com.backend.ToDoList.controller;

import com.backend.ToDoList.dto.apiResponse.BaseResponseTokenResponse;
import com.backend.ToDoList.dto.apiResponse.ErrorResponse;
import com.backend.ToDoList.dto.request.*;
import com.backend.ToDoList.dto.response.BaseResponse;
import com.backend.ToDoList.dto.response.TokenResponse;
import com.backend.ToDoList.service.impl.AuthenticateServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authenticate" , description = "Authenticate related APIs")
public class AuthenticateController {
    private final AuthenticateServiceImpl authenticateServiceImpl;

    @Operation(summary = "Register user",
            description = "Allow client create account"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "create success",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                            name = "register success",
                            summary = "example for register success",
                            value = "{\n" +
                                    "    \"code\": 1000,\n" +
                                    "    \"message\": \"success\",\n" +
                                    "}"
                    )
            )),
            @ApiResponse(responseCode = "400", description = "create fail",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE
            , schema = @Schema(implementation = ErrorResponse.class)
            , examples = {
                    @ExampleObject(
                            name = "email already exist",
                            value= "{\n" +
                                    "    \"code\": 1005,\n" +
                                    "    \"message\": \"Email already exists\"\n" +
                                    "}"),
                    @ExampleObject(
                            name = "password is invalid",
                            description = "password must have least 8 characters",
                            value = "{\n" +
                                    "    \"code\": 1009,\n" +
                                    "    \"message\": \"Password must have least 8 characters\"\n" +
                                    "}"),
                    @ExampleObject(
                            name = "email is invalid",
                            value = "{\n" +
                                    "    \"code\": 1008,\n" +
                                    "    \"message\": \"Email is invalid\"\n" +
                                    "}")
            })),
    })
    @PostMapping("/register")
    public BaseResponse<Void> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "input for register user",
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    summary = "",
                                    description = "request include email (email is valid), name and password (password must have least 8 characters)",
                                    value = "{\n" +
                                            "    \"email\": \"dai3gmail.com\",\n" +
                                            "    \"name\" : \"tfasf\",\n" +
                                            "    \"password\": \"12345678\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody @Valid RegisterRequest req)
    {
        authenticateServiceImpl.handleRegister(req);
        return BaseResponse.<Void>builder().build();
    }

    @Operation(
            summary = "user login",
            description = "use to login"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "login success",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BaseResponseTokenResponse.class),
                    examples = @ExampleObject(
                            value = "{\n" +
                                    "    \"code\": 1000,\n" +
                                    "    \"message\": \"success\",\n" +
                                    "    \"data\": {\n" +
                                    "        \"token\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkYWk2QGdtYWlsLmNvbSIsImp0aSI6Ijc1YTZmNzcwLWNjMTctNGNkNi1hNzkwLWUyNmY1NjcxOWE5NSIsImlhdCI6MTc1MjI0NDk2OCwiZXhwIjoxNzUyMjQ1MDY4fQ.7cNy_QFr1EhTe4E683KxuWqu6X83EsVaIyMsVO3_xlRx5LSFVsWU1ZJbtwIh33w7MUrBvC234Iz80E8whw2dJQ\"\n" +
                                    "    }\n" +
                                    "}"
                    )
            )),
            @ApiResponse(responseCode = "400",description = "email or password is incorrect",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1006,\n" +
                                            "    \"message\": \"Email or password is incorrect\"\n" +
                                            "}"
                            ))),
    })
    @PostMapping("/login")
    public BaseResponse<TokenResponse> login(@RequestBody @Valid
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "input for login",
            content = @Content(
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(
                            summary = "",
                            description = "request include email and password",
                            value = "{\n" +
                                    "    \"email\": \"dai6@gmail.com\", \n" +
                                    "    \"password\": \"12345678\"\n" +
                                    "}"
                    )
            )
    )
                                                 LoginRequest req) {
        return BaseResponse.<TokenResponse>builder().data(authenticateServiceImpl.handleLogin(req)).build();
    }
    @Operation(
            summary = "logout",
            description = "logout account",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "logout" ,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "token is invalid or missing" ,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1001,\n" +
                                            "    \"message\": \"Unauthorized\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request) {
        authenticateServiceImpl.handleLogout(request);
        return BaseResponse.<Void>builder().build();
    }

    @Operation(
            summary = "verify otp code.",
            description = ""
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "verify otp success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema =  @Schema(implementation = BaseResponseTokenResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\",\n" +
                                            "    \"data\": {\n" +
                                            "        \"token\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkYWk2QGdtYWlsLmNvbSIsImp0aSI6Ijc1YTZmNzcwLWNjMTctNGNkNi1hNzkwLWUyNmY1NjcxOWE5NSIsImlhdCI6MTc1MjI0NDk2OCwiZXhwIjoxNzUyMjQ1MDY4fQ.7cNy_QFr1EhTe4E683KxuWqu6X83EsVaIyMsVO3_xlRx5LSFVsWU1ZJbtwIh33w7MUrBvC234Iz80E8whw2dJQ\"\n" +
                                            "    }\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400", description = "verify otp failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema =  @Schema(implementation = ErrorResponse.class),
                            examples ={
                                    @ExampleObject(
                                            name = "otp code is incorrect",
                                            value = "{\n" +
                                                    "    \"code\": 1010,\n" +
                                                    "    \"message\": \"OTP is invalid\"\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "otp code is expired",
                                            value = "{\n" +
                                                    "    \"code\": 1011,\n" +
                                                    "    \"message\": \"OTP is expired\"\n" +
                                                    "}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "token is invalid or missing" ,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1001,\n" +
                                            "    \"message\": \"Unauthorized\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "email is invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1008,\n" +
                                            "    \"message\": \"Email is invalid\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @PostMapping("/verifyOtp")
    public BaseResponse<TokenResponse> verifyOtp(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VerifyOtpRequest.class),
                            examples = @ExampleObject(
                                    description = "request body include email, otp and action type " +
                                            "(action type accepts three values: register, change_email, reset_password)",
                                    value = "{\n" +
                                            "    \"email\": \"dinhquocdai0303@gmail.com\",\n" +
                                            "    \"otp\": \"636431\",\n" +
                                            "    \"actionType\": \"change_email\"\n" +
                                            "}"
                            )
                    )
            )
            VerifyOtpRequest request) {
        return BaseResponse.<TokenResponse>builder().data(authenticateServiceImpl.verifyOtp(request)).build();
    }

    @Operation(summary = "forgot password",description = "feature helps user can recover their account")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Function to verify a user's email is valid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "email doesn't exist",
                                            value = "{\n" +
                                                    "    \"code\": 1012,\n" +
                                                    "    \"message\": \"Email doesn't exist\"\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "email is invalid",
                                            value = "{\n" +
                                                    "    \"code\": 1008,\n" +
                                                    "    \"message\": \"Email is invalid\"\n" +
                                                    "}"
                                    )
                            }
                    )
            ),
    })
    @PostMapping("/forgotPassword")
    public BaseResponse<Void> forgotPassword(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ForgotPasswordRequest.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"email\": \"dinhquocdai0303@gmail.com\"\n" +
                                            "}"
                            )
                    )
            )
            @Valid ForgotPasswordRequest request) {
        authenticateServiceImpl.handleForgotPassword(request);
        return BaseResponse.<Void>builder().build();
    }
    @Operation(
            summary = "reset password",
            description = "after verify email, this feature help user update password",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "password is invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples =
                                    @ExampleObject(
                                            value = "{\n" +
                                                    "    \"code\": 1009,\n" +
                                                    "    \"message\": \"Password must have least 8 characters\"\n" +
                                                    "}"
                                    )

                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "token is invalid or missing" ,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1001,\n" +
                                            "    \"message\": \"Unauthorized\"\n" +
                                            "}"
                            )
                    )
            ),
    })
    @PostMapping("/resetPassword")
    public BaseResponse<Void> resetPassword(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "new password must have at least 8 characters",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResetPasswordRequest.class),
                            examples = @ExampleObject(
                                    description = "",
                                    value = "{\n" +
                                            "    \"password\": \"123456789\"\n" +
                                            "}"
                            )
                    )
            )
            @Valid ResetPasswordRequest req) {
        authenticateServiceImpl.handleResetPassword(req);
        return BaseResponse.<Void>builder().build();
    }
    @Operation(summary = "login By using google account",description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "login success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponseTokenResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\",\n" +
                                            "    \"data\": {\n" +
                                            "        \"token\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkYWk2QGdtYWlsLmNvbSIsImp0aSI6Ijc1YTZmNzcwLWNjMTctNGNkNi1hNzkwLWUyNmY1NjcxOWE5NSIsImlhdCI6MTc1MjI0NDk2OCwiZXhwIjoxNzUyMjQ1MDY4fQ.7cNy_QFr1EhTe4E683KxuWqu6X83EsVaIyMsVO3_xlRx5LSFVsWU1ZJbtwIh33w7MUrBvC234Iz80E8whw2dJQ\"\n" +
                                            "    }\n" +
                                            "}"
                            )
                    )),
            @ApiResponse(responseCode = "400",description = "code is invalid.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1013,\n" +
                                            "    \"message\": \"invalid grant\"\n" +
                                            "}"
                            ))),
    })
    @PostMapping("/googleLogin")
    public BaseResponse<TokenResponse> loginWithGoogle(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginWithGoogleRequest.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\":\"4/0AVMBsJj7DAbwoo7ZscywfsEBp3IhGQmIvbPHmxS_jnWsv7kA\"\n" +
                                            "}"
                            )
                    )
            )
            LoginWithGoogleRequest request) {
        return BaseResponse.<TokenResponse>builder()
                .data(authenticateServiceImpl.handleLoginWithGoogle(request.getCode()))
                .build();
    }

    @Operation(summary = "verify user",description = "use to verify token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    value =  "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "token is invalid or missing" ,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1001,\n" +
                                            "    \"message\": \"Unauthorized\"\n" +
                                            "}"
                            )
                    )
            ),

    })
    @PostMapping("/token")
    public BaseResponse<Void> verifyToken() {
        return BaseResponse.<Void>builder().build();
    }
    @Operation(summary = "resend email",description = "use to resend email include otp code when user register.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    value =  "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "resend mail failed" ,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name=  "email is invalid.",
                                            value = "{\n" +
                                                    "    \"code\": 1008,\n" +
                                                    "    \"message\": \"Email is invalid\"\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name=  "email doesn't exists",
                                            description = "email doesn't register in system.",
                                            value = "{\n" +
                                                    "    \"code\": 1012,\n" +
                                                    "    \"message\": \"Email doesn't exist\"\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name=  "email exists",
                                            value = "{\n" +
                                                    "    \"code\": 1005,\n" +
                                                    "    \"message\": \"Email already exists\"\n" +
                                                    "}"
                                    ),
                            }
                    )
            ),

    })
    @PostMapping("/resendEmail")
    public BaseResponse<Void> resendEmail(@RequestParam @Email(message = "EMAIL_INVALID") String email) {
        authenticateServiceImpl.resendEmail(email);
        return BaseResponse.<Void>builder().build();
    }
}
