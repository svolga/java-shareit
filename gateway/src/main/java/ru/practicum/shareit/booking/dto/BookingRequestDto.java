package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class BookingRequestDto {
    private final Long id;
    @FutureOrPresent
    @NotNull
    private final LocalDateTime start;
    @Future
    @NotNull
    private final LocalDateTime end;
    private final Long itemId;

}
