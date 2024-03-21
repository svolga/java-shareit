package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getItemId())
                .name(item.getName())
                .user(UserMapper.toDto(item.getUser()))
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto item, User owner) {
        return Item.builder()
                .itemId(item.getId())
                .name(item.getName())
                .user(owner)
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemBookingDto toDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
        return ItemBookingDto.builder()
                .id(item.getItemId())
                .name(item.getName())
                .user(UserMapper.toDto(item.getUser()))
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static List<ItemDto> toDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }
}