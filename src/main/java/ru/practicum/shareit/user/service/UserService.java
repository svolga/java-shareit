package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    User create(@Valid UserDto userDto);

    User update(@Valid UserDto userDto) throws ValidateException;

    User findById(long userId);

    void removeById(long userId);

    List<User> findAll();
}
