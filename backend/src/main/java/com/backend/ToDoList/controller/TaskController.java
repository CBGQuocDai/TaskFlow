package com.backend.ToDoList.controller;

import com.backend.ToDoList.dto.apiResponse.BaseResponseStatisticResponse;
import com.backend.ToDoList.dto.apiResponse.BaseResponseTaskListResponse;
import com.backend.ToDoList.dto.apiResponse.BaseResponseTaskResponse;
import com.backend.ToDoList.dto.apiResponse.ErrorResponse;
import com.backend.ToDoList.dto.request.TaskRequest;
import com.backend.ToDoList.dto.response.BaseResponse;
import com.backend.ToDoList.dto.response.ApiResponsePage;
import com.backend.ToDoList.dto.response.StatisticResponse;
import com.backend.ToDoList.dto.response.TaskResponse;
import com.backend.ToDoList.service.impl.TaskServiceImpl;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todos")
@Tag(name = "Task" , description = "Task related APIs")
public class TaskController {
    private final TaskServiceImpl taskServiceImpl;

    @Operation(
            summary = "add task",
            description = "use to add task to list",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "create success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponseTaskResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\",\n" +
                                            "    \"data\": {\n" +
                                            "        \"id\": 58,\n" +
                                            "        \"title\": \"Hehehhe4\",\n" +
                                            "        \"description\": \"Buy milk, eggs, bread, and cheese\",\n" +
                                            "        \"dueDate\": \"2025-10-10T00:00:00.000+00:00\",\n" +
                                            "        \"completed\": false\n" +
                                            "    }\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "token is invalid or missing",
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
                    description = "input is not valid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name= "title is empty",
                                            value = "{\n" +
                                                    "    \"code\": 1007,\n" +
                                                    "    \"message\": \"missing value title\"\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "can't parse date from string",
                                            value = "{\n" +
                                                    "    \"code\": 123456,\n" +
                                                    "    \"message\": \"JSON parse error: Cannot deserialize value of type `java.util.Date` from String \\\"2025-10\\\": not a valid representation (error: Failed to parse Date value '2025-10': Cannot parse date \\\"2025-10\\\": while it seems to fit format 'yyyy-MM-dd', parsing fails (leniency? null))\"\n" +
                                                    "}"
                                    )
                            }
                    )
            )
    })
    @PostMapping("")
    public BaseResponse<TaskResponse> createTask(@RequestBody @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskRequest.class),
                            examples = @ExampleObject(
                                    description = "request body include title (title must difference from empty), description and dueDate (format : YYYY-MM-DD)",
                                    value = "{\n" +
                                            "  \"title\": \"Hehehhe4\", \n" +
                                            "  \"description\": \"Buy milk, eggs, bread, and cheese\",\n" +
                                            "  \"dueDate\": \"2025-10-10\"\n" +
                                            "}"
                            )
                    )
            )
                                                     TaskRequest req) {
        return BaseResponse.<TaskResponse>builder().data(taskServiceImpl.createTask(req)).build();
    }
    @Operation(
            summary = "add task",
            description = "use to add task to list",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "create success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponseTaskResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\",\n" +
                                            "    \"data\": {\n" +
                                            "        \"id\": 58,\n" +
                                            "        \"title\": \"Hehehhe4\",\n" +
                                            "        \"description\": \"Buy milk, eggs, bread, and cheese\",\n" +
                                            "        \"dueDate\": \"2025-08-10T00:00:00.000+00:00\",\n" +
                                            "        \"completed\": false\n" +
                                            "    }\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "token is invalid or missing",
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
                    description = "input is not valid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name= "title is empty",
                                            value = "{\n" +
                                                    "    \"code\": 1007,\n" +
                                                    "    \"message\": \"missing value title\"\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "can't parse date from string",
                                            value = "{\n" +
                                                    "    \"code\": 123456,\n" +
                                                    "    \"message\": \"JSON parse error: Cannot deserialize value of type `java.util.Date` from String \\\"2025-10\\\": not a valid representation (error: Failed to parse Date value '2025-10': Cannot parse date \\\"2025-10\\\": while it seems to fit format 'yyyy-MM-dd', parsing fails (leniency? null))\"\n" +
                                                    "}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found task",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1003,\n" +
                                            "    \"message\": \"Task not found \"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1004,\n" +
                                            "    \"message\": \"Forbidden\"\n" +
                                            "}"
                            )
                    )
            ),
    })
    @PutMapping("/{id}")
    public BaseResponse<TaskResponse> updateTask(@PathVariable int id, @RequestBody @Valid
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                required = true,
                description = "",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = TaskRequest.class),
                        examples = @ExampleObject(
                                description = "request body include title (title must difference from empty), description and dueDate (format: YYYY-MM-DD)",
                                value = "{\n" +
                                        "  \"title\": \"Hehehhe4\", \n" +
                                        "  \"description\": \"Buy milk, eggs, bread, and cheese\",\n" +
                                        "  \"dueDate\": \"2025-08-10\"\n" +
                                        "}"
                        )
                )
        )
    TaskRequest req) {
        return BaseResponse.<TaskResponse>builder().data(taskServiceImpl.updateTask(id,req)).build();
    }
    @Operation(
            summary = "delete task",
            description = "use to delete task from list",
            security = @SecurityRequirement(name = "bearer-key")
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
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1004,\n" +
                                            "    \"message\": \"Forbidden\"\n" +
                                            "}"
                            )
                    )
            ),
    })

    @DeleteMapping("/{id}")
    public BaseResponse<TaskResponse> deleteTask(@PathVariable int id) {
        taskServiceImpl.deleteTask(id);
        return BaseResponse.<TaskResponse>builder().build();
    }
    @Operation(
            summary = "get task list",
            description = "get task list ",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "get task list success",
                    content = @Content(
                           mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponseTaskListResponse.class),
                            examples = @ExampleObject(
                                    description = "with page = 1 and limit = 10",
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\",\n" +
                                            "    \"data\": [\n" +
                                            "        {\n" +
                                            "            \"id\": 58,\n" +
                                            "            \"title\": \"Hehehhe4\",\n" +
                                            "            \"description\": \"Buy milk, eggs, bread, and cheese\",\n" +
                                            "            \"dueDate\": \"2025-08-10T00:00:00.000+00:00\",\n" +
                                            "            \"completed\": false\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"id\": 57,\n" +
                                            "            \"title\": \"DRP Update 2\",\n" +
                                            "            \"description\": \"DRP update heheh\",\n" +
                                            "            \"dueDate\": \"2025-06-24T17:00:00.000+00:00\",\n" +
                                            "            \"completed\": true\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"id\": 56,\n" +
                                            "            \"title\": \"DRP Update\",\n" +
                                            "            \"description\": \"Erp application\",\n" +
                                            "            \"dueDate\": \"2025-05-14T17:00:00.000+00:00\",\n" +
                                            "            \"completed\": false\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"id\": 47,\n" +
                                            "            \"title\": \"Report sale \",\n" +
                                            "            \"description\": \"Prepare quarterly shareholder report.\",\n" +
                                            "            \"dueDate\": \"2025-08-31T17:00:00.000+00:00\",\n" +
                                            "            \"completed\": true\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"id\": 45,\n" +
                                            "            \"title\": \"User Manuals\",\n" +
                                            "            \"description\": \"Create user manuals for new product.\",\n" +
                                            "            \"dueDate\": \"2025-09-09T07:00:00.000+00:00\",\n" +
                                            "            \"completed\": false\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"id\": 41,\n" +
                                            "            \"title\": \"Privacy Policy\",\n" +
                                            "            \"description\": \"Update company privacy policy.\",\n" +
                                            "            \"dueDate\": \"2025-09-05T03:00:00.000+00:00\",\n" +
                                            "            \"completed\": false\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"id\": 39,\n" +
                                            "            \"title\": \"Web Analytics\",\n" +
                                            "            \"description\": \"Analyze website traffic and user engagement.\",\n" +
                                            "            \"dueDate\": \"2025-09-03T04:00:00.000+00:00\",\n" +
                                            "            \"completed\": false\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"id\": 37,\n" +
                                            "            \"title\": \"Case Study\",\n" +
                                            "            \"description\": \"Write case study for successful client project.\",\n" +
                                            "            \"dueDate\": \"2025-09-01T08:00:00.000+00:00\",\n" +
                                            "            \"completed\": false\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"id\": 35,\n" +
                                            "            \"title\": \"Payroll Processing\",\n" +
                                            "            \"description\": \"Process payroll for current month.\",\n" +
                                            "            \"dueDate\": \"2025-08-30T03:00:00.000+00:00\",\n" +
                                            "            \"completed\": true\n" +
                                            "        },\n" +
                                            "        {\n" +
                                            "            \"id\": 33,\n" +
                                            "            \"title\": \"Tax Preparation\",\n" +
                                            "            \"description\": \"Prepare quarterly tax filings.\",\n" +
                                            "            \"dueDate\": \"2025-08-28T09:00:00.000+00:00\",\n" +
                                            "            \"completed\": false\n" +
                                            "        }\n" +
                                            "    ],\n" +
                                            "    \"page\": 1,\n" +
                                            "    \"limit\": 10,\n" +
                                            "    \"total\": 26\n" +
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
    @GetMapping("")
    public ApiResponsePage<List<TaskResponse>> getAllTasks(
            @RequestParam(defaultValue = "1") int page ,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "all") String complete,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {

        List<TaskResponse> res = taskServiceImpl.getListTasks(page,limit,orderBy, complete,sortOrder);
        return ApiResponsePage.<List<TaskResponse>>builder()
                .data(res)
                .page(page)
                .limit(limit)
                .total(taskServiceImpl.getTotalTasks(orderBy,complete,sortOrder))
                .build();
    }

    @Operation(
            summary = "change completed task",
            description = "change task's status from done to process and vice versa.",
            security = @SecurityRequirement(name = "bearer-key"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "create success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponseTaskResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\",\n" +
                                            "    \"data\": {\n" +
                                            "        \"id\": 58,\n" +
                                            "        \"title\": \"Hehehhe4\",\n" +
                                            "        \"description\": \"Buy milk, eggs, bread, and cheese\",\n" +
                                            "        \"dueDate\": \"2025-08-10T00:00:00.000+00:00\",\n" +
                                            "        \"completed\": false\n" +
                                            "    }\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "token is invalid or missing",
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
                    responseCode = "404",
                    description = "not found task",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1003,\n" +
                                            "    \"message\": \"Task not found \"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1004,\n" +
                                            "    \"message\": \"Forbidden\"\n" +
                                            "}"
                            )
                    )
            ),
    })
    @PutMapping("/changeCompleted/{id}")
    public BaseResponse<TaskResponse> changeCompletedTask(@PathVariable int id) {
        return BaseResponse.<TaskResponse>builder().data(taskServiceImpl.changeTaskCompleted(id)).build();
    }

    @Operation(
            summary = "get statistic",
            description = "get statistic about your task which you finished and are doing",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "get statistic success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponseStatisticResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"code\": 1000,\n" +
                                            "    \"message\": \"success\",\n" +
                                            "    \"data\": {\n" +
                                            "        \"totalTasks\": 31,\n" +
                                            "        \"completedTasks\": 16,\n" +
                                            "        \"overdueTasks\": 2,\n" +
                                            "        \"inProgressTasks\": 13,\n" +
                                            "        \"dueSoonToday\": 0,\n" +
                                            "        \"dueSoonThisWeek\": 0,\n" +
                                            "        \"dueSoonNextWeek\": 3,\n" +
                                            "        \"dueSoonAfterTwoWeeks\": 10\n" +
                                            "    }\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "token is invalid or missing",
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
    @GetMapping("/stat")
    public BaseResponse<StatisticResponse> stat() {
        return BaseResponse.<StatisticResponse>builder()
                .data(taskServiceImpl.getStatistic()).build();
    }
}
