package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.user.dto.UserDtoMapper.toDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        log.info("Найден User --> {}", user);
        return UserDtoMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        log.info("Найдены users --> {}", users);
        return toDto(users);
    }

    @Override
    @Transactional
    public UserDto create(@Valid UserDto userDto) {

        User user = UserDtoMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        log.info("Создан пользователь: {} ", savedUser);
        return UserDtoMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        update(user, userDto);
        userRepository.save(user);
        log.info("Обновлен User с userId --> {} ", userId);
        return toDto(user);
    }

    @Override
    @Transactional
    public void remove(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.deleteById(userId);
        log.info("Удален User с userId --> {} ", userId);
    }

    private User update(User user, UserDto userDto) {
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return user;
    }
}