package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.dto.AdvanceInfo;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.ValidateDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private long itemId;
    private final UserRepository<User> userRepository;

    public User create(@Valid UserDto userDto) {
        ValidateDto.validate(userDto, AdvanceInfo.class);
        User user = UserMapper.toUser(userDto);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с email = " + user.getEmail() + " уже существует");
        }

        user.setId(getNewId());
        return userRepository.create(user);
    }

    public User update(long userId, @Valid UserDto userDto) throws ValidateException {
        User user = userRepository.findById(userId);

        Optional<User> userByEmail = userRepository.findByEmailAndExcludeId(userDto.getEmail(), userId);
        if (userByEmail.isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с email = " + userDto.getEmail() + " уже существует");
        }

        user = UserMapper.toUser(userDto, user);
        return userRepository.update(user);
    }

    public User findById(long userId) {
        return userRepository.findById(userId);
    }

    public void removeById(long userId) {
        userRepository.remove(userId);
    }

    public List<User> findAll() {
        return userRepository.getAll();
    }

    private long getNewId() {
        return ++itemId;
    }

}
