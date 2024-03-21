package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repostitory.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.CommentCreationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public ItemBookingDto getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        ItemBookingDto itemDto;
        List<Booking> bookings = bookingRepository.findAllByItemItemId(id);
        List<CommentDto> comments = CommentMapper.toDto(commentRepository.findAllCommentByItemItemId(id));


        if (item.getUser().getUserId().equals(userId) && !bookings.isEmpty()) {
            Booking lastBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) && !booking.getStatusBooking().equals(StatusBooking.REJECTED))
                    .min(Booking::compareTo)
                    .orElse(null);

            Booking nextBooking = bookings.stream()
                    .filter((booking) -> booking.getStart().isAfter(LocalDateTime.now()) && !booking.getStatusBooking().equals(StatusBooking.REJECTED))
                    .max(Booking::compareTo)
                    .orElse(null);

            itemDto = ItemMapper.toDto(item, lastBooking == null ? null : BookingMapper.toDto(lastBooking),
                    nextBooking == null ? null : BookingMapper.toDto(nextBooking), comments);
        } else {
            itemDto = ItemMapper.toDto(item, null, null, comments);
        }

        log.info("Найден item --> {}", item);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBookingDto> getItemsByUser(Long userId) {
        List<Item> items = itemRepository.findAllByUserUserId(userId);

        log.info("Найдены items --> {}", items);
        return items.stream()
                .map(item -> getItemById(item.getItemId(), userId))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        checkOwner(user, item);
        update(item, itemDto);
        itemDto.setId(itemId);
        itemRepository.save(item);
        log.info("Обновлен Item с itemId -->{}", itemId);

        return ItemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Item savedItem = itemRepository.save(ItemMapper.toItem(item, user));
        log.info("Создан Item с itemId --> {}", savedItem.getItemId());

        return ItemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    public void remove(Long itemId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (!item.getUser().getUserId().equals(user.getUserId())) {
            throw new ItemNotOwnerException(userId, itemId);
        }

        itemRepository.delete(item);
        log.info("Удален Item c itemId --> {}", itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true, text, text);

        log.info("Найдены Items --> {}", items);
        return ItemMapper.toDto(items);
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (item.getUser().getUserId().equals(userId)) {
            throw new CommentCreationException("Owner для item не может добавлять комментарии");
        }

        List<Booking> bookings = bookingRepository.findAllByUserUserIdAndEndIsBeforeOrderByEndDesc(userId, LocalDateTime.now());

        bookings.stream()
                .filter((booking) -> booking.getUser().getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CommentCreationException("User с userId = " + userId + " не бронирует itemId = " + itemId));

        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, author, item));

        log.info("Comment сохранен с commentId --> {}", comment.getCommentId());

        return CommentMapper.toDto(comment);
    }

    private void checkOwner(User user, Item item) {
        if (!item.getUser().getUserId().equals(user.getUserId())) {
            throw new ItemNotOwnerException(user.getUserId(), item.getItemId());
        }
    }

    private void update(Item item, ItemDto itemDto) {

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

    }
}
