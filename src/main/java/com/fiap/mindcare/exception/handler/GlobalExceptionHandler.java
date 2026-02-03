package com.fiap.mindcare.exception.handler;

import com.fiap.mindcare.exception.ExceptionResponse;
import com.fiap.mindcare.service.exception.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(
            Exception ex, WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                getMessage("error.default", ex.getMessage()),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<ExceptionResponse> handleBadCredentials(
            Exception ex, WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                getMessage("error.badCredentials", ex.getMessage()),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(
            Exception ex, WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                getMessage("error.notFound", ex.getMessage()),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public final ResponseEntity<ExceptionResponse> handleBusinessExceptions(
            Exception ex, WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                getMessage("error.business", ex.getMessage()),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ExceptionResponse> handleAccessDeniedException(
            Exception ex, WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                getMessage("error.denied", ex.getMessage()),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidJwtExceptions(
            Exception ex, WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                getMessage("error.jwt", ex.getMessage()),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MindCheckAiException.class)
    public final ResponseEntity<ExceptionResponse> handleMindCheckAiException(
            MindCheckAiException ex, WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                getMessage("error.ai", ex.getMessage()),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
    }

    private String getMessage(String key, String fallback) {
        return messageSource.getMessage(
                key,
                new Object[]{fallback},
                fallback,
                LocaleContextHolder.getLocale()
        );
    }
}
