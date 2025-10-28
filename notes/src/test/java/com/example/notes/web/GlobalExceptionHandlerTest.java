package com.example.notes.web;

import com.example.notes.common.ConflictException;
import com.example.notes.common.NotFoundException;
import com.example.notes.web.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.core.MethodParameter;
import jakarta.validation.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404() {
        ResponseEntity<ApiResponse<?>> response = handler.handleNotFound(new NotFoundException("Note not found"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().error().code()).isEqualTo("not_found");
    }

    @Test
    void handleConflict_shouldReturn409() {
        ResponseEntity<ApiResponse<?>> response = handler.handleConflict(new ConflictException("Conflict"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().error().code()).isEqualTo("conflict");
    }

    @Test
    void handleValidation_shouldReturn400() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "test");
        bindingResult.addError(new FieldError("test", "field", "must not be blank"));
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException((MethodParameter) null, bindingResult);

        ResponseEntity<ApiResponse<?>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error().code()).isEqualTo("validation_error");
        assertThat(response.getBody().error().message()).contains("must not be blank");
    }

    @Test
    void handleConstraintViolation_shouldReturn400() {
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("field");

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ApiResponse<?>> response = handler.handleConstraintViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error().code()).isEqualTo("validation_error");
        assertThat(response.getBody().error().message()).contains("must not be null");
    }

    @Test
    void handleGeneric_shouldReturn500() {
        ResponseEntity<ApiResponse<?>> response = handler.handleGeneric(new RuntimeException("Unexpected error"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().error().code()).isEqualTo("internal_error");
        assertThat(response.getBody().error().message()).contains("Unexpected error");
    }
}
