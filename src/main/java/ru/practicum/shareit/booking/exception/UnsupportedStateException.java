package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnsupportedStateException extends RuntimeException{
    private static final String MESSAGE = "Unknown state: %s";

    public UnsupportedStateException(String state) {
        super(String.format(MESSAGE, state));
        log.info(String.format(MESSAGE, state));
    }
}
