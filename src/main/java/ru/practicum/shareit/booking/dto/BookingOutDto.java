package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.dto.AdvanceInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingOutDto {

    private long id;

    private ItemDto item;
    private UserDto booker;

    @NotNull(message = "Дата start не может быть null", groups = {AdvanceInfo.class})
    @FutureOrPresent(message = "Значение не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата end не может быть null", groups = {AdvanceInfo.class})
    @Future(message = "Значение не может быть в прошлом")
    private LocalDateTime end;

    private StatusBooking status;

    public Boolean validate() {
        return start.isBefore(end);
    }

}
