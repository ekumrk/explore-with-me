package ru.practicum.user.model;

import org.mapstruct.Mapper;
import ru.practicum.user.model.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userDtoToUser(UserDto dto);

    UserDto userToUserDto(User user);
}
