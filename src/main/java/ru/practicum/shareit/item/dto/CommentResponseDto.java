package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class CommentResponseDto {
    private final Long id;
    @NotNull
    @NotBlank
    private final String text;
    private final String authorName;
    private final Long itemId;
    private final LocalDateTime created;
}
