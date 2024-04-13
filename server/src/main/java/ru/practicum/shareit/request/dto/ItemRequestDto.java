package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder(toBuilder = true)
@Value
@RequiredArgsConstructor
public class ItemRequestDto {
    private final Long id;
    private final String description;
}
