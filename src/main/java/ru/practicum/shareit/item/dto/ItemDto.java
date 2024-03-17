package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.dto.AdvanceInfo;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private long id;
    @NotBlank(message = "Название не может быть пустым", groups = {AdvanceInfo.class})
    private String name;
    @NotBlank(message = "Description не может быть пустым", groups = {AdvanceInfo.class})
    private String description;
    @NotNull(message = "Available не может быть пустым", groups = {AdvanceInfo.class})
    @AssertTrue(groups = {AdvanceInfo.class})
    private Boolean available;

    private User owner;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
