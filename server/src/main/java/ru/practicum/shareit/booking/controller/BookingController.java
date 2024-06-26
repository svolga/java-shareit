package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Server POST-request: создание Booking c userId --> {}, bookingRequestDto --> {}", userId, bookingRequestDto);
        return bookingService.create(userId, bookingRequestDto);
    }

    @GetMapping("{bookingId}")
    public BookingResponseDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Get-request: получение Booking по id бронирования: {}, владелец вещи: {}", userId, bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @PatchMapping("{bookingId}")
    public BookingResponseDto updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable("bookingId") Long bookingId, @RequestParam("approved") Boolean approved) {
        log.info("PATCH-request: подтверждение бронирования для bookingId: {}, userId {}, статус: {}",
                bookingId, userId, approved);
        return bookingService.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("owner")
    public List<BookingResponseDto> getListByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestParam(
                    name = "from", defaultValue = "0") Integer from,
            @RequestParam(
                    name = "size", defaultValue = "10") Integer size
    ) {
        log.info("GET-запрос: список Bookings для userId владельца: id --> {}, статус Booking --> {}, " +
                        "from --> {}, size --> {} ",
                userId, state, from, size);
        return bookingService.getListByOwner(userId, state, from, size);
    }

    @GetMapping()
    public List<BookingResponseDto> getListByBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestParam(
                    name = "from", defaultValue = "0") Integer from,
            @RequestParam(
                    name = "size", defaultValue = "10") Integer size
    ) {
        log.info("GET-запрос: получить список Bookings для userId --> {}, статус брони --> {}, " +
                        "from --> {}, size --> {}",
                userId, state, from, size);
        return bookingService.getListByBooker(userId, state, from, size);
    }

}