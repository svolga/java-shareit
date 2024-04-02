package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.util.groups.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class CommentRequestDto {
    private final Long id;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private final String text;
    private final String authorName;
    private final Long itemId;

}
