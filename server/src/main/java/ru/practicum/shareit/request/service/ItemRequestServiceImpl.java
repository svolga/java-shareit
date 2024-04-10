package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.util.exceptions.ObjectNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestOutDto create(Long userId, ItemRequestDto itemRequestDto) {

        User requester = getUserByIdIfExists(userId);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        ItemRequest itemRequestWithId = itemRequestRepository.save(itemRequest);
        log.info("Сохранена информация о запросе: {}", itemRequestWithId);
        return ItemRequestMapper.toItemRequestOutDto(itemRequestWithId, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestOutDto> getOwnRequests(Long userId) {
        checkUserExists(userId);

//        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderById(userId);
        Map<ItemRequest, List<ItemDto>> map = getAllItemsForListRequests(itemRequests);

        List<ItemRequestOutDto> requests = itemRequests.stream()
                .map(item -> ItemRequestMapper
                        .toItemRequestOutDto(item, map.getOrDefault(item, Collections.emptyList())))
                .collect(Collectors.toList());
        logResultList(requests);
        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestOutDto> getOtherUsersRequests(Long userId, Integer from, Integer size) {
        checkUserExists(userId);
        int page = from / size;
//        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdIsNot(userId, pageRequest);

        Map<ItemRequest, List<ItemDto>> map = getAllItemsForListRequests(itemRequests);

        List<ItemRequestOutDto> requests = itemRequests.stream()
                .map(item -> ItemRequestMapper
                        .toItemRequestOutDto(item, map.getOrDefault(item, Collections.emptyList())))
                .collect(Collectors.toList());
        logResultList(requests);
        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestOutDto getRequestById(Long userId, Long requestId) {
        checkUserExists(userId);
        ItemRequest itemRequest = getItemRequestByIdIfExists(requestId);

        List<ItemDto> items = getItemsByRequestId(requestId);
        ItemRequestOutDto itemRequestOutDto = ItemRequestMapper.toItemRequestOutDto(itemRequest, items);
        log.info("Найден запрос с id {}: {}", requestId, itemRequestOutDto);
        return itemRequestOutDto;
    }

    private Map<ItemRequest, List<ItemDto>> getAllItemsForListRequests(List<ItemRequest> itemRequests) {

        List<Item> items = itemRepository.findAllByRequestIn(itemRequests);

        return items.stream()
                .collect(Collectors
                        .groupingBy(Item::getRequest, Collectors.mapping(ItemMapper::toItemDto, Collectors.toUnmodifiableList())));

    }

    private User getUserByIdIfExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("User с userId %d не найден", userId)));
    }

    private ItemRequest getItemRequestByIdIfExists(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("request с requestId %d не найден", requestId)));
    }

    private List<ItemDto> getItemsByRequestId(Long requestId) {
        List<Item> itemsByRequestId = itemRepository.findAllByRequestId(requestId);
        return itemsByRequestId.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User с userId %d не существует", userId));
        }
    }

    private void logResultList(List<ItemRequestOutDto> requests) {
        String result = requests.stream()
                .map(ItemRequestOutDto::toString)
                .collect(Collectors.joining(", "));
        log.info("Список запросов для items: {}", result);
    }

}
