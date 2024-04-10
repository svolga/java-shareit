package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@Component
public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);
    ItemResponseDto getById(Long userId, Long itemId);
    ItemDto update(Long userId, ItemDto itemDto, Long itemId);
    void deleteById(Long itemId);
    List<ItemResponseDto> getListByUser(Long userId);
    List<ItemResponseDto> searchItemsBySubstring(String text);
    CommentResponseDto addComment(CommentRequestDto commentRequestDto, Long userId, Long itemId);
}
