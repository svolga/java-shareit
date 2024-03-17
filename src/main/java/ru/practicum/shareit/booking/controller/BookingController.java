package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto create(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody BookingDto bookingDto) {
        log.info("Создать booking --> {}", bookingDto);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto changeApprovedStatus(@RequestHeader ("X-Sharer-User-Id") long userId,
                                              @PathVariable long bookingId, @RequestParam boolean approved){
        log.info("Изменить статус booking: userId--> {}, bookingId --> {}, approved --> {} ", userId, bookingId, approved);
        return bookingService.updateApprovedStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto findBooking(@RequestHeader ("X-Sharer-User-Id") long userId, @PathVariable long bookingId){
        log.info("Найти booking: userId--> {}, bookingId --> {} ", userId, bookingId);
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> findBookingsByUser(@RequestHeader ("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state){
        log.info("Найти bookings by User: userId--> {}, state --> {} ", userId, state);
        return bookingService.findBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> findBookingsByOwner(@RequestHeader ("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "ALL") String state){
        log.info("Найти bookings by Owner: userId--> {}, state --> {} ", userId, state);
        return bookingService.findBookingsByOwner(userId, state);
    }

}
