package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exceptions.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
public class BookingServiceIntegrationTest {

    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;
    @Autowired
    BookingService bookingService;
    @Autowired
    ItemRequestService itemRequestService;


    @Test
    public void shouldFailCreateIfStartEqualsEnd() {

        // create Booker
        Long bookerId = 1L;
        UserDto bookerDto = UserDto.builder()
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        userService.create(bookerDto);

        // create input BookingDto with invalid start equals end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(start)
                .build();

        //invoke tested method to check throws
        assertThrows(DateTimeException.class,
                () -> bookingService.create(bookerId, bookingDto),
                "Указаны некорректные даты начала и/или конца бронирования");

    }

    @Test
    public void shouldFailCreateIfEndIsBeforeStart() {

        // create Booker
        Long bookerId = 1L;
        UserDto bookerDto = UserDto.builder()
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        userService.create(bookerDto);

        // create input BookingDto with invalid start equals end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime endBeforeStart = start.minusYears(1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(endBeforeStart)
                .build();

        //invoke tested method to check throws
        assertThrows(DateTimeException.class,
                () -> bookingService.create(bookerId, bookingDto),
                "Указаны некорректные даты начала и/или конца бронирования");
    }

    @Test
    public void shouldFailCreateIfItemIsNotFound() {

        // create Booker
        Long bookerId = 2L;
        UserDto bookerDto = UserDto.builder()
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        userService.create(bookerDto);

        // create Item
        Long itemId = 1L;

        // create input BookingDto with valid start and end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime endAfterStart = start.plusWeeks(1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(endAfterStart)
                .itemId(itemId)
                .build();

        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(bookerId, bookingDto),
                String.format("Вещи с id %d не существует", itemId));

    }

    @Test
    public void shouldFailCreateIfUserNotFound() {

        //  create Owner
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();
        userService.create(ownerDto);

        // create bookerId;
        Long bookerId = 2L;

        // create Item
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("bike")
                .description("new")
                .available(true)
                .build();
        itemService.create(ownerId, itemDto);

        // create input BookingDto with valid start and end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime endAfterStart = start.plusWeeks(1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(endAfterStart)
                .itemId(itemId)
                .build();

        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(bookerId, bookingDto),
                String.format("Пользователя с id %d не существует", bookerId));

    }

    @Test
    public void shouldFailCreateIfUserIsOwner() {

        //  create Owner
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();
        userService.create(ownerDto);

        // create Item
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("bike")
                .description("new")
                .available(true)
                .build();
        itemService.create(ownerId, itemDto);

        // create input BookingDto with valid start and end fields to save
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 1, 1, 1);
        LocalDateTime endAfterStart = start.plusWeeks(1);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .start(start)
                .end(endAfterStart)
                .itemId(itemId)
                .build();

        //invoke tested method to check throws
        assertThrows(AccessIsNotAllowedException.class,
                () -> bookingService.create(ownerId, bookingDto),
                "Объект не найден среди доступных для бронирования: "
                        + "владелец не может забронировать свою вещь.");
    }

    @Test
    public void shouldFailGetListByUserNotFound() {

        //  create ownerId
        Long ownerId = 1L;

        //create state
        String state = "WAITING";


        //create parameters of page
        int from = 0;
        int size = 10;
        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getListByOwner(ownerId, state, from, size),
                String.format("Пользователя с id %d не существует", ownerId));

    }

    @Test
    public void shouldFailGetListByUserWithInvalidState() {

        //  create Owner
        Long ownerId = 1L;
        UserDto ownerDto = UserDto.builder()
                .name("Alex")
                .email("Alex@yandex.ru")
                .build();
        userService.create(ownerDto);

        //create state
        String state = "INVALID";

        //create parameters of page
        int from = 0;
        int size = 10;

        //invoke tested method to check throws
        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getListByOwner(ownerId, state, from, size),
                "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    public void shouldFailGetListByBookerIfUserNotFound() {

        // create Booker
        Long bookerId = 1L;

        //create state
        String state = "WAITING";

        //create parameters of page
        int from = 0;
        int size = 10;

        //invoke tested method to check throws
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getListByBooker(bookerId, state, from, size),
                String.format("Пользователя с id %d не существует", bookerId));

    }

    @Test
    public void shouldFailGetListByBookerWhenInvalidState() {

        // create Booker
        Long bookerId = 1L;
        UserDto bookerDto = UserDto.builder()
                .name("CustomerName")
                .email("CustomerName@yandex.ru")
                .build();
        userService.create(bookerDto);

        //create state
        String state = "INVALID";

        //create parameters of page
        int from = 0;
        int size = 10;

        //invoke tested method to check throws
        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getListByBooker(bookerId, state, from, size),
                "Unknown state: UNSUPPORTED_STATUS");

    }

}