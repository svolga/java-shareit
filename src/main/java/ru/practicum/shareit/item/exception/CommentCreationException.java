package ru.practicum.shareit.item.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommentCreationException extends RuntimeException {

    public CommentCreationException(String message) {
        super(message);
        log.info(message);
    }
}
