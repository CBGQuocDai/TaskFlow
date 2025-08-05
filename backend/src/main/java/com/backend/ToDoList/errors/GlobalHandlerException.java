package com.backend.ToDoList.errors;

import com.backend.ToDoList.dto.response.BaseResponse;
import com.backend.ToDoList.enums.ErrorCode;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<BaseResponse<?>> handleRuntimeException(RuntimeException e) {
        BaseResponse<?> resp = new BaseResponse<>();
        resp.setMessage(e.getMessage() );
        resp.setCode(123456);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<BaseResponse<?>> handleAppException(AppException e) {
        ErrorCode error = e.getErrorCode();
        BaseResponse<?> resp = new BaseResponse<>();
        resp.setMessage(error.getMessage());
        resp.setCode(error.getCode());
        return new ResponseEntity<>(resp, error.getHttpStatus());
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException
            (MethodArgumentNotValidException e) {
        String err = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        if(err==null) return ResponseEntity.badRequest().body(new BaseResponse<>());
        String field = e.getFieldError().getField();
        ErrorCode errorCode = ErrorCode.valueOf(err);
        BaseResponse<?> resp = new BaseResponse<>();
        if(err.equals("MISSING_FIELD")){
            resp.setMessage(errorCode.getMessage()+field);
        }else {
            resp.setMessage(errorCode.getMessage());
        }
        resp.setCode(errorCode.getCode());
        return ResponseEntity.badRequest().body(resp);
    }
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<?>> handleDateTimeParseException
            (HttpMessageNotReadableException e) {
        BaseResponse<?> resp = new BaseResponse<>();
        resp.setMessage(e.getMessage());
        resp.setCode(123456);
        return ResponseEntity.badRequest().body(resp);
    }
}
