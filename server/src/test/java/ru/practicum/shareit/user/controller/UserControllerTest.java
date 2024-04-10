package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    private Long userId;
    private MediaType jsonType;
    
    @BeforeEach
    public void before() {
        userId = 1L;
        jsonType = MediaType.APPLICATION_JSON;
    }

    @Test
    @SneakyThrows
    public void create_whenUserIsValid_statusIsOk_andInvokeService() {

        //create UserDto with valid fields
        UserDto user = UserDto.builder()
                .id(userId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        //map Dto to String
        String userString = objectMapper.writeValueAsString(user);

        //mock service answer
        when(userService.create(user)).thenReturn(user);

        //perform request and check status and content
        String result = mockMvc.perform(post("/users")
                .contentType(jsonType)
                .content(userString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        //verify invokes
        verify(userService).create(user);

        //check result
        assertEquals(result, userString);
    }

    @Test
    @SneakyThrows
    public void create_whenUserHasEmptyName_statusIsBadRequest_doesNotInvokeService() {

        //create invalid field
        String emptyString = "";

        //create UserDto with invalid field
        UserDto invalidUser = UserDto.builder()
                .id(userId)
                .name(emptyString)
                .email("CustomerName@yandex.ru").build();

        //map Dto to String
        String invalidUserString = objectMapper.writeValueAsString(invalidUser);

        //mock service answer
        when(userService.create(invalidUser)).thenReturn(invalidUser);

        //perform request and check status
        mockMvc.perform(post("/users")
                .contentType(jsonType)
                .content(invalidUserString))
                .andExpect(status().isBadRequest());

        //verify invokes
        verify(userService, Mockito.never()).create(any());
    }

    @Test
    @SneakyThrows
    public void create_whenUserHasEmptyEmail_statusIsBadRequest_doesNotInvokeService() {

        //create invalid field
        String emptyString = "";

        //create UserDto with invalid field
        UserDto invalidUser = UserDto.builder()
                .id(userId)
                .name("CustomerName")
                .email(emptyString)
                .build();

        //map Dto to String
        String invalidUserString = objectMapper.writeValueAsString(invalidUser);

        //mock service answer
        when(userService.create(invalidUser)).thenReturn(invalidUser);

        //perform request and check status
        mockMvc.perform(post("/users")
                .contentType(jsonType)
                .content(invalidUserString))
                .andExpect(status().isBadRequest());

        //verify invokes
        verify(userService, Mockito.never()).create(any());
    }

    @Test
    @SneakyThrows
    public void create_whenUserHasNullName_statusIsBadRequest_doesNotInvokeService() {

        //create UserDto with null field
        UserDto invalidUser = UserDto.builder()
                .id(userId)
                .name(null)
                .email("CustomerName@yandex.ru")
                .build();
        //map Dto to String
        String invalidUserString = objectMapper.writeValueAsString(invalidUser);

        //mock service answer
        when(userService.create(invalidUser)).thenReturn(invalidUser);

        //perform request and check status
        mockMvc.perform(post("/users")
                .contentType(jsonType)
                .content(invalidUserString))
                .andExpect(status().isBadRequest());

        //verify invokes
        verify(userService, Mockito.never()).create(any());
    }

    @Test
    @SneakyThrows
    public void create_whenUserHasNullEmail_statusIsBadRequest_doesNotInvokeService() {

        //create UserDto with null field
        UserDto invalidUser = UserDto.builder()
                .id(userId)
                .name("CustomerName")
                .email(null)
                .build();

        //map Dto to String
        String invalidUserString = objectMapper.writeValueAsString(invalidUser);

        //mock service answer
        when(userService.create(invalidUser)).thenReturn(invalidUser);

        //perform request and check status
        mockMvc.perform(post("/users")
                .contentType(jsonType)
                .content(invalidUserString))
                .andExpect(status().isBadRequest());

        //verify invokes
        verify(userService, Mockito.never()).create(any());
    }

    @Test
    @SneakyThrows
    public void create_whenUserHasInvalidEmail_statusIsBadRequest_doesNotInvokeService() {

        //create invalid field
        String invalidEmail = "@ku";

        //create UserDto with invalid field
        UserDto invalidUser = UserDto.builder()
                .id(userId)
                .name("CustomerName")
                .email(invalidEmail)
                .build();

        //map Dto to String
        String invalidUserString = objectMapper.writeValueAsString(invalidUser);

        //mock service answer
        when(userService.create(invalidUser)).thenReturn(invalidUser);

        //perform request and check status
        mockMvc.perform(post("/users")
                .contentType(jsonType)
                .content(invalidUserString))
                .andExpect(status().isBadRequest());

        //verify invokes
        verify(userService, Mockito.never()).create(any());
    }

    @SneakyThrows
    @Test
    public void getById_statusIsOk_andInvokeService() {

        //create UserDto with valid fields
        UserDto validUser = UserDto.builder()
                .id(userId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        //map Dto to String
        String validUserString = objectMapper.writeValueAsString(validUser);

        //mock service answer
        when(userService.getById(userId)).thenReturn(validUser);

        //perform request and check status and content
        String result = mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(jsonType))
                .andExpect(content().json(validUserString))
                .andReturn()
                .getResponse()
                .getContentAsString();

        //verify invokes
        verify(userService).getById(userId);

        // check result
        assertEquals(result, validUserString);
    }

    @Test
    @SneakyThrows
    public void update_whenUserIsValid_statusIsOk_andInvokeService() {

        //create UserDto with valid fields
        UserDto validUser = UserDto.builder()
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();

        //map Dto to String
        String validUserString = objectMapper.writeValueAsString(validUser);

        //mock service answer
        when(userService.update(validUser, userId)).thenReturn(validUser);

        //perform request and check status and content
        String result = mockMvc.perform(patch("/users/{userId}", userId)
                .contentType(jsonType)
                .content(validUserString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(validUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(validUser.getName())))
                .andExpect(jsonPath("$.email", is(validUser.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        //verify invokes
        verify(userService).update(validUser, userId);

        //check result
        assertEquals(validUserString, result);
    }

    @Test
    @SneakyThrows
    public void update_whenUserHasInvalidEmail_statusIsBadRequest_doesNotInvokeService() {

        //create invalid field
        String invalidEmail = "@ku";

        //create UserDto with invalid field
        UserDto invalidUser = UserDto.builder()
                .id(userId)
                .name("CustomerName")
                .email(invalidEmail)
                .build();

        //map Dto to String
        String invalidUserString = objectMapper.writeValueAsString(invalidUser);

        //mock service answer
        when(userService.update(invalidUser, userId)).thenReturn(invalidUser);

        //perform request and check status
        mockMvc.perform(patch("/users/{userId}", userId)
                .contentType(jsonType)
                .content(invalidUserString))
                .andExpect(status().isBadRequest());

        //verify invokes
        verify(userService, Mockito.never()).update(any(), anyLong());
    }

    @Test
    @SneakyThrows
    public void delete_statusIsOk_invokeService() {

        //perform request and check status
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        //verify invokes
        verify(userService).deleteById(userId);
    }

    @Test
    @SneakyThrows
    public void getList_statusIsOk_invokeService() {

        //create UserDto with valid fields
        UserDto validUser1 = UserDto.builder().id(userId).name("CustomerName").email("CustomerName@yandex.ru").build();
        UserDto validUser2 = UserDto.builder().id(userId).name("Alex").email("Alex@yandex.ru").build();

        // create list of users
        List<UserDto> users = List.of(validUser1, validUser2);

        //map list to String
        String expectedUsersListAsString = objectMapper.writeValueAsString(users);

        //mock service answer
        when(userService.findAll()).thenReturn(users);

        //perform request and check status and content
        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        //verify invokes
        verify(userService).findAll();

        //check result
        assertEquals(result, expectedUsersListAsString);
    }

}
