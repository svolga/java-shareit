package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemBookingDto getItemById(Long id, Long userId);

    List<ItemBookingDto> getItemsByUser(Long userId);

    ItemDto update(ItemDto item, Long userId, Long itemId);

    ItemDto create(ItemDto item, Long userId);

    void remove(Long itemId, Long userId);

    List<ItemDto> findByText(String text);

    CommentDto addComment(CommentDto comment, Long userId, Long itemId);
}
