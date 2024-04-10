package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingItemResponseDto;

import java.util.List;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class ItemResponseDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final BookingItemResponseDto lastBooking;
    private final BookingItemResponseDto nextBooking;
    private final Long requestId;
    private final List<CommentResponseDto> comments;
}
