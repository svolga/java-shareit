package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingItemResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }

    public static BookingItemResponseDto toBookingItemResponseDto(Booking booking) {
        return BookingItemResponseDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto,
                                    User user, Item item, BookingStatus status) {
        return Booking.builder()
                .id(bookingRequestDto.getId())
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .booker(user)
                .item(item)
                .status(status)
                .build();
    }

    public static List<BookingResponseDto> toBookingResponseDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public static BookingItemResponseDto toBookingItemResponseDto(BookingResponseDto bookingResponseDto) {
        return BookingItemResponseDto.builder()
                .id(bookingResponseDto.getId())
                .bookerId(bookingResponseDto.getBooker().getId())
                .build();
    }

}

