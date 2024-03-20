package ru.practicum.shareit.item.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Item с itemId: %d не найден";

    public ItemNotFoundException(long id) {
        super(String.format(MESSAGE, id));
        log.info(String.format(MESSAGE, id));
    }

    public ItemNotFoundException(String message) {
        super(message);
        log.info(message);
    }
}