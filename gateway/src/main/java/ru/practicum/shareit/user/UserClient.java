package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Const;

@Service
@Slf4j
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value(Const.SERVER_URL) String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> getUserById(Long id) {
        return get("/" + id, id);
    }

    public ResponseEntity<Object> update(UserDto userDto, Long id) {
        return patch("/" + id, id, userDto);
    }

    public ResponseEntity<Object> deleteById(Long id) {
        return delete("/" + id, id);
    }

    public ResponseEntity<Object> findAll() {
        return get("");
    }
}
