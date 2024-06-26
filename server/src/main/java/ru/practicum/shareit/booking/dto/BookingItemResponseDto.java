package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class BookingItemResponseDto {
    private final Long id;
    private final Long bookerId;
}
