package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.util.groups.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class BookingRequestDto {
    private final Long id;
    @FutureOrPresent(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private final LocalDateTime start;
    @Future(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private final LocalDateTime end;
    private final Long itemId;

}
