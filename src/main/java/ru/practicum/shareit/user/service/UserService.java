package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto getUserById(Long id);

    List<UserDto> getUsers();

    UserDto create(UserDto user);

    UserDto updateUser(UserDto user, Long userId);

    void remove(Long userId);
}
