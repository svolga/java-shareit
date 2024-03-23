package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingItemResponseDto;
import ru.practicum.shareit.util.groups.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class ItemResponseDto {
    private final Long id;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private final String name;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private final String description;
    @NotNull(groups = {Create.class})
    private final Boolean available;
    private final BookingItemResponseDto lastBooking;
    private final BookingItemResponseDto nextBooking;
    private final Long requestId;
    private final List<CommentResponseDto> comments;
}
