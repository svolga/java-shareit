package ru.practicum.shareit.item.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создать item --> {}", itemDto);
        return itemService.create(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getItemById(@PathVariable("itemId") Long itemId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Найти для пользователя --> {} item с itemId --> {}", userId, itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemBookingDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Найти все items для пользователя " + userId);
        return itemService.getItemsByUser(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable("itemId") Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Изменить item --> {}", itemDto);
        return itemService.update(itemDto, userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void remove(@PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Удалить item --> {}", itemId);
        itemService.remove(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam("text") String text,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поиск Item с текстом " + text);
        return itemService.findByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("itemId") Long itemId) {
        log.info("Добавление comment для userId --> {}, itemId --> {}", userId, itemId);
        return itemService.addComment(commentDto, userId, itemId);
    }
}
