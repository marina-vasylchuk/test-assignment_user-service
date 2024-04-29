package org.mvasylchuk.userservice;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlingController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Void> handle(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getFieldError();
        return new BaseResponse<>(null, fieldError.getField() + ": " + fieldError.getDefaultMessage());
    }

    @ExceptionHandler(UserServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Void> handle(UserServiceException e) {
        return new BaseResponse<>(null, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<Void> handle(Exception e) {
        System.out.println(e.getMessage());
        return new BaseResponse<>(null, "Internal error");
    }
}
