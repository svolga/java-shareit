package ru.practicum.shareit.item.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotAvailableException extends RuntimeException {

    private static final String MESSAGE = "Item с itemId: %d не доступен";

    public ItemNotAvailableException(Long id) {
        super(String.format(MESSAGE, id));
        log.info(String.format(MESSAGE, id));
    }

    public ItemNotAvailableException(String message) {
        super(message);
        log.info(message);
    }

}
