package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.booking.state.BookingStatus;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class BookingResponseDto {
    private final Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime end;
    private final BookingStatus status;
//    private final User booker;
//    private final Item item;
}
