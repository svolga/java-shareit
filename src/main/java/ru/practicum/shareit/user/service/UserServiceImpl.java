package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.util.Validation;
import ru.practicum.shareit.util.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.util.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User savedUser = userJpaRepository.save(user);
        log.info("Создан user --> {} ", savedUser);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = getUserByIdIfExists(userId);
        log.info("Найден user --> {} для userId --> {}", user, userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long userId) {
        User user = updateValidFields(userDto, userId);
        userJpaRepository.save(user);
        log.info("Изменен user --> {} для userId --> {}", user, user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        if (userJpaRepository.existsById(userId)) {
            log.info("Удален user для userId --> {}", userId);
            userJpaRepository.deleteById(userId);
        }
        log.info("Не найден user для userId --> {} не найден", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> users = userJpaRepository.findAll();
        List<UserDto> usersDto = UserMapper.toUserDtoList(users);
        writeToLog(usersDto);
        return usersDto;
    }

    private User updateValidFields(UserDto userDto, Long userId) {

        User user = getUserByIdIfExists(userId);
        String newEmail = userDto.getEmail();
        String newName = userDto.getName();
        if (Validation.stringIsNotNullOrBlank(newEmail) && Validation.validEmail(newEmail)) {
            checkEmailExists(newEmail, userId);
            user = user.toBuilder().email(userDto.getEmail()).build();
        }
        if (Validation.stringIsNotNullOrBlank(newName)) {
            user = user.toBuilder().name(newName).build();
        }
        return user;
    }

    private void checkEmailExists(String email, Long userId) {

        Optional<User> userWithSameEmail = userJpaRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email) && !user.getId().equals(userId))
                .findAny();

        if (userWithSameEmail.isPresent()) {
            log.info("Email {} уже есть в БД --> {}", email);
            throw new EmailAlreadyExistsException(String.format("Email %s уже зарегистрирован в базе.", email));
        }
    }

    private User getUserByIdIfExists(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Пользователя с id %d не существует", userId)));
    }

    private void writeToLog(List<UserDto> users) {
        String result = users.stream()
                .map(UserDto::toString)
                .collect(Collectors.joining(", "));
        log.info("Найден список пользователей {}", result);
    }
}