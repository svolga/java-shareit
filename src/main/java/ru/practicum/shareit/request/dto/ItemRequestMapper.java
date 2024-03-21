package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getItemRequestId())
                .itemName(itemRequest.getItemName())
                .authorId(itemRequest.getAuthor().getUserId())
                .createdAt(itemRequest.getCreatedAt())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User author) {
        return ItemRequest.builder()
                .itemRequestId(itemRequestDto.getId())
                .itemName(itemRequestDto.getItemName())
                .author(author)
                .createdAt(itemRequestDto.getCreatedAt())
                .build();
    }

    public static List<ItemRequestDto> toDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toUnmodifiableList());
    }
}
