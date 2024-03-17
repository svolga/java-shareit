package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@RestController
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Создать item --> {}", itemDto);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @Valid @RequestBody ItemDto itemDto) throws ValidateException {
        log.info("Изменить item --> {}", itemDto);
        itemDto.setId(itemId);
        return itemService.update(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Найти для пользователя --> {} item с itemId --> {}", userId, itemId);
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Найти все items для пользователя " + userId);
        return itemService.findAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(value = "text") String text) {
        log.info("Поиск Item с текстом " + text);
        return itemService.findByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @RequestBody CommentDto commentDto,
                                 @PathVariable int itemId) {
        log.info("Добавление comment для userId --> {}, itemId --> {}", userId, itemId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
