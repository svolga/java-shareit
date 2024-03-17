package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    public static BookingOutDto toBookingOutDto(Booking booking) {

        return BookingOutDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .build();
    }


    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
//                .item(booking.getItem())
//                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
//                .item((bookingDto.getItemId()))
//                .description(itemDto.getDescription())
//                .isAvailable(itemDto.getAvailable() != null && itemDto.getAvailable())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto, Booking booking) {
        return Booking.builder()
                .id(booking.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
/*
                .name(itemDto.getName() == null ? item.getName() : itemDto.getName())
                .description(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription())
                .isAvailable(itemDto.getAvailable() == null ? item.isAvailable() : itemDto.getAvailable())
                .owner(item.getOwner())
*/
                .build();
    }

/*
   public static ItemDto toItemDto(Item item) {

            return ItemDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.isAvailable())
                    .request(item.getRequest() != null ? item.getRequest().getId() : null)
                    .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable() != null && itemDto.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(item.getId())
                .name(itemDto.getName() == null ? item.getName() : itemDto.getName())
                .description(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription())
                .isAvailable(itemDto.getAvailable() == null ? item.isAvailable() : itemDto.getAvailable())
                .owner(item.getOwner())
                .build();
    }
 */


}
