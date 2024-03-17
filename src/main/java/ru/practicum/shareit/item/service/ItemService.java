package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {
    ItemDto create(long userId, @Valid ItemDto itemDto);

    Item findById(long itemId);

    ItemDto findItemById(long userId, long itemId);

    ItemDto update(long userId, @Valid ItemDto itemDto) throws ValidateException;

    void removeById(long itemId);

    List<ItemDto> findAll(long userId);

    List<ItemDto> findByText(long userId, String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
