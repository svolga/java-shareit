package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.Valid;
import java.util.List;

public interface BookingService {

    BookingOutDto create(long userId, @Valid BookingDto bookingDto);

    BookingOutDto updateApprovedStatus(long userId, long id, boolean approved);

    BookingOutDto findBooking(long userId, long bookingId);

    Booking findById(long bookingId);

    List<BookingOutDto> findBookingsByOwner(long userId, String state);

    List<BookingOutDto> findBookingsByUser(long userId, String state);

}
