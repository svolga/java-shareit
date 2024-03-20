package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestDto itemRequest,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создать itemRequest --> {} для userId --> {}", itemRequest, userId);
        return itemRequestService.createItemRequest(itemRequest, userId);
    }

    @PatchMapping("/{id}")
    public ItemRequestDto updateItemRequest(@RequestBody ItemRequestDto itemRequest,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long id) {
        log.info("Изменить itemRequest --> {} c itemRequestId --> {}, для userId --> {}", itemRequest, id, userId);
        return itemRequestService.updateItemRequest(itemRequest, userId, id);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests() {
        log.info("Найти itemRequests");
        return itemRequestService.getItemRequests();
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequest(@PathVariable Long id) {
        log.info("Найти itemRequest с id --> {}", id);
        return itemRequestService.getItemRequestById(id);
    }

    @DeleteMapping("/id")
    public ItemRequestDto deleteItemRequest(@PathVariable("id") Long id,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Удалить itemRequest с id --> {} для userId --> {}", id, userId);
        return itemRequestService.deleteItemRequest(id, userId);
    }
}