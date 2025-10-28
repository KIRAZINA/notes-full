package com.example.notes.web.dto;

/**
 * Generic API response wrapper.
 */
public record ApiResponse<T>(
        boolean success,
        T data,
        ApiError error
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> fail(ApiError error) {
        return new ApiResponse<>(false, null, error);
    }

    /**
     * Error payload.
     */
    public record ApiError(String code, String message) {}
}
