package com.example.notes.web.mapper;

import com.example.notes.user.User;
import com.example.notes.web.dto.UserResponse;
import org.mapstruct.Mapper;

/**
 * Maps User entity to UserResponse DTO.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
