package com.backend.ToDoList.controller;

import com.backend.ToDoList.dto.apiResponse.BaseResponseUserResponse;
import com.backend.ToDoList.dto.apiResponse.ErrorResponse;
import com.backend.ToDoList.dto.request.ChangeEmailRequest;
import com.backend.ToDoList.dto.request.ChangeInfoRequest;
import com.backend.ToDoList.dto.request.ChangePasswordRequest;
import com.backend.ToDoList.dto.response.BaseResponse;
import com.backend.ToDoList.dto.response.UserResponse;
import com.backend.ToDoList.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "User related APIs")
public class UserController {
    private final UserServiceImpl userServiceImpl;
    @Operation(
            summary= "change password",
            description = "used to change password if it's necessary",
            security = {@SecurityRequirement(name = "bear-key")}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "change password success",
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
                    description = "change password failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "password is invalid",
                                    description = "password must have least 8 characters",
                                    value = "{\n" +
                                            "    \"code\": 1009,\n" +
                                            "    \"message\": \"Password must have least 8 characters\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401", description = "token is invalid or missing" ,
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
    @PutMapping("/changePassword")
    public BaseResponse<Void> changePassword(@RequestBody @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody (
                    required = true,
                    description = "input for change password",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChangePasswordRequest.class),
                            examples = {
                                    @ExampleObject(
                                            summary = "example input",
                                            description = "request body include new password and it must have least 8 characters.",
                                            value = "{\n" +
                                                    "    \"password\":\"123456789\"\n" +
                                                    "}"
                                    )
                            }
                    )
            )
                                                 ChangePasswordRequest req) {
        userServiceImpl.changePassword(req);
        return BaseResponse.<Void>builder().build();
    }

    @Operation(
            summary = "change email for client",
            description = "feature helps client change email",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
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
                    description = "email already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    name = "email already exists",
                                    value = "{\n" +
                                            "    \"code\": 1005,\n" +
                                            "    \"message\": \"Email already exists\"\n" +
                                            "}"
                            ), @ExampleObject(
                                    name = "email is invalid",
                                    value ="{\n" +
                                            "    \"code\": 1008,\n" +
                                            "    \"message\": \"Email is invalid\"\n" +
                                            "}"
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "401", description = "token is invalid or missing" ,
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
    
    @PutMapping("/changeEmail")
    public BaseResponse<Void> changeEmail(
            @RequestBody @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChangeInfoRequest.class),
                            examples = @ExampleObject(
                                    value ="{\n" +
                                            "    \"email\": \"dinhquocdai@gmail.com\"\n" +
                                            "}"
                            )
                    )
            )
                                              ChangeEmailRequest req) {
        userServiceImpl.changeEmail(req.getEmail());
        return BaseResponse.<Void>builder().build();
    }

    @Operation(summary = "change name for client",
            description = "feature allows client change name",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema  = @Schema(implementation = BaseResponseUserResponse.class),
                            examples = @ExampleObject (
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\",\n" +
                                            "    \"data\": {\n" +
                                            "        \"id\": 1,\n" +
                                            "        \"name\": \"dinh quoc dai hehe\",\n" +
                                            "        \"email\": \"dinhquocdai0303@gmail.com\",\n" +
                                            "        \"avatarUrl\": \"https://todolist12dinhquocdai.s3.amazonaws.com/1_1753797224221.slide_2.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250730T103949Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3599&X-Amz-Credential=AKIARHJJNFQMZKEKHDUR%2F20250730%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=9d8726a254b4fc40bb35b7fe4626157e4f9f0b539831509d41ac54411feb57db\",\n" +
                                            "        \"active\": true\n" +
                                            "    }\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401", description = "token is invalid or missing" ,
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
    @PutMapping("/changeInfo")
    public BaseResponse<UserResponse> changeInfo(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "request include information of user (name)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChangeInfoRequest.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"name\":\"dinh quoc dai hehe\"\n" +
                                            "}"
                            )
                    )
            )
            ChangeInfoRequest req) {
        return BaseResponse.<UserResponse>builder().data(userServiceImpl.changeName(req)).build();
    }

    @Operation(summary = "get info of user",
            description = "The system gets user info by relying on the token in the headers",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema  = @Schema(implementation = BaseResponseUserResponse.class),
                            examples = @ExampleObject (
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\",\n" +
                                            "    \"data\": {\n" +
                                            "        \"id\": 1,\n" +
                                            "        \"name\": \"dinh quoc dai hehe\",\n" +
                                            "        \"email\": \"dinhquocdai0303@gmail.com\",\n" +
                                            "        \"avatarUrl\": \"https://todolist12dinhquocdai.s3.amazonaws.com/1_1753797224221.slide_2.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250730T103949Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3599&X-Amz-Credential=AKIARHJJNFQMZKEKHDUR%2F20250730%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=9d8726a254b4fc40bb35b7fe4626157e4f9f0b539831509d41ac54411feb57db\",\n" +
                                            "        \"active\": true\n" +
                                            "    }\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401", description = "token is invalid or missing" ,
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
    @GetMapping("/info")
    public BaseResponse<UserResponse> info() {
        return BaseResponse.<UserResponse>builder().data(userServiceImpl.getCurrentUser()).build();
    }


    @Operation(summary = "change avatar",
            description = "allow user change avatar as you like, request include param avatar has type if file. Client only can upload file image.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "change avatar success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema =  @Schema(implementation = BaseResponseUserResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\",\n" +
                                            "    \"data\": {\n" +
                                            "        \"id\": 1,\n" +
                                            "        \"name\": \"Đinh Quốc Đại\",\n" +
                                            "        \"email\": \"dinhquocdai0303@gmail.com\",\n" +
                                            "        \"avatarUrl\": \"https://todolist12dinhquocdai.s3.amazonaws.com/1_1753797224221.slide_2.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250729T135346Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=AKIARHJJNFQMZKEKHDUR%2F20250729%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=e77f8bb7737c1316c4f843ff07716d88ae5302e82118005b44f791828d81e677\",\n" +
                                            "        \"active\": true\n" +
                                            "    }\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401", description = "token is invalid or missing" ,
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
                    responseCode = "400", description = "file isn't image or file size is too large",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    name = "file size is too large",
                                    value = "{\n" +
                                            "    \"code\": 123456,\n" +
                                            "    \"message\": \"Maximum upload size exceeded\"\n" +
                                            "}"
                            ),@ExampleObject(
                                    name = "file isn't image",
                                    value = "{\n" +
                                            "    \"code\": 1012,\n" +
                                            "    \"message\": \"file is not image\"\n" +
                                            "}"
                            )
                            }
                    )
            )
    })
    @PutMapping(value = "/changeAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<UserResponse> changeAvatar(@RequestParam("avatar")
                                                       @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                               required = true,
                                                               description = "Max of file size is 2 MB",
                                                               content = @Content(
                                                                       mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                                                               )
                                                       )
                                                       MultipartFile file) {
        return BaseResponse.<UserResponse>builder().data(userServiceImpl.changeAvatar(file)).build();
    }

}
