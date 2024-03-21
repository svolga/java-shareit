package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .booker(UserMapper.toDto(booking.getUser()))
                .bookerId(booking.getUser().getUserId())
                .item(ItemMapper.toDto(booking.getItem()))
                .itemId(booking.getItem().getItemId())
                .itemName(booking.getItem().getName())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatusBooking())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        return Booking.builder()
                .Id(bookingDto.getId())
                .user(user)
                .item(item)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .statusBooking(bookingDto.getStatus())
                .build();
    }

    public static List<BookingDto> toDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
