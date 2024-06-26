package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.util.groups.Create;
import ru.practicum.shareit.util.groups.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class UserDto {
    private final Long id;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private final String name;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    private final String email;

}

