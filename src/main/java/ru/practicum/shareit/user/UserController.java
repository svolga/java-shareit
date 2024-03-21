package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.groups.Create;
import ru.practicum.shareit.util.groups.Update;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping()
    @Validated({Create.class})
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("POST-request создать User --> {}", userDto);
        return userService.create(userDto);
    }

    @GetMapping("{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("GET-request Получить user по userId --> {}", id);
        return userService.getById(id);
    }

    @PatchMapping("/{id}")
    @Validated({Update.class})
    public UserDto update(@Valid @RequestBody UserDto userDto,
                          @PathVariable("id") Long id) {
        log.info("PATCH-request изменить user для userId --> {}", id);
        return userService.update(userDto, id);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@Valid @PathVariable Long id) {
        log.info("DELETE-удалить user для userId --> {}", id);
        userService.deleteById(id);

    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        log.info("GET-получить All Users");
        return userService.findAll();

    }
}