package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.dto.AdvanceInfo;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.ValidateDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(long userId, @Valid ItemDto itemDto) {
        ValidateDto.validate(itemDto, AdvanceInfo.class);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Не найден пользователь с id = " + userId));

        Item item = ItemMapper.toItem(itemDto, user);

        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public Item findById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Не найден item с id = " + itemId));
    }

    @Override
    public ItemDto findItemById(long userId, long itemId) {
        Item item = findById(itemId);

        List<Comment> comments = commentRepository.findCommentsByItem_Id(itemId);
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(CommentMapper.tocommentDto(comment));
        }

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentsDto);

        if (userId == item.getOwner().getId()) {
            var bookings = bookingRepository.findBookingByItem_IdAndStatus(item.getId(),
                    StatusBooking.APPROVED);

            if (bookings.size() != 0) {
                bookings = bookings.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList());

                for (Booking booking : bookings) {
                    if (booking.getStart().isBefore(LocalDateTime.now())) {
                        itemDto.setLastBooking(BookingMapper.toBookingDto(booking));
                        break;
                    }
                }

                bookings = bookings.stream()
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());

                for (Booking booking : bookings) {
                    if (booking.getStart().isAfter(LocalDateTime.now())) {
                        itemDto.setNextBooking(BookingMapper.toBookingDto(booking));
                        break;
                    }
                }
            }
        }

        return itemDto;
    }

    @Override
    public ItemDto update(long userId, @Valid ItemDto itemDto) throws ValidateException {
        userRepository.findById(userId);
        Item item = findById(itemDto.getId());
        item = ItemMapper.toItem(itemDto, item);

        if (item.getOwner().getId() != userId) {
            throw new UserNotFoundException("Ошибка редактирования Item для owner с id = " + item.getOwner().getId() +
                    " от имени другого пользователя с id = " + userId);
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public void removeById(long itemId) {
        findById(itemId);
        itemRepository.deleteById(itemId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> findAll(long userId) {

        List<ItemDto> itemDtos = new ArrayList<>();
        List<Item> items = itemRepository.findItemByOwnerId(userId);

        items = items.stream()
                .sorted(Comparator.comparingLong(Item::getId))
                .collect(Collectors.toList());

        for (Item item : items) {
            var itemDto = ItemMapper.toItemDto(item);

            if (userId == item.getOwner().getId()) {
                var bookings = bookingRepository.findBookingByItem_IdAndStatus(item.getId(), StatusBooking.APPROVED);

                if (bookings.size() > 0) {
                    bookings = bookings.stream()
                            .sorted(Comparator.comparing(Booking::getStart).reversed())
                            .collect(Collectors.toList());

                    for (Booking booking : bookings) {
                        if (booking.getStart().isBefore(LocalDateTime.now())) {
                            itemDto.setLastBooking(BookingMapper.toBookingDto(booking));
                            break;
                        }
                    }
                    bookings = bookings.stream()
                            .sorted(Comparator.comparing(Booking::getStart))
                            .collect(Collectors.toList());
                    for (Booking booking : bookings) {
                        if (booking.getStart().isAfter(LocalDateTime.now())) {
                            itemDto.setNextBooking(BookingMapper.toBookingDto(booking));
                            break;
                        }
                    }
                }
            }

            List<Comment> comments = commentRepository.findCommentsByItem_Id(item.getId());
            List<CommentDto> commentsDto = new ArrayList<>();
            for (Comment comment : comments) {
                commentsDto.add(CommentMapper.tocommentDto(comment));
            }
            itemDto.setComments(commentsDto);

            itemDtos.add(itemDto);
        }
        return itemDtos;

    }

    @Override
    public List<ItemDto> findByText(long userId, String text) {
        userRepository.findById(userId);

        return text.isEmpty() ? Collections.emptyList() :
                itemRepository.findByText(text).stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {

        String text = commentDto.getText();
        if (text.isEmpty()) {
            throw new ValidateException("Текст comment должен быть заполнен");
        }
        commentDto.setText(text);

        var itemOptional = itemRepository.findById(itemId);

        if (itemOptional.isEmpty()) {
            throw new ItemNotFoundException("Нет item для itemId = " + itemId);
        }
        var item = itemOptional.get();

        Optional<User> userOptional = userRepository.findById(userId);

        List<Booking> bookings = bookingRepository.findByItem_Id(itemId);
        boolean isExist = bookings.stream()
                .anyMatch(booking -> booking.getBooker().getId() == userId
                        && booking.getStart().isBefore(LocalDateTime.now())
                        && booking.getStatus().equals(StatusBooking.APPROVED));
        if (!isExist) {
            throw new ValidateException("Item не существует для user");
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(userOptional.get());
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.tocommentDto(commentRepository.save(comment));
    }
}
