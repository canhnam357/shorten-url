package canhnam357.shortenurlproject.exception;

import canhnam357.shortenurlproject.dto.error.ErrorDetails;
import canhnam357.shortenurlproject.dto.error.FieldErrorDetail;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private ResponseEntity<ErrorDetails> buildErrorResponse(HttpStatus status,
                                                            String message,
                                                            WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                status,
                message,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorDetails, status);
    }

    /*
    * BEGIN ExceptionHandler for Validation
    * */

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            WebRequest request) {

        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new FieldErrorDetail(err.getField(), err.getDefaultMessage(), err.getCode()))
                .collect(Collectors.toList());
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields.",
                request.getDescription(false).replace("uri=", ""),
                fieldErrors
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /*
    * END ExceptionHandler for Validation
    * */



    /*
     * BEGIN ExceptionHandler for Authentication
     * */

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorDetails> handleDisabledException(DisabledException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorDetails> handleLockedException(LockedException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(TooManyRequestException.class)
    public ResponseEntity<ErrorDetails> handleTooManyRequestsException(TooManyRequestException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request);
    }

    /*
    * END ExceptionHandler for Authentication
    * */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }
}
