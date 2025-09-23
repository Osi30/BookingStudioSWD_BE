package com.studio.booking.exceptions;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.exceptions.exceptions.AccountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ExceptionGlobalHandler {

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<BaseResponse> handleAccountException(AccountException ex, WebRequest request) {
        BaseResponse exceptionResponse = BaseResponse.builder()
                .message(ex.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .data(request.getDescription(false))
                .build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
