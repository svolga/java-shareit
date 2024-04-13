package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.groups.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping()
    @Validated({Create.class})
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST-request: create запроса от пользователя для userId --> {}, itemRequest --> {}", userId, itemRequestDto);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET-request: получить инфу о своих запросах и ответах для userId --> {} ", userId);
        return itemRequestClient.getOwnRequests(userId);
    }

    @GetMapping({"all"})
    public ResponseEntity<Object> getOtherUsersRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET-request: получить инфу для всех requests для userId --> {}, from --> {}, size --> {}",
                userId, from, size);
        return itemRequestClient.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long requestId) {
        log.info("GET-request: получить инфу для userId --> {}, request --> {}", userId, requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}