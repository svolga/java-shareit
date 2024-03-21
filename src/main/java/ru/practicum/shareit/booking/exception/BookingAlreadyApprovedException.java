package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingAlreadyApprovedException extends RuntimeException {

    private static final String MESSAGE = "Booking ранее уже был присвоен статус Approved, bookingId -->  %d";

    public BookingAlreadyApprovedException(Long id) {
        super(String.format(MESSAGE, id));
        log.info(String.format(MESSAGE, id));
    }
}
