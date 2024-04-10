package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class CommentRequestDto {
    private final Long id;
    private final String text;
    private final String authorName;
    private final Long itemId;

}
