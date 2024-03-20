package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Не найден Booking с bookingId: %d";

    public BookingNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
        log.info(String.format(MESSAGE, id));
    }

    public BookingNotFoundException(String message) {
        super(message);
        log.info(message);
    }
}
