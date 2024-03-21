package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;


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
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        log.info("Найдены users --> {}", users);
        return UserMapper.toDto(users);
    }

    @Override
    @Transactional
    public UserDto create(@Valid UserDto userDto) {

        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        log.info("Создан пользователь: {} ", savedUser);
        return UserMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        update(user, userDto);
        userRepository.save(user);
        log.info("Обновлен User с userId --> {} ", userId);
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public void remove(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.deleteById(userId);
        log.info("Удален User с userId --> {} ", userId);
    }

    private void update(User user, UserDto userDto) {
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

    }
}