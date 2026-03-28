package com.fer.student_analytics.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j; // lombok za log.error

@Slf4j // lombok za log.error i log.warn

@RestControllerAdvice // govori springu da sluša sve controllere i hvata exceptione (hvata sve exceptione, jer čak ako se dogodi npr. u serviceu, i dalje putuje preko controllera)

public class GlobalExceptionHandler {

    // hvata ResourceNotFoundException, kada nešto nije pronađeno u bazi
    @ExceptionHandler(ResourceNotFoundException.class) // kaže Springu da ako se dogodi ResourceNotFoundException, pozovi ovu metodu
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) { // metoda prima exception (ex) i vraća HTTP odgovor (ResponseEntity), tijelo odgovora je JSON (ErrorResponse)
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            404,
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 404, NOT_FOUND je enum iz springa za 404
    }

    // hvata IllegalArgumentException, kada su podaci neispravni
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            400,
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error); // 400, BAD_REQUEST je enum iz springa za 400
    }

    // hvata sve ostale exceptione koje nismo predvidjeli
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Neočekivana greška", ex);
        ErrorResponse error = new ErrorResponse(
            500,
            "Interna greška servera.",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error); // 500, INTERNAL_SERVER_ERROR je enum iz springa za 500
    }
}