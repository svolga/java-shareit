package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.dto.AdvanceInfo;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private long id;

    private long itemId;
    private long bookerId;

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
