package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) throws ValidateException {
        log.info("Создать пользователя --> {}", userDto);
        return UserMapper.toUserDto(userService.create(userDto));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @Valid @RequestBody UserDto userDto) throws ValidateException {
        log.info("Изменить пользователя с id --> {}", userId);
        return UserMapper.toUserDto(userService.update(userId, userDto));
    }

    @GetMapping("/{userId}")
    public UserDto findUser(@PathVariable long userId) throws Throwable {
        log.info("Найти пользователя с id --> {}", userId);
        return UserMapper.toUserDto(userService.findById(userId));
    }

    @DeleteMapping("/{userId}")
    public void removeUserById(@PathVariable long userId) throws Throwable {
        log.info("Удалить пользователя с id --> {}", userId);
        userService.removeById(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Найти всех пользователей");
        return userService.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toUnmodifiableList());
    }
}
