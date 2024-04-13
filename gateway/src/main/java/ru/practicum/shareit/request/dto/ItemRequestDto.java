package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.util.groups.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder(toBuilder = true)
@Value
@RequiredArgsConstructor
public class ItemRequestDto {

    private final Long id;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private final String description;
}
