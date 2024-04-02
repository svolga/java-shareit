package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestOutDto create(Long userId, ItemRequestDto itemRequestDto);
    List<ItemRequestOutDto> getOwnRequests(Long userId);
    List<ItemRequestOutDto> getOtherUsersRequests(Long userId, Integer from, Integer size);
    ItemRequestOutDto getRequestById(Long userId, Long requestId);
}
