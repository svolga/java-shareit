package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Value
@RequiredArgsConstructor
public class ItemRequestOutDto {

    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private final List<ItemDto> items;
}
