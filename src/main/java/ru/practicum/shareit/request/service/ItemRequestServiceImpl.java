package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.exception.ItemNotOwnerException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.request.dto.ItemRequestMapper.toDto;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        ItemRequest itemRequest = itemRequestRepository.save(toItemRequest(itemRequestDto, user));
        log.info("Создан Item с itemId --> {}", itemRequest.getItemRequestId());
        return toDto(itemRequest);
    }

    @Override
    @Transactional
    public ItemRequestDto updateItemRequest(ItemRequestDto itemRequestDto, Long userId, Long itemRequestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(itemRequestId));

        if (!itemRequest.getAuthor().getUserId().equals(user.getUserId())) {
            throw new ItemNotOwnerException(user.getUserId(), itemRequestId);
        }

        itemRequestDto.setId(itemRequestId);
        ItemRequest updatedItemRequest = itemRequestRepository.save(toItemRequest(itemRequestDto, user));
        log.info("Обновлен Item с itemId --> {}", itemRequestId);

        return toDto(updatedItemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequests() {
        return toDto(itemRequestRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new ItemRequestNotFoundException(id));

        log.info("Найден Item --> {} с itemId --> {}", itemRequest, id);
        return toDto(itemRequest);
    }

    @Override
    @Transactional
    public ItemRequestDto deleteItemRequest(Long id, Long userId) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new ItemRequestNotFoundException(id));

        log.info("Удален itemRequest c id -->{}", id);
        return toDto(itemRequest);
    }
}