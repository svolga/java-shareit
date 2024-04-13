package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.groups.Create;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping()
    @Validated({Create.class})
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("POST-client request создать User --> {}", userDto);
        return userClient.create(userDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("GET-request Получить user по userId --> {}", id);
        return userClient.getUserById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto,
                                         @PathVariable("id") Long id) {
        log.info("PATCH-request изменить user для userId --> {}", id);
        return userClient.update(userDto, id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("DELETE-удалить user для userId --> {}", id);
        return userClient.deleteById(id);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET-получить All Users");
        return userClient.findAll();
    }
}