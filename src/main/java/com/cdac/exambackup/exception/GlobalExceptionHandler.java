package com.cdac.exambackup.exception;

import com.cdac.exambackup.dto.ResponseDto;
import com.fasterxml.jackson.core.JacksonException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/3/24
 */

@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpServletRequest httpServletRequest = ((ServletWebRequest) request).getRequest();
        log.error("**NoResourceFound exception occurred. Requested resource: {}", httpServletRequest.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto<>("No resource found for path: " + httpServletRequest.getRequestURL(), false));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("**HttpMessageNotReadable exception occurred. {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ResponseDto<>("Required request body is missing or invalid json data.", false));
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errorMessageList = ex.getBindingResult().getFieldErrors().stream().map(e -> "{fieldName: " + e.getField() + ", message: " + e.getDefaultMessage() + "}").toList();
        return ResponseEntity.badRequest().body(new ResponseDto<>(errorMessageList + "", false));
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ProblemDetail problemDetail = (ProblemDetail) body;
        if (problemDetail == null) {
            return ResponseEntity.internalServerError().body(new ResponseDto<>("Internal server error occurred", false));
        }
        return ResponseEntity.status(statusCode).body(new ResponseDto<>(problemDetail.getDetail(), false));
    }

    @ExceptionHandler({JDBCException.class,
            DataIntegrityViolationException.class,
            SQLIntegrityConstraintViolationException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<?> constraintViolationException(Exception ex) {
        log.error("-> Some error occured {}", ex);
        return new ResponseDto<>("Constraint violated. Please enter valid data.", false);
    }

    @ExceptionHandler(JacksonException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<?> parsingError(Exception ex) {
        log.error("-> Some error occured {}", ex);
        return new ResponseDto<>("Error parsing json data.", false);
    }

    @ExceptionHandler({EntityNotFoundException.class, UsernameNotFoundException.class, JpaObjectRetrievalFailureException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<?> invalidRequest(Exception exception) {
        log.error("Entity Not Found -->{}", exception.getMessage());
        return new ResponseDto<>(exception.getMessage(), false);
    }

    @ExceptionHandler(GenericException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<?> handleGenericException(GenericException ex) {
        log.info("**Generic exception occurred: {}", ex.getMessage());
        return new ResponseDto<>(ex.getMessage(), false);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseDto<?> handleBadCredentialsException(BadCredentialsException ex) {
        log.info("**Bad credentials exception occurred: {}", ex.getMessage());
        return new ResponseDto<>(ex.getMessage(), false);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDto<?> handleUncaughtException(Exception ex) {
        log.error("**Uncaught exception occurred: ", ex);
        return new ResponseDto<>("Internal server error occurred", false);
    }
}
