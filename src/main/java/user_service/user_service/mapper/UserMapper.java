package user_service.user_service.mapper;

import org.mapstruct.Mapper;
import user_service.user_service.dto.UserRequestDto;
import user_service.user_service.dto.UserResponseDto;
import user_service.user_service.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDto userRequestDto);

    UserResponseDto toDto(User user);
}
