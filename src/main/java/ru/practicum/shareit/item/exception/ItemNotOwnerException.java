package ru.practicum.shareit.item.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotOwnerException extends RuntimeException {

    private static final String MESSAGE = "User с userId: %d не является Owner для itemId: %d";

    public ItemNotOwnerException(long userId, long itemId) {
        super(String.format(MESSAGE, userId, itemId));
        log.info(String.format(MESSAGE, userId, itemId));
    }
}
