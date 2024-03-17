package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User create(@Valid UserDto userDto) {
        ValidateDto.validate(userDto, AdvanceInfo.class);
        User user = UserMapper.toUser(userDto);
        try {
            return userRepository.save(user);
        }
        catch (DataAccessException ex){
            log.info("Exception при создании USer --> {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public User update(@Valid UserDto userDto) throws ValidateException {
        User user = findById(userDto.getId());

        Optional<User> userByEmail = userRepository.findByEmailAndIdNot(userDto.getEmail(), userDto.getId());
        if (userByEmail.isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с email = " + userDto.getEmail() + " уже существует");
        }

        user = UserMapper.toUser(userDto, user);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User findById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Не найден пользователь с id = " + userId));
    }

    @Override
    public void removeById(long userId) {
        findById(userId);
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
