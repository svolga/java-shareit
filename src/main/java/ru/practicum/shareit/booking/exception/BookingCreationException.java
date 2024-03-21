package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingCreationException extends RuntimeException {

    private static final String MESSAGE = "User с userId: %d является Owner для item c itemId: %d";

    public BookingCreationException(Long userId, Long itemId) {
        super(String.format(MESSAGE, userId, itemId));
        log.info(String.format(MESSAGE, userId, itemId));
    }
}
