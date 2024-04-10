package ru.practicum.gate.booking.validation;

import ru.practicum.gate.booking.dto.BookingRequestDto;
import ru.practicum.gate.booking.state.BookingState;
import ru.practicum.gate.util.exceptions.DateTimeException;
import ru.practicum.gate.util.exceptions.UnsupportedStatusException;

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
