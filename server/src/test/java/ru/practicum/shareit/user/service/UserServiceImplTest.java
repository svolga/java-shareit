package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.util.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserJpaRepository userRepository;

    @Test
    public void create_returnSavedUser() {

        UserDto userDto = UserDto.builder()
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        User user = UserMapper.toUser(userDto);

        Long userId = 1L;
        User savedUser = user.toBuilder()
                .id(userId)
                .build();


        UserDto expectedUserDto = UserMapper.toUserDto(savedUser);

        when(userRepository.save(user)).thenReturn(savedUser);

        UserDto result = userService.create(userDto);

        verify(userRepository).save(user);

        assertEquals(result, expectedUserDto);
    }

    @Test
    public void getById_whenUserFound_thenReturnedUser() {

        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        UserDto expectedUserDto = UserMapper.toUserDto(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(userId);

        verify(userRepository).findById(userId);

        assertEquals(result, expectedUserDto);
    }

    @Test
    public void getById_whenUserNotFound_thenThrowObjectNotFound() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class,
                () -> userService.getById(userId),
                String.format("Пользователя с id %d не существует", userId));
        verify(userRepository).findById(userId);
    }

    @Test
    public void update_whenUserExists_andWhenFieldsToUpdateAreValid_ignoreIdField_returnUpdatedUser() {

        //create user
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        //mock repository answer
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //create userDto with valid fields to update
        UserDto validNew = UserDto.builder()
                .id(3L)
                .name("NewName")
                .email("New@Email")
                .build();

        //create updated User
        User updatedUser = user.toBuilder()
                .name(validNew.getName())
                .email(validNew.getEmail())
                .build();

        //create expected updated UserDto
        UserDto expectedUserDto = UserMapper.toUserDto(updatedUser);

        //invoke tested method
        UserDto result = userService.update(validNew, userId);

        //verify invokes
        verify(userRepository).save(updatedUser);

        //check result
        assertEquals(result, expectedUserDto);
        assertThat(expectedUserDto)
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("name", "NewName")
                .hasFieldOrPropertyWithValue("email", "New@Email");

    }

    @Test
    public void update_whenUserDoesNotExists_thenThrowObjectNotFound() {

        //create userId
        Long userId = 1L;

        //create userDto with valid fields to update
        UserDto validNew = UserDto.builder()
                .id(3L)
                .name("NewName")
                .email("New@Email")
                .build();

        //mock repository answer
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> userService.update(validNew, userId),
                String.format("Пользователя с id %d не существует", userId));

        //verify invokes
        verify(userRepository, only()).findById(userId);
        verifyNoMoreInteractions(userRepository);

    }

    @Test
    public void update_whenUserFound_nameToUpdateIsValid_emailIsNull_ignoreIdField_returnUpdatedUser() {

        //create user
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        //create UserDto to update valid name only
        UserDto validNew = UserDto.builder()
                .id(3L)
                .name("NewName")
                .build();

        //create updated User
        User updatedUser = user.toBuilder()
                .name(validNew.getName())
                .build();

        //create expected updated UserDto
        UserDto expectedUserDto = UserMapper.toUserDto(updatedUser);

        //mock repository answer
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //invoke tested method
        UserDto result = userService.update(validNew, userId);

        //verify invokes
        verify(userRepository).save(updatedUser);

        //check result
        assertEquals(result, expectedUserDto);
        assertThat(expectedUserDto)
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("name", "NewName")
                .hasFieldOrPropertyWithValue("email", "CustomerName@yandex.ru");

    }

    @Test
    public void update_whenUserExists_emailIsValid_nameIsNull_ignoreIdField_returnUpdatedUser() {

        //create user
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        //create UserDto to update valid email only
        UserDto validNew = UserDto.builder()
                .id(3L)
                .email("new@mail.ru")
                .build();

        //create updated User
        User updatedUser = user.toBuilder()
                .email(validNew.getEmail())
                .build();

        //create expected updated UserDto
        UserDto expectedUserDto = UserMapper.toUserDto(updatedUser);

        //mock repository answer
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //invoke tested method
        UserDto result = userService.update(validNew, userId);

        //verify invokes
        verify(userRepository).save(updatedUser);

        //check result
        assertEquals(result, expectedUserDto);
        assertThat(expectedUserDto)
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("name", "CustomerName")
                .hasFieldOrPropertyWithValue("email", "new@mail.ru");

    }

    @Test
    public void update_whenUserExists_emailToUpdateIsInvalid_ignoreIdField_returnUpdatedUser() {

        //create user
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        //create UserDto to update invalid email
        UserDto invalidNew = UserDto.builder()
                .id(3L)
                .email("NewEmail")
                .build();

        //create updated User

        //create expected updated UserDto
        UserDto expectedUserDto = UserMapper.toUserDto(user);

        //mock repository answer
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //invoke tested method
        UserDto result = userService.update(invalidNew, userId);

        //verify invokes
        verify(userRepository).save(user);

        //check result
        assertEquals(result, expectedUserDto);
        assertThat(expectedUserDto)
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("name", "CustomerName")
                .hasFieldOrPropertyWithValue("email", "CustomerName@yandex.ru");

    }

    @Test
    public void delete_whenUserExists_invokeRepository() {

        //create userId
        Long userId = 1L;

        //mock repository answer
        when(userRepository.existsById(userId)).thenReturn(true);

        //invoke tested method
        userService.deleteById(userId);

        //verify invokes
        verify(userRepository).deleteById(userId);
    }


    @Test
    public void delete_whenUserDoesNotExists_doesNotInvokeRepository() {

        //create userId
        Long userId = 1L;

        //mock repository answer
        when(userRepository.existsById(userId)).thenReturn(false);

        //invoke tested method
        userService.deleteById(userId);

        //verify invokes
        verify(userRepository, never()).deleteById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void findAll_invokeRepository_returnListOfUsers() {

        //create users
        Long user1Id = 1L;
        User user1 = User.builder()
                .id(user1Id)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        Long user2Id = 1L;
        User user2 = User.builder()
                .id(user2Id)
                .name("Name")
                .email("Email")
                .build();
        List<UserDto> users = UserMapper.toUserDtoList(List.of(user1, user2));

        //mock repository answer
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        //invoke tested method
        List<UserDto> result = userService.findAll();

        //verify invokes
        verify(userRepository).findAll();

        //check result
        assertEquals(result, users);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getName(), "CustomerName");
        assertEquals(result.get(1).getName(), "Name");
    }

}