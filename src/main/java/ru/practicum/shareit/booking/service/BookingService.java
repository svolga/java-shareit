package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingDto booking, Long userId);

    BookingDto remove(Long bookingId, Long userId);

    BookingDto getBookingById(Long userId, Long bookingId);

    BookingDto updateApprovedStatus(Long userId, Long bookingId, String approved);

    List<BookingDto> getBookingByCurrentUser(Long userId, String state);

    List<BookingDto> getBookingByOwner(Long userId, String state);
}
