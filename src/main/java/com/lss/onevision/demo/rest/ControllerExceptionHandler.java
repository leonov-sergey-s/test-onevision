package com.lss.onevision.demo.rest;

import com.lss.onevision.demo.dto.ExceptionResponseDto;
import com.lss.onevision.demo.exception.ApiRuntimeException;
import com.lss.onevision.demo.exception.ExceptionMessageKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(ApiRuntimeException.class)
    public ResponseEntity<ExceptionResponseDto> handleApiRuntimeException(
            ApiRuntimeException ex, Locale locale) {
        HttpStatus status = Optional
                .ofNullable(AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class))
                .map(ResponseStatus::value)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        String message =
                messageSource.getMessage(ex.getMessageCode(), ex.getMessageArgs(), ex.getMessageCode(), locale);
        return new ResponseEntity<>(
                new ExceptionResponseDto(status.value(), status.getReasonPhrase(), message),
                status
        );
    }


    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(Exception ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new ExceptionResponseDto(status.value(), status.getReasonPhrase(), ex.getMessage()),
                status
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getConstraintViolations().stream().findFirst()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .orElse(null);

        return new ResponseEntity<>(
                new ExceptionResponseDto(status.value(), status.getReasonPhrase(), message),
                status
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception ex, Locale locale) {
        log.error("Unknown error", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = messageSource.getMessage(ExceptionMessageKeys.DEFAULT, null, "Internal Server Error", locale);
        return new ResponseEntity<>(
                new ExceptionResponseDto(status.value(), status.getReasonPhrase(), message),
                status
        );
    }

}
