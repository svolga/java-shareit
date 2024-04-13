package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestOutDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST-request: create запроса от пользователя для userId --> {}, itemRequest --> {}", userId, itemRequestDto);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping()
    public List<ItemRequestOutDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET-request: получить инфу о своих запросах и ответах для userId --> {} ", userId);
        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping({"all"})
    public List<ItemRequestOutDto> getOtherUsersRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET-request: получить инфу для всех requests для userId --> {}, from --> {}, size --> {}",
                userId, from, size);
        return itemRequestService.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestOutDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long requestId) {
        log.info("GET-request: получить инфу для userId --> {}, request --> {}", userId, requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }

}
