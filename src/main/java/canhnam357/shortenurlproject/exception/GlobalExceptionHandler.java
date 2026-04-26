package canhnam357.shortenurlproject.exception;

import canhnam357.shortenurlproject.dto.error.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorDetails> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                status,
                message,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorDetails, status);
    }

    @ExceptionHandler(TooManyRequestException.class)
    public ResponseEntity<ErrorDetails> handleTooManyRequestsException(TooManyRequestException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }
}
