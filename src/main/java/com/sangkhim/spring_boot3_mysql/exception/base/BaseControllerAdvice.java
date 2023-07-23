package com.sangkhim.spring_boot3_mysql.exception.base;

import com.sangkhim.spring_boot3_mysql.exception.BadRequestException;
import com.sangkhim.spring_boot3_mysql.exception.DataNotFoundException;
import com.sangkhim.spring_boot3_mysql.exception.DuplicateException;
import com.sangkhim.spring_boot3_mysql.exception.TooManyRequestsException;
import com.sangkhim.spring_boot3_mysql.exception.dto.ErrorResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/** Base Handler Exception class. Manage response for all exception Class */
@Slf4j
@RestControllerAdvice
public class BaseControllerAdvice {

  public static final Instant TIMESTAMP =
      LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();

  @ExceptionHandler({NoHandlerFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse noHandlerFoundException(NoHandlerFoundException ex) {
    LOGGER.debug(ex.getMessage(), ex.getCause());
    return new ErrorResponse(
        String.valueOf(HttpStatus.NOT_FOUND.value()),
        "No resource found for your request. Please verify you request",
        TIMESTAMP);
  }

  @ExceptionHandler({DataNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse dataNotFoundException(Exception ex) {
    LOGGER.debug(ex.getMessage(), ex.getCause());
    return new ErrorResponse(
        String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage(), TIMESTAMP);
  }

  @ExceptionHandler({BadRequestException.class, DuplicateException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBadRequestException(Exception ex) {
    return new ErrorResponse(
        String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage(), TIMESTAMP);
  }

  @ExceptionHandler({TooManyRequestsException.class})
  @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
  public ErrorResponse handleTooManyRequestsException(Exception ex) {
    return new ErrorResponse(
        String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()), ex.getMessage(), TIMESTAMP);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ErrorResponse notSupportedException(HttpRequestMethodNotSupportedException ex) {
    LOGGER.debug(ex.getMessage(), ex.getCause());
    return new ErrorResponse(
        String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()),
        "Method Not Allowed. Please verify you request",
        TIMESTAMP);
  }

  @ExceptionHandler({Exception.class, ServiceException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleAllExceptions(Exception ex) {
    LOGGER.error(ex.getMessage(), ex.getLocalizedMessage());
    return new ErrorResponse(
        String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), ex.getMessage(), TIMESTAMP);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidationExceptionHandler(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return new ErrorResponse(
        String.valueOf(HttpStatus.BAD_REQUEST.value()), errors.toString(), TIMESTAMP);
  }
}
