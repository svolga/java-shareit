package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.dto.AdvanceInfo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private long id;
    private String name;

    @Email(message = "Электронная почта должна содержать символ @", groups = {AdvanceInfo.class})
    @NotBlank(message = "Электронная почта не может быть пустой", groups = {AdvanceInfo.class})
    private String email;
}
