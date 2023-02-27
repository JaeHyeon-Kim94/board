package io.oauth2.client.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {IllegalArgumentException.class ,IllegalStateException.class, NullPointerException.class})
    public ErrorResult handleClientException(RuntimeException ex){
        return new ErrorResult(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = {NonTransientDataAccessException.class})
    public ErrorResult handleConflictException(Exception ex){
        return new ErrorResult(HttpStatus.CONFLICT.toString(), "Data Exception");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult handleServerException(Exception ex){
        log.error("[Handled Exception] ", ex);
        return new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "알 수 없는 오류");
    }

}
