package com.backend.ToDoList.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    @Builder.Default
    private int code=1000;
    @Builder.Default
    private String message="success";
    private T data;
}
