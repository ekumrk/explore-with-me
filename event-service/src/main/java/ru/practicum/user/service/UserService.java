package ru.practicum.user.service;

import ru.practicum.user.model.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addNewUser(UserDto dto);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(long id);
}
