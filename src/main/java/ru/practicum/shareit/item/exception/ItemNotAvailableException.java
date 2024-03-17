package ru.practicum.shareit.item.exception;

public class ItemNotAvailableException extends RuntimeException{
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
