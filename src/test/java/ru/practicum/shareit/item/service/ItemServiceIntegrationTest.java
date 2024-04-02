package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exceptions.AccessIsNotAllowedException;
import ru.practicum.shareit.util.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.util.exceptions.UnavailableItemException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

//import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;
    @Autowired
    BookingService bookingService;
    @Autowired
    ItemRequestService itemRequestService;

    @Test
    public void shouldCreateItemWithoutRequest() {
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        UserDto savedOwnerDto = userService.create(ownerDto);
        User owner = UserMapper.toUser(savedOwnerDto);

        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("bike")
                .description("new")
                .available(true)
                .build();

        Item item = ItemMapper.toItem(itemDto, owner, null);
        ItemDto itemDtoWithoutRequest = ItemMapper.toItemDto(item);
        ItemDto expectedItemDto = itemDtoWithoutRequest.toBuilder().id(itemId).build();

        ItemDto result = itemService.create(ownerId, itemDtoWithoutRequest);

        assertEquals(result, expectedItemDto);
        assertEquals(result.getAvailable(), true);
        assertEquals(result.getName(), "bike");
        assertEquals(result.getDescription(), "new");
        assertNull(result.getRequestId());
    }

    @Test
    public void shouldFailCreateIfUserNotFound() {

        Long ownerId = 1L;

        ItemDto itemDto = ItemDto.builder()
                .name("bike")
                .description("new")
                .available(true)
                .build();

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.create(ownerId, itemDto),
                String.format("Пользователя с id %d не существует", ownerId));

    }

    @Test
    public void shouldFailCreateIfRequestIsNotFoundByRequestId() {

        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        userService.create(ownerDto);

        Long requestId = 1L;

        ItemDto itemDto = ItemDto.builder()
                .name("bike")
                .description("new")
                .requestId(requestId)
                .available(true)
                .build();

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.create(ownerId, itemDto),
                String.format("Запроса id %d не существует", requestId));
    }

    @Test
    public void shouldThrowExceptionIfItemNotFoundById() {

        Long ownerId = 1L;
        Long itemId = 1L;

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.getById(ownerId, itemId),
                String.format("Запроса с id %d не существует", itemId));
    }


    @Test
    public void shouldFailUpdateIfUserDoesNotExist() {

        Long ownerId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("bike")
                .description("new")
                .available(true)
                .build();

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(ownerId, itemDto, itemId),
                String.format("Пользователя с id %d не существует", ownerId));
    }

    @Test
    public void shouldFailUpdateIfItemDoesNotExist() {
        Long ownerId = 1L;
        UserDto owner = UserDto.builder()
                .id(ownerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        userService.create(owner);

        Long itemId = 1L;

        ItemDto itemDto = ItemDto.builder()
                .name("bike")
                .description("new")
                .available(true)
                .build();

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(ownerId, itemDto, itemId),
                String.format("Вещи id %d не существует", itemId));
    }

    @Test
    public void shouldFailUpdateIfUserIsNotOwner() {
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .id(ownerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        UserDto savedOwnerDto = userService.create(ownerDto);
        User owner = UserMapper.toUser(savedOwnerDto);

        Long notOwnerId = 2L;
        UserDto notOwnerDto = UserDto.builder()
                .id(notOwnerId)
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();
        userService.create(notOwnerDto);

        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("bike")
                .description("new")
                .available(true)
                .build();
        ItemDto savedItemDto = itemService.create(ownerId, itemDto);
        Item item = ItemMapper.toItem(savedItemDto, owner, null);

        assertThrows(AccessIsNotAllowedException.class,
                () -> itemService.update(notOwnerId, itemDto, itemId),
                String.format("Операция доступна только владельцу вещи %s :", item));

    }

    @Test
    public void shouldDeleteItemById() {
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .id(ownerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        userService.create(ownerDto);

        Long itemId = 1L;

        itemService.deleteById(itemId);

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.getById(ownerId, itemId),
                String.format("Вещи id %d не существует", itemId));

    }

    @Test
    public void shouldDoesNotThrowExceptionIfItemIsNotFoundToDelete() {
        Long itemId = 1L;
        assertDoesNotThrow(() -> itemService.deleteById(itemId));
    }

    @Test
    public void shouldGetEmptyListOfItems() {
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .id(ownerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        userService.create(ownerDto);

        List<ItemResponseDto> result = itemService.getListByUser(ownerId);

        assertEquals(result, Collections.emptyList());
        assertEquals(result.size(), 0);
    }

    @Test
    public void shouldFailAddCommentWhenItemDoesNotExist() {
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .id(ownerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        userService.create(ownerDto);

        Long itemId = 1L;

        CommentRequestDto commentDto = CommentRequestDto.builder()
                .itemId(itemId)
                .text("bad bike!")
                .build();

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.addComment(commentDto, ownerId, itemId),
                String.format("Вещи с id %d не существует", itemId));
    }

    @Test
    public void shouldFailAddCommentWhenUserDoesNotExist() {
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .id(ownerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        userService.create(ownerDto);

        Long itemId = 1L;
        ItemDto item1Dto = ItemDto.builder()
                .name("bike")
                .description("new")
                .available(true)
                .build();
        itemService.create(ownerId, item1Dto);

        CommentRequestDto commentDto = CommentRequestDto.builder()
                .itemId(itemId)
                .text("bad bike!")
                .build();

        Long nonExistedId = -1L;

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.addComment(commentDto, nonExistedId, itemId),
                String.format("Пользователя с id %d не существует", ownerId));

    }

    @Test
    public void shouldFailAddCommentWhenUserIsOwner() {
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .id(ownerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        UserDto savedOwnerDto = userService.create(ownerDto);
        User owner = UserMapper.toUser(savedOwnerDto);

        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("bike")
                .description("new")
                .available(true)
                .build();
        itemService.create(ownerId, itemDto);

        CommentRequestDto commentDto = CommentRequestDto.builder()
                .itemId(itemId)
                .authorName(owner.getName())
                .text("bad bike!")
                .build();

        assertThrows(AccessIsNotAllowedException.class,
                () -> itemService.addComment(commentDto, ownerId, itemId),
                "Объект не найден среди доступных для бронирования:"
                        + " владелец не может забронировать свою вещь.");
    }

    @Test
    public void shouldFailAddCommentWhenUserHasNotCurrentOrPastBookings() {
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .id(ownerId)
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        UserDto savedOwnerDto = userService.create(ownerDto);
        User owner = UserMapper.toUser(savedOwnerDto);

        Long itemId = 1L;
        ItemDto item1Dto = ItemDto.builder()
                .name("bike")
                .description("new")
                .available(true)
                .build();
        itemService.create(ownerId, item1Dto);

        CommentRequestDto commentDto = CommentRequestDto.builder()
                .itemId(itemId)
                .authorName(owner.getName())
                .text("bad bike!")
                .build();

        Long notOwnerId = 2L;
        UserDto notOwnerDto = UserDto.builder()
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();
        userService.create(notOwnerDto);

        assertThrows(UnavailableItemException.class,
                () -> itemService.addComment(commentDto, notOwnerId, itemId),
                "Вы не вправе оставлять отзывы, "
                        + " поскольку не пользовались данной вещью.");

    }

}
