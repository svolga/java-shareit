package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class BookingStartEndTimeException extends RuntimeException {

    private static final String MESSAGE = "Неверная очередность дат start: %s и end: %s";

    public BookingStartEndTimeException(LocalDateTime start, LocalDateTime end) {
        super(String.format(MESSAGE, start.toString(), end.toString()));
        log.info(String.format(MESSAGE, start, end));
    }
}
