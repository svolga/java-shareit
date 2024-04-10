package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Component
public interface UserService {
    UserDto create(UserDto userDto);
    UserDto getById(Long userId);
    UserDto update(UserDto userDto, Long userId);
    void deleteById(Long userId);
    List<UserDto> findAll();

}
