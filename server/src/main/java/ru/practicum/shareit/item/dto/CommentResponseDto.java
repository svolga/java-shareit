package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class CommentResponseDto {
    private final Long id;
    private final String text;
    private final String authorName;
    private final Long itemId;
    private final LocalDateTime created;
}
