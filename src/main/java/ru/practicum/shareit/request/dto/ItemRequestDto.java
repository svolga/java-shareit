package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder(toBuilder = true)
@Value
@RequiredArgsConstructor
public class ItemRequestDto {

    private final Long id;
    @NotNull
    @NotBlank
    private final String description;
}
