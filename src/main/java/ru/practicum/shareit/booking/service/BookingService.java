package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto getById(Long userId, Long bookingId);

    BookingResponseDto updateStatus(Long bookingId, Long userId, Boolean isApproved);

    List<BookingResponseDto> getListByOwner(Long ownerId, String bookingState);

    List<BookingResponseDto> getListByBooker(Long bookerId, String bookingState);
}
