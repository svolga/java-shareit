package ru.practicum.shareit.booking.exception;

public class OwnerNotFoundException extends RuntimeException{
    public OwnerNotFoundException(String message) {
        super(message);
    }
}
