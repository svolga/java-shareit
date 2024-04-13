package ru.practicum.shareit.item;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.groups.Create;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    /*
    @PostMapping()
    @Validated({Create.class})
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("POST-request: создать Item --> {} для userId --> {}",itemDto,  userId);
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long itemId) {
        log.info("GET-request: Получить item для itemId --> {} для userId --> {}", itemId, userId);
        return itemService.getById(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        log.info("PATCH-request: Обновить item для itemId --> {}, userId --> {}, item --> {}",
                itemId, userId, itemDto);
        return itemService.update(userId, itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        log.info("DELETE-request: Удалить item для itemId --> {}", itemId);
        itemService.deleteById(itemId);
    }

    @GetMapping()
    public List<ItemResponseDto> getListByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET-request: получение списка items для userId --> {}", userId);
        return itemService.getListByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItemsBySubstring(@RequestParam("text") String text) {
        log.info("GET-request: Поиск доступных к бронированию items по фразе --> {}", text);
        return itemService.searchItemsBySubstring(text);
    }

    @PostMapping("{itemId}/comment")
    @Validated({Create.class})
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody CommentRequestDto commentRequestDto,
                                         @PathVariable long itemId) {
        log.info("POST-request Для userId --> {} и itemId --> {} добавить comment --> {}",
                userId, itemId, commentRequestDto);
        return itemService.addComment(commentRequestDto, userId, itemId);
    }
     */

    @PostMapping()
    @Validated({Create.class})
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("POST-request: создать Item --> {} для userId --> {}",itemDto,  userId);
        return itemClient.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long itemId) {
        log.info("GET-request: Получить item для itemId --> {} для userId --> {}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        log.info("PATCH-request: Обновить item для itemId --> {}, userId --> {}, item --> {}",
                itemId, userId, itemDto);
        return itemClient.update(userId, itemDto, itemId);
    }

    //
    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        log.info("DELETE-request: Удалить item для itemId --> {}", itemId);
        itemClient.deleteById(itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getListByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET-request: получение списка items для userId --> {}", userId);
//        return itemService.getListByUser(userId);
        return itemClient.getListByUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsBySubstring(@RequestParam("text") String text) {
        log.info("GET-request: Поиск доступных к бронированию items по фразе --> {}", text);
        return itemClient.searchItemsBySubstring(text);
    }

    @PostMapping("{itemId}/comment")
    @Validated({Create.class})
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody CommentRequestDto commentRequestDto,
                                         @PathVariable long itemId) {
        log.info("POST-request Для userId --> {} и itemId --> {} добавить comment --> {}",
                userId, itemId, commentRequestDto);
//        return itemService.addComment(commentRequestDto, userId, itemId);
        return itemClient.addComment(commentRequestDto, userId, itemId);
    }

}
