package ru.practicum.shareit.item.mapper;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingItemResponseDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }

    public static ItemResponseDto toItemResponseDto(Item item,
                                                    BookingItemResponseDto lastBooking,
                                                    BookingItemResponseDto nextBooking,
                                                    List<CommentResponseDto> comments) {

        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .comments(comments)
                .build();
    }

    public static Item toItem(ItemDto itemDto, User user, @Nullable ItemRequest request) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .request(request)
                .build();
    }

}