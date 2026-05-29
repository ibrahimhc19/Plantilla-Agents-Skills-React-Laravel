package com.example.demo.users.mapper;

import com.example.demo.users.dto.UserResponse;
import com.example.demo.users.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserResponse toResponse(User user);
}
