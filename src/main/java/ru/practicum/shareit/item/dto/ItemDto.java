package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.dto.AdvanceInfo;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private long id;
    @NotBlank(message = "Название не может быть пустым", groups = {AdvanceInfo.class})
    private String name;
    @NotBlank(message = "Описание не может быть пустым", groups = {AdvanceInfo.class})
    private String description;
    @NotNull(message = "Available не может быть пустым", groups = {AdvanceInfo.class})
    @AssertTrue(groups = {AdvanceInfo.class})
    private Boolean available;
    private Long request;
}
