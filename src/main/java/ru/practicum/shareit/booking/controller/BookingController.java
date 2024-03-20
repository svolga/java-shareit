package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final String STATUS_ALL = "ALL";

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создать booking --> {}", bookingDto);
        return bookingService.create(bookingDto, userId);
    }

    @DeleteMapping("/{bookingId}")
    public BookingDto remove(@PathVariable Long bookingId,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Удалить booking --> {}", bookingId);
        return bookingService.remove(bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        log.info("Найти booking: userId--> {}, bookingId --> {} ", userId, bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeApprovedStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId,
                                 @RequestParam String approved) {
        log.info("Изменить статус booking: userId--> {}, bookingId --> {}, approved --> {} ", userId, bookingId, approved);
        return bookingService.updateApprovedStatus(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingDto> getBookingByCurrentUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(required = false, defaultValue = STATUS_ALL) String state) {
        log.info("Найти bookings by Current User: userId--> {}, state --> {} ", userId, state);
        return bookingService.getBookingByCurrentUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(required = false, defaultValue = STATUS_ALL) String state) {
        log.info("Найти bookings by Owner: userId--> {}, state --> {} ", userId, state);
        return bookingService.getBookingByOwner(userId, state);
    }
}
