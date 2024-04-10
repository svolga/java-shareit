package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingItemResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.util.Validation;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.util.exceptions.AccessIsNotAllowedException;
import ru.practicum.shareit.util.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.util.exceptions.UnavailableItemException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final UserJpaRepository userJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = getUserByIdIfExists(userId);
        ItemRequest itemRequest = getItemRequestIfExists(itemDto);
        Item item = ItemMapper.toItem(itemDto, owner, itemRequest);
        Item savedItem = itemJpaRepository.save(item);
        log.info("Создан Item: {}", savedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getById(Long userId, Long itemId) {

        Item item = getItemByIdIfExists(itemId);

        LocalDateTime now = LocalDateTime.now();
        BookingItemResponseDto lastBooking = null;
        BookingItemResponseDto nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = getLastBooking(itemId, now);
            nextBooking = getNextBooking(itemId, now);
        }

        List<CommentResponseDto> commentsDto = getCommentsByItemId(itemId);
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, commentsDto);
        log.info("Получить itemId --> {} для userId --> {}, itemResponseDto --> {}", itemId, userId, itemResponseDto);
        return itemResponseDto;
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        checkUserExists(userId);
        Item item = getItemByIdIfExists(itemId);
        checkAccessAllowedOnlyForOwner(item, userId);

        Item updatedItem = updateValidFields(item, itemDto);

        itemJpaRepository.save(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteById(Long itemId) {
        if (itemJpaRepository.existsById(itemId)) {
            log.info("Удалена item для itemId --> {}", itemId);
            itemJpaRepository.deleteById(itemId);
        }
        log.info("Попытка удаления Item, но не найден itemId --> {}", itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getListByUser(Long userId) {

//        List<Item> items = itemJpaRepository.findAllByOwnerId(userId);
        List<Item> items = itemJpaRepository.findAllByOwnerIdOrderById(userId);
        Map<Item, List<Comment>> mapComments = getCommentsToAllItems(items);
        List<ItemResponseDto> itemsResponses = items.stream()
                .map(item -> getItemResponseDto(item,
                        mapComments.getOrDefault(item, Collections.emptyList()),
                        LocalDateTime.now()))
                .collect(Collectors.toUnmodifiableList());
        writeToLog(itemsResponses);
        return itemsResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> searchItemsBySubstring(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemJpaRepository.searchItemsBySubstring(text);
        Map<Item, List<Comment>> mapComments = getCommentsToAllItems(items);
        List<ItemResponseDto> itemsResponses = items.stream()
                .map(item -> getItemResponseDto(item,
                        mapComments.getOrDefault(item, Collections.emptyList()),
                        LocalDateTime.now()))
                .collect(Collectors.toUnmodifiableList());

        writeToLog(itemsResponses);
        return itemsResponses;
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto, Long userId, Long itemId) {

        Item item = getItemByIdIfExists(itemId);
        User user = getUserByIdIfExists(userId);
        checkAccessForOwnerNotAllowed(item, userId);
        checkAccessToCommentAllowed(userId, itemId);

        Comment comment = CommentMapper.toComment(commentRequestDto, user, item);
        comment = commentJpaRepository.save(comment);
        log.info("Для вещи c id {} пользователь id {} добавил новый отзыв: {}", itemId, userId, comment);
        return CommentMapper.toCommentResponseDto(comment);
    }

    private Item updateValidFields(Item item, ItemDto itemDto) {

        String newName = itemDto.getName();
        String newDescription = itemDto.getDescription();
        Boolean newStatus = itemDto.getAvailable();

        if (Validation.stringIsNotNullOrBlank(newName)) {
            item = item.toBuilder().name(newName).build();
        }
        if (Validation.stringIsNotNullOrBlank(newDescription)) {
            item = item.toBuilder().description(newDescription).build();
        }
        if (Validation.objectIsNotNull(newStatus)) {
            item = item.toBuilder().available(newStatus).build();
        }
        return item;
    }

    private User getUserByIdIfExists(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Не найден User с userId %d", userId)));
    }

    private Item getItemByIdIfExists(Long itemId) {
        return itemJpaRepository.findById(itemId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Не найден item для itemId: %d", itemId)));
    }

    private void checkUserExists(Long userId) {
        if (!userJpaRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("Не найден user для userId: %d", userId));
        }
    }

    private void checkAccessToCommentAllowed(Long userId, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingJpaRepository
                .findAllByItem_IdAndBooker_IdAndStatusAndStartIsBefore(itemId, userId, BookingStatus.APPROVED, now);
        if (bookings.isEmpty()) {
            throw new UnavailableItemException("Нет доступа к comments");
        }
    }

    private void checkAccessForOwnerNotAllowed(Item item, Long userId) {
        if (isOwner(item, userId)) {
            throw new AccessIsNotAllowedException(
                    "Для User с userId " + userId + "не может забронировать item " + item);
        }
    }

    private void checkAccessAllowedOnlyForOwner(Item item, Long userId) {

        if (!isOwner(item, userId)) {
            throw new AccessIsNotAllowedException(
                    (String.format("Доступ только для владельца item %s :", item)));
        }
    }

    private boolean isOwner(Item item, Long userId) {
        return item.getOwner().getId().equals(userId);
    }

    private ItemResponseDto getItemResponseDto(Item item, List<Comment> comments, LocalDateTime now) {

        Long itemId = item.getId();
        BookingItemResponseDto lastBooking = getLastBooking(itemId, now);
        BookingItemResponseDto nextBooking = getNextBooking(itemId, now);
        List<CommentResponseDto> commentsDto = CommentMapper.toCommentResponseDtoList(comments);

        return ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, commentsDto);
    }

    private Map<Item, List<Comment>> getCommentsToAllItems(List<Item> items) {

        List<Comment> comments = commentJpaRepository.findAllByItemIn(items);
        return comments.stream()
                .collect(Collectors.groupingBy(Comment::getItem));
    }

    private BookingItemResponseDto getLastBooking(Long itemId, LocalDateTime now) {
        return bookingJpaRepository
                .findFirstByItemIdAndStatusAndStartIsBeforeOrStartEqualsOrderByEndDesc(itemId,
                        BookingStatus.APPROVED, now, now)
                .map(BookingMapper::toBookingItemResponseDto)
                .orElse(null);

    }

    private BookingItemResponseDto getNextBooking(Long itemId, LocalDateTime now) {
        return bookingJpaRepository
                .findFirstByItemIdAndStatusAndStartIsAfterOrStartEqualsOrderByStart(itemId,
                        BookingStatus.APPROVED, now, now)
                .map(BookingMapper::toBookingItemResponseDto)
                .orElse(null);
    }

    private void writeToLog(List<ItemResponseDto> items) {
        String result = items.stream()
                .map(ItemResponseDto::toString)
                .collect(Collectors.joining(", "));
        log.info("Items list --> {}", result);
    }

    private List<CommentResponseDto> getCommentsByItemId(Long itemId) {
        List<Comment> comments = commentJpaRepository.findAllByItemId(itemId);
        return CommentMapper.toCommentResponseDtoList(comments);
    }

    private ItemRequest getItemRequestIfExists(ItemDto itemDto) {
        if (itemDto.getRequestId() == null) {
            return null;
        }
        Long requestId = itemDto.getRequestId();
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Не найден запрос с requestId %d", requestId)));
    }
}