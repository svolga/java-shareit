package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.util.exceptions.DateTimeException;
import ru.practicum.shareit.util.exceptions.UnsupportedStatusException;

import java.time.LocalDateTime;

public class Validation {

    public static void checkDates(BookingRequestDto bookingDto) {
        checkEmptyStart(bookingDto);
        checkStartAndEndEnd(bookingDto);
    }

    private static void checkStartAndEndEnd(BookingRequestDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        if (start.equals(end) || start.isAfter(end)) {
            throw new DateTimeException("Неверные даты начала и/или конца бронирования");
        }
    }

    private static void checkEmptyStart(BookingRequestDto bookingDto) {
        if (bookingDto.getStart() == null) {
            throw new DateTimeException("Не заполнена дата начала бронирования");
        }
    }

    public static void checkBookingState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
