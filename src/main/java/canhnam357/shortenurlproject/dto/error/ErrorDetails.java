package canhnam357.shortenurlproject.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public record ErrorDetails(
        Instant timestamp,     // Current time
        int status,            // HTTP status code
        String error,          // A concise, human-readable error type (e.g., "Bad Request", "Not Found")
        String message,        // Detailed explanation of the error
        String path,           // The request URI
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<FieldErrorDetail> errors // List of validation errors
) {
    // Convenience constructor
    public ErrorDetails(HttpStatus httpStatus, String message, String path) {
        this(Instant.now(), httpStatus.value(), httpStatus.getReasonPhrase(), message, path, null);
    }
    // Convenience constructor for validation errors
    public ErrorDetails(HttpStatus httpStatus, String message, String path, List<FieldErrorDetail> errors) {
        this(Instant.now(), httpStatus.value(), httpStatus.getReasonPhrase(), message, path, errors);
    }
}