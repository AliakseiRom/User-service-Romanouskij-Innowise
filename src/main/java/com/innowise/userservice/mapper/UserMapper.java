package com.innowise.userservice.mapper;

import com.innowise.userservice.model.User;
import org.mapstruct.Mapper;
import com.innowise.userservice.dto.UserRequestDto;
import com.innowise.userservice.dto.UserResponseDto;


@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDto userRequestDto);

    UserResponseDto toDto(User user);
}
